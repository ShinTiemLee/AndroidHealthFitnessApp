package com.shin.hfapp;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.shin.hfapp.models.Meal;
import com.shin.hfapp.models.Step;

import java.util.ArrayList;
import java.util.List;

public class DatabaseHelper extends SQLiteOpenHelper {

      // Table details
    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "fitnessTracker.db";

    // Table names
    private static final String TABLE_MEALS = "meals";
    private static final String TABLE_BMI = "bmi_records";
    private static final String TABLE_STEPS = "steps";

    // Meals Table Information
    private static final String COLUMN_MEAL_ID = "id";
    private static final String COLUMN_MEAL_NAME = "name";
    private static final String COLUMN_CALORIES = "calories";
    private static final String COLUMN_PROTEIN = "protein";
    private static final String COLUMN_CARBS = "carbs";
    private static final String COLUMN_FAT = "fat";
    private static final String COLUMN_DATE = "date"; // Store date as a string (YYYY-MM-DD)


    // BMI Table Columns
    private static final String COLUMN_BMI_ID = "id";
    private static final String COLUMN_WEIGHT = "weight";
    private static final String COLUMN_HEIGHT = "height";
    private static final String COLUMN_BMI = "bmi";
    private static final String COLUMN_BMI_DATE = "date";

    // Steps Table Columns
    private static final String COLUMN_STEP_ID = "id";
    private static final String COLUMN_STEPS = "step_count";
    private static final String COLUMN_STEP_DATE = "date";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // Create Meals Table
        String CREATE_MEALS_TABLE = "CREATE TABLE " + TABLE_MEALS + "("
                + COLUMN_MEAL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + COLUMN_MEAL_NAME + " TEXT,"
                + COLUMN_CALORIES + " INTEGER,"
                + COLUMN_PROTEIN + " REAL,"
                + COLUMN_CARBS + " REAL,"
                + COLUMN_FAT + " REAL,"
                + COLUMN_DATE + " TEXT" + ")";
        db.execSQL(CREATE_MEALS_TABLE);

        // Create BMI Table
        String CREATE_BMI_TABLE = "CREATE TABLE " + TABLE_BMI + "("
                + COLUMN_BMI_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + COLUMN_WEIGHT + " REAL,"
                + COLUMN_HEIGHT + " REAL,"
                + COLUMN_BMI + " REAL,"
                + COLUMN_BMI_DATE + " TEXT" + ")";
        db.execSQL(CREATE_BMI_TABLE);

        // Create Steps Table
        String CREATE_STEPS_TABLE = "CREATE TABLE " + TABLE_STEPS + "("
                + COLUMN_STEP_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + COLUMN_STEPS + " INTEGER,"
                + COLUMN_STEP_DATE + " TEXT" + ")";
        db.execSQL(CREATE_STEPS_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_MEALS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_BMI);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_STEPS);
        onCreate(db);
    }

    public void onDeleteHistory(SQLiteDatabase db) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_MEALS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_BMI);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_STEPS);
        onCreate(db);
    }


    // Insert Meal Data
    public void insertMeal(String name, int calories, float protein, float carbs, float fats, String date) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_MEAL_NAME, name);
        values.put(COLUMN_CALORIES, calories);
        values.put(COLUMN_PROTEIN, protein);
        values.put(COLUMN_CARBS, carbs);
        values.put(COLUMN_FAT, fats);
        values.put(COLUMN_DATE, date);
        db.insert(TABLE_MEALS, null, values);
        db.close();
    }

    // Retrieve all meals for a specific date
    public List<Meal> getMealsForDay(String date) {
        List<Meal> meals = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_MEALS, null, COLUMN_DATE + "=?", new String[]{date}, null, null, null);

        if (cursor.moveToFirst()) {
            do {
                Meal meal = new Meal();
                meal.setId(cursor.getInt(cursor.getColumnIndex(COLUMN_MEAL_ID)));
                meal.setName(cursor.getString(cursor.getColumnIndex(COLUMN_MEAL_NAME)));
                meal.setCalories(cursor.getInt(cursor.getColumnIndex(COLUMN_CALORIES)));
                meal.setProtein(cursor.getFloat(cursor.getColumnIndex(COLUMN_PROTEIN)));
                meal.setCarbs(cursor.getFloat(cursor.getColumnIndex(COLUMN_CARBS)));
                meal.setFat(cursor.getFloat(cursor.getColumnIndex(COLUMN_FAT)));
                meal.setDate(cursor.getString(cursor.getColumnIndex(COLUMN_DATE)));
                meals.add(meal);
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return meals;
    }


    // Calculate total intake for a day
    public TotalIntake getTotalIntakeForDay(String date) {
        TotalIntake totalIntake = new TotalIntake(0,0,0,0);
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT SUM(" + COLUMN_CALORIES + ") AS totalCalories, " +
                "SUM(" + COLUMN_PROTEIN + ") AS totalProtein, " +
                "SUM(" + COLUMN_CARBS + ") AS totalCarbs, " +
                "SUM(" + COLUMN_FAT + ") AS totalFat " +
                "FROM " + TABLE_MEALS + " WHERE " + COLUMN_DATE + "=?", new String[]{date});

        if (cursor.moveToFirst()) {
            totalIntake.setTotalCalories(cursor.getInt(cursor.getColumnIndex("totalCalories")));
            totalIntake.setTotalProtein(cursor.getFloat(cursor.getColumnIndex("totalProtein")));
            totalIntake.setTotalCarbs(cursor.getFloat(cursor.getColumnIndex("totalCarbs")));
            totalIntake.setTotalFat(cursor.getFloat(cursor.getColumnIndex("totalFat")));
        }
        cursor.close();
        db.close();
        return totalIntake;
    }

    // Insert BMI Data
    public void insertBMI(double weight, double height, double bmi, String date) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_WEIGHT, weight);
        values.put(COLUMN_HEIGHT, height);
        values.put(COLUMN_BMI, bmi);
        values.put(COLUMN_BMI_DATE, date);
        db.insert(TABLE_BMI, null, values);
        db.close();
    }

    // Insert step record for a specific day
    // Insert or update steps for the current day
    public void insertOrUpdateSteps(String date, int steps) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_DATE, date);
        values.put(COLUMN_STEPS, steps);

        // Check if a row exists for the given date
        int rowsAffected = db.update(TABLE_STEPS, values, COLUMN_DATE + "=?", new String[]{date});

        // If no rows were updated, insert a new row
        if (rowsAffected == 0) {
            db.insert(TABLE_STEPS, null, values);
        }

        db.close();
    }


    // Get steps for a given day
    public int getStepsForDay(String date) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_STEPS, new String[]{COLUMN_STEPS}, COLUMN_DATE + "=?",
                new String[]{date}, null, null, null);
        if (cursor != null && cursor.moveToFirst()) {
            int steps = cursor.getInt(cursor.getColumnIndex(COLUMN_STEPS));
            cursor.close();
            return steps;
        } else {
            return 0; // No steps recorded for this day
        }
    }


    public List<Step> getAllSteps() {
        List<Step> stepsList = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();

        // Query to get all records
        String selectQuery = "SELECT * FROM " + TABLE_STEPS;
        Cursor cursor = db.rawQuery(selectQuery, null);

        // Loop through all rows and add to the list
        if (cursor.moveToFirst()) {
            do {
                Step step = new Step();
                step.setDate(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_DATE)));
                step.setSteps(cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_STEPS)));
                stepsList.add(step);
            } while (cursor.moveToNext());
        }
        cursor.close();
        return stepsList;
    }

    // Retrieve Meal Data
    public Cursor getAllMeals() {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.rawQuery("SELECT * FROM " + TABLE_MEALS, null);
    }

    // Retrieve BMI Data
    public Cursor getAllBMIRecords() {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.rawQuery("SELECT * FROM " + TABLE_BMI, null);
    }

    // Retrieve Step Data
    public Cursor getAllStepCounts() {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.rawQuery("SELECT * FROM " + TABLE_STEPS, null);
    }
}
