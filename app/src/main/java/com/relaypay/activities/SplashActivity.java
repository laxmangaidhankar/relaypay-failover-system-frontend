package com.relaypay.activities;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;

import androidx.appcompat.app.AppCompatActivity;

import com.relaypay.R;
import com.relaypay.storage.SessionManager;


public class SplashActivity extends AppCompatActivity {

    private static final long MIN_SPLASH_DURATION_MS = 2000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        long startTime = System.currentTimeMillis();
        resolveStartDestination(startTime);
    }

    private void resolveStartDestination(long startTime) {
        SessionManager session = new SessionManager(getApplicationContext());

        long elapsed = System.currentTimeMillis() - startTime;
        long delay = Math.max(0, MIN_SPLASH_DURATION_MS - elapsed);

        new Handler(Looper.getMainLooper()).postDelayed(() -> {

            Intent intent = new Intent(SplashActivity.this, AuthenticationActivity.class);

            if (session.isLoggedIn()) {
                intent.putExtra(
                        AuthenticationActivity.EXTRA_START_DEST,
                        AuthenticationActivity.DEST_MPIN_LOGIN
                );
            } else {
                session.clearSession();
                intent.putExtra(
                        AuthenticationActivity.EXTRA_START_DEST,
                        AuthenticationActivity.DEST_MOBILE_ENTRY
                );
            }

            startActivity(intent);
            overridePendingTransition(
                    android.R.anim.fade_in,
                    android.R.anim.fade_out
            );
            finish();

        }, delay);
    }
}