package com.shin.hfapp;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

import com.shin.hfapp.models.Step;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class StepTrackerActivity extends AppCompatActivity implements SensorEventListener {

    private SensorManager sensorManager;
    private Sensor stepCounterSensor;
    private boolean isSensorPresent;
    private int steps = 0;
    private int stepGoal = 10000; // Set a daily goal
    private DatabaseHelper databaseHelper;
    private String currentDate;
    private ListView listViewAllSteps;
    private TextView stepCountTextView;
    private ProgressBar stepProgressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_step_tracker);

        stepCountTextView = findViewById(R.id.stepCountTextView);
        stepProgressBar = findViewById(R.id.stepProgressBar);
        listViewAllSteps = findViewById(R.id.listViewAllSteps);
        databaseHelper = new DatabaseHelper(this);

        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);

        if (sensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER) != null) {
            stepCounterSensor = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER);
            isSensorPresent = true;
        } else {
            stepCountTextView.setText("Step Counter Sensor not available!");
            isSensorPresent = false;
        }

        stepProgressBar.setMax(stepGoal);

        // Get today's date
        currentDate = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());

        // Load steps from the database for today
        steps = databaseHelper.getStepsForDay(currentDate);
        stepCountTextView.setText("Steps: " + steps);
        stepProgressBar.setProgress(steps);

        List<Step> allSteps = databaseHelper.getAllSteps();
        ArrayAdapter<Step> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, allSteps);
        listViewAllSteps.setAdapter(adapter);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (isSensorPresent) {
            sensorManager.registerListener(this, stepCounterSensor, SensorManager.SENSOR_DELAY_UI);
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
        if (event.sensor.getType() == Sensor.TYPE_STEP_COUNTER) {
            // Update steps in the UI
            steps = (int) event.values[0];
            stepCountTextView.setText("Steps: " + steps);
            stepProgressBar.setProgress(steps);

            // Save steps in the database
            databaseHelper.insertOrUpdateSteps(currentDate, steps);
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        // You can handle accuracy changes here if needed
    }
}
