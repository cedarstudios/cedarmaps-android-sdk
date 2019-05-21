package com.cedarstudios.cedarmapssdk.mapbox;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;

import com.cedarstudios.cedarmapssdk.CedarMapsException;
import com.cedarstudios.cedarmapssdk.CedarOkHttpClient;
import com.cedarstudios.cedarmapssdk.listeners.AccessTokenListener;
import com.mapbox.mapboxsdk.Mapbox;

import org.json.JSONObject;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.ResponseBody;

final public class AuthenticationManager {

    private static final String defaultBaseURL = "https://api.cedarmaps.com/v1/";
    private static final String SAVED_ACCESS_TOKEN_KEY = "com.cedarstudios.cedarmapssdk.saved_access_token";

    @SuppressLint("StaticFieldLeak")
    private static AuthenticationManager instance;

    private String mClientID;
    private String mClientSecret;
    private String mAccessToken;

    private String mBaseURL;

    private Context mContext = null;

    private boolean isFetchingNewAccessToken = false;

    private AuthenticationManager() {
        mBaseURL = defaultBaseURL;
    }

    public static AuthenticationManager getInstance() {
        if (instance == null) {
            instance = new AuthenticationManager();
        }
        return instance;
    }

    public void setClientID(@NonNull String clientID) {
        if (!clientID.equals(mClientID)) {
            mClientID = clientID;
        }
    }

    public void setClientSecret(@NonNull String clientSecret) {
        if (!clientSecret.equals(mClientSecret)) {
            mClientSecret = clientSecret;
        }
    }

    public void getAccessToken(final @Nullable AccessTokenListener completionHandler) {

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

    public void setContext(@NonNull Context context) {
        if (mContext == null) {
            mContext = context.getApplicationContext();
            Mapbox.getInstance(mContext, Constants.INITIAL_TOKEN);
        }
    }

    public Context getContext() {
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

    public void regenerateAccessToken() throws CedarMapsException {
        if (isFetchingNewAccessToken) {
            return;
        }
        isFetchingNewAccessToken = true;
        invalidateCredentials();

        if (Mapbox.getAccessToken() == null) {
            Mapbox.getInstance(getContext(), Constants.INITIAL_TOKEN);
        } else {
            Mapbox.setAccessToken(Constants.INITIAL_TOKEN);
        }

        fetchAccessTokenFromServer(new AccessTokenListener() {
            @Override
            public void onSuccess(@NonNull String accessToken) {
                isFetchingNewAccessToken = false;
                if (!TextUtils.isEmpty(accessToken)) {
                    Mapbox.setAccessToken("pk." + accessToken);
                }
            }

            @Override
            public void onFailure(@NonNull String error) {
                isFetchingNewAccessToken = false;
            }
        });
    }

    private void saveAccessToken() throws CedarMapsException {
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
    }

    @Nullable
    public String getSavedAccessToken() throws CedarMapsException {
        if (mContext == null) {
            throw new CedarMapsException("Context is not set. Please call 'setContext' method on CedarMaps.getInstance()");
        }

        SharedPreferences sharedPreferences = mContext.getSharedPreferences(SAVED_ACCESS_TOKEN_KEY, Context.MODE_PRIVATE);
        return sharedPreferences.getString(SAVED_ACCESS_TOKEN_KEY, null);
    }

    public String getAPIBaseURL() {
        return mBaseURL;
    }

    public void setAPIBaseURL(@Nullable String baseURL) {
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

    private void fetchAccessTokenFromServer(final AccessTokenListener completionHandler) throws CedarMapsException {

        if (mContext == null) {
            throw new CedarMapsException("Context is not set. Please call 'setContext' method on CedarMaps.getInstance()");
        }

        OkHttpClient client = CedarOkHttpClient.getSharedInstance(mContext);
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
            public void onResponse(Call call, final Response response) {
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
                            } catch (final UnsupportedEncodingException e) {
                                handler.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        if (completionHandler != null) {
                                            completionHandler.onFailure(e.getMessage());
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
