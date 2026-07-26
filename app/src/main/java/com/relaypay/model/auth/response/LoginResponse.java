package com.relaypay.auth.model.response;

public class LoginResponse {

    private String message;
    private UserResponse user;
    private String accessToken;

    private boolean success;


    public boolean isSuccess() {
        return success;
    }


    public String getMessage() {
        return message;
    }


    public UserResponse getUser() {
        return user;
    }

    public String getAccessToken() {
        return accessToken;
    }
}