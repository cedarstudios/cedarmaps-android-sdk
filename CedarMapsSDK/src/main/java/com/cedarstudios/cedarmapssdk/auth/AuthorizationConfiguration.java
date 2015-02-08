package com.cedarstudios.cedarmapssdk.auth;

public interface AuthorizationConfiguration {

    String getOAuth2TokenType();

    String getOAuth2AccessToken();
}