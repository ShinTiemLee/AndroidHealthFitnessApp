package com.shin.hfapp;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link HomeFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class HomeFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public HomeFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment HomeFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static HomeFragment newInstance(String param1, String param2) {
        HomeFragment fragment = new HomeFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }
View view;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view=inflater.inflate(R.layout.fragment_home, container, false);
        Button bmiTrackerBtn = view.findViewById(R.id.bmiTrackerBtn);

        // Set the onClickListener for the logout button
        bmiTrackerBtn.setOnClickListener(v -> {
            // Access SharedPreferences using the Activity context
            getActivity();

            Intent intent = new Intent(getActivity(), BMICalculatorActivity.class);
            startActivity(intent);

        });

        Button stepTrackerBtn = view.findViewById(R.id.stepTrackerBtn);

        // Set the onClickListener for the logout button
        stepTrackerBtn.setOnClickListener(v -> {
            // Access SharedPreferences using the Activity context
            getActivity();

            Intent intent = new Intent(getActivity(), StepTrackerActivity.class);
            startActivity(intent);

        });

        Button dietTrackerBtn = view.findViewById(R.id.dietTrackerBtn);

        // Set the onClickListener for the logout button
        dietTrackerBtn.setOnClickListener(v -> {
            // Access SharedPreferences using the Activity context
            getActivity();

            Intent intent = new Intent(getActivity(), DietTrackerActivity.class);
            startActivity(intent);

        });

        return view;
    }
}