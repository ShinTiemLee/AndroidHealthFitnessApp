package com.shin.hfapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.auth.FirebaseAuth;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;

public class DietTrackerActivity extends AppCompatActivity {

    private FirebaseFirestore db;
    private FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_diet_tracker);

        // Initialize Firestore and Firebase Auth
        db = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();

        // Initialize views
        EditText editTextMealName = findViewById(R.id.editTextMealName);
        EditText editTextFats = findViewById(R.id.editTextFats);
        EditText editTextProteins = findViewById(R.id.editTextProteins);
        EditText editTextCalories = findViewById(R.id.editTextCalories);
        EditText editTextCarbs = findViewById(R.id.editTextCarbs);
        Button buttonSaveMeal = findViewById(R.id.buttonSaveMeal);
        Button buttonViewMeals = findViewById(R.id.buttonViewMeals);

        // Save meal button click
        buttonSaveMeal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String mealName = editTextMealName.getText().toString();
                int calories = Integer.parseInt(editTextCalories.getText().toString());
                float fats = Float.parseFloat(editTextFats.getText().toString());
                float proteins = Float.parseFloat(editTextProteins.getText().toString());
                float carbs = Float.parseFloat(editTextCarbs.getText().toString());

                // Get current date
                String currentDate = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());

                // Create a HashMap to store meal data
                HashMap<String, Object> mealData = new HashMap<>();
                mealData.put("name", mealName);
                mealData.put("calories", calories);
                mealData.put("fat", fats);
                mealData.put("protein", proteins);
                mealData.put("carbs", carbs);
                mealData.put("date", currentDate);
                mealData.put("userId", auth.getCurrentUser().getUid()); // Store user ID for data association

                // Insert meal into Firestore
                db.collection("meals")
                        .add(mealData)
                        .addOnSuccessListener(documentReference -> {
                            Toast.makeText(DietTrackerActivity.this, "Meal saved successfully!", Toast.LENGTH_SHORT).show();
                            editTextMealName.setText("");
                            editTextCalories.setText("");
                            editTextFats.setText("");
                            editTextProteins.setText("");
                            editTextCarbs.setText("");
                        })
                        .addOnFailureListener(e -> {
                            Toast.makeText(DietTrackerActivity.this, "Error saving meal: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                        });
            }
        });

        // View meals button click
        buttonViewMeals.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(DietTrackerActivity.this, MealListActivity.class);
                startActivity(intent);
            }
        });
    }
}
