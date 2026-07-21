package com.relaypay.storage;

import android.content.Context;
import android.content.SharedPreferences;

import androidx.security.crypto.EncryptedSharedPreferences;
import androidx.security.crypto.MasterKey;

import java.io.IOException;
import java.security.GeneralSecurityException;

public class SessionManager {
    private static final String PREF_NAME = "relaypay_secure_session";
    private static final String KEY_ACCESS_TOKEN = "access_token";
    private static final String KEY_REFRESH_TOKEN = "refresh_token";
    private static final String KEY_TOKEN_EXPIRY = "token_expiry";
    private static final String KEY_USER_ID = "user_id";
    private static final String KEY_MOBILE_NUMBER = "mobile_number";


    private final SharedPreferences prefs;

    public SessionManager(Context context) {
        try {
            MasterKey masterKey = new MasterKey.Builder(context)
                    .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
                    .build();

            prefs = EncryptedSharedPreferences.create(
                    context,
                    PREF_NAME,
                    masterKey,
                    EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
                    EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
            );
        } catch (GeneralSecurityException | IOException e) {
            throw new RuntimeException("Failed to initialize secure session storage", e);
        }
    }

    public void saveSession(String accessToken, String refreshToken, long expiryEpochMillis,
                            String userId, String mobileNumber) {
        prefs.edit()
                .putString(KEY_ACCESS_TOKEN, accessToken)
                .putString(KEY_REFRESH_TOKEN, refreshToken)
                .putLong(KEY_TOKEN_EXPIRY, expiryEpochMillis)
                .putString(KEY_USER_ID, userId)
                .putString(KEY_MOBILE_NUMBER, mobileNumber)
                .apply();
    }

    public String getMobileNumber() {
        return prefs.getString(KEY_MOBILE_NUMBER, null);
    }

    public String getAccessToken() {
        return prefs.getString(KEY_ACCESS_TOKEN, null);
    }

    public String getRefreshToken() {
        return prefs.getString(KEY_REFRESH_TOKEN, null);
    }

    public String getUserId() {
        return prefs.getString(KEY_USER_ID, null);
    }

    public boolean hasValidAccessToken() {
        String token = prefs.getString(KEY_ACCESS_TOKEN, null);
        long expiry = prefs.getLong(KEY_TOKEN_EXPIRY, 0L);
        return token != null && System.currentTimeMillis() < expiry;
    }

    public boolean hasRefreshToken() {
        return prefs.getString(KEY_REFRESH_TOKEN, null) != null;
    }

    public boolean isLoggedIn() {
        return hasValidAccessToken() || hasRefreshToken();
    }

    public void clearSession() {
        prefs.edit().clear().apply();
    }
}