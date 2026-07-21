package com.relaypay.fragments;

import android.os.Bundle;
import android.os.CountDownTimer;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;

import com.google.android.material.button.MaterialButton;
import com.relaypay.R;
import com.relaypay.viewmodel.VerifyOtpViewModel;

public class VerifyOtpFragment extends Fragment {

    private static final long RESEND_TIMEOUT_MS = 120_000L;
    private static final long TICK_INTERVAL_MS = 1_000L;

    private VerifyOtpViewModel viewModel;
    private String phone;

    private EditText etOtp;
    private MaterialButton btnVerify;
    private TextView tvResendOtp;

    private CountDownTimer resendTimer;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_otp, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        viewModel = new ViewModelProvider(this).get(VerifyOtpViewModel.class);

        phone = getArguments() != null ? getArguments().getString("phone") : null;

        etOtp = view.findViewById(R.id.mobileOtp);
        btnVerify = view.findViewById(R.id.btnVerifyOtp);
        tvResendOtp = view.findViewById(R.id.tvResendOtp);

        btnVerify.setOnClickListener(v -> attemptVerify());
        tvResendOtp.setOnClickListener(v -> resendOtp());

        startResendTimer();
        observeViewModel();
    }

    private void attemptVerify() {
        String otp = etOtp.getText() != null ? etOtp.getText().toString().trim() : "";

        if (TextUtils.isEmpty(phone)) {
            Toast.makeText(requireContext(), "Something went wrong, restart login", Toast.LENGTH_SHORT).show();
            return;
        }

        if (TextUtils.isEmpty(otp)) {
            Toast.makeText(requireContext(), "Enter OTP", Toast.LENGTH_SHORT).show();
            return;
        }

        viewModel.verifyOtp(phone, otp);
    }

    private void resendOtp() {
        if (TextUtils.isEmpty(phone)) {
            Toast.makeText(requireContext(), "Something went wrong, restart login", Toast.LENGTH_SHORT).show();
            return;
        }

        tvResendOtp.setEnabled(false);
        startResendTimer();
    }

    private void startResendTimer() {
        tvResendOtp.setEnabled(false);
        tvResendOtp.setClickable(false);

        if (resendTimer != null) {
            resendTimer.cancel();
        }

        resendTimer = new CountDownTimer(RESEND_TIMEOUT_MS, TICK_INTERVAL_MS) {
            @Override
            public void onTick(long millisUntilFinished) {
                long totalSeconds = millisUntilFinished / 1000;
                tvResendOtp.setText(String.format("Resend OTP in %02d:%02d",
                        totalSeconds / 60, totalSeconds % 60));
            }

            @Override
            public void onFinish() {
                tvResendOtp.setText("Resend OTP");
                tvResendOtp.setEnabled(true);
                tvResendOtp.setClickable(true);
            }
        }.start();
    }

    private void observeViewModel() {
        viewModel.getLoading().observe(getViewLifecycleOwner(), isLoading -> {
            btnVerify.setEnabled(!isLoading);
            btnVerify.setText(isLoading ? "Verifying..." : "Verify");
        });

        viewModel.getErrorMessage().observe(getViewLifecycleOwner(), errorMsg -> {
            if (errorMsg != null && !errorMsg.isEmpty()) {
                Toast.makeText(requireContext(), errorMsg, Toast.LENGTH_SHORT).show();
            }
        });

        viewModel.getVerified().observe(getViewLifecycleOwner(), response -> {
            if (response == null) return;

            Bundle args = new Bundle();
            args.putString("phone", phone);

            Navigation.findNavController(requireView())
                    .navigate(R.id.action_verifyOtp_to_mpinRegister, args);
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (resendTimer != null) {
            resendTimer.cancel();
            resendTimer = null;
        }
    }
}