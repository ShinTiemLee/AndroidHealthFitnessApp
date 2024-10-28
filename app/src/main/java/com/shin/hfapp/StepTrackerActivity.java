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

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.shin.hfapp.models.Step;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
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
    private FirebaseFirestore db;
    private FirebaseAuth auth;
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

        // Initialize Firestore and Firebase Auth
        db = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();

        stepCountTextView = findViewById(R.id.stepCountTextView);
        stepProgressBar = findViewById(R.id.stepProgressBar);
        listViewAllSteps = findViewById(R.id.listViewAllSteps);

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

        // Load steps from Firestore for today
        loadStepsFromFirestore();

        // Load all steps from Firestore
        loadAllStepsFromFirestore();
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

                // Update UI and save to Firestore
                stepCountTextView.setText("Steps: " + steps);
                stepProgressBar.setProgress(steps);
                saveStepsToFirestore( steps, currentDate);
            }

            // Reset step detection after movement stabilizes
            if (Math.abs(y - previousY) < THRESHOLD / 2) {
                stepDetected = false;
            }

            previousY = y;
        }
        // Update the UI with the list of all steps
        loadAllStepsFromFirestore();
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        // Handle changes in sensor accuracy if necessary
    }

    // Load steps for today from Firestore
    private void loadStepsFromFirestore() {
        String userId = auth.getCurrentUser().getUid();
        db.collection("steps")
                .document(userId)
                .collection("dailySteps")
                .document(currentDate)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        steps = documentSnapshot.getLong("steps").intValue();
                    } else {
                        steps = 0; // No steps recorded yet
                    }
                    stepCountTextView.setText("Steps: " + steps);
                    stepProgressBar.setProgress(steps);
                })
                .addOnFailureListener(e -> Log.e("Firestore", "Error loading steps: ", e));
    }

    // Save steps to Firestore
    private void saveStepsToFirestore(int stepCount, String date) {
        String userId = auth.getCurrentUser().getUid();
        db.collection("steps")
                .document(userId)
                .collection("dailySteps")
                .document(date)
                .set(new Step(stepCount, date)) // Ensure the correct order of parameters
                .addOnSuccessListener(aVoid -> Log.d("Firestore", "Steps saved successfully!"))
                .addOnFailureListener(e -> Log.e("Firestore", "Error saving steps: ", e));
    }


    // Load all steps from Firestore
    private void loadAllStepsFromFirestore() {
        String userId = auth.getCurrentUser().getUid();
        db.collection("steps")
                .document(userId)
                .collection("dailySteps")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        List<Step> allSteps = new ArrayList<>();
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            int stepCount = document.getLong("steps").intValue();
                            String date = document.getString("date");
                            allSteps.add(new Step(stepCount, date));
                        }

                        ArrayAdapter<Step> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, allSteps);
                        listViewAllSteps.setAdapter(adapter);
                    } else {
                        Log.e("Firestore", "Error loading all steps: ", task.getException());
                    }
                });
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
