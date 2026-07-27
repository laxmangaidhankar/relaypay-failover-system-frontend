package com.relaypay.ui.auth;


import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.OptIn;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.media3.common.util.Log;
import androidx.media3.common.util.UnstableApi;

import com.relaypay.storage.SessionManager;
import com.relaypay.ui.activities.AuthenticationActivity;
import com.relaypay.ui.activities.HomeActivity;
import com.relaypay.R;
import com.relaypay.viewmodel.auth.MpinLoginViewModel;

public class MpinLoginFragment extends Fragment {

    private EditText etMPIN;
    private Button btnMpinLogin;

    private String mobile;
    private MpinLoginViewModel viewModel;
    private SessionManager sessionManager;

    public MpinLoginFragment() {
        super(R.layout.fragment_mpin_login);
    }

    @SuppressLint("WrongViewCast")
    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        etMPIN = view.findViewById(R.id.etMPIN);
        btnMpinLogin = view.findViewById(R.id.btnMpinLogin);
        sessionManager = new SessionManager(requireContext());
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

    @OptIn(markerClass = UnstableApi.class)
    private void login() {
        Log.d("LOGIN", "Button clicked");

        String pin = getPin();

        Log.d("LOGIN", "Phone = " + mobile);
        Log.d("LOGIN", "PIN = " + pin);

        if (TextUtils.isEmpty(pin) || pin.length() != 4) {
            Toast.makeText(requireContext(),
                    "Enter a valid 4-digit MPIN",
                    Toast.LENGTH_SHORT).show();
            return;
        }

        viewModel.login(mobile, pin);

    }

    @OptIn(markerClass = UnstableApi.class)
    private void observeViewModel() {

        viewModel.getLoginResponse().observe(getViewLifecycleOwner(), response -> {

            if (response == null) return;

            if (response.isSuccess()) {

                sessionManager.saveSession(
                        response.getAccessToken(),
                        response.getRefreshToken(),
                        response.getExpiresIn(),
                        response.getUser().getId(),
                        response.getUser().getPhone()
                );
                Log.d("SESSION", "Access Token: " + sessionManager.getAccessToken());
                Log.d("SESSION", "Refresh Token: " + sessionManager.getRefreshToken());
                Log.d("SESSION", "Phone: " + sessionManager.getMobileNumber());
                Log.d("SESSION", "Logged In: " + sessionManager.isLoggedIn());



                Intent intent = new Intent(requireActivity(), HomeActivity.class);
                startActivity(intent);
                requireActivity().finish();

            } else {

                Toast.makeText(
                        requireContext(),
                        response.getMessage(),
                        Toast.LENGTH_SHORT
                ).show();
            }
        });
    }

}