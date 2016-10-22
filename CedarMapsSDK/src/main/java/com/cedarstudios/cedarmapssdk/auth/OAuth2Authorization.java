package com.cedarstudios.cedarmapssdk.auth;


import com.cedarstudios.cedarmapssdk.CedarMapsException;
import com.cedarstudios.cedarmapssdk.config.Configuration;

import org.json.JSONObject;

import java.io.Serializable;

import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class OAuth2Authorization implements Authorization, Serializable, OAuth2Support {

    private final Configuration conf;

    private String clientId;

    private String clientSecret;

    private OAuth2Token token;

    public OAuth2Authorization(Configuration conf) {
        this.conf = conf;
        setOAuthClient(conf.getOAuthClientId(), conf.getOAuthClientSecret());
    }

    @Override
    public void setOAuthClient(String clientId, String clientSecret) {
        this.clientId = clientId != null ? clientId : "";
        this.clientSecret = clientSecret != null ? clientSecret : "";
    }

    @Override
    public OAuth2Token getOAuth2Token() throws CedarMapsException {
        if (token != null) {
            throw new IllegalStateException("OAuth 2 Bearer Token is already available.");
        }

        OkHttpClient client = new OkHttpClient();
        RequestBody formBody = new FormBody.Builder()
                .add("client_id", clientId)
                .add("client_secret", clientSecret)
                .build();
        Request request = new Request.Builder()
                .url(conf.getOAuth2TokenURL())
                .post(formBody)
                .build();

        JSONObject responseObject;
        Response response;
        try {
            response = client.newCall(request).execute();
            responseObject = new JSONObject(response.body().string());
        } catch (Exception e) {
            throw new CedarMapsException(e);
        }

        if (response.code() != 200) {
            throw new CedarMapsException("Obtaining OAuth2 Bearer Token failed.", response);
        }

        token = new OAuth2Token(responseObject);
        return token;
    }

    @Override
    public void setOAuth2Token(OAuth2Token oauth2Token) {
        this.token = oauth2Token;
    }

    @Override
    public void invalidateOAuth2Token() throws CedarMapsException {
        //TODO
    }

    @Override
    public boolean isEnabled() {
        return token != null;
    }

}
