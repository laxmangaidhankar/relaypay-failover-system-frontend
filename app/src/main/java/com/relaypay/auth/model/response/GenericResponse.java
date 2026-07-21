package com.relaypay.auth.model.response;

public class GenericResponse {
    private boolean success;
    private String purpose;
    private String error;

    public boolean isSuccess() { return success; }
    public String getPurpose() { return purpose; }
    public String getError() { return error; }
}