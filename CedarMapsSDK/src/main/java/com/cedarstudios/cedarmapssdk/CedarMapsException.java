package com.cedarstudios.cedarmapssdk;


import com.squareup.okhttp.Response;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

public class CedarMapsException extends Exception {

    private Response response;

    private int statusCode = -1;

    private String errorMessage = null;

    public CedarMapsException(String message, Throwable cause) {
        super(message, cause);
    }

    public CedarMapsException(String message) {
        this(message, (Throwable) null);
    }


    public CedarMapsException(Exception cause) {
        this(cause.getMessage(), cause);
        if (cause instanceof CedarMapsException) {
            ((CedarMapsException) cause).setNested();
        }
    }

    public CedarMapsException(String message, Response res) {
        this(message);
        response = res;
        this.statusCode = res.code();
    }

    public CedarMapsException(String message, Exception cause, int statusCode) {
        this(message, cause);
        this.statusCode = statusCode;
    }

    @Override
    public String getMessage() {
        StringBuilder value = new StringBuilder();
        if (errorMessage != null) {
            value.append("message - ").append(errorMessage)
                    .append("\n");
        } else {
            value.append(super.getMessage());
        }
        if (statusCode != -1) {
            return getCause(statusCode) + "\n" + value.toString();
        } else {
            return value.toString();
        }
    }

    public int getStatusCode() {
        return this.statusCode;
    }

    /**
     * Tests if the exception is caused by network issue
     *
     * @return if the exception is caused by network issue
     * @since CedarMaps 0.5
     */
    public boolean isCausedByNetworkIssue() {
        return getCause() instanceof IOException;
    }

    private boolean nested = false;

    void setNested() {
        nested = true;
    }

    /**
     * Returns error message from the API if available.
     *
     * @return error message from the API
     * @since CedarMaps 0.5
     */
    public String getErrorMessage() {
        return errorMessage;
    }

    /**
     * Tests if error message from the API is available
     *
     * @return true if error message from the API is available
     * @since CedarMaps 0.5
     */
    public boolean isErrorMessageAvailable() {
        return errorMessage != null;
    }

    private static String getCause(int statusCode) {
        String cause;
        switch (statusCode) {
            case 401:
                cause
                        = "Authentication credentials were missing or incorrect. Ensure that you have set valid client id  or secret, access token and the system clock is in sync.";
                break;
            default:
                cause = "";
        }
        return statusCode + ":" + cause;
    }
}
