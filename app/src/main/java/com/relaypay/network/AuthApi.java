package com.relaypay.network;

import com.relaypay.auth.model.request.MpinSetRequest;
import com.relaypay.auth.model.response.CheckMobileResponse;
import com.relaypay.auth.model.response.GenericResponse;
import com.relaypay.auth.model.request.OtpVerifyRequest;
import com.relaypay.auth.model.response.OtpVerifyResponse;
import com.relaypay.auth.model.request.PhoneRequest;
import com.relaypay.auth.model.response.SetMpinResponse;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface AuthApi {
    @POST("api/v1/auth/check-mobile")
    Call<CheckMobileResponse> checkMobile(@Body PhoneRequest request);

    @POST("api/v1/auth/request-otp")
    Call<GenericResponse> requestOtp(@Body PhoneRequest body);

    @POST("api/v1/auth/verify-otp")
    Call<OtpVerifyResponse> verifyOtp(@Body OtpVerifyRequest body);

    @POST("api/v1/auth/register")
    Call<SetMpinResponse> setMpin(@Body MpinSetRequest request);
}