package com.relaypay.viewmodel.auth;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.relaypay.model.auth.response.GenericResponse;
import com.relaypay.model.auth.response.OtpVerifyResponse;
import com.relaypay.repository.AuthRepository;
import com.relaypay.repository.RepositoryCallback;

public class VerifyOtpViewModel extends ViewModel {

    private final AuthRepository repository = new AuthRepository();

    private final MutableLiveData<Boolean> loading = new MutableLiveData<>(false);
    private final MutableLiveData<String> errorMessage = new MutableLiveData<>();

    private final MutableLiveData<OtpVerifyResponse> verifyOtpResponse =
            new MutableLiveData<>();

    private final MutableLiveData<GenericResponse> resendOtpResponse =
            new MutableLiveData<>();

    public LiveData<Boolean> getLoading() {
        return loading;
    }

    public LiveData<String> getErrorMessage() {
        return errorMessage;
    }

    public LiveData<OtpVerifyResponse> getVerifyOtpResponse() {
        return verifyOtpResponse;
    }

    public LiveData<GenericResponse> getResendOtpResponse() {
        return resendOtpResponse;
    }

    public void verifyOtp(String phone, String otp) {

        loading.setValue(true);

        repository.verifyOtp(phone, otp,
                new RepositoryCallback<OtpVerifyResponse>() {

                    @Override
                    public void onSuccess(OtpVerifyResponse response) {

                        loading.setValue(false);
                        verifyOtpResponse.setValue(response);

                    }

                    @Override
                    public void onError(String message) {

                        loading.setValue(false);
                        errorMessage.setValue(message);

                    }
                });
    }

    public void requestOtp(String phone) {

        loading.setValue(true);

        repository.requestOtp(phone,
                new RepositoryCallback<GenericResponse>() {

                    @Override
                    public void onSuccess(GenericResponse response) {

                        loading.setValue(false);
                        resendOtpResponse.setValue(response);

                    }

                    @Override
                    public void onError(String message) {

                        loading.setValue(false);
                        errorMessage.setValue(message);

                    }
                });
    }

}