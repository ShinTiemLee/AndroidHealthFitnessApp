package com.shin.hfapp;

import android.content.Context;
import android.content.pm.PackageManager;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.shin.hfapp.models.Step;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class StepTrackerActivity extends AppCompatActivity implements SensorEventListener {

    private static final int SENSOR_PERMISSION_CODE = 1;
    private SensorManager sensorManager;
    private Sensor accelerometerSensor;
    private boolean isSensorPresent;
    private int steps = 0;
    private int stepGoal = 10000; // Daily goal
    private DatabaseHelper databaseHelper;
    private String currentDate;
    private ListView listViewAllSteps;
    private TextView stepCountTextView;
    private ProgressBar stepProgressBar;

    // Variables for step detection
    private static final float THRESHOLD = 10.0f; // Acceleration threshold for step detection
    private float previousY = 0;
    private boolean stepDetected = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_step_tracker);

        stepCountTextView = findViewById(R.id.stepCountTextView);
        stepProgressBar = findViewById(R.id.stepProgressBar);
        listViewAllSteps = findViewById(R.id.listViewAllSteps);
        databaseHelper = new DatabaseHelper(this);

        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);

        // Check for sensor permission
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.BODY_SENSORS) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.BODY_SENSORS}, SENSOR_PERMISSION_CODE);
        } else {
            initializeSensor();
        }

        // Set the progress bar max value to the step goal
        stepProgressBar.setMax(stepGoal);

        // Get today's date
        currentDate = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());

        // Load steps from the database for today
        steps = databaseHelper.getStepsForDay(currentDate);
        stepCountTextView.setText("Steps: " + steps);
        stepProgressBar.setProgress(steps);

        // Load all steps from the database
        List<Step> allSteps = databaseHelper.getAllSteps();
        ArrayAdapter<Step> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, allSteps);
        listViewAllSteps.setAdapter(adapter);
    }

    // Initialize accelerometer sensor
    private void initializeSensor() {
        if (sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER) != null) {
            accelerometerSensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
            isSensorPresent = true;
        } else {
            stepCountTextView.setText("Accelerometer Sensor not available!");
            isSensorPresent = false;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (isSensorPresent) {
            sensorManager.registerListener(this, accelerometerSensor, SensorManager.SENSOR_DELAY_UI);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (isSensorPresent) {
            sensorManager.unregisterListener(this);
        }
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            float y = event.values[1]; // Use Y-axis for detecting vertical movement

            // Check if the step threshold is crossed
            if (Math.abs(y - previousY) > THRESHOLD && !stepDetected) {
                steps++;
                stepDetected = true;

                // Update UI and save to database
                stepCountTextView.setText("Steps: " + steps);
                stepProgressBar.setProgress(steps);
                databaseHelper.insertOrUpdateSteps(currentDate, steps);
            }

            // Reset step detection after movement stabilizes
            if (Math.abs(y - previousY) < THRESHOLD / 2) {
                stepDetected = false;
            }

            previousY = y;
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        // Handle changes in sensor accuracy if necessary
    }

    // Handle permission request result
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == SENSOR_PERMISSION_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                initializeSensor();
            } else {
                stepCountTextView.setText("Permission denied!");
            }
        }
    }
}
