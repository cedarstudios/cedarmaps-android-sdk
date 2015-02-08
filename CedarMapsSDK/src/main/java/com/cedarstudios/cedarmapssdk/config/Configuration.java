package com.cedarstudios.cedarmapssdk.config;


import com.cedarstudios.cedarmapssdk.auth.AuthorizationConfiguration;

import java.io.Serializable;

public interface Configuration extends AuthorizationConfiguration, Serializable {


    String getOAuth2TokenType();

    String getOAuth2AccessToken();

    String getRestBaseURL();

    String getOAuth2Scope();

    String getOAuth2TokenURL();

    String getOAuthClientId();

    String getOAuthClientSecret();

    String getMapId();
}
