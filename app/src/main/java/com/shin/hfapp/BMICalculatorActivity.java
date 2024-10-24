package com.shin.hfapp;

import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import com.shin.hfapp.models.BMIRecord;

import androidx.appcompat.app.AppCompatActivity;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class BMICalculatorActivity extends AppCompatActivity {

    private DatabaseHelper databaseHelper;
    private EditText weightInput, heightInput;
    private TextView resultTextView;
    private ListView bmiListView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bmicalculator);

        weightInput = findViewById(R.id.weightInput);
        heightInput = findViewById(R.id.heightInput);
        resultTextView = findViewById(R.id.resultTextView);
        bmiListView = findViewById(R.id.bmiListView);
        Button calculateButton = findViewById(R.id.calculateButton);

        databaseHelper = new DatabaseHelper(this);

        // Calculate BMI when button is clicked
        calculateButton.setOnClickListener(v -> {
            String weightStr = weightInput.getText().toString();
            String heightStr = heightInput.getText().toString();

            if (!weightStr.isEmpty() && !heightStr.isEmpty()) {
                float weight = Float.parseFloat(weightStr);
                float height = Float.parseFloat(heightStr);
                float bmi = calculateBMI(weight, height);
                String date = getCurrentDate();

                // Insert the BMI record into the database
                databaseHelper.insertBMI(weight, height, bmi, date);

                resultTextView.setText(String.format("Your BMI is: %.2f", bmi));
                Toast.makeText(BMICalculatorActivity.this, "BMI recorded", Toast.LENGTH_SHORT).show();

                // Update the list of BMI records
                loadBMIRecords();
            } else {
                Toast.makeText(BMICalculatorActivity.this, "Please enter both weight and height", Toast.LENGTH_SHORT).show();
            }
        });

        // Load the BMI records when the app is started
        loadBMIRecords();
    }

    // Calculate BMI (BMI = weight in kg / height in meters squared)
    private float calculateBMI(float weight, float height) {
        return weight / (height * height);
    }

    // Get the current date
    private String getCurrentDate() {
        return new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());
    }

    // Load all BMI records and display them in a ListView using ArrayAdapter
    private void loadBMIRecords() {
        List<BMIRecord> allBmiRecords = databaseHelper.getAllBMIRecords();
        ArrayAdapter<BMIRecord> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, allBmiRecords);
        bmiListView.setAdapter(adapter);
    }
}
