package com.relaypay.fragments;

import android.os.Bundle;
import android.os.CountDownTimer;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.android.material.button.MaterialButton;
import com.relaypay.R;
import com.relaypay.activities.AuthenticationActivity;
import com.relaypay.auth.model.response.GenericResponse;
import com.relaypay.auth.model.request.OtpVerifyRequest;
import com.relaypay.auth.model.request.PhoneRequest;
import com.relaypay.auth.model.response.OtpVerifyResponse;
import com.relaypay.network.ApiClient;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class OtpFragment extends Fragment {

    private static final long RESEND_TIMEOUT_MS = 120_000L; // 02:00
    private static final long TICK_INTERVAL_MS = 1_000L;

    private EditText etOtp;
    private MaterialButton btnVerify;
    private TextView tvResendOtp;

    private String phone;
    private CountDownTimer resendTimer;

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
            Toast.makeText(requireContext(), "Something went wrong, restart login", Toast.LENGTH_SHORT).show();
            return;
        }

        setVerifying(true);

        ApiClient.getAuthApi()
                .verifyOtp(new OtpVerifyRequest(phone, otp))
                .enqueue(new Callback<OtpVerifyResponse>() {
                    @Override
                    public void onResponse(@NonNull Call<OtpVerifyResponse> call,
                                           @NonNull Response<OtpVerifyResponse> response) {

                        if (!isAdded()) return;

                        setVerifying(false);

                        if (response.isSuccessful() && response.body() != null
                                && response.body().isSuccess()) {

                            ((AuthenticationActivity) requireActivity())
                                    .openRegisterMpinFragment(
                                            phone,
                                            response.body().getVerificationToken()
                                    );

                        } else {

                            String error = (response.body() != null)
                                    ? response.body().getError()
                                    : "Invalid OTP, try again";

                            Toast.makeText(requireContext(), error, Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(@NonNull Call<OtpVerifyResponse> call,
                                          @NonNull Throwable t) {

                        if (!isAdded()) return;

                        setVerifying(false);
                        Toast.makeText(requireContext(),
                                "Network error: " + t.getMessage(),
                                Toast.LENGTH_SHORT).show();
                    }
                });
    }


    private void resendOtp() {
        if (TextUtils.isEmpty(phone)) {
            Toast.makeText(requireContext(), "Something went wrong, restart login", Toast.LENGTH_SHORT).show();
            return;
        }

        tvResendOtp.setEnabled(false);

        ApiClient.getAuthApi()
                .requestOtp(new PhoneRequest(phone))
                .enqueue(new Callback<GenericResponse>() {
                    @Override
                    public void onResponse(@NonNull Call<GenericResponse> call,
                                           @NonNull Response<GenericResponse> response) {
                        if (!isAdded()) return;

                        if (response.isSuccessful() && response.body() != null
                                && response.body().isSuccess()) {
                            Toast.makeText(requireContext(), "OTP resent", Toast.LENGTH_SHORT).show();
                            startResendTimer();
                        } else {
                            tvResendOtp.setEnabled(true);
                            Toast.makeText(requireContext(), "Failed to resend OTP", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(@NonNull Call<GenericResponse> call, @NonNull Throwable t) {
                        if (!isAdded()) return;

                        tvResendOtp.setEnabled(true);
                        Toast.makeText(requireContext(),
                                "Network error: " + t.getMessage(),
                                Toast.LENGTH_SHORT).show();
                    }
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