package com.shin.hfapp;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main); // Linking to the main XML layout

        SharedPreferences sharedPreferences = getSharedPreferences("UserSession", MODE_PRIVATE);
        boolean isLoggedIn = sharedPreferences.getBoolean("isLoggedIn", false);  // Default is false

        if (isLoggedIn) {
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
                try{
                Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                startActivity(intent);
                        // Your sign-up logic here
                    } catch (Exception e) {
                        Log.e("LogInError", "Error occurred: ", e);
                    }
            }
        });

        // Navigate to Sign Up Activity
        btnSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try{
                Intent intent = new Intent(MainActivity.this, RegisterActivity.class);
                startActivity(intent);

                    } catch (Exception e) {
                        Log.e("SignUpError", "Error occurred: ", e);
                    }
            }
        });

    }}
}
