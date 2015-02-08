package com.cedarstudios.cedarmapssdk.auth;

import java.io.Serializable;

public class NullAuthorization implements Authorization, Serializable {

    private static final NullAuthorization SINGLETON = new NullAuthorization();

    private NullAuthorization() {

    }

    public static NullAuthorization getInstance() {
        return SINGLETON;
    }

    @Override
    public boolean isEnabled() {
        return false;
    }

}