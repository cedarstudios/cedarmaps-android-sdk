package com.cedarstudios.cedarmapssdk.auth;


import com.cedarstudios.cedarmapssdk.CedarMapsException;

import org.json.JSONObject;

import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;


public class OAuth2Token implements Serializable {

    private String tokenType;

    private String accessToken;

    OAuth2Token(JSONObject json) throws CedarMapsException {
        tokenType = json.optString("token_type");
        try {
            accessToken = URLDecoder.decode(json.optString("access_token"), "UTF-8");
        } catch (UnsupportedEncodingException ignore) {
        }
    }

    public OAuth2Token(String tokenType, String accessToken) {
        this.tokenType = tokenType;
        this.accessToken = accessToken;
    }

    public String getTokenType() {
        return tokenType;
    }

    public String getAccessToken() {
        return accessToken;
    }

    String generateAuthorizationHeader() {
        String encoded = "";
        try {
            encoded = URLEncoder.encode(accessToken, "UTF-8");
        } catch (UnsupportedEncodingException ignore) {
        }
        return "Bearer " + encoded;
    }
}