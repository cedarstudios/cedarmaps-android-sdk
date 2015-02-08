package com.cedarstudios.cedarmapssdk.config;

import com.cedarstudios.cedarmapssdk.CedarMapsConstants;

import java.io.ObjectStreamException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;


class ConfigurationBase implements Configuration, Serializable {

    private String oAuthClientId;

    private String oAuthClientSecret;

    private String oAuth2TokenType;

    private String oAuth2AccessToken;

    private String oAuth2Scope;

    private String mapId;

    private String oAuth2TokenURL = CedarMapsConstants.CEDARMAPS_BASE_URL_V1 + "token";

    private String restBaseURL = CedarMapsConstants.CEDARMAPS_BASE_URL_V1;


    public void setOAuthClientId(String oAuthClientId) {
        this.oAuthClientId = oAuthClientId;
    }

    public void setClientSecret(String clientSecret) {
        this.oAuthClientSecret = clientSecret;
    }

    public void setMapId(String mapId) {
        this.mapId = mapId;
    }

    @Override
    public String getRestBaseURL() {
        return restBaseURL;
    }

    protected final void setRestBaseURL(String restBaseURL) {
        this.restBaseURL = restBaseURL;
    }

    @Override
    public String getOAuth2TokenType() {
        return oAuth2TokenType;
    }

    protected final void setOAuth2TokenType(String oAuth2TokenType) {
        this.oAuth2TokenType = oAuth2TokenType;
    }

    @Override
    public String getOAuth2AccessToken() {
        return oAuth2AccessToken;
    }

    @Override
    public String getOAuth2Scope() {
        return oAuth2Scope;
    }

    protected final void setOAuth2AccessToken(String oAuth2AccessToken) {
        this.oAuth2AccessToken = oAuth2AccessToken;
    }

    protected final void setOAuth2Scope(String oAuth2Scope) {
        this.oAuth2Scope = oAuth2Scope;
    }

    @Override
    public String getOAuth2TokenURL() {
        return oAuth2TokenURL;
    }

    @Override
    public String getOAuthClientId() {
        return oAuthClientId;
    }

    @Override
    public String getOAuthClientSecret() {
        return oAuthClientSecret;
    }

    @Override
    public String getMapId() {
        return mapId;
    }

    protected final void setOAuth2TokenURL(String oAuth2TokenURL) {
        this.oAuth2TokenURL = oAuth2TokenURL;
    }


    private static final List<ConfigurationBase> instances = new ArrayList<ConfigurationBase>();

    private static void cacheInstance(ConfigurationBase conf) {
        if (!instances.contains(conf)) {
            instances.add(conf);
        }
    }

    protected void cacheInstance() {
        cacheInstance(this);
    }

    private static ConfigurationBase getInstance(ConfigurationBase configurationBase) {
        int index;
        if ((index = instances.indexOf(configurationBase)) == -1) {
            instances.add(configurationBase);
            return configurationBase;
        } else {
            return instances.get(index);
        }
    }

    // assures equality after deserialization
    protected Object readResolve() throws ObjectStreamException {
        return getInstance(this);
    }

}
