package com.cedarstudios.cedarmapssdk;

import com.cedarstudios.cedarmapssdk.auth.Authorization;
import com.cedarstudios.cedarmapssdk.auth.NullAuthorization;
import com.cedarstudios.cedarmapssdk.auth.OAuth2Authorization;
import com.cedarstudios.cedarmapssdk.auth.OAuth2Support;
import com.cedarstudios.cedarmapssdk.auth.OAuth2Token;
import com.cedarstudios.cedarmapssdk.config.Configuration;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.Serializable;

abstract class CedarMapsBaseImpl implements CedarMapsBase, Serializable, OAuth2Support {

    Configuration conf;

    Authorization auth;

    CedarMapsBaseImpl(Configuration conf, Authorization auth) {
        this.conf = conf;
        this.auth = auth;
        init();
    }

    private void init() {
        if (null == auth) {
            // try to populate OAuthAuthorization if available in the configuration
            String clientId = conf.getOAuthClientId();
            String clientSecret = conf.getOAuthClientSecret();
            // try to find oauth tokens in the configuration
            if (clientId != null && clientSecret != null) {
                OAuth2Authorization oauth2 = new OAuth2Authorization(conf);
                String tokenType = conf.getOAuth2TokenType();
                String accessToken = conf.getOAuth2AccessToken();
                if (tokenType != null && accessToken != null) {
                    oauth2.setOAuth2Token(new OAuth2Token(tokenType, accessToken));
                }
                this.auth = oauth2;
            } else {
                this.auth = NullAuthorization.getInstance();
            }
        }
    }


    @Override
    public final Authorization getAuthorization() {
        return auth;
    }

    @Override
    public Configuration getConfiguration() {
        return this.conf;
    }

    final void ensureAuthorizationEnabled() {
        if (!auth.isEnabled()) {
            throw new IllegalStateException("Authentication credentials are missing. ");
        }
    }


    private void writeObject(java.io.ObjectOutputStream out) throws IOException {
        // http://docs.oracle.com/javase/6/docs/platform/serialization/spec/output.html#861
        out.putFields();
        out.writeFields();

        out.writeObject(conf);
        out.writeObject(auth);
    }

    private void readObject(ObjectInputStream stream)
            throws IOException, ClassNotFoundException {
        // http://docs.oracle.com/javase/6/docs/platform/serialization/spec/input.html#2971
        stream.readFields();

        conf = (Configuration) stream.readObject();
        auth = (Authorization) stream.readObject();
    }

    // methods declared in OAuthSupport interface


    @Override
    public synchronized OAuth2Token getOAuth2Token() throws CedarMapsException {
        return getOAuth2().getOAuth2Token();
    }

    @Override
    public void setOAuth2Token(OAuth2Token oauth2Token) {
        getOAuth2().setOAuth2Token(oauth2Token);
    }

    @Override
    public synchronized void invalidateOAuth2Token() throws CedarMapsException {
        getOAuth2().invalidateOAuth2Token();
    }

    private OAuth2Support getOAuth2() {
        if (!(auth instanceof OAuth2Support)) {
            throw new IllegalStateException(
                    "OAuth client id/secret combination not supplied");
        }
        return (OAuth2Support) auth;
    }

}
