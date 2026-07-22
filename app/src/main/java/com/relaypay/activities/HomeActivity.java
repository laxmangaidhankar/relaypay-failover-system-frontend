package com.relaypay.activities;

import android.os.Bundle;
import android.widget.LinearLayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.relaypay.FamilyFragment;
import com.relaypay.HistoryFragment;
import com.relaypay.fragments.HomeFragment;
import com.relaypay.ProfileFragment;
import com.relaypay.R;

public class HomeActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        LinearLayout navHome = findViewById(R.id.navHome);
        LinearLayout navHistory = findViewById(R.id.navBills);
        LinearLayout navFamily = findViewById(R.id.navFamily);
        LinearLayout navAccount = findViewById(R.id.navAccount);

        if (savedInstanceState == null) {
            loadFragment(new HomeFragment());
        }

        navHome.setOnClickListener(v ->
                loadFragment(new HomeFragment()));

        navHistory.setOnClickListener(v ->
                loadFragment(new HistoryFragment()));

        navFamily.setOnClickListener(v ->
                loadFragment(new FamilyFragment()));

        navAccount.setOnClickListener(v ->
                loadFragment(new ProfileFragment()));
    }

    private void loadFragment(Fragment fragment) {

        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.frameLayout, fragment)
                .commit();

    }
}