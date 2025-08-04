package com.example.myapplication1;

import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.view.ViewGroup;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;

public class MainActivity extends AppCompatActivity {

    private TextView medicineList;        // class field for medicine list TextView
    private LinearLayout contentLayout;   // class field for content layout to add views

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Top-level layout
        CoordinatorLayout coordinatorLayout = new CoordinatorLayout(this);
        coordinatorLayout.setLayoutParams(new CoordinatorLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
        ));

        // Vertical layout inside
        contentLayout = new LinearLayout(this);
        contentLayout.setOrientation(LinearLayout.VERTICAL);
        contentLayout.setPadding(50, 150, 50, 50);
        contentLayout.setGravity(Gravity.TOP);
        contentLayout.setLayoutParams(new CoordinatorLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
        ));

        // Toolbar
        Toolbar toolbar = new Toolbar(this);
        toolbar.setTitle("Medicine Reminder");
        toolbar.setTitleTextColor(0xFFFFFFFF);
        toolbar.setBackgroundColor(0xFF6200EE);
        toolbar.setLayoutParams(new Toolbar.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                dpToPx(56)
        ));

        // Button to go to Add page
        Button goToAddButton = new Button(this);
        goToAddButton.setText("Add Medicine");
        goToAddButton.setLayoutParams(new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
        ));
        goToAddButton.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, MainActivity2.class);
            startActivity(intent);
        });

        // TextView for medicine list
        medicineList = new TextView(this);
        medicineList.setTextSize(20); // Larger font size for better readability
        medicineList.setLineSpacing(1.2f, 1.3f); // More spacing between lines
        medicineList.setPadding(dpToPx(16), dpToPx(16), dpToPx(16), dpToPx(16)); // Padding on all sides
        medicineList.setLayoutParams(new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
        ));


        // Add views
        contentLayout.addView(goToAddButton);
        contentLayout.addView(medicineList);

        coordinatorLayout.addView(toolbar);
        coordinatorLayout.addView(contentLayout);

        setContentView(coordinatorLayout);
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Refresh medicine list every time activity resumes
        medicineList.setText(readMedicinesFromFile());
    }

    // Method to read medicine list from file
    private String readMedicinesFromFile() {
        StringBuilder builder = new StringBuilder();
        try {
            FileInputStream fis = openFileInput("medicines.txt");
            BufferedReader reader = new BufferedReader(new InputStreamReader(fis));
            String line;
            while ((line = reader.readLine()) != null) {
                builder.append("• ").append(line).append("\n");
            }
            reader.close();
        } catch (IOException e) {
            builder.append("No reminders found.");
        }
        return builder.toString();
    }

    // Convert dp to pixels helper
    private int dpToPx(int dp) {
        float scale = getResources().getDisplayMetrics().density;
        return (int)(dp * scale + 0.5f);
    }
}
