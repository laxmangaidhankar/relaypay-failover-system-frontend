package com.relaypay.ui.auth;

import android.os.Bundle;
import android.os.CountDownTimer;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.material.button.MaterialButton;
import com.relaypay.R;
import com.relaypay.ui.activities.AuthenticationActivity;
import com.relaypay.viewmodel.auth.VerifyOtpViewModel;

public class OtpFragment extends Fragment {

    private static final long RESEND_TIMEOUT_MS = 120_000L; // 02:00
    private static final long TICK_INTERVAL_MS = 1_000L;

    private EditText etOtp;
    private MaterialButton btnVerify;
    private TextView tvResendOtp;

    private String phone;
    private CountDownTimer resendTimer;
    private VerifyOtpViewModel viewModel;

    public OtpFragment() {
        super(R.layout.fragment_otp);
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        Bundle args = getArguments();
        phone = args != null ? args.getString("phone") : null;

        etOtp = view.findViewById(R.id.mobileOtp);
        btnVerify = view.findViewById(R.id.btnVerifyOtp);
        tvResendOtp = view.findViewById(R.id.tvResendOtp);

        // IMPORTANT
        viewModel = new ViewModelProvider(this).get(VerifyOtpViewModel.class);

        observeViewModel();

        btnVerify.setOnClickListener(v -> verifyOtp());
        tvResendOtp.setOnClickListener(v -> resendOtp());

        startResendTimer();
    }

    private String getOtp() {
        return etOtp.getText() != null ? etOtp.getText().toString().trim() : "";
    }

    private void verifyOtp() {

        String otp = getOtp();

        if (TextUtils.isEmpty(otp)) {
            Toast.makeText(requireContext(), "Enter OTP", Toast.LENGTH_SHORT).show();
            return;
        }

        if (otp.length() != 6) {
            Toast.makeText(requireContext(), "OTP must be 6 digits", Toast.LENGTH_SHORT).show();
            return;
        }

        if (TextUtils.isEmpty(phone)) {
            Toast.makeText(requireContext(), "Something went wrong", Toast.LENGTH_SHORT).show();
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
        viewModel.requestOtp(phone);
    }
    private void observeViewModel() {

        viewModel.getLoading().observe(getViewLifecycleOwner(),
                this::setVerifying);

        viewModel.getErrorMessage().observe(getViewLifecycleOwner(), error -> {

            if (error != null) {
                Toast.makeText(requireContext(),
                        error,
                        Toast.LENGTH_SHORT).show();
            }

        });

        viewModel.getVerifyOtpResponse().observe(getViewLifecycleOwner(), response -> {

            if (response == null)
                return;

            ((AuthenticationActivity) requireActivity())
                    .openRegisterMPINFragment(
                            phone,
                            response.getVerificationToken()
                    );

        });

        viewModel.getResendOtpResponse().observe(getViewLifecycleOwner(), response -> {

            if (response == null)
                return;

            Toast.makeText(requireContext(),
                    "OTP resent",
                    Toast.LENGTH_SHORT).show();

            startResendTimer();

        });

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
                long minutes = totalSeconds / 60;
                long seconds = totalSeconds % 60;
                tvResendOtp.setText(String.format("Resend OTP in %02d:%02d", minutes, seconds));
            }

            @Override
            public void onFinish() {
                tvResendOtp.setText("Resend OTP");
                tvResendOtp.setEnabled(true);
                tvResendOtp.setClickable(true);
            }
        }.start();
    }

    private void setVerifying(boolean verifying) {
        btnVerify.setEnabled(!verifying);
        btnVerify.setText(verifying ? "Verifying..." : "Verify");
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