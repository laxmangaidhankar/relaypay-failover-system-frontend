package com.relaypay.viewmodel;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.relaypay.auth.model.response.OtpVerifyResponse;
import com.relaypay.repository.AuthRepository;
import com.relaypay.utils.PhoneValidator;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class VerifyOtpViewModel extends ViewModel {

    private final AuthRepository repository = new AuthRepository();

    private final MutableLiveData<Boolean> loading = new MutableLiveData<>(false);
    private final MutableLiveData<String> errorMessage = new MutableLiveData<>();
    private final MutableLiveData<OtpVerifyResponse> verified = new MutableLiveData<>();

    public LiveData<Boolean> getLoading() { return loading; }
    public LiveData<String> getErrorMessage() { return errorMessage; }
    public LiveData<OtpVerifyResponse> getVerified() { return verified; }

    public void verifyOtp(String phone, String otp) {
        if (!PhoneValidator.isValidOtp(otp)) {
            errorMessage.setValue("Enter the 6-digit OTP");
            return;
        }

        loading.setValue(true);

        repository.verifyOtp(phone, otp, new Callback<OtpVerifyResponse>() {
            @Override
            public void onResponse(@NonNull Call<OtpVerifyResponse> call,
                                   @NonNull Response<OtpVerifyResponse> response) {
                loading.setValue(false);

                if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                    verified.setValue(response.body());
                } else if (response.body() != null) {
                    errorMessage.setValue(response.body().getError());
                } else {
                    errorMessage.setValue("Invalid OTP, please try again");
                }
            }

            @Override
            public void onFailure(@NonNull Call<OtpVerifyResponse> call, @NonNull Throwable t) {
                loading.setValue(false);
                errorMessage.setValue("Network error, check your connection");
            }
        });
    }
}