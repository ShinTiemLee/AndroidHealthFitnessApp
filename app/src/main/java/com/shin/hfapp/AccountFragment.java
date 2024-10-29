package com.shin.hfapp;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

import androidx.fragment.app.Fragment;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.shin.hfapp.models.BMIRecord;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import lecho.lib.hellocharts.model.Axis;
import lecho.lib.hellocharts.model.AxisValue;
import lecho.lib.hellocharts.model.Line;
import lecho.lib.hellocharts.model.LineChartData;
import lecho.lib.hellocharts.model.PointValue;
import lecho.lib.hellocharts.model.ValueShape;
import lecho.lib.hellocharts.view.LineChartView;

public class AccountFragment extends Fragment {

    private LineChartView lineChart;
    private FirebaseFirestore db;
    private FirebaseAuth auth;

    private List<BMIRecord> bmiRecords = new ArrayList<>();
    public AccountFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_account, container, false);

        // Initialize chart and Firebase Firestore and Auth
        lineChart = view.findViewById(R.id.lineChartBMI);
        db = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();

        // Other UI initializations
        ListView listViewIntervals = view.findViewById(R.id.listViewIntervals);
        Button btnLogout = view.findViewById(R.id.btnLogout);

        // Set up the logout button
        btnLogout.setOnClickListener(v -> {
            SharedPreferences sharedPreferences = requireActivity().getSharedPreferences("UserSession", Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putBoolean("isLoggedIn", false);
            editor.apply();
            startActivity(new Intent(getActivity(), LoginActivity.class));
            requireActivity().finish();
        });

        // Define time intervals
        String[] intervals = {"3 Months", "6 Months", "9 Months", "12 Months"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_list_item_1, intervals);
        listViewIntervals.setAdapter(adapter);

        // Set up click listener for ListView
        listViewIntervals.setOnItemClickListener((parent, view1, position, id) -> {
            int months;
            switch (position) {

                case 1: months = 6; break;
                case 2: months = 9; break;
                case 3: months = 12; break;
                default: months = 3;
            }
            fetchBMIDataFromFirestore(months);
        });

        // Default: Display the last 3 months' data initially
        fetchBMIDataFromFirestore(3);

        return view;
    }

    private void fetchBMIDataFromFirestore(int months) {
        String userId = auth.getCurrentUser().getUid();
        Date cutoffDate = getStartDateForMonths(months);
        bmiRecords = new ArrayList<>();
        db.collection("bmiRecords").document(userId).collection("userBMIRecords")
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                        BMIRecord record = document.toObject(BMIRecord.class);
                        Date recordDate;
                        Log.d("1111110", "fetchBMIDataFromFirestore: "+record);
                        try {
                            recordDate = new SimpleDateFormat("yyyy-MM-dd").parse(record.getDate());
                        } catch (ParseException e) {
                            Log.e("DateParsing", "Invalid date format", e);
                            continue;
                        }

                        // Compare with cutoff date
                        if (recordDate != null && recordDate.after(cutoffDate)) {
                            bmiRecords.add(record);
                            Log.d("111111100", "fetchBMIDataFromFirestore: "+bmiRecords);
                        }
                    }

                    // Run on main thread to update UI
                    requireActivity().runOnUiThread(() -> displayBMIData(bmiRecords));
                })
                .addOnFailureListener(e -> Log.e("Firestore", "Error fetching BMI data", e));

    }


    // Display and chart the fetched BMI data
    private void displayBMIData(List<BMIRecord> bmiRecords) {
        HashMap<String, Float> bmiMap = new HashMap<>();
        for (BMIRecord record : bmiRecords) {
            String dateKey = record.getDate();
            float currentBmi = record.getBmi();
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                bmiMap.put(dateKey, Math.max(currentBmi, bmiMap.getOrDefault(dateKey, 0f)));
            }
        }

        ArrayList<PointValue> points = new ArrayList<>();
        ArrayList<String> xLabels = new ArrayList<>();
        int index = 0;

        for (Map.Entry<String, Float> entry : bmiMap.entrySet()) {
            points.add(new PointValue(index, entry.getValue()));
            xLabels.add(entry.getKey());
            index++;
        }

        // Create line with increased point size
        Line line = new Line(points).setColor(getResources().getColor(R.color.black))
                .setCubic(true)
                .setShape(ValueShape.CIRCLE) // Change shape as needed
                .setFilled(true)
                .setHasLabels(true)
                .setPointRadius(6); // Increase point size (default is usually 2)

        List<Line> lines = new ArrayList<>();
        lines.add(line);
        LineChartData data = new LineChartData();
        data.setLines(lines);

        Axis axisX = new Axis();
        axisX.setName("Date");
        axisX.setValues(createAxisValues(xLabels));
        Axis axisY = new Axis();
        axisY.setName("BMI Value");

        data.setAxisXBottom(axisX);
        data.setAxisYLeft(axisY);

        lineChart.setLineChartData(data);
        lineChart.invalidate();
    }


    private List<AxisValue> createAxisValues(List<String> labels) {
        List<AxisValue> axisValues = new ArrayList<>();
        for (int i = 0; i < labels.size(); i++) {
            axisValues.add(new AxisValue(i).setLabel(labels.get(i)));
        }
        return axisValues;
    }

    private Date getStartDateForMonths(int months) {
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.MONTH, -months);
        return calendar.getTime();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        // Clear the bmiRecords to free up memory
        bmiRecords.clear();
    }

}
