package com.relaypay.model.auth.request;

public class CheckMobileRequest {
    private final String phone;

    public CheckMobileRequest(String phone){
        this.phone = phone;
    }

    public String getPhone() {
        return phone;
    }
}
