package com.relaypay.viewmodel.auth;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.relaypay.model.auth.response.SetMpinResponse;
import com.relaypay.repository.AuthRepository;
import com.relaypay.repository.RepositoryCallback;

public class MpinRegisterViewModel extends ViewModel {

    private final AuthRepository repository = new AuthRepository();

    private final MutableLiveData<Boolean> loading = new MutableLiveData<>(false);
    private final MutableLiveData<String> errorMessage = new MutableLiveData<>();

    private final MutableLiveData<SetMpinResponse> mpinSet = new MutableLiveData<>();

    public LiveData<Boolean> getLoading() { return loading; }
    public LiveData<String> getErrorMessage() { return errorMessage; }
    public LiveData<SetMpinResponse> getMpinSet() { return mpinSet; }

    public void setMpin(String phone,
                        String verificationToken,
                        String loginPin) {

        if (loginPin == null || !loginPin.matches("\\d{4}")) {
            errorMessage.setValue("MPIN must be exactly 4 digits");
            return;
        }

        loading.setValue(true);

        repository.setMpin(phone, verificationToken, loginPin,
                new RepositoryCallback<SetMpinResponse>() {

                    @Override
                    public void onSuccess(SetMpinResponse response) {
                        loading.setValue(false);
                        mpinSet.setValue(response);
                    }

                    @Override
                    public void onError(String message) {
                        loading.setValue(false);
                        errorMessage.setValue(message);
                    }
                });
    }
}