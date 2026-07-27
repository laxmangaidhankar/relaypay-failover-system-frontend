package com.relaypay.repository;

import android.se.omapi.Session;

import com.relaypay.model.bank.request.CheckBalanceRequest;
import com.relaypay.model.bank.response.CheckBalanceResponse;
import com.relaypay.network.ApiClient;
import com.relaypay.network.bank.BankApi;
import com.relaypay.storage.SessionManager;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class BankRepository {

    private final BankApi bankApi;

    SessionManager sessionManager;
    public BankRepository() {
        bankApi = ApiClient.getBankApi();
    }

    public void checkBalance(String bankId,
                             String tpin,
                             RepositoryCallback<CheckBalanceResponse> callback) {

        String bearerToken = "Bearer " + sessionManager.getAccessToken();

        CheckBalanceRequest request = new CheckBalanceRequest(bankId, tpin);

        bankApi.checkBalance(bearerToken, bankId, request)
                .enqueue(new Callback<CheckBalanceResponse>() {

                    @Override
                    public void onResponse(Call<CheckBalanceResponse> call,
                                           Response<CheckBalanceResponse> response) {

                        if (response.isSuccessful() && response.body() != null) {
                            callback.onSuccess(response.body());
                        } else {
                            callback.onError("Unable to fetch balance.");
                        }
                    }

                    @Override
                    public void onFailure(Call<CheckBalanceResponse> call, Throwable t) {
                        callback.onError(t.getMessage());
                    }
                });
    }
}
