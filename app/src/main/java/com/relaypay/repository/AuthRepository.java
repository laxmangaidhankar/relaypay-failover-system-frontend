package com.relaypay.repository;

import com.relaypay.model.auth.request.CheckMobileRequest;
import com.relaypay.model.auth.request.LoginRequest;
import com.relaypay.model.auth.response.CheckMobileResponse;
import com.relaypay.model.auth.response.GenericResponse;
import com.relaypay.model.auth.request.MPINSetRequest;
import com.relaypay.model.auth.request.OtpVerifyRequest;
import com.relaypay.model.auth.response.LoginResponse;
import com.relaypay.model.auth.response.OtpVerifyResponse;
import com.relaypay.model.auth.request.PhoneRequest;
import com.relaypay.model.auth.response.SetMpinResponse;
import com.relaypay.network.ApiClient;
import com.relaypay.network.auth.AuthApi;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AuthRepository {

    private final AuthApi authApi;

    public AuthRepository() {
        this.authApi = ApiClient.getAuthApi();
    }


    public void setMpin(String phone,
                        String verificationToken,
                        String mpin,
                        RepositoryCallback<SetMpinResponse> callback) {

        authApi.setMpin(new MPINSetRequest(phone, verificationToken, mpin))
                .enqueue(new Callback<SetMpinResponse>() {

                    @Override
                    public void onResponse(Call<SetMpinResponse> call,
                                           Response<SetMpinResponse> response) {

                        if (response.isSuccessful() && response.body() != null) {

                            SetMpinResponse body = response.body();

                            if (response.isSuccessful() && response.body() != null) {
                                callback.onSuccess(response.body());
                            } else {
                                callback.onError("Failed to set MPIN");
                            }

                        } else {

                            switch (response.code()) {

                                case 400:
                                    callback.onError("Invalid request");
                                    break;

                                case 401:
                                    callback.onError("Verification expired");
                                    break;

                                case 409:
                                    callback.onError("MPIN already exists");
                                    break;

                                case 500:
                                    callback.onError("Server error");
                                    break;

                                default:
                                    callback.onError("Something went wrong");
                            }
                        }
                    }

                    @Override
                    public void onFailure(Call<SetMpinResponse> call,
                                          Throwable t) {

                        callback.onError(
                                t.getMessage() != null
                                        ? t.getMessage()
                                        : "Network error"
                        );
                    }
                });
    }

    public void loginMpin(String phone,
                          String mpin,
                          RepositoryCallback<LoginResponse> callback) {

        authApi.loginMpin(new LoginRequest(phone, mpin))
                .enqueue(new Callback<LoginResponse>() {

                    @Override
                    public void onResponse(Call<LoginResponse> call,
                                           Response<LoginResponse> response) {

                        if (response.isSuccessful() && response.body() != null) {

                            LoginResponse body = response.body();

                            if (body.isSuccess()) {
                                callback.onSuccess(body);
                            } else {
                                callback.onError(body.getMessage());
                            }

                        } else {
                            callback.onError("Invalid MPIN");
                        }
                    }

                    @Override
                    public void onFailure(Call<LoginResponse> call,
                                          Throwable t) {

                        callback.onError(
                                t.getMessage() != null
                                        ? t.getMessage()
                                        : "Network error"
                        );
                    }
                });
    }


    public void checkMobile(String phone,
                            RepositoryCallback<CheckMobileResponse> callback) {

        authApi.checkMobile(new CheckMobileRequest(phone))
                .enqueue(new Callback<CheckMobileResponse>() {

                    @Override
                    public void onResponse(Call<CheckMobileResponse> call,
                                           Response<CheckMobileResponse> response) {

                        if (response.isSuccessful() && response.body() != null) {

                            CheckMobileResponse body = response.body();

                            if (body.isSuccess()) {
                                callback.onSuccess(body);
                            } else {
                                callback.onError("Unable to verify mobile number");
                            }

                        } else {
                            callback.onError("Server error");
                        }
                    }

                    @Override
                    public void onFailure(Call<CheckMobileResponse> call,
                                          Throwable t) {

                        callback.onError(
                                t.getMessage() != null
                                        ? t.getMessage()
                                        : "Network error");
                    }
                });
    }

    public void requestOtp(String phone,
                           RepositoryCallback<GenericResponse> callback) {

        authApi.requestOtp(new PhoneRequest(phone))
                .enqueue(new Callback<GenericResponse>() {

                    @Override
                    public void onResponse(Call<GenericResponse> call,
                                           Response<GenericResponse> response) {

                        if (response.isSuccessful() && response.body() != null) {

                            GenericResponse body = response.body();

                            if (body.isSuccess()) {
                                callback.onSuccess(body);
                            } else {
                                callback.onError("Failed to send OTP");
                            }

                        } else {
                            callback.onError("Server error");
                        }
                    }

                    @Override
                    public void onFailure(Call<GenericResponse> call,
                                          Throwable t) {

                        callback.onError(
                                t.getMessage() != null
                                        ? t.getMessage()
                                        : "Network error");
                    }
                });
    }

    public void verifyOtp(String phone,
                          String otp,
                          RepositoryCallback<OtpVerifyResponse> callback) {

        authApi.verifyOtp(new OtpVerifyRequest(phone, otp))
                .enqueue(new Callback<OtpVerifyResponse>() {

                    @Override
                    public void onResponse(Call<OtpVerifyResponse> call,
                                           Response<OtpVerifyResponse> response) {

                        if (response.isSuccessful() && response.body() != null) {

                            OtpVerifyResponse body = response.body();

                            if (body.isSuccess()) {
                                callback.onSuccess(body);
                            } else {
                                callback.onError(body.getError());
                            }

                        } else {
                            callback.onError("Invalid OTP");
                        }
                    }

                    @Override
                    public void onFailure(Call<OtpVerifyResponse> call,
                                          Throwable t) {

                        callback.onError(
                                t.getMessage() != null
                                        ? t.getMessage()
                                        : "Network error");
                    }
                });
    }
}