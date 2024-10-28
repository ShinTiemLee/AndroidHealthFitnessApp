package com.shin.hfapp;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

public class DietGuidelinesActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_diet_guidelines); // Set the content view to your updated layout

        // Get references to the TextViews in activity_diet_guidelines.xml
        TextView guidelinesTitle = findViewById(R.id.title);
        TextView guideline1 = findViewById(R.id.guideline1);
        TextView guideline1Link = findViewById(R.id.guideline1_link);
        TextView guideline2 = findViewById(R.id.guideline2);
        TextView guideline2Link = findViewById(R.id.guideline2_link);
        TextView guideline3 = findViewById(R.id.guideline3);
        TextView guideline3Link = findViewById(R.id.guideline3_link);
        TextView guideline4 = findViewById(R.id.guideline4);
        TextView guideline4Link = findViewById(R.id.guideline4_link);
        TextView guideline5 = findViewById(R.id.guideline5);
        TextView guideline5Link = findViewById(R.id.guideline5_link);
        TextView guideline6 = findViewById(R.id.guideline6);
        TextView guideline6Link = findViewById(R.id.guideline6_link);
        TextView guideline7 = findViewById(R.id.guideline7);
        TextView guideline7Link = findViewById(R.id.guideline7_link);
        TextView guideline8 = findViewById(R.id.guideline8);
        TextView guideline8Link = findViewById(R.id.guideline8_link);
        TextView guideline9 = findViewById(R.id.guideline9);
        TextView guideline9Link = findViewById(R.id.guideline9_link);
        TextView guideline10 = findViewById(R.id.guideline10);
        TextView guideline10Link = findViewById(R.id.guideline10_link);

        // Set click listeners to open the URLs in a browser
        guideline1Link.setOnClickListener(v -> openUrl("https://www.healthline.com/nutrition/balanced-diet"));
        guideline2Link.setOnClickListener(v -> openUrl("https://www.healthline.com/nutrition/how-much-water-should-you-drink"));
        guideline3Link.setOnClickListener(v -> openUrl("https://www.healthline.com/nutrition/processed-foods"));
        guideline4Link.setOnClickListener(v -> openUrl("https://www.healthline.com/nutrition/healthy-fats"));
        guideline5Link.setOnClickListener(v -> openUrl("https://www.healthline.com/nutrition/lean-protein"));
        guideline6Link.setOnClickListener(v -> openUrl("https://www.healthline.com/nutrition/small-frequent-meals"));
        guideline7Link.setOnClickListener(v -> openUrl("https://www.healthline.com/nutrition/reading-food-labels"));
        guideline8Link.setOnClickListener(v -> openUrl("https://www.healthline.com/nutrition/mindful-eating"));
        guideline9Link.setOnClickListener(v -> openUrl("https://www.healthline.com/nutrition/healthy-eating-tips"));
        guideline10Link.setOnClickListener(v -> openUrl("https://www.healthline.com/nutrition/consulting-doctor"));

        // Set the title of the guidelines
        guidelinesTitle.setText("Healthy Diet Guidelines");
    }

    // Method to open URL in a browser
    private void openUrl(String url) {
        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
        startActivity(browserIntent);
    }
}
