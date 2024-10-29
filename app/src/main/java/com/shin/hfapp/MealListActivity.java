package com.shin.hfapp;

import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
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
    private FirebaseFirestore db;
    private String currentDate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_meal_list);

        listViewMeals = findViewById(R.id.listViewMeals);
        textViewTotalIntake = findViewById(R.id.textViewTotalIntake);
        db = FirebaseFirestore.getInstance();

        currentDate = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());

        // Fetch meals from Firestore
        fetchMealsFromFirestore();
    }

    private void fetchMealsFromFirestore() {
        db.collection("meals")
                .whereEqualTo("userId", FirebaseAuth.getInstance().getCurrentUser().getUid()) // Filter meals by user ID
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        List<Meal> allMeals = new ArrayList<>();
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            Meal meal = document.toObject(Meal.class);
                            allMeals.add(meal);
                        }
                        // Group meals by date after fetching
                        Map<String, List<Meal>> mealsByDate = groupMealsByDate(allMeals);

                        // Create a list to display both dates and meals
                        List<String> detailedMeals = new ArrayList<>();
                        for (String date : mealsByDate.keySet()) {
                            // Add the date as a header
                            detailedMeals.add("Date: " + date);

                            // Add meals for the specific date
                            List<Meal> mealsForDate = mealsByDate.get(date);
                            for (Meal meal : mealsForDate) {
                                detailedMeals.add(meal.toString());  // Ensure this method displays meal details
                            }

                            // Calculate total intake for the day
                            TotalIntake totalIntake = calculateTotalIntake(mealsForDate);
                            detailedMeals.add("Total for " + date + ": " +
                                    "Calories: " + totalIntake.getTotalCalories() +
                                    ", Protein: " + totalIntake.getTotalProtein() +
                                    ", Carbs: " + totalIntake.getTotalCarbs() +
                                    ", Fat: " + totalIntake.getTotalFat());
                        }

                        // Populate the ListView with the detailed meals
                        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, detailedMeals);
                        listViewMeals.setAdapter(adapter);

                        // Calculate total intake for all meals
                        TotalIntake totalIntakeForAllDays = calculateTotalIntake(allMeals);
                        textViewTotalIntake.setText("Total for All Days: " +
                                "Calories: " + totalIntakeForAllDays.getTotalCalories() +
                                ", Protein: " + totalIntakeForAllDays.getTotalProtein() +
                                ", Carbs: " + totalIntakeForAllDays.getTotalCarbs() +
                                ", Fat: " + totalIntakeForAllDays.getTotalFat());
                    } else {
                        // Handle the error
                        Toast.makeText(MealListActivity.this, "Error fetching meals: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
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

    // Helper method to calculate total intake
    private TotalIntake calculateTotalIntake(List<Meal> meals) {
        int totalCalories = 0;
        float totalProtein = 0;
        float totalCarbs = 0;
        float totalFats = 0;

        for (Meal meal : meals) {
            totalCalories += meal.getCalories();
            totalProtein += meal.getProtein();
            totalCarbs += meal.getCarbs();
            totalFats += meal.getFat();
        }

        return new TotalIntake(totalCalories, totalProtein, totalCarbs, totalFats);
    }
}
