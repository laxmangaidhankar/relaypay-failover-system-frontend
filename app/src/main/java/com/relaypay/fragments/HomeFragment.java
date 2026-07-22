package com.relaypay.fragments;


import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.google.android.material.button.MaterialButton;
import com.relaypay.R;

public class HomeFragment extends Fragment {

    // Header


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

    }

    private void loadUserData() {


    }

    private void setClickListeners() {




    }

}