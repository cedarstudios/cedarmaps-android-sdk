package com.cedarstudios.cedarmapssdk;

import okhttp3.OkHttpClient;

final class CedarOkHttpClient extends OkHttpClient {

    private static CedarOkHttpClient client = new CedarOkHttpClient();

    public static CedarOkHttpClient getInstance() {
        return client;
    }
}
