package com.relaypay.fragments;

import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.google.android.material.button.MaterialButton;
import com.relaypay.LinkedBankBottomSheet;
import com.relaypay.R;

public class HomeFragment extends Fragment {
    private MaterialButton btnLinkBank;

    public HomeFragment() {
        super(R.layout.fragment_home);
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        initializeViews(view);
        loadUserData();
        setClickListeners();
    }
    private void initializeViews(View view) {
        btnLinkBank = view.findViewById(R.id.btnLinkBank);
    }

    private void loadUserData() {
        // TODO: fetch linked banks / balance for this screen
    }

    private void setClickListeners() {
        btnLinkBank.setOnClickListener(v -> {
            LinkedBankBottomSheet.newInstance((bankId, isPrimary) -> {
                // Called when bank linking succeeds — bankId and isPrimary
                // come from LinkBankBottomSheet.OnBankLinkedListener,
                // which is a two-arg interface, not a Runnable.
                requireActivity()
                        .getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.fragmentContainer, new BankLinkedSuccessFragment())
                        .addToBackStack(null)
                        .commit();
            }).show(getParentFragmentManager(), "LinkBank");
        });
    }
}