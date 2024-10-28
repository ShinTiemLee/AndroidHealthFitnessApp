package com.shin.hfapp;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.shin.hfapp.models.NicotineLog;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

public class NicotineTrackerActivity extends AppCompatActivity {

    private static final String TOBACCO_HELPLINE_NUMBER = "1800 112 356";
    private static final int CALL_PERMISSION_CODE = 102;

    private ListView listView;
    private EditText editTextType, editTextCount, editTextNotes;
    private FirebaseFirestore db;
    private FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d("nicotine", "NicotineTrackerActivity started");
        setContentView(R.layout.activity_nicotine_tracker);

        // Initialize Firestore and Firebase Auth
        db = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();

        listView = findViewById(R.id.listViewNicotineHistory);
        editTextType = findViewById(R.id.editTextType);
        editTextCount = findViewById(R.id.editTextCount);
        editTextNotes = findViewById(R.id.editTextNotes);
        Button buttonAddLog = findViewById(R.id.btnLog);
        Button buttonCallHelpline = findViewById(R.id.buttonCallHelpline);

        // Set up button click listener for adding logs
        buttonAddLog.setOnClickListener(v -> insertNicotineLog());

        // Set up button click listener for calling helpline
        buttonCallHelpline.setOnClickListener(v -> makeCallToHelpline());

        // Retrieve and display logs
        displayNicotineLogs();
    }

    private void insertNicotineLog() {
        String type = editTextType.getText().toString().trim();
        String countStr = editTextCount.getText().toString().trim();
        String notes = editTextNotes.getText().toString().trim();

        if (type.isEmpty() || countStr.isEmpty()) {
            Toast.makeText(this, "Please enter type and count", Toast.LENGTH_SHORT).show();
            return;
        }

        int count = Integer.parseInt(countStr);
        String date = getCurrentDate();
        String userId = auth.getCurrentUser().getUid();

        // Prepare data for Firestore
        HashMap<String, Object> nicotineData = new HashMap<>();
        nicotineData.put("type", type);
        nicotineData.put("count", count);
        nicotineData.put("date", date);
        nicotineData.put("notes", notes);
        nicotineData.put("userId", userId); // Associate with user ID

        // Save data to Firestore
        db.collection("nicotineLogs")
                .add(nicotineData)
                .addOnSuccessListener(documentReference -> {
                    Toast.makeText(NicotineTrackerActivity.this, "Log saved successfully!", Toast.LENGTH_SHORT).show();
                    editTextType.setText("");
                    editTextCount.setText("");
                    editTextNotes.setText("");
                    displayNicotineLogs(); // Refresh displayed logs
                })
                .addOnFailureListener(e ->
                        Toast.makeText(NicotineTrackerActivity.this, "Error saving log: " + e.getMessage(), Toast.LENGTH_SHORT).show()
                );
    }

    private void displayNicotineLogs() {
        String userId = auth.getCurrentUser().getUid();

        db.collection("nicotineLogs")
                .whereEqualTo("userId", userId)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        List<NicotineLog> logs = new ArrayList<>();
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            String type = document.getString("type");
                            int count = document.getLong("count").intValue();
                            String date = document.getString("date");
                            String notes = document.getString("notes");

                            logs.add(new NicotineLog(type, count, date, notes));
                        }

                        if (logs.isEmpty()) {
                            Toast.makeText(this, "No nicotine logs found", Toast.LENGTH_SHORT).show();
                        } else {
                            // Display logs in ListView
                            String[] logEntries = new String[logs.size()];
                            for (int i = 0; i < logs.size(); i++) {
                                NicotineLog log = logs.get(i);
                                logEntries[i] = "Type: " + log.getType() + ", Count: " + log.getCount() +
                                        ", Date: " + log.getDate() + ", Notes: " + log.getNotes();
                            }

                            ArrayAdapter<String> adapter = new ArrayAdapter<>(this,
                                    android.R.layout.simple_list_item_1, logEntries);
                            listView.setAdapter(adapter);
                        }
                    } else {
                        Toast.makeText(NicotineTrackerActivity.this, "Error loading logs: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private String getCurrentDate() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        return sdf.format(new Date());
    }

    private void makeCallToHelpline() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CALL_PHONE}, CALL_PERMISSION_CODE);
        } else {
            initiateCall();
        }
    }

    private void initiateCall() {
        Intent callIntent = new Intent(Intent.ACTION_CALL);
        callIntent.setData(android.net.Uri.parse("tel:" + TOBACCO_HELPLINE_NUMBER));
        startActivity(callIntent);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == CALL_PERMISSION_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                initiateCall();
            } else {
                Toast.makeText(this, "Call Permission Denied", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
