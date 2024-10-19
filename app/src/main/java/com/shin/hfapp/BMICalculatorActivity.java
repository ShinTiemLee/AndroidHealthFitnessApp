package com.shin.hfapp;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

public class BMICalculatorActivity extends AppCompatActivity {

    private EditText weightInput;
    private EditText heightInput;
    private TextView bmiResult;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bmicalculator);

        weightInput = findViewById(R.id.weightInput);
        heightInput = findViewById(R.id.heightInput);
        bmiResult = findViewById(R.id.bmiResult);
        Button calculateBmiButton = findViewById(R.id.calculateBmiButton);

        calculateBmiButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                calculateBMI();
            }
        });
    }

    private void calculateBMI() {
        String weightStr = weightInput.getText().toString();
        String heightStr = heightInput.getText().toString();

        if (!weightStr.isEmpty() && !heightStr.isEmpty()) {
            double weight = Double.parseDouble(weightStr);
            double height = Double.parseDouble(heightStr);

            // Calculate BMI
            double bmi = weight / (height * height);

            // Determine BMI category
            String bmiCategory;
            if (bmi < 18.5) {
                bmiCategory = "Underweight";
            } else if (bmi >= 18.5 && bmi < 24.9) {
                bmiCategory = "Normal weight";
            } else if (bmi >= 25 && bmi < 29.9) {
                bmiCategory = "Overweight";
            } else {
                bmiCategory = "Obesity";
            }

            // Set the result
            String result = "Your BMI: " + String.format("%.2f", bmi) + "\nCategory: " + bmiCategory;
            bmiResult.setText(result);
        } else {
            bmiResult.setText("Please enter valid weight and height.");
        }
    }
}
