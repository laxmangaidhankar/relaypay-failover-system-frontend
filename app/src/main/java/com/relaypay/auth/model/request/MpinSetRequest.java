package com.relaypay.auth.model.request;

public class MpinSetRequest {

    private final String phone;
    private final String verificationToken;
    private final String loginPin;

    public MpinSetRequest(String phone, String verificationToken, String loginPin) {
        this.phone = phone;
        this.verificationToken = verificationToken;
        this.loginPin = loginPin;
    }

    public String getPhone() { return phone; }
    public String getVerificationToken() { return verificationToken; }
    public String getMpin() { return loginPin; }
}