package com.cedarstudios.cedarmapssdk.config;

public final class ConfigurationBuilder {

    private ConfigurationBase configurationBase = new ConfigurationBase();

    public ConfigurationBuilder setAPIBaseURL(String baseURL) {
        checkNotBuilt();
        configurationBase.setAPIBaseUrl(baseURL);
        return this;
    }


    public ConfigurationBuilder setClientId(String clientId) {
        checkNotBuilt();
        configurationBase.setOAuthClientId(clientId);
        return this;
    }

    public ConfigurationBuilder setClientSecret(String clientSecret) {
        checkNotBuilt();
        configurationBase.setClientSecret(clientSecret);
        return this;
    }

    public ConfigurationBuilder setMapId(String mapId) {
        checkNotBuilt();
        configurationBase.setMapId(mapId);
        return this;
    }

    public ConfigurationBuilder setOAuth2TokenType(String oAuth2TokenType) {
        checkNotBuilt();
        configurationBase.setOAuth2TokenType(oAuth2TokenType);
        return this;
    }

    public ConfigurationBuilder setOAuth2AccessToken(String oAuth2AccessToken) {
        checkNotBuilt();
        configurationBase.setOAuth2AccessToken(oAuth2AccessToken);
        return this;
    }

    public ConfigurationBuilder setOAuth2Scope(String oAuth2Scope) {
        checkNotBuilt();
        configurationBase.setOAuth2Scope(oAuth2Scope);
        return this;
    }

    public ConfigurationBuilder setOAuth2TokenURL(String oAuth2TokenURL) {
        checkNotBuilt();
        configurationBase.setOAuth2TokenURL(oAuth2TokenURL);
        return this;
    }


    public ConfigurationBuilder setRestBaseURL(String restBaseURL) {
        checkNotBuilt();
        configurationBase.setRestBaseURL(restBaseURL);
        return this;
    }

    public Configuration build() {
        checkNotBuilt();
        configurationBase.cacheInstance();
        try {
            return configurationBase;
        } finally {
            configurationBase = null;
        }
    }

    private void checkNotBuilt() {
        if (configurationBase == null) {
            throw new IllegalStateException(
                    "Cannot use this builder any longer, build() has already been called");
        }
    }
}
