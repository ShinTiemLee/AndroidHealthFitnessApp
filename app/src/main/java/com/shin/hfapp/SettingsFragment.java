package com.shin.hfapp;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import androidx.appcompat.widget.SwitchCompat;
import android.widget.Toast;

import androidx.appcompat.widget.SwitchCompat;
import androidx.fragment.app.Fragment;

import java.util.Calendar;
import java.util.Objects;

public class SettingsFragment extends Fragment {

    private static final String PREFS_NAME = "waterReminderPrefs";
    private static final String REMINDER_KEY = "waterReminderEnabled";

    private View view;
    private SwitchCompat switchWaterReminder;

    public SettingsFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_settings, container, false);

        DatabaseHelper databaseHelper = new DatabaseHelper(getActivity());


        // Initialize the water reminder switch
        switchWaterReminder = view.findViewById(R.id.switchWaterReminder);
        SharedPreferences prefs = getActivity().getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        boolean isReminderEnabled = prefs.getBoolean(REMINDER_KEY, false);
        switchWaterReminder.setChecked(isReminderEnabled);

        switchWaterReminder.setOnCheckedChangeListener((buttonView, isChecked) -> {
            SharedPreferences.Editor editor = prefs.edit();
            editor.putBoolean(REMINDER_KEY, isChecked);
            editor.apply();

            if (isChecked) {
                scheduleHourlyReminder();
                Log.d("waterReminderOn", "onCreateView: ");
            } else {
                cancelHourlyReminder();
                Log.d("waterReminderOff", "onCreateView: ");
            }
        });

        return view;
    }
    private void scheduleHourlyReminder() {
        AlarmManager alarmManager = (AlarmManager) requireActivity().getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(getActivity(), WaterReminderReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(
                getContext(),
                0,
                intent,
                PendingIntent.FLAG_IMMUTABLE
        );

        // Set the alarm to start now and repeat every 10 seconds for testing
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());

        alarmManager.setRepeating(
                AlarmManager.RTC_WAKEUP,
                calendar.getTimeInMillis(),
                10 * 1000, // 10 seconds for testing
                pendingIntent
        );
    }



    private void cancelHourlyReminder() {
        AlarmManager alarmManager = (AlarmManager) getActivity().getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(getActivity(), WaterReminderReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(
                getContext(),
                0,
                intent,
                PendingIntent.FLAG_IMMUTABLE // Add this flag to comply with Android 12+ requirements
        );

        alarmManager.cancel(pendingIntent);
    }
}
