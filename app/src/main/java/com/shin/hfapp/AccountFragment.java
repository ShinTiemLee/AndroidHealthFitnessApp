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
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

import androidx.fragment.app.Fragment;

import lecho.lib.hellocharts.model.AxisValue;
import lecho.lib.hellocharts.model.Line;
import lecho.lib.hellocharts.model.LineChartData;
import lecho.lib.hellocharts.model.PointValue;
import lecho.lib.hellocharts.view.LineChartView;
import lecho.lib.hellocharts.model.Axis;
import lecho.lib.hellocharts.model.ValueShape;
import com.shin.hfapp.models.BMIRecord;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AccountFragment extends Fragment {

    private LineChartView lineChart;
    private DatabaseHelper databaseHelper;

    public AccountFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_account, container, false);

        // Initialize chart and list view
        lineChart = view.findViewById(R.id.lineChartBMI);
        ListView listViewIntervals = view.findViewById(R.id.listViewIntervals);
        databaseHelper = new DatabaseHelper(requireContext());

        Button btnLogout = view.findViewById(R.id.btnLogout);

        // Set up the logout button
        btnLogout.setOnClickListener(v -> {
            // Clear login session
            SharedPreferences sharedPreferences = requireActivity().getSharedPreferences("UserSession", Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putBoolean("isLoggedIn", false);
            editor.apply();

            // Redirect to login screen
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
                case 0: months = 3; break;
                case 1: months = 6; break;
                case 2: months = 9; break;
                case 3: months = 12; break;
                default: months = 3;
            }
            displayBMIData(months);
        });

        // Default: Display the last 3 months' data initially
        displayBMIData(3);

        return view;
    }

    private void displayBMIData(int months) {
        List<BMIRecord> bmiRecords = databaseHelper.getBMIRecordsForLastMonths(months);
        Log.d("BMIRecords", "Fetched Records: " + bmiRecords.toString());

        // Use a map to group BMI values by date and keep the highest BMI
        HashMap<String, Float> bmiMap = new HashMap<>();
        for (BMIRecord record : bmiRecords) {
            String dateKey = record.getDate();  // Assuming getDate() returns a String representation of the date
            float currentBmi = record.getBmi();

            // Update the map with the highest BMI value for each date
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                bmiMap.put(dateKey, Math.max(currentBmi, bmiMap.getOrDefault(dateKey, 0f)));
            }
        }
        Log.d("BMIAggregation", "Aggregated Data: " + bmiMap.toString());

        // Prepare points for the chart based on the aggregated data
        ArrayList<PointValue> points = new ArrayList<>();
        ArrayList<String> xLabels = new ArrayList<>();  // Store x-axis labels
        int index = 0;

        for (Map.Entry<String, Float> entry : bmiMap.entrySet()) {
            points.add(new PointValue(index, entry.getValue()));  // x = index, y = highest BMI value
            xLabels.add(entry.getKey());  // Add the date to the labels
            index++;
        }
        Log.d("ChartPoints", "Points: " + points.toString());

        // Create a line for the chart
        Line line = new Line(points).setColor(getResources().getColor(R.color.black))
                .setCubic(true)
                .setShape(ValueShape.CIRCLE)
                .setFilled(true)
                .setHasLabels(true);

        // Prepare the chart data
        List<Line> lines = new ArrayList<>();
        lines.add(line);
        LineChartData data = new LineChartData();
        data.setLines(lines);

        // Set axis labels
        Axis axisX = new Axis();
        axisX.setName("Date");  // Set X-axis label
        axisX.setValues(createAxisValues(xLabels));  // Set the X-axis values
        Axis axisY = new Axis();
        axisY.setName("BMI Value");  // Set Y-axis label
        data.setAxisXBottom(axisX);
        data.setAxisYLeft(axisY);

        // Set the chart data and refresh
        lineChart.setLineChartData(data);
        lineChart.invalidate();  // Refresh chart
    }

    private List<AxisValue> createAxisValues(List<String> labels) {
        List<AxisValue> axisValues = new ArrayList<>();
        for (int i = 0; i < labels.size(); i++) {
            axisValues.add(new AxisValue(i).setLabel(labels.get(i)));  // Set label for each date
        }
        return axisValues;
    }

}
