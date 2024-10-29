package com.shin.hfapp;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.auth.FirebaseAuth;
import com.shin.hfapp.models.BMIRecord;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;

public class BMICalculatorActivity extends AppCompatActivity {

    private FirebaseFirestore db;
    private FirebaseAuth auth;
    private EditText weightInput, heightInput;
    private TextView resultTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bmicalculator);

        // Initialize Firestore and Firebase Auth
        db = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();

        // Initialize views
        weightInput = findViewById(R.id.weightInput);
        heightInput = findViewById(R.id.heightInput);
        resultTextView = findViewById(R.id.resultTextView);
        Button calculateButton = findViewById(R.id.calculateButton);

        // Calculate BMI and save to Firestore
        calculateButton.setOnClickListener(v -> {
            String weightStr = weightInput.getText().toString();
            String heightStr = heightInput.getText().toString();

            if (!weightStr.isEmpty() && !heightStr.isEmpty()) {
                float weight = Float.parseFloat(weightStr);
                float height = Float.parseFloat(heightStr);
                float bmi = calculateBMI(weight, height);
                String date = getCurrentDate();

                // Display BMI result
                resultTextView.setText(String.format("Your BMI is: %.2f", bmi));

                // Store BMI data in Firestore
                saveBMIToFirestore(weight, height, bmi, date);
            } else {
                Toast.makeText(BMICalculatorActivity.this, "Please enter both weight and height", Toast.LENGTH_SHORT).show();
            }
        });
    }

    // Calculate BMI (BMI = weight in kg / height in meters squared)
    private float calculateBMI(float weight, float height) {
        return weight / (height * height);
    }

    // Get the current date
    private String getCurrentDate() {
        return new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());
    }

    private void saveBMIToFirestore(float weight, float height, float bmi, String date) {
        String userId = auth.getCurrentUser() != null ? auth.getCurrentUser().getUid() : null;

        if (userId == null) {
            Toast.makeText(BMICalculatorActivity.this, "User not authenticated", Toast.LENGTH_SHORT).show();
            return;
        }

        // Generate a unique ID for each BMI record
        String recordId = db.collection("bmiRecords").document(userId).collection("userBMIRecords").document().getId();

        // Create a BMIRecord object
        BMIRecord bmiRecord = new BMIRecord(recordId, weight, height, bmi, date);

        // Save the BMI record in the userBMIRecords subcollection
        db.collection("bmiRecords").document(userId)
                .collection("userBMIRecords")
                .document(date)
                .set(bmiRecord)
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(BMICalculatorActivity.this, "BMI recorded successfully!", Toast.LENGTH_SHORT).show();
                    weightInput.setText("");
                    heightInput.setText("");
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(BMICalculatorActivity.this, "Error recording BMI: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }

}
