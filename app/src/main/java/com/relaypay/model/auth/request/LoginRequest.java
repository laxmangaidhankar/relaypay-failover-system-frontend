package com.relaypay.model.auth.request;

public class LoginRequest {
    private final String phone;

    private final String loginPin;

    public LoginRequest(String phone, String loginPin){
        this.phone = phone;
        this.loginPin = loginPin;
    }

    public String getAccount(){ return phone; }
}
