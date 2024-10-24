package com.shin.hfapp;

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
import com.shin.hfapp.models.NicotineLog;
import java.util.List;

public class NicotineTrackerActivity extends AppCompatActivity {

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

        databaseHelper = new DatabaseHelper(this);

        // Set up button click listener for adding logs
        buttonAddLog.setOnClickListener(v->{
                insertNicotineLog();

        });

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
        String date = getCurrentDate();  // You can implement a method to get the current date.

        NicotineLog log = new NicotineLog(type, count, date, notes);
        databaseHelper.insertNicotineLog(type, count, date, notes); // Make sure this method is implemented in DatabaseHelper
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
        // Implement a method to return the current date as a String
        // You can use SimpleDateFormat for formatting the date
        java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("yyyy-MM-dd", java.util.Locale.getDefault());
        return sdf.format(new java.util.Date());
    }

}
