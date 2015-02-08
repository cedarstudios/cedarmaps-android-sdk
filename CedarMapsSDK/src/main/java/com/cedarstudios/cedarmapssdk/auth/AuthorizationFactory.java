package com.cedarstudios.cedarmapssdk.auth;

import com.cedarstudios.cedarmapssdk.config.Configuration;

public final class AuthorizationFactory {

    /**
     * @param conf configuration
     * @since CedarMaps 0.5
     */
    public static Authorization getInstance(Configuration conf) {
        Authorization auth = null;
        String clientId = conf.getOAuthClientId();
        String clientSecret = conf.getOAuthClientSecret();

        if (clientId != null && clientSecret != null) {
            OAuth2Authorization oauth2 = new OAuth2Authorization(conf);
            String tokenType = conf.getOAuth2TokenType();
            String accessToken = conf.getOAuth2AccessToken();
            if (tokenType != null && accessToken != null) {
                oauth2.setOAuth2Token(new OAuth2Token(tokenType, accessToken));
            }
            auth = oauth2;
        }
        if (null == auth) {
            auth = NullAuthorization.getInstance();
        }
        return auth;
    }
}
