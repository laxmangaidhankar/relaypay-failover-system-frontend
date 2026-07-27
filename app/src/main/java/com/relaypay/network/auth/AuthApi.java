package com.relaypay.network.auth;

import com.relaypay.model.auth.request.CheckMobileRequest;
import com.relaypay.model.auth.request.LoginRequest;
import com.relaypay.model.auth.request.MPINSetRequest;
import com.relaypay.model.auth.response.CheckMobileResponse;
import com.relaypay.model.auth.response.GenericResponse;
import com.relaypay.model.auth.request.OtpVerifyRequest;
import com.relaypay.model.auth.response.LoginResponse;
import com.relaypay.model.auth.response.OtpVerifyResponse;
import com.relaypay.model.auth.request.PhoneRequest;
import com.relaypay.model.auth.response.SetMpinResponse;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface AuthApi {
    @POST("api/v1/auth/check-mobile")
    Call<CheckMobileResponse> checkMobile(@Body CheckMobileRequest request);

    @POST("api/v1/auth/request-otp")
    Call<GenericResponse> requestOtp(@Body PhoneRequest body);

    @POST("api/v1/auth/verify-otp")
    Call<OtpVerifyResponse> verifyOtp(@Body OtpVerifyRequest body);

    @POST("api/v1/auth/register")
    Call<SetMpinResponse> setMpin(@Body MPINSetRequest request);

    @POST("api/v1/auth/login")
    Call<LoginResponse> loginMpin(@Body LoginRequest request);
}