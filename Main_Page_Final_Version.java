package com.example.myapplication1;

import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.coordinatorlayout.widget.CoordinatorLayout;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 * MainActivity class.
 *
 * This activity serves as the main screen of the Medicine Reminder app.
 * It shows a toolbar with the app title, a button to navigate to the add medicine screen,
 * and a text view displaying the list of saved medicine reminders.
 */
public class MainActivity extends AppCompatActivity {

    // Constants for padding in dp units
    private static final int PADDING_LEFT_DP = 25;
    private static final int PADDING_TOP_DP = 70;
    private static final int PADDING_RIGHT_DP = 25;
    private static final int PADDING_BOTTOM_DP = 25;

    // Constant for toolbar height in dp
    private static final int TOOLBAR_HEIGHT_DP = 56;

    // Constants for colors (ARGB format)
    private static final int COLOR_TOOLBAR_BG = 0xFF888888;     // Grey background color
    private static final int COLOR_TOOLBAR_TITLE = 0xFFFFFFFF;  // White title text color

    // Constant for medicine list padding in dp
    private static final int MEDICINE_LIST_PADDING_DP = 16;

    private TextView medicineList;       // TextView to display saved medicines
    private LinearLayout contentLayout;  // Layout container for main content views

    /**
     * Called when the activity is starting.
     * Sets up UI components including toolbar, button, and medicine list.
     *
     * @param savedInstanceState Saved state bundle (if any).
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Create the root CoordinatorLayout for overall screen layout
        CoordinatorLayout coordinatorLayout = new CoordinatorLayout(this);
        coordinatorLayout.setLayoutParams(new CoordinatorLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
        ));

        // Create vertical LinearLayout for stacking UI elements vertically
        contentLayout = new LinearLayout(this);
        contentLayout.setOrientation(LinearLayout.VERTICAL);
        contentLayout.setPadding(
                dpToPx(PADDING_LEFT_DP), dpToPx(PADDING_TOP_DP),
                dpToPx(PADDING_RIGHT_DP), dpToPx(PADDING_BOTTOM_DP)
        );
        contentLayout.setGravity(Gravity.TOP);
        contentLayout.setLayoutParams(new CoordinatorLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
        ));

        // Create toolbar with title and styling
        Toolbar toolbar = new Toolbar(this);
        toolbar.setTitle("Medicine Reminder");
        toolbar.setTitleTextColor(COLOR_TOOLBAR_TITLE);  // Use constant for title color
        toolbar.setBackgroundColor(COLOR_TOOLBAR_BG);    // Use constant for toolbar background
        toolbar.setLayoutParams(new Toolbar.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                dpToPx(TOOLBAR_HEIGHT_DP)
        ));

        // Create button to navigate to Add Medicine screen
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

        // Create TextView to display list of medicines
        medicineList = new TextView(this);
        medicineList.setTextSize(20); // Increase font size for readability
        medicineList.setLineSpacing(1.2f, 1.3f); // Adjust line spacing
        medicineList.setPadding(
                dpToPx(MEDICINE_LIST_PADDING_DP),
                dpToPx(MEDICINE_LIST_PADDING_DP),
                dpToPx(MEDICINE_LIST_PADDING_DP),
                dpToPx(MEDICINE_LIST_PADDING_DP)
        );
        medicineList.setLayoutParams(new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
        ));

        // Add button and medicine list TextView to content layout
        contentLayout.addView(goToAddButton);
        contentLayout.addView(medicineList);

        // Add toolbar and content layout to the root CoordinatorLayout
        coordinatorLayout.addView(toolbar);
        coordinatorLayout.addView(contentLayout);

        // Set the CoordinatorLayout as the activity content view
        setContentView(coordinatorLayout);
    }

    /**
     * Called when the activity resumes.
     * Refreshes the medicine list display by reading saved medicines from file.
     */
    @Override
    protected void onResume() {
        super.onResume();
        medicineList.setText(readMedicinesFromFile());
    }

    /**
     * Reads saved medicine reminders from internal file "medicines.txt".
     * Each line is prefixed with a bullet point for readability.
     * Returns a default message if no reminders found or file cannot be read.
     *
     * @return String containing formatted medicine list or a default message.
     */
    private String readMedicinesFromFile() {
        StringBuilder builder = new StringBuilder();
        try (FileInputStream fis = openFileInput("medicines.txt");
             BufferedReader reader = new BufferedReader(new InputStreamReader(fis))) {
            String line;
            while ((line = reader.readLine()) != null) {
                builder.append("• ").append(line).append("\n");
            }
        } catch (IOException e) {
            builder.append("No reminders found.");
        }
        return builder.toString();
    }

    /**
     * Converts density-independent pixels (dp) to pixels (px) based on device density.
     *
     * @param dp Value in dp units.
     * @return Corresponding value in pixels.
     */
    private int dpToPx(int dp) {
        float scale = getResources().getDisplayMetrics().density;
        return (int) (dp * scale + 0.5f);
    }
}

/*
 * Module Description:
 *
 * This MainActivity class serves as the main screen for the Medicine Reminder app.
 * It displays a toolbar with the title, a button to navigate to the Add Medicine screen,
 * and a scrollable list of saved medicine reminders retrieved from internal storage.
 *
 * When the activity resumes, it refreshes the displayed medicine list by reading from
 * the "medicines.txt" file stored internally. The medicine entries are displayed as
 * bullet points for easy reading.
 *
 * The UI elements are created programmatically using Android layouts and views,
 * including CoordinatorLayout for overall layout, LinearLayout for content stacking,
 * Toolbar for app title, Buttons for navigation, and TextView for showing the medicine list.
 *
 * Utility methods handle converting dp units to pixels for consistent UI scaling
 * and reading the medicine list from the file with appropriate exception handling.
 */
