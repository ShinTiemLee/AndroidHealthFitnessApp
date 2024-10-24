package com.shin.hfapp;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

public class NutritionVideosActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nutrition_videos); // Set the content view to the layout file

        // Get references to the TextViews in activity_nutrition_videos.xml
        TextView video1Link = findViewById(R.id.video1_link);
        TextView video2Link = findViewById(R.id.video2_link);
        TextView video3Link = findViewById(R.id.video3_link);
        TextView video4Link = findViewById(R.id.video4_link);
        TextView video5Link = findViewById(R.id.video5_link);
        TextView video6Link = findViewById(R.id.video6_link);
        TextView video7Link = findViewById(R.id.video7_link);
        TextView video8Link = findViewById(R.id.video8_link);
        TextView video9Link = findViewById(R.id.video9_link);
        TextView video10Link = findViewById(R.id.video10_link);

        // Set click listeners to open the video URLs in a browser
        video1Link.setOnClickListener(v -> openUrl("https://www.youtube.com/watch?v=video1"));
        video2Link.setOnClickListener(v -> openUrl("https://www.youtube.com/watch?v=video2"));
        video3Link.setOnClickListener(v -> openUrl("https://www.youtube.com/watch?v=video3"));
        video4Link.setOnClickListener(v -> openUrl("https://www.youtube.com/watch?v=video4"));
        video5Link.setOnClickListener(v -> openUrl("https://www.youtube.com/watch?v=video5"));
        video6Link.setOnClickListener(v -> openUrl("https://www.youtube.com/watch?v=video6"));
        video7Link.setOnClickListener(v -> openUrl("https://www.youtube.com/watch?v=video7"));
        video8Link.setOnClickListener(v -> openUrl("https://www.youtube.com/watch?v=video8"));
        video9Link.setOnClickListener(v -> openUrl("https://www.youtube.com/watch?v=video9"));
        video10Link.setOnClickListener(v -> openUrl("https://www.youtube.com/watch?v=video10"));
    }

    // Method to open URL in a browser
    private void openUrl(String url) {
        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
        startActivity(browserIntent);
    }
}
