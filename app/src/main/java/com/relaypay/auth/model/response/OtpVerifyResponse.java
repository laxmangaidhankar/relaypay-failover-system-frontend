package com.relaypay.auth.model.response;


public class OtpVerifyResponse {
    private boolean success;
    private String purpose;
    private String verificationToken;
    private String error;

    public boolean isSuccess() { return success; }
    public String getPurpose() { return purpose; }
    public String getVerificationToken() { return verificationToken; }
    public String getError() { return error; }
}