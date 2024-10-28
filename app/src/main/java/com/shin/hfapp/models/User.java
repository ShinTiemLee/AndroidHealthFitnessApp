package com.shin.hfapp.models;

import java.util.List;

public class User {
    private List<BMIRecord> bmiRecords;
    private List<Step> steps;
    private List<Meal> meals;
    private List<NicotineLog> nicotineLogs;

    public User() {
        // Required for Firestore serialization
    }

    public User(List<BMIRecord> bmiRecords, List<Step> steps, List<Meal> meals, List<NicotineLog> nicotineLogs) {
        this.bmiRecords = bmiRecords;
        this.steps = steps;
        this.meals = meals;
        this.nicotineLogs = nicotineLogs;
    }

    public List<BMIRecord> getBmiRecords() {
        return bmiRecords;
    }

    public List<Step> getSteps() {
        return steps;
    }

    public List<Meal> getMeals() {
        return meals;
    }

    public List<NicotineLog> getNicotineLogs() {
        return nicotineLogs;
    }
}
