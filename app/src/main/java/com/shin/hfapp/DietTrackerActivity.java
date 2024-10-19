package com.shin.hfapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.ArrayList;
import java.util.Locale;

public class DietTrackerActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_diet_tracker);


        // Initialize views
        EditText editTextMealName = findViewById(R.id.editTextMealName);
        EditText editTextFats = findViewById(R.id.editTextFats);
        EditText editTextProteins = findViewById(R.id.editTextProteins);
        EditText editTextCalories = findViewById(R.id.editTextCalories);
        EditText editTextCarbs = findViewById(R.id.editTextCarbs);
        Button buttonSaveMeal = findViewById(R.id.buttonSaveMeal);
        Button buttonViewMeals = findViewById(R.id.buttonViewMeals);

        DatabaseHelper databaseHelper = new DatabaseHelper(this);

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

                // Insert meal into the database
                databaseHelper.insertMeal(mealName, calories, proteins, carbs, fats, currentDate);
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