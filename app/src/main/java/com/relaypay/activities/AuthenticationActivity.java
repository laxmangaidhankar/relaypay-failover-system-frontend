package com.relaypay.activities;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.relaypay.R;
import com.relaypay.fragments.LoginFragment;
import com.relaypay.fragments.MpinLoginFragment;
import com.relaypay.fragments.MpinRegisterFragment;
import com.relaypay.storage.SessionManager;


public class AuthenticationActivity extends AppCompatActivity {

    public static final String ARG_MOBILE = "mobile";
    public static final String ARG_VERIFICATION_TOKEN = "verificationToken";

    public static final String EXTRA_START_DEST = "start_dest";
    public static final String EXTRA_NEEDS_REFRESH = "needs_refresh";

    public static final String DEST_MPIN_LOGIN = "mpin_login";
    public static final String DEST_MOBILE_ENTRY = "mobile_entry";

    private boolean needsSilentRefresh = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_authentication);

        if (savedInstanceState == null) {
            routeFromSplashIntent();
        }
    }

    private void routeFromSplashIntent() {
        Intent intent = getIntent();
        String startDest = intent.getStringExtra(EXTRA_START_DEST);
        needsSilentRefresh = intent.getBooleanExtra(EXTRA_NEEDS_REFRESH, false);

        if (DEST_MPIN_LOGIN.equals(startDest)) {
            SessionManager session = new SessionManager(getApplicationContext());
            String mobile = session.getMobileNumber();

            if (mobile == null) {
                session.clearSession();
                openLoginFragment();
            } else {
                openMpinFragment(mobile);
            }
        } else {
            openLoginFragment();
        }
    }

    public boolean isNeedsSilentRefresh() {
        return needsSilentRefresh;
    }
    public void openLoginFragment() {
        getSupportFragmentManager().popBackStack(
                null,
                getSupportFragmentManager().POP_BACK_STACK_INCLUSIVE
        );
        replaceFragment(new LoginFragment(), false);
    }

    public void openRegisterMpinFragment(String mobile, String verificationToken) {
        Bundle bundle = new Bundle();
        bundle.putString(ARG_MOBILE, mobile);
        bundle.putString(ARG_VERIFICATION_TOKEN, verificationToken);

        MpinRegisterFragment fragment = new MpinRegisterFragment();
        fragment.setArguments(bundle);
        replaceFragment(fragment, true);
    }

    public void openMpinFragment(String mobile) {
        Bundle bundle = new Bundle();
        bundle.putString(ARG_MOBILE, mobile);

        MpinLoginFragment fragment = new MpinLoginFragment();
        fragment.setArguments(bundle);
        replaceFragment(fragment, true);
    }

    public void onRegistrationComplete(String accessToken, String refreshToken,
                                       long expiryEpochMillis, String userId,
                                       String mobile) {
        SessionManager session = new SessionManager(getApplicationContext());
        session.saveSession(accessToken, refreshToken, expiryEpochMillis, userId, mobile);

        Intent intent = new Intent(this, com.relaypay.activities.HomeActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }

    private void replaceFragment(Fragment fragment, boolean addToBackStack) {
        if (addToBackStack) {
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.fragmentContainer, fragment)
                    .addToBackStack(null)
                    .commit();
        } else {
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.fragmentContainer, fragment)
                    .commit();
        }
    }
}