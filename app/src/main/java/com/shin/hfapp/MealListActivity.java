package com.shin.hfapp;

import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

import com.shin.hfapp.models.Meal;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class MealListActivity extends AppCompatActivity {

    private ListView listViewMeals;
    private TextView textViewDate, textViewTotalIntake;
    private DatabaseHelper databaseHelper;
    private String currentDate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_meal_list);

        listViewMeals = findViewById(R.id.listViewMeals);
        textViewDate = findViewById(R.id.textViewDate);
        textViewTotalIntake = findViewById(R.id.textViewTotalIntake);

        databaseHelper = new DatabaseHelper(this);

        // Get current date
        currentDate = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());
        textViewDate.setText("Meals for " + currentDate);

        // Retrieve meals for today
        List<Meal> meals = databaseHelper.getMealsForDay(currentDate);

        // Populate the list
        ArrayAdapter<Meal> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, meals);
        listViewMeals.setAdapter(adapter);

        // Calculate total intake
        TotalIntake totalIntake = databaseHelper.getTotalIntakeForDay(currentDate);
        textViewTotalIntake.setText("Total Calories: " + totalIntake.getTotalCalories() +
                "\nTotal Protein: " + totalIntake.getTotalProtein() +
                "\nTotal Carbs: " + totalIntake.getTotalCarbs() +
                "\nTotal Fat: " + totalIntake.getTotalFat());
    }
}
