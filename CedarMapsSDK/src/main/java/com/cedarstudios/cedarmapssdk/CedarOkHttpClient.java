package com.cedarstudios.cedarmapssdk;

import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.v4.os.ConfigurationCompat;

import java.io.IOException;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Response;

public class CedarOkHttpClient {

    private static OkHttpClient sharedInstance;

    public static OkHttpClient getSharedInstance(final Context applicationContext) {
        if (sharedInstance != null) {
            return sharedInstance;
        }
        sharedInstance = new OkHttpClient.Builder()
                .connectTimeout(30, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .addNetworkInterceptor(new Interceptor() {
                    @Override
                    public Response intercept(Chain chain) throws IOException {
                        String userAgent;
                        if (userAgent(applicationContext) != null) {
                            userAgent = userAgent(applicationContext);
                        } else {
                            userAgent = "CedarMaps SDK";
                        }
                        assert userAgent != null;
                        return chain.proceed(
                                chain.request().newBuilder().header(
                                        "User-Agent",
                                        userAgent
                                ).build()
                        );
                    }
                })
                .build();
        return sharedInstance;
    }

    private static String userAgent(Context applicationContext) {
        String abi;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            abi = Build.SUPPORTED_ABIS[0];
        } else {
            abi = Build.CPU_ABI;
        }

        try {
            return String.format(
                    Locale.US, "%s/%s (%d) %s Android/%s %s-%s (%s)",
                    applicationContext.getPackageName(),
                    applicationContext.getPackageManager().getPackageInfo(
                            applicationContext.getPackageName(),
                            0
                    ).versionName,
                    applicationContext.getPackageManager().getPackageInfo(
                            applicationContext.getPackageName(),
                            0
                    ).versionCode,
                    ConfigurationCompat
                            .getLocales(applicationContext.getResources().getConfiguration()).get(0),
                    Build.VERSION.RELEASE,
                    Build.MANUFACTURER,
                    Build.MODEL,
                    abi
            );
        } catch (PackageManager.NameNotFoundException e) {
            return null;
        }
    }
}
