package com.relaypay.ui.auth;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.material.button.MaterialButton;
import com.relaypay.R;

import com.relaypay.ui.activities.AuthenticationActivity;
import com.relaypay.viewmodel.auth.LoginViewModel;

import java.util.regex.Pattern;


public class LoginFragment extends Fragment {

    private static final Pattern PHONE_PATTERN = Pattern.compile("^[6-9]\\d{9}$");
    private static final String BTN_IDLE_TEXT = "Proceed";
    private static final String BTN_LOADING_TEXT = "Sending...";

    private LoginViewModel viewModel;

    private EditText etPhone;
    private MaterialButton btnContinue;

    public LoginFragment() {
        super(R.layout.fragment_login);
    }


    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        etPhone = view.findViewById(R.id.mobileNumberInput);
        btnContinue = view.findViewById(R.id.btnContinue);

        viewModel = new ViewModelProvider(this).get(LoginViewModel.class);

        observeViewModel();

        etPhone.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (etPhone.getError() != null) {
                    etPhone.setError(null);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        btnContinue.setOnClickListener(v -> {

            String phone = etPhone.getText().toString().trim();
            if (!isValidPhone(phone)) {
                etPhone.requestFocus();
                return;
            }

            Log.d("LOGIN", "Checking mobile: " + phone);

                viewModel.checkMobile(phone);
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

    private void observeViewModel() {

        viewModel.getLoading().observe(getViewLifecycleOwner(), this::setLoading);

        viewModel.getErrorMessage().observe(getViewLifecycleOwner(), error -> {
            if (error != null) {
                Toast.makeText(requireContext(), error, Toast.LENGTH_SHORT).show();
            }
        });

        viewModel.getCheckMobileResponse().observe(getViewLifecycleOwner(), response -> {

            if (response == null) return;

            if (response.isSuccess()) {

                String phone = etPhone.getText().toString().trim();

                if (response.isUserExists()) {

                    navigateToEnterMPIN(phone);

                } else {

                    viewModel.requestOtp(phone);
                }

                viewModel.clearCheckMobileResponse();
            }
        });

        viewModel.getOtpResponse().observe(getViewLifecycleOwner(), response -> {
            Log.d("OTP", "Observer called");

            if (response == null) return;

            if (response.isSuccess()) {

                String phone = etPhone.getText().toString().trim();
                navigateToOtp(phone);
                viewModel.clearOtpResponse();
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

    private void navigateToEnterMPIN(String phone) {

        Bundle bundle = new Bundle();
        bundle.putString(AuthenticationActivity.ARG_MOBILE, phone);

        MpinLoginFragment fragment = new MpinLoginFragment();
        fragment.setArguments(bundle);

        getParentFragmentManager()
                .beginTransaction()
                .replace(R.id.fragmentContainer, fragment)
                .addToBackStack(null)
                .commit();
    }
}