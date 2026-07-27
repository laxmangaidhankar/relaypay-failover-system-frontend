package com.relaypay.viewmodel.auth;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.relaypay.model.auth.response.LoginResponse;
import com.relaypay.repository.AuthRepository;
import com.relaypay.repository.RepositoryCallback;

public class MpinLoginViewModel extends ViewModel {

    private final AuthRepository repository = new AuthRepository();

    private final MutableLiveData<LoginResponse> loginResponse =
            new MutableLiveData<>();

    private final MutableLiveData<String> error =
            new MutableLiveData<>();

    private final MutableLiveData<Boolean> loading =
            new MutableLiveData<>(false);

    public LiveData<LoginResponse> getLoginResponse() {
        return loginResponse;
    }

    public LiveData<String> getError() {
        return error;
    }

    public LiveData<Boolean> isLoading() {
        return loading;
    }

    public void login(String phone, String pin) {

        loading.setValue(true);

        repository.loginMpin(phone, pin,
                new RepositoryCallback<LoginResponse>() {

                    @Override
                    public void onSuccess(LoginResponse response) {

                        loading.setValue(false);
                        loginResponse.setValue(response);

                    }

                    @Override
                    public void onError(String message) {

                        loading.setValue(false);
                        error.setValue(message);

                    }
                });
    }
}