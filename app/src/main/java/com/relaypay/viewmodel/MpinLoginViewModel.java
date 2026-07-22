package com.relaypay.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.relaypay.auth.model.request.LoginRequest;
import com.relaypay.auth.model.response.LoginResponse;
import com.relaypay.repository.AuthRepository;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MpinLoginViewModel extends ViewModel {

    private final AuthRepository repository = new AuthRepository();

    private final MutableLiveData<LoginResponse> loginResponse = new MutableLiveData<>();
    private final MutableLiveData<String> error = new MutableLiveData<>();
    private final MutableLiveData<Boolean> loading = new MutableLiveData<>();

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

        repository.loginMpin(phone, pin, new Callback<LoginResponse>() {

            @Override
            public void onResponse(Call<LoginResponse> call,
                                   Response<LoginResponse> response) {

                loading.postValue(false);

                if (response.isSuccessful() && response.body() != null) {
                    loginResponse.postValue(response.body());
                } else {
                    error.postValue("Invalid MPIN");
                }
            }

            @Override
            public void onFailure(Call<LoginResponse> call, Throwable t) {

                loading.postValue(false);
                error.postValue(t.getMessage());
            }
        });
    }
}