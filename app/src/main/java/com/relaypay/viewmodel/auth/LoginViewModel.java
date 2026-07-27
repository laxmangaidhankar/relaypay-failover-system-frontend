package com.relaypay.viewmodel.auth;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.relaypay.model.auth.response.CheckMobileResponse;
import com.relaypay.model.auth.response.GenericResponse;
import com.relaypay.repository.AuthRepository;
import com.relaypay.repository.RepositoryCallback;

public class LoginViewModel extends ViewModel {

    private final AuthRepository repository = new AuthRepository();

    private final MutableLiveData<Boolean> loading = new MutableLiveData<>(false);
    private final MutableLiveData<String> errorMessage = new MutableLiveData<>();

    private final MutableLiveData<CheckMobileResponse> checkMobileResponse =
            new MutableLiveData<>();

    private final MutableLiveData<GenericResponse> otpResponse =
            new MutableLiveData<>();

    public LiveData<Boolean> getLoading() {
        return loading;
    }

    public LiveData<String> getErrorMessage() {
        return errorMessage;
    }

    public LiveData<CheckMobileResponse> getCheckMobileResponse() {
        return checkMobileResponse;
    }

    public LiveData<GenericResponse> getOtpResponse() {
        return otpResponse;
    }


    public void checkMobile(String phone) {
        loading.setValue(true);
        repository.checkMobile(phone, new RepositoryCallback<CheckMobileResponse>() {
            @Override
            public void onSuccess(CheckMobileResponse response) {
                loading.setValue(false);
                checkMobileResponse.setValue(response);
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

        repository.requestOtp(phone, new RepositoryCallback<GenericResponse>() {

            @Override
            public void onSuccess(GenericResponse response) {

                loading.setValue(false);
                otpResponse.setValue(response);

            }
            @Override
            public void onError(String message) {

                loading.setValue(false);
                errorMessage.setValue(message);

            }
        });
    }
    public void clearCheckMobileResponse() {
        checkMobileResponse.setValue(null);
    }
    public void clearOtpResponse() {
        otpResponse.setValue(null);
    }

}