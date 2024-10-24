package com.shin.hfapp;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

import com.shin.hfapp.models.Meal;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class MealListActivity extends AppCompatActivity {

    private ListView listViewMeals;
    private TextView textViewTotalIntake;
    private DatabaseHelper databaseHelper;
    private String currentDate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_meal_list);

        listViewMeals = findViewById(R.id.listViewMeals);
        textViewTotalIntake = findViewById(R.id.textViewTotalIntake);
        databaseHelper = new DatabaseHelper(this);

        // Retrieve all meals
        List<Meal> allMeals = databaseHelper.getAllMeals();

        // Group meals by date
        Map<String, List<Meal>> mealsByDate = groupMealsByDate(allMeals);

        // Create a list to display both dates and meals
        List<String> detailedMeals = new ArrayList<>();
        for (String date : mealsByDate.keySet()) {
            // Add the date as a header
            detailedMeals.add("Date: " + date);

            // Add meals for the specific date
            List<Meal> mealsForDate = mealsByDate.get(date);
            for (Meal meal : mealsForDate) {
                detailedMeals.add(meal.toString());  // meal.toString() should display meal details
            }

            // Calculate total intake for the day
            TotalIntake totalIntake = databaseHelper.getTotalIntakeForDay(date);
            detailedMeals.add("Total for " + date + ": " +
                    "Calories: " + totalIntake.getTotalCalories() +
                    ", Protein: " + totalIntake.getTotalProtein() +
                    ", Carbs: " + totalIntake.getTotalCarbs() +
                    ", Fat: " + totalIntake.getTotalFat());
        }

        // Populate the ListView with the detailed meals
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, detailedMeals);
        listViewMeals.setAdapter(adapter);
        currentDate = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());
        // Calculate total intake for all meals
        TotalIntake totalIntake = databaseHelper.getTotalIntakeForDay(currentDate);
        textViewTotalIntake.setText("Total for All Days: " +
                "Calories: " + totalIntake.getTotalCalories() +
                ", Protein: " + totalIntake.getTotalProtein() +
                ", Carbs: " + totalIntake.getTotalCarbs() +
                ", Fat: " + totalIntake.getTotalFat());
    }

    // Helper method to group meals by date
    private Map<String, List<Meal>> groupMealsByDate(List<Meal> allMeals) {
        Map<String, List<Meal>> mealsByDate = new HashMap<>();
        for (Meal meal : allMeals) {
            String date = meal.getDate();
            if (!mealsByDate.containsKey(date)) {
                mealsByDate.put(date, new ArrayList<>());
            }
            mealsByDate.get(date).add(meal);
        }
        return mealsByDate;
    }
}


