package com.shin.hfapp;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.fragment.app.Fragment;

import java.util.Objects;

public class AccountFragment extends Fragment {

    public AccountFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_account, container, false);

        // Find the logout button in the layout
        Button btnLogout = view.findViewById(R.id.btnLogout);

        // Set the onClickListener for the logout button
        btnLogout.setOnClickListener(v -> {
            // Access SharedPreferences using the Activity context
            getActivity();
            SharedPreferences sharedPreferences = requireActivity().getSharedPreferences("UserSession", Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putBoolean("isLoggedIn", false);  // Reset the login status
            editor.apply();

            // Redirect to the login screen
            Intent intent = new Intent(getActivity(), LoginActivity.class);
            startActivity(intent);

            // Finish the current activity (HomeActivity in this case)
            requireActivity().finish();
        });

        return view;
    }
}
