package com.relaypay.auth.model.request;


public class OtpVerifyRequest {
    private String phone;
    private String otp;

    public OtpVerifyRequest(String phone, String otp) {
        this.phone = phone;
        this.otp = otp;
    }
}