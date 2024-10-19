package com.shin.hfapp;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import com.shin.hfapp.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.shin.hfapp.databinding.ActivityHomepageBinding;
import com.shin.hfapp.databinding.ActivityMainBinding;


public class HomeActivity extends AppCompatActivity {

    ActivityHomepageBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding= ActivityHomepageBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot()); // Linking to the main XML layout
        replaceFragment(new HomeFragment());


        Log.d("000","on create");
        binding.bottomNavigationView.setOnItemSelectedListener(item->{
            if (item.getItemId() == R.id.homeFragment) {
                replaceFragment(new HomeFragment());
            } else if (item.getItemId() == R.id.accountFragment) {
                replaceFragment(new AccountFragment());
            } else if (item.getItemId() == R.id.settingsFragment) {
                replaceFragment(new SettingsFragment());
            }
            else{
                Log.d("123","false");
                return false;
            }
            Log.d("111", "onCreate: "+item.getItemId());
                return true;
        });

    }

    private void replaceFragment(Fragment fragment) {
        Log.d("000","on replace");
        FragmentManager fragmentManager=getSupportFragmentManager();
        FragmentTransaction fragmentTransaction=fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.frame_layout, fragment);
        fragmentTransaction.commit();
    }



}


//        // Initialize BottomNavigationView
//        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);
//
//        // Setup NavController
//        NavHostFragment navHostFragment =
//                (NavHostFragment) getSupportFragmentManager().findFragmentById(R.id.nav_host_fragment);
//        assert navHostFragment != null;
//        NavController navController = navHostFragment.getNavController();
//
//        // Link BottomNavigationView with NavController
//        NavigationUI.setupWithNavController(bottomNavigationView, navController);