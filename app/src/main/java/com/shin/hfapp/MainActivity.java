package com.shin.hfapp;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.auth.FirebaseAuth;

public class MainActivity extends AppCompatActivity {
    private FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main); // Linking to the main XML layout

        // Initialize Firebase Auth
        auth = FirebaseAuth.getInstance();

        // Check if the user is logged in
        if (auth.getCurrentUser() != null) {
            // User is already logged in, redirect to HomeActivity
            Intent intent = new Intent(MainActivity.this, HomeActivity.class);
            startActivity(intent);
            finish();  // Close the login activity
        } else {
            // Initialize buttons
            Button btnLogin = findViewById(R.id.btn_login);
            Button btnSignUp = findViewById(R.id.btn_sign_up);

            // Navigate to Login Activity
            btnLogin.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    try {
                        Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                        startActivity(intent);
                    } catch (Exception e) {
                        Log.e("LogInError", "Error occurred: ", e);
                    }
                }
            });

            // Navigate to Sign Up Activity
            btnSignUp.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    try {
                        Intent intent = new Intent(MainActivity.this, RegisterActivity.class);
                        startActivity(intent);
                    } catch (Exception e) {
                        Log.e("SignUpError", "Error occurred: ", e);
                    }
                }
            });
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        // No need to save user data here as we are using Firebase Auth
    }
}
