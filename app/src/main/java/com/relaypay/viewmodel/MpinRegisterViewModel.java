package com.relaypay.viewmodel;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.relaypay.auth.model.response.GenericResponse;
import com.relaypay.auth.model.response.SetMpinResponse;
import com.relaypay.repository.AuthRepository;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MpinRegisterViewModel extends ViewModel {

    private final AuthRepository repository = new AuthRepository();

    private final MutableLiveData<Boolean> loading = new MutableLiveData<>(false);
    private final MutableLiveData<String> errorMessage = new MutableLiveData<>();

    private final MutableLiveData<SetMpinResponse> mpinSet = new MutableLiveData<>();

    public LiveData<Boolean> getLoading() { return loading; }
    public LiveData<String> getErrorMessage() { return errorMessage; }
    public LiveData<SetMpinResponse> getMpinSet() { return mpinSet; }

    public void setMpin(String phone, String verificationToken, String loginPin) {
        if (loginPin == null || !loginPin.matches("\\d{4}")) {
            errorMessage.setValue("MPIN must be 4 digits");
            return;
        }

        loading.setValue(true);

        repository.setMpin(phone, verificationToken, loginPin,
                new Callback<SetMpinResponse>() {

                    @Override
                    public void onResponse(@NonNull Call<SetMpinResponse> call,
                                           @NonNull Response<SetMpinResponse> response) {

                        loading.setValue(false);

                        if (response.isSuccessful() && response.body() != null) {
                            SetMpinResponse body = response.body();

                            if (body.getAccessToken() == null || body.getRefreshToken() == null) {
                                errorMessage.setValue("Setup incomplete, please try again");
                                return;
                            }

                            mpinSet.setValue(body);

                        } else {
                            errorMessage.setValue("Failed to set MPIN");
                        }
                    }

                    @Override
                    public void onFailure(@NonNull Call<SetMpinResponse> call,
                                          @NonNull Throwable t) {

                        loading.setValue(false);
                        errorMessage.setValue("Network error, check your connection");
                    }
                });
    }
}