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

/**
 * MainActivity class.
 *
 * This activity serves as the main screen of the Medicine Reminder app.
 * It shows a toolbar with the app title, a button to navigate to the add medicine screen,
 * and a text view displaying the list of saved medicine reminders.
 */
public class MainActivity extends AppCompatActivity {

    // Constants for padding in dp units
    private static final int PADDING_LEFT_DP = 20;
    private static final int PADDING_TOP_DP = 70;
    private static final int PADDING_RIGHT_DP = 20;
    private static final int PADDING_BOTTOM_DP = 20;

    // Constant for toolbar height in dp
    private static final int TOOLBAR_HEIGHT_DP = 56;

    // Constants for colors (ARGB format)
    private static final int COLOR_TOOLBAR_BG = 0xFF888888;     // Grey background color
    private static final int COLOR_TOOLBAR_TITLE = 0xFFFFFFFF;  // White title text color

    // Constant for medicine list padding in dp
    private static final int MEDICINE_LIST_PADDING_DP = 16;

    private TextView medicine_list;        // TextView to display saved medicines
    private LinearLayout content_layout;   // Layout container for main content views

    /**
     * Called when the activity is starting.
     * Sets up UI components including toolbar, button, and medicine list.
     *
     * @param saved_instance_state Saved state bundle (if any).
     */
    @Override
    protected void onCreate(Bundle saved_instance_state) {
        super.onCreate(saved_instance_state);

        // Create the root CoordinatorLayout for overall screen layout
        CoordinatorLayout coordinator_layout = new CoordinatorLayout(this);
        coordinator_layout.setLayoutParams(new CoordinatorLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
        ));

        // Create vertical LinearLayout for stacking UI elements vertically
        content_layout = new LinearLayout(this);
        content_layout.setOrientation(LinearLayout.VERTICAL);
        content_layout.setPadding(
                dp_to_px(PADDING_LEFT_DP), dp_to_px(PADDING_TOP_DP),
                dp_to_px(PADDING_RIGHT_DP), dp_to_px(PADDING_BOTTOM_DP)
        );
        content_layout.setGravity(Gravity.TOP);
        content_layout.setLayoutParams(new CoordinatorLayout.LayoutParams(
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
                dp_to_px(TOOLBAR_HEIGHT_DP)
        ));

        // Create button to navigate to Add Medicine screen
        Button go_to_add_button = new Button(this);
        go_to_add_button.setText("Add Medicine");
        go_to_add_button.setLayoutParams(new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
        ));
        // Set click listener to start MainActivity2 for adding medicines
        go_to_add_button.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, MainActivity2.class);
            startActivity(intent);
        });

        // Create TextView to display list of medicines
        medicine_list = new TextView(this);
        medicine_list.setTextSize(20); // Increase font size for readability
        medicine_list.setLineSpacing(1.2f, 1.3f); // Adjust line spacing
        // Add padding around text content using constant values
        medicine_list.setPadding(
                dp_to_px(MEDICINE_LIST_PADDING_DP),
                dp_to_px(MEDICINE_LIST_PADDING_DP),
                dp_to_px(MEDICINE_LIST_PADDING_DP),
                dp_to_px(MEDICINE_LIST_PADDING_DP)
        );
        medicine_list.setLayoutParams(new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
        ));

        // Add button and medicine list TextView to content layout
        content_layout.addView(go_to_add_button);
        content_layout.addView(medicine_list);

        // Add toolbar and content layout to the root CoordinatorLayout
        coordinator_layout.addView(toolbar);
        coordinator_layout.addView(content_layout);

        // Set the CoordinatorLayout as the activity content view
        setContentView(coordinator_layout);
    }

    /**
     * Called when the activity resumes.
     * Refreshes the medicine list display by reading saved medicines from file.
     */
    @Override
    protected void onResume() {
        super.onResume();
        medicine_list.setText(read_medicines_from_file());
    }

    /**
     * Reads saved medicine reminders from internal file "medicines.txt".
     * Each line is prefixed with a bullet point for readability.
     * Returns a default message if no reminders found or file cannot be read.
     *
     * @return String containing formatted medicine list or a default message.
     */
    private String read_medicines_from_file() {
        StringBuilder builder = new StringBuilder();
        try {
            // Open file input stream to read medicines.txt
            FileInputStream fis = openFileInput("medicines.txt");
            BufferedReader reader = new BufferedReader(new InputStreamReader(fis));
            String line;
            // Read each line and append to builder with bullet point
            while ((line = reader.readLine()) != null) {
                builder.append("• ").append(line).append("\n");
            }
            reader.close();
        } catch (IOException e) {
            // If reading fails, show default message
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
    private int dp_to_px(int dp) {
        float scale = getResources().getDisplayMetrics().density;
        return (int)(dp * scale + 0.5f);
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
