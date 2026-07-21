package com.relaypay.auth.model.response;

public class CheckMobileResponse {

    private boolean success;
    private boolean userExists;
    private String nextScreen;
    private String error;

    public boolean isSuccess() {
        return success;
    }

    public boolean isUserExists() {
        return userExists;
    }

    public String getNextScreen() {
        return nextScreen;
    }

    public String getError() {
        return error;
    }
}