package com.cedarstudios.cedarmapssdk;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.LocalBroadcastManager;
import android.text.TextUtils;

import com.cedarstudios.cedarmapssdk.listeners.AccessTokenListener;
import com.mapbox.mapboxsdk.Mapbox;

import org.json.JSONObject;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.ResponseBody;

import static com.mapbox.mapboxsdk.constants.MapboxConstants.INTENT_FILTER_TILE_HTTP_CODE_401;

final class AuthenticationManager {

    static final String ACCESS_TOKEN_READY_INTENT = "ACCESS_TOKEN_READY_INTENT";

    private static final String defaultBaseURL = "https://api.cedarmaps.com/v1/";
    private static final String SAVED_ACCESS_TOKEN_KEY = "com.cedarstudios.cedarmapssdk.saved_access_token";

    private static AuthenticationManager instance;

    private String mClientID;
    private String mClientSecret;
    private String mAccessToken;

    private String mBaseURL;

    private Context mContext = null;

    private boolean isFetchingNewAccessToken = false;
    private BroadcastReceiver mapViewError401BroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            regenerateAccessToken();
        }
    };

    private AuthenticationManager() {
        mBaseURL = defaultBaseURL;
    }

    public static AuthenticationManager getInstance() {
        if (instance == null) {
            instance = new AuthenticationManager();
        }
        return instance;
    }

    void setClientID(@NonNull String clientID) {
        mClientID = clientID;
    }

    void setClientSecret(@NonNull String clientSecret) {
        mClientSecret = clientSecret;
    }

    void getAccessToken(final @Nullable AccessTokenListener completionHandler) {

        Handler handler = new Handler(Looper.getMainLooper());

        if (TextUtils.isEmpty(mAccessToken)) {
            String result;
            try {
                result = getSavedAccessToken();
                if (TextUtils.isEmpty(result)) {
                    fetchAccessTokenFromServer(completionHandler);
                } else {
                    mAccessToken = result;
                    if (completionHandler == null) {
                        return;
                    }
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            completionHandler.onSuccess(mAccessToken);
                        }
                    });
                }
            } catch (final CedarMapsException e) {
                if (completionHandler == null) {
                    return;
                }
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        completionHandler.onFailure(e.getMessage());

                    }
                });
            }
        } else {
            if (completionHandler == null) {
                return;
            }
            handler.post(new Runnable() {
                @Override
                public void run() {
                    completionHandler.onSuccess(mAccessToken);
                }
            });
        }
    }

    void setContext(@NonNull Context context) {
        mContext = context.getApplicationContext();
        LocalBroadcastManager.getInstance(mContext)
                .registerReceiver(instance.mapViewError401BroadcastReceiver,
                        new IntentFilter(INTENT_FILTER_TILE_HTTP_CODE_401));
    }

    Context getContext() {
        return mContext;
    }

    private void invalidateCredentials() {
        mAccessToken = null;
        if (mContext != null) {
            SharedPreferences sharedPreferences = mContext.getSharedPreferences(SAVED_ACCESS_TOKEN_KEY, Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.remove(SAVED_ACCESS_TOKEN_KEY);
            editor.apply();
        }
    }

    void regenerateAccessToken() {
        if (isFetchingNewAccessToken) {
            return;
        }
        isFetchingNewAccessToken = true;
        invalidateCredentials();

        fetchAccessTokenFromServer(new AccessTokenListener() {
            @Override
            public void onSuccess(@NonNull String accessToken) {
                isFetchingNewAccessToken = false;
                if (!TextUtils.isEmpty(accessToken)) {
                    Mapbox.getInstance(getContext(), accessToken);

                    Intent intent = new Intent(ACCESS_TOKEN_READY_INTENT);
                    LocalBroadcastManager.getInstance(getContext()).sendBroadcast(intent);
                }
            }

            @Override
            public void onFailure(@NonNull String error) {
                isFetchingNewAccessToken = false;
            }
        });
    }

    private boolean saveAccessToken() throws CedarMapsException {
        if (TextUtils.isEmpty(mAccessToken)) {
            throw new CedarMapsException("AccessToken is not available to save. Try calling 'getAccessToken' first.");
        }
        if (mContext == null) {
            throw new CedarMapsException("Context is not set. Please call 'setContext' method on CedarMaps.getInstance()");
        }
        SharedPreferences sharedPreferences = mContext.getSharedPreferences(SAVED_ACCESS_TOKEN_KEY, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(SAVED_ACCESS_TOKEN_KEY, mAccessToken);
        editor.apply();
        return true;
    }

    @Nullable
    private String getSavedAccessToken() throws CedarMapsException {
        if (mContext == null) {
            throw new CedarMapsException("Context is not set. Please call 'setContext' method on CedarMaps.getInstance()");
        }

        SharedPreferences sharedPreferences = mContext.getSharedPreferences(SAVED_ACCESS_TOKEN_KEY, Context.MODE_PRIVATE);
        return sharedPreferences.getString(SAVED_ACCESS_TOKEN_KEY, null);
    }

    String getAPIBaseURL() {
        return mBaseURL;
    }

    void setAPIBaseURL(@Nullable String baseURL) {
        if (baseURL == null) {
            this.mBaseURL = defaultBaseURL;
        } else {
            if ((baseURL.substring(baseURL.length() - 1)).equals("/")) {
                this.mBaseURL = baseURL;
            } else {
                this.mBaseURL = baseURL + "/";
            }
        }
    }

    private void fetchAccessTokenFromServer(final AccessTokenListener completionHandler) {

        CedarOkHttpClient client = CedarOkHttpClient.getInstance();
        final Handler handler = new Handler(Looper.getMainLooper());

        String url = mBaseURL + "token";
        RequestBody formBody = new FormBody.Builder()
                .add("client_id", mClientID)
                .add("client_secret", mClientSecret)
                .build();
        Request request = new Request.Builder()
                .url(url)
                .post(formBody)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, final IOException e) {
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        if (completionHandler != null) {
                            completionHandler.onFailure(e.getMessage());
                        }
                    }
                });
            }

            @Override
            public void onResponse(Call call, final Response response) throws IOException {
                ResponseBody body = response.body();
                if (body == null) {
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            if (completionHandler != null) {
                                completionHandler.onFailure("Response body was null. HTTP code: " + response.code());
                            }
                        }
                    });
                } else {
                    try {
                        JSONObject responseObject = new JSONObject(body.string());

                        if (response.code() == 200) {
                            try {
                                mAccessToken = URLDecoder.decode(responseObject.optString("access_token"), "UTF-8");
                                saveAccessToken();
                                handler.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        if (completionHandler != null) {
                                            completionHandler.onSuccess(mAccessToken);
                                        }
                                    }
                                });
                            } catch (final UnsupportedEncodingException ignore) {
                                handler.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        if (completionHandler != null) {
                                            completionHandler.onFailure(ignore.getMessage());
                                        }
                                    }
                                });
                            }
                        } else if (response.code() == 401) {
                            handler.post(new Runnable() {
                                @Override
                                public void run() {
                                    if (completionHandler != null) {
                                        completionHandler.onFailure("Client Secret or Client ID is wrong. HTTP code: "
                                                + response.code());
                                    }
                                }
                            });
                        } else {
                            handler.post(new Runnable() {
                                @Override
                                public void run() {
                                    if (completionHandler != null) {
                                        completionHandler.onFailure("Obtaining Bearer Token failed. HTTP code: "
                                                + response.code());
                                    }
                                }
                            });
                        }

                    } catch (final Exception e) {
                        handler.post(new Runnable() {
                            @Override
                            public void run() {
                                if (completionHandler != null) {
                                    completionHandler.onFailure(e.getMessage());
                                }
                            }
                        });
                    }
                }
            }
        });
    }
}
