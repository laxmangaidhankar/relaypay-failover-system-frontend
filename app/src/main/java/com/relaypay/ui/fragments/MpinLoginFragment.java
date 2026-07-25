package com.relaypay.fragments;


import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.relaypay.activities.AuthenticationActivity;
import com.relaypay.activities.HomeActivity;
import com.relaypay.R;
import com.relaypay.auth.model.request.LoginRequest;
import com.relaypay.viewmodel.MpinLoginViewModel;

public class MpinLoginFragment extends Fragment {

    private EditText etMPIN;
    private Button btnMpinLogin;

    private String mobile;
    private MpinLoginViewModel viewModel;

    public MpinLoginFragment() {
        super(R.layout.fragment_mpin_login);
    }

    @SuppressLint("WrongViewCast")
    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        etMPIN = view.findViewById(R.id.etMPIN);
        btnMpinLogin = view.findViewById(R.id.btnMpinLogin);

        viewModel = new ViewModelProvider(this).get(MpinLoginViewModel.class);

        if (getArguments() != null) {
            mobile = getArguments().getString(AuthenticationActivity.ARG_MOBILE);
        }

        btnMpinLogin.setOnClickListener(v -> login());

        observeViewModel();
    }

    private String getPin() {
        return etMPIN.getText().toString().trim();
    }

    private void login() {

        String pin = getPin();

        if (TextUtils.isEmpty(pin) || pin.length() != 4) {
            Toast.makeText(requireContext(),
                    "Enter a valid 4-digit MPIN",
                    Toast.LENGTH_SHORT).show();
            return;
        }

        viewModel.login(mobile, pin);

    }

    private void observeViewModel() {

        viewModel.getLoginResponse().observe(getViewLifecycleOwner(), response -> {

            if (response.isSuccess()) {

                // TODO: Save access token
                // sessionManager.saveAccessToken(response.getAccessToken());

                Intent intent = new Intent(requireActivity(), HomeActivity.class);
                startActivity(intent);
                requireActivity().finish();

            } else {

                Toast.makeText(requireContext(),
                        response.getMessage(),
                        Toast.LENGTH_SHORT).show();
            }
        });

        viewModel.getError().observe(getViewLifecycleOwner(), message ->
                Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
        );
    }

}