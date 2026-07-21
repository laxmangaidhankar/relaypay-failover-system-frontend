package com.relaypay.fragments;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.google.android.material.button.MaterialButton;
import com.relaypay.R;
import com.relaypay.auth.model.response.CheckMobileResponse;
import com.relaypay.auth.model.response.GenericResponse;
import com.relaypay.auth.model.request.PhoneRequest;
import com.relaypay.network.ApiClient;

import java.util.regex.Pattern;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginFragment extends Fragment {

    private static final Pattern PHONE_PATTERN = Pattern.compile("^[6-9]\\d{9}$");
    private static final String BTN_IDLE_TEXT = "Proceed";
    private static final String BTN_LOADING_TEXT = "Sending...";

    private EditText etPhone;
    private MaterialButton btnContinue;

    public LoginFragment() {
        super(R.layout.fragment_login);
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        etPhone = view.findViewById(R.id.mobileNumberInput);
        btnContinue = view.findViewById(R.id.btnSendOtp);

        etPhone.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (etPhone.getError() != null) {
                    etPhone.setError(null);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        btnContinue.setOnClickListener(v -> {
            String phone = etPhone.getText() != null
                    ? etPhone.getText().toString().trim()
                    : "";

            if (!isValidPhone(phone)) {
                etPhone.requestFocus();
                return;
            }

            checkMobile(phone);
        });
    }

    private boolean isValidPhone(String phone) {
        if (phone.isEmpty()) {
            etPhone.setError("Mobile number is required");
            return false;
        }

        if (phone.length() != 10) {
            etPhone.setError("Enter a valid 10-digit mobile number");
            return false;
        }

        if (!PHONE_PATTERN.matcher(phone).matches()) {
            etPhone.setError("Enter a valid Indian mobile number");
            return false;
        }

        return true;
    }

    private void checkMobile(String phone) {

        setLoading(true);

        ApiClient.getAuthApi()
                .checkMobile(new PhoneRequest(phone))
                .enqueue(new Callback<CheckMobileResponse>() {

                    @Override
                    public void onResponse(@NonNull Call<CheckMobileResponse> call,
                                           @NonNull Response<CheckMobileResponse> response) {

                        if (!isAdded()) return;

                        setLoading(false);

                        if (response.isSuccessful()
                                && response.body() != null
                                && response.body().isSuccess()) {

                            if (response.body().isUserExists()) {

                                navigateToEnterMpin(phone);

                            } else {

                                requestOtp(phone);

                            }

                        } else {

                            String error = response.body() != null
                                    ? response.body().getError()
                                    : "Something went wrong";

                            Toast.makeText(requireContext(),
                                    error,
                                    Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(@NonNull Call<CheckMobileResponse> call,
                                          @NonNull Throwable t) {

                        if (!isAdded()) return;

                        setLoading(false);

                        Toast.makeText(requireContext(),
                                "Network error: " + t.getMessage(),
                                Toast.LENGTH_SHORT).show();
                    }
                });
    }


    private void requestOtp(String phone) {

        setLoading(true);

        ApiClient.getAuthApi()
                .requestOtp(new PhoneRequest(phone))
                .enqueue(new Callback<GenericResponse>() {

                    @Override
                    public void onResponse(@NonNull Call<GenericResponse> call,
                                           @NonNull Response<GenericResponse> response) {

                        if (!isAdded()) return;

                        setLoading(false);

                        if (response.isSuccessful()
                                && response.body() != null
                                && response.body().isSuccess()) {

                            navigateToOtp(phone);

                        } else {

                            String error = response.body() != null
                                    ? response.body().getError()
                                    : "Failed to send OTP";

                            Toast.makeText(requireContext(),
                                    error,
                                    Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(@NonNull Call<GenericResponse> call,
                                          @NonNull Throwable t) {

                        if (!isAdded()) return;

                        setLoading(false);

                        Toast.makeText(requireContext(),
                                "Network error: " + t.getMessage(),
                                Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void setLoading(boolean loading) {
        btnContinue.setEnabled(!loading);
        btnContinue.setText(loading ? BTN_LOADING_TEXT : BTN_IDLE_TEXT);
    }

    private void navigateToOtp(String phone) {
        Bundle bundle = new Bundle();
        bundle.putString("phone", phone);

        OtpFragment otpFragment = new OtpFragment();
        otpFragment.setArguments(bundle);

        getParentFragmentManager()
                .beginTransaction()
                .replace(R.id.fragmentContainer, otpFragment)
                .addToBackStack(null)
                .commit();
    }

    private void navigateToEnterMpin(String phone) {

        Bundle bundle = new Bundle();
        bundle.putString("phone", phone);

        MpinLoginFragment fragment = new MpinLoginFragment();
        fragment.setArguments(bundle);

        getParentFragmentManager()
                .beginTransaction()
                .replace(R.id.fragmentContainer, fragment)
                .addToBackStack(null)
                .commit();
    }
}