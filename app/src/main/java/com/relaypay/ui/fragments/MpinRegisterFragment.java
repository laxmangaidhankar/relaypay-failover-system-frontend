package com.relaypay.fragments;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.material.button.MaterialButton;
import com.relaypay.R;
import com.relaypay.activities.AuthenticationActivity;
import com.relaypay.viewmodel.MpinRegisterViewModel;

public class MpinRegisterFragment extends Fragment {

    private static final int MPIN_LENGTH = 4;

    private MpinRegisterViewModel viewModel;

    private String mobile;
    private String verificationToken;

    private EditText etMpin;
    private MaterialButton btnSetMpin;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_mpin_registration, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        viewModel = new ViewModelProvider(this).get(MpinRegisterViewModel.class);

        Bundle args = getArguments();
        mobile = args != null ? args.getString(AuthenticationActivity.ARG_MOBILE) : null;
        verificationToken = args != null ? args.getString(AuthenticationActivity.ARG_VERIFICATION_TOKEN) : null;

        etMpin = view.findViewById(R.id.etMPIN);
        btnSetMpin = view.findViewById(R.id.btnSetMPIN);

        btnSetMpin.setOnClickListener(v -> attemptSetMpin());

        observeViewModel();
    }


    private void attemptSetMpin() {
        String mpin = etMpin.getText() != null ? etMpin.getText().toString().trim() : "";

        Toast.makeText(requireContext(),
                "Mobile=" + mobile + "\nToken=" + verificationToken,
                Toast.LENGTH_LONG).show();

        if (TextUtils.isEmpty(mpin)) {
            Toast.makeText(requireContext(), "Enter an MPIN", Toast.LENGTH_SHORT).show();
            return;
        }

        if (mpin.length() != MPIN_LENGTH || !mpin.matches("\\d{4}")) {
            Toast.makeText(requireContext(), "MPIN must be 4 digits", Toast.LENGTH_SHORT).show();
            return;
        }

        if (TextUtils.isEmpty(mobile) || TextUtils.isEmpty(verificationToken)) {
            Toast.makeText(requireContext(), "Session expired, restart login", Toast.LENGTH_SHORT).show();
            return;
        }

        viewModel.setMpin(mobile, verificationToken, mpin);
    }

    private void observeViewModel() {
        viewModel.getLoading().observe(getViewLifecycleOwner(), isLoading -> {
            btnSetMpin.setEnabled(!isLoading);
            btnSetMpin.setText(isLoading ? "Setting up..." : "Set MPIN");
        });

        viewModel.getErrorMessage().observe(getViewLifecycleOwner(), errorMsg -> {
            if (errorMsg != null && !errorMsg.isEmpty()) {
                Toast.makeText(requireContext(), errorMsg, Toast.LENGTH_SHORT).show();
            }
        });

        viewModel.getMpinSet().observe(getViewLifecycleOwner(), response -> {
            if (response != null) {
                long expiryEpochMillis = System.currentTimeMillis() + (response.getExpiresIn() * 1000L);

                ((AuthenticationActivity) requireActivity()).onRegistrationComplete(
                        response.getAccessToken(),
                        response.getRefreshToken(),
                        expiryEpochMillis,
                        response.getUserId(),
                        mobile
                );
            }
        });
    }
}