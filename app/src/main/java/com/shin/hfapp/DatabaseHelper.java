package com.shin.hfapp;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.shin.hfapp.models.BMIRecord;
import com.shin.hfapp.models.Meal;
import com.shin.hfapp.models.NicotineLog;
import com.shin.hfapp.models.Step;
import com.shin.hfapp.models.User;

import java.util.ArrayList;
import java.util.List;

import com.google.firebase.firestore.FirebaseFirestore;

public class DatabaseHelper extends SQLiteOpenHelper {

    // Table details
    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "fitnessTracker.db";

    // Table names

    private static final String TABLE_MEALS = "meals";
    private static final String TABLE_BMI = "bmi_records";
    private static final String TABLE_STEPS = "steps";
    private static final String TABLE_NICOTINE = "nicotine_log";

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

    //Nicotine table cols
    public static final String COLUMN_NICOTINE_ID = "id";
    public static final String COLUMN_NICOTINE_TYPE = "type";
    public static final String COLUMN_NICOTINE_COUNT = "count";
    public static final String COLUMN_NICOTINE_DATE = "date";
    public static final String COLUMN_NOTES = "notes";


    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // Create Meals Table
        String CREATE_NICOTINE_TABLE = "CREATE TABLE " + TABLE_NICOTINE + "("
                + COLUMN_NICOTINE_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + COLUMN_NICOTINE_TYPE + " TEXT,"
                + COLUMN_NICOTINE_COUNT + " INTEGER,"
                + COLUMN_NICOTINE_DATE + " TEXT,"
                + COLUMN_NOTES + " TEXT" + ")";
        db.execSQL(CREATE_NICOTINE_TABLE);


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
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NICOTINE);
        onCreate(db);
    }

    public void onDeleteHistory(SQLiteDatabase db) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_MEALS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_BMI);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_STEPS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NICOTINE);
        onCreate(db);
    }

    public void insertNicotineLog(String type, int count, String date, String notes) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_NICOTINE_TYPE, type);
        values.put(COLUMN_NICOTINE_COUNT, count);
        values.put(COLUMN_NICOTINE_DATE, date);
        values.put(COLUMN_NOTES, notes);
        db.insert(TABLE_NICOTINE, null, values);
        db.close();
    }

    public List<NicotineLog> getNicotineLogsByDate(String date) {
        List<NicotineLog> logs = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_NICOTINE, null, COLUMN_NICOTINE_DATE + "=?", new String[]{date}, null, null, null);

        if (cursor.moveToFirst()) {
            do {
                @SuppressLint("Range") String type = cursor.getString(cursor.getColumnIndex(COLUMN_NICOTINE_TYPE));
                @SuppressLint("Range") int count = cursor.getInt(cursor.getColumnIndex(COLUMN_NICOTINE_COUNT));
                @SuppressLint("Range") String notes = cursor.getString(cursor.getColumnIndex(COLUMN_NOTES));
                logs.add(new NicotineLog(type, count, date, notes));
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return logs;
    }

    public List<NicotineLog> getAllNicotineLogs() {
        List<NicotineLog> logs = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_NICOTINE, null, null, null, null, null, COLUMN_DATE + " DESC"); // Order by date descending

        if (cursor.moveToFirst()) {
            do {
                @SuppressLint("Range") String type = cursor.getString(cursor.getColumnIndex(COLUMN_NICOTINE_TYPE));
                @SuppressLint("Range") int count = cursor.getInt(cursor.getColumnIndex(COLUMN_NICOTINE_COUNT));
                @SuppressLint("Range") String date = cursor.getString(cursor.getColumnIndex(COLUMN_NICOTINE_DATE));
                @SuppressLint("Range") String notes = cursor.getString(cursor.getColumnIndex(COLUMN_NOTES));
                logs.add(new NicotineLog(type, count, date, notes));
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return logs;
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
    @SuppressLint("Range")
    public List<Meal> getMealsForDay(String date) {
        List<Meal> meals = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_MEALS, null, COLUMN_DATE + "=?", new String[]{date}, null, null, COLUMN_DATE + " DESC");

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
    @SuppressLint("Range")
    public TotalIntake getTotalIntakeForDay(String date) {
        TotalIntake totalIntake = new TotalIntake(0, 0, 0, 0);
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

    // Get all BMI records as a List
    public List<BMIRecord> getAllBMIRecords() {
        List<BMIRecord> bmiList = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT * FROM " + TABLE_BMI + " ORDER BY " + COLUMN_BMI_DATE + " DESC";
        Cursor cursor = db.rawQuery(query, null);

        if (cursor.moveToFirst()) {
            do {
                BMIRecord bmiRecord = new BMIRecord();
                bmiRecord.setId(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_BMI_ID)));
                bmiRecord.setWeight(cursor.getFloat(cursor.getColumnIndexOrThrow(COLUMN_WEIGHT)));
                bmiRecord.setHeight(cursor.getFloat(cursor.getColumnIndexOrThrow(COLUMN_HEIGHT)));
                bmiRecord.setBmi(cursor.getFloat(cursor.getColumnIndexOrThrow(COLUMN_BMI)));
                bmiRecord.setDate(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_BMI_DATE)));
                bmiList.add(bmiRecord);
            } while (cursor.moveToNext());
        }
        cursor.close();
        return bmiList;
    }

    @SuppressLint("Range")
    public List<BMIRecord> getBMIRecordsForLastMonths(int months) {
        List<BMIRecord> bmiList = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();

        // Query to get records for the last 'n' months
        String query = "SELECT * FROM " + TABLE_BMI + " WHERE " + COLUMN_BMI_DATE + " >= date('now', '-" + months + " months') ORDER BY " + COLUMN_BMI_DATE + " ASC";
        Cursor cursor = db.rawQuery(query, null);

        if (cursor.moveToFirst()) {
            do {
                BMIRecord bmiRecord = new BMIRecord();
                bmiRecord.setId(cursor.getString(cursor.getColumnIndex(COLUMN_BMI_ID)));
                bmiRecord.setWeight(cursor.getFloat(cursor.getColumnIndex(COLUMN_WEIGHT)));
                bmiRecord.setHeight(cursor.getFloat(cursor.getColumnIndex(COLUMN_HEIGHT)));
                bmiRecord.setBmi(cursor.getFloat(cursor.getColumnIndex(COLUMN_BMI)));
                bmiRecord.setDate(cursor.getString(cursor.getColumnIndex(COLUMN_BMI_DATE)));
                bmiList.add(bmiRecord);
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return bmiList;
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
            @SuppressLint("Range") int steps = cursor.getInt(cursor.getColumnIndex(COLUMN_STEPS));
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
        String selectQuery = "SELECT * FROM " + TABLE_STEPS + " ORDER BY " + COLUMN_STEP_DATE + " DESC";
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

    // Retrieve all meals for all dates
    @SuppressLint("Range")
    public List<Meal> getAllMeals() {
        List<Meal> meals = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_MEALS, null, null, null, null, null, COLUMN_DATE + " ASC"); // Order by date

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



    // (DatabaseHelper.java)

    public void saveUserToFirestore(User user, String userId) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("users").document(userId).set(user);
    }

    public void saveCurrentUserData(String userId) {
        List<BMIRecord> bmiRecords = getAllBMIRecords();
        List<Step> steps = getAllSteps();
        List<Meal> meals = getAllMeals();
        List<NicotineLog> nicotineLogs = getAllNicotineLogs();

        User user = new User(bmiRecords, steps, meals, nicotineLogs);
        saveUserToFirestore(user, userId);
    }


    // Retrieve user data from Firestore and save to SQLite
    public void retrieveUserFromFirestore(String userId) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("users").document(userId).get().addOnSuccessListener(documentSnapshot -> {
            if (documentSnapshot.exists()) {
                User user = documentSnapshot.toObject(User.class);

                // Clear existing records before inserting new data
                clearAllTables();

                // Insert BMI records
                for (BMIRecord bmi : user.getBmiRecords()) {
                    insertBMI(bmi.getWeight(), bmi.getHeight(), bmi.getBmi(), bmi.getDate());
                }

                // Insert steps
                for (Step step : user.getSteps()) {
                    insertOrUpdateSteps(step.getDate(), step.getSteps());
                }

                // Insert meals
                for (Meal meal : user.getMeals()) {
                    insertMeal(meal.getName(), meal.getCalories(), meal.getProtein(), meal.getCarbs(), meal.getFat(), meal.getDate());
                }

                // Insert nicotine logs
                for (NicotineLog log : user.getNicotineLogs()) {
                    insertNicotineLog(log.getType(), log.getCount(), log.getDate(), log.getNotes());
                }
            }
        });
    }

    private void clearAllTables() {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("DELETE FROM " + TABLE_MEALS);
        db.execSQL("DELETE FROM " + TABLE_BMI);
        db.execSQL("DELETE FROM " + TABLE_STEPS);
        db.execSQL("DELETE FROM " + TABLE_NICOTINE);
    }

}



