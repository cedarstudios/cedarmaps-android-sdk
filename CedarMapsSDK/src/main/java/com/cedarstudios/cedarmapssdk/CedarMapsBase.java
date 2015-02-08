package com.cedarstudios.cedarmapssdk;


import com.cedarstudios.cedarmapssdk.auth.Authorization;
import com.cedarstudios.cedarmapssdk.config.Configuration;

public interface CedarMapsBase {


    /**
     * Returns the authorization scheme for this instance.<br>
     * The returned type will be either of OAuthAuthorization or NullAuthorization
     *
     * @return the authorization scheme for this instance
     */
    Authorization getAuthorization();

    /**
     * Returns the configuration associated with this instance
     *
     * @return configuration associated with this instance
     * @since CedarMaps 0.5
     */
    Configuration getConfiguration();
}
