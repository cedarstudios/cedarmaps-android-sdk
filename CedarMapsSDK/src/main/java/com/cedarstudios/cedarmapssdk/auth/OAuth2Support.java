package com.cedarstudios.cedarmapssdk.auth;


import com.cedarstudios.cedarmapssdk.CedarMapsException;


public interface OAuth2Support {

    /**
     * Sets the OAuth client id and client secret.
     *
     * @param clientId OAuth client id
     * @param clientSecret OAuth client secret
     * @throws IllegalStateException when OAuth client has already been set, or the instance is
     *                               using basic authorization.
     */
    void setOAuthClient(String clientId, String clientSecret);

    /**
     * Obtains an OAuth 2 Bearer token.
     *
     * @return OAuth 2 Bearer token
     * @throws CedarMapsException    when service or network is unavailable, or connecting
     *                               non-SSL endpoints.
     * @throws IllegalStateException when Bearer token is already available, or OAuth client is
     *                               not available.
     */
    OAuth2Token getOAuth2Token() throws CedarMapsException;

    /**
     * Sets the OAuth 2 Bearer token.
     *
     * @param oauth2Token OAuth 2 Bearer token
     */
    void setOAuth2Token(OAuth2Token oauth2Token);

    /**
     * Revokes an issued OAuth 2 Bearer Token.
     *
     * @throws CedarMapsException    when service or network is unavailable, or connecting
     *                               non-SSL endpoints.
     * @throws IllegalStateException when Bearer token is not available.
     */
    void invalidateOAuth2Token() throws CedarMapsException;
}
