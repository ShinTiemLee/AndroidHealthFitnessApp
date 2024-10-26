package com.shin.hfapp;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import com.shin.hfapp.models.NicotineLog;
import java.util.List;

public class NicotineTrackerActivity extends AppCompatActivity {

    private static final String TOBACCO_HELPLINE_NUMBER = "1800 112 356"; // Replace with actual helpline number
    private static final int CALL_PERMISSION_CODE = 102;

    private ListView listView;
    private EditText editTextType, editTextCount, editTextNotes;
    private DatabaseHelper databaseHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d("nicotine", "Nicotine");
        setContentView(R.layout.activity_nicotine_tracker);

        listView = findViewById(R.id.listViewNicotineHistory);
        editTextType = findViewById(R.id.editTextType);
        editTextCount = findViewById(R.id.editTextCount);
        editTextNotes = findViewById(R.id.editTextNotes);
        Button buttonAddLog = findViewById(R.id.btnLog);
        Button buttonCallHelpline = findViewById(R.id.buttonCallHelpline);

        databaseHelper = new DatabaseHelper(this);

        // Set up button click listener for adding logs
        buttonAddLog.setOnClickListener(v -> insertNicotineLog());

        // Set up button click listener for calling helpline
        buttonCallHelpline.setOnClickListener(v -> makeCallToHelpline());

        // Retrieve logs and display them
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

        NicotineLog log = new NicotineLog(type, count, date, notes);
        databaseHelper.insertNicotineLog(type, count, date, notes);
        displayNicotineLogs();
    }

    private void displayNicotineLogs() {
        List<NicotineLog> logs = databaseHelper.getAllNicotineLogs();

        if (logs.isEmpty()) {
            Toast.makeText(this, "No nicotine logs found", Toast.LENGTH_SHORT).show();
            return;
        }

        String[] logEntries = new String[logs.size()];
        for (int i = 0; i < logs.size(); i++) {
            NicotineLog log = logs.get(i);
            logEntries[i] = "Type: " + log.getType() + ", Count: " + log.getCount() +
                    ", Date: " + log.getDate() + ", Notes: " + log.getNotes();
        }

        // Use ArrayAdapter to display the logs in the ListView
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this,
                android.R.layout.simple_list_item_1, logEntries);
        listView.setAdapter(adapter);
    }

    private String getCurrentDate() {
        java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("yyyy-MM-dd", java.util.Locale.getDefault());
        return sdf.format(new java.util.Date());
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
