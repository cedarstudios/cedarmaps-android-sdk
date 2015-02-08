package com.cedarstudios.cedarmapssdk;

import com.cedarstudios.cedarmapssdk.auth.Authorization;
import com.cedarstudios.cedarmapssdk.auth.AuthorizationFactory;
import com.cedarstudios.cedarmapssdk.config.Configuration;

public class CedarMapsFactory {

    private static CedarMaps singleton;

    private final Configuration configuration;

    public CedarMaps getInstance() {
        return getInstance(AuthorizationFactory.getInstance(configuration));
    }

    public CedarMaps getInstance(Authorization auth) {
        return new CedarMapsImpl(configuration, auth);
    }

    public CedarMapsFactory(Configuration configuration) {
        this.configuration = configuration;
    }

}
