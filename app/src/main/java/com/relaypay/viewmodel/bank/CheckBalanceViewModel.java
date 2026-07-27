package com.relaypay.viewmodel.bank;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.relaypay.model.bank.response.CheckBalanceResponse;
import com.relaypay.repository.BankRepository;
import com.relaypay.repository.RepositoryCallback;

public class CheckBalanceViewModel extends ViewModel {

    private final BankRepository repository = new BankRepository();

    private final MutableLiveData<Boolean> loading = new MutableLiveData<>(false);
    private final MutableLiveData<String> errorMessage = new MutableLiveData<>();
    private final MutableLiveData<CheckBalanceResponse> checkBalanceResponse =
            new MutableLiveData<>();

    public LiveData<Boolean> getLoading() {
        return loading;
    }

    public LiveData<String> getErrorMessage() {
        return errorMessage;
    }

    public LiveData<CheckBalanceResponse> getCheckBalanceResponse() {
        return checkBalanceResponse;
    }

    public void checkBalance(String accountId, String tpin) {

        loading.setValue(true);

        repository.checkBalance(
                accountId,
                tpin,
                new RepositoryCallback<CheckBalanceResponse>() {

                    @Override
                    public void onSuccess(CheckBalanceResponse response) {
                        loading.setValue(false);
                        checkBalanceResponse.setValue(response);
                    }

                    @Override
                    public void onError(String message) {
                        loading.setValue(false);
                        errorMessage.setValue(message);
                    }
                }
        );
    }
}