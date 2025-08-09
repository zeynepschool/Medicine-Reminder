package com.example.myapplication1;

import android.app.TimePickerDialog;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Gravity;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Calendar;
import android.text.InputFilter;

/**
 * MainActivity2 class
 *
 * This activity allows users to add medicine reminders.
 * Users can input the medicine name, select a time using a TimePickerDialog,
 * specify how many times per day the medicine should be taken,
 * select days of the week for the reminder,
 * and save the data persistently in a text file.
 */
public class MainActivity2 extends AppCompatActivity {

    /**
     * Called when the activity is first created.
     * Sets up the entire user interface programmatically,
     * including inputs, buttons, and event listeners.
     */
    @Override
    protected void onCreate(Bundle saved_instance_state) {
        super.onCreate(saved_instance_state);

        // Create a scrollable container for the UI
        ScrollView scroll_view = new ScrollView(this);

        // Root layout with vertical orientation to hold all UI elements
        LinearLayout root_layout = new LinearLayout(this);
        root_layout.setOrientation(LinearLayout.VERTICAL);

        // Set padding around the root layout in pixels (converted from dp)
        int padding = dp_to_px(16);
        root_layout.setPadding(padding, padding, padding, padding);

        // Create a horizontal navigation bar layout with back button
        LinearLayout nav_bar = new LinearLayout(this);
        nav_bar.setOrientation(LinearLayout.HORIZONTAL);
        nav_bar.setGravity(Gravity.START | Gravity.CENTER_VERTICAL);
        nav_bar.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT));

        // Create a "Back" button to close the activity when clicked
        Button back_button = new Button(this);
        back_button.setText("← Back");
        back_button.setOnClickListener(v -> finish());
        nav_bar.addView(back_button);

        // Add navigation bar to the root layout
        root_layout.addView(nav_bar);

        // Add the root layout inside the scroll view
        scroll_view.addView(root_layout);

        // Layout parameters with margin for UI components
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);
        params.setMargins(0, dp_to_px(16), 0, 0);

        // Title TextView for the screen
        TextView title = new TextView(this);
        title.setText("Add Medicine");
        title.setTextSize(24);
        title.setGravity(Gravity.CENTER);
        title.setLayoutParams(params);
        root_layout.addView(title);

        // Input field for medicine name with max length 20 characters
        EditText med_name_input = new EditText(this);
        med_name_input.setHint("Medicine Name");
        med_name_input.setLayoutParams(params);
        med_name_input.setFilters(new InputFilter[] { new InputFilter.LengthFilter(20) });
        root_layout.addView(med_name_input);

        // Button to open time picker dialog
        Button time_button = new Button(this);
        time_button.setText("Select Time");
        time_button.setLayoutParams(params);
        root_layout.addView(time_button);

        // TextView to display the selected time or default message
        TextView selected_time_text = new TextView(this);
        selected_time_text.setText("No time selected");
        selected_time_text.setLayoutParams(params);
        root_layout.addView(selected_time_text);

        // Set OnClickListener to open a TimePickerDialog when "Select Time" button is clicked
        time_button.setOnClickListener(v -> {
            // Get current time as default values in time picker
            Calendar calendar = Calendar.getInstance();
            int hour = calendar.get(Calendar.HOUR_OF_DAY);
            int minute = calendar.get(Calendar.MINUTE);

            // Create and show time picker dialog
            TimePickerDialog dialog = new TimePickerDialog(this,
                    (view, hour_of_day, minute1) -> {
                        // Convert 24-hour time to 12-hour format with AM/PM
                        int hour12 = hour_of_day % 12;
                        if (hour12 == 0) hour12 = 12;
                        String am_pm = (hour_of_day >= 12) ? "PM" : "AM";
                        // Update the TextView to show selected time
                        selected_time_text.setText(String.format("Time: %02d:%02d %s", hour12, minute1, am_pm));
                    }, hour, minute, false);
            dialog.show();
        });

        // Input field for number of times to take medicine per day (numeric input)
        EditText times_per_day_input = new EditText(this);
        times_per_day_input.setHint("Times per day (e.g., 3)");
        times_per_day_input.setInputType(android.text.InputType.TYPE_CLASS_NUMBER);
        times_per_day_input.setLayoutParams(params);
        times_per_day_input.setFilters(new InputFilter[] { new InputFilter.LengthFilter(3) });
        root_layout.addView(times_per_day_input);

        // Label TextView for day selection section
        TextView days_label = new TextView(this);
        days_label.setText("Select Days:");
        days_label.setLayoutParams(params);
        root_layout.addView(days_label);

        // Array of days for checkboxes
        String[] days = {"Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday", "Sunday"};

        // Create checkboxes for each day of the week and add to layout
        CheckBox[] checkboxes = new CheckBox[days.length];
        for (int i = 0; i < days.length; i++) {
            checkboxes[i] = new CheckBox(this);
            checkboxes[i].setText(days[i]);
            checkboxes[i].setLayoutParams(params);
            root_layout.addView(checkboxes[i]);
        }

        // Button to submit the form and add medicine reminder
        Button submit_button = new Button(this);
        submit_button.setText("Add to List");
        submit_button.setLayoutParams(params);
        root_layout.addView(submit_button);

        // Handle submit button click event
        submit_button.setOnClickListener(v -> {
            // Get input values from UI components
            String name = med_name_input.getText().toString().trim();
            String time = selected_time_text.getText().toString();
            String times_per_day = times_per_day_input.getText().toString().trim();

            // Validate medicine name is not empty
            if (TextUtils.isEmpty(name)) {
                Toast.makeText(this, "Please enter medicine name", Toast.LENGTH_SHORT).show();
                return;
            }
            // Validate a time has been selected
            if (time.equals("No time selected")) {
                Toast.makeText(this, "Please select a time", Toast.LENGTH_SHORT).show();
                return;
            }
            // Validate times per day input is not empty
            if (TextUtils.isEmpty(times_per_day)) {
                Toast.makeText(this, "Please enter times per day", Toast.LENGTH_SHORT).show();
                return;
            }

            // Build a string of selected days separated by spaces
            StringBuilder selected_days_builder = new StringBuilder();
            for (CheckBox cb : checkboxes) {
                if (cb.isChecked()) {
                    selected_days_builder.append(cb.getText()).append(" ");
                }
            }

            // Validate at least one day is selected
            if (selected_days_builder.length() == 0) {
                Toast.makeText(this, "Please select at least one day", Toast.LENGTH_SHORT).show();
                return;
            }

            // Combine all information into one string for saving
            String full_entry = name + " at " + time.replace("Time: ", "") +
                    ", " + times_per_day + " times/day on " + selected_days_builder.toString().trim();

            // Save the medicine reminder info to internal storage file
            save_medicine_to_file(full_entry);

            // Show confirmation Toast with entered details
            Toast.makeText(this,
                    "Medicine: " + name + "\n" +
                            time + "\n" +
                            "Times/day: " + times_per_day + "\n" +
                            "Days: " + selected_days_builder.toString(),
                    Toast.LENGTH_LONG).show();
        });

        // Set the entire scrollable layout as the activity content view
        setContentView(scroll_view);
    }

    /**
     * Converts density-independent pixels (dp) to pixels (px) for current device.
     */
    private int dp_to_px(int dp) {
        float density = getResources().getDisplayMetrics().density;
        return Math.round((float) dp * density);
    }

    /**
     * Saves medicine information string to a file named "medicines.txt" in append mode.
     */
    private void save_medicine_to_file(String medicine_info) {
        try {
            // Open file output stream in append mode
            FileOutputStream fos = openFileOutput("medicines.txt", MODE_APPEND);
            // Write the medicine info followed by a newline
            fos.write((medicine_info + "\n").getBytes());
            fos.close();
        } catch (IOException e) {
            // Print stack trace if an error occurs during file writing
            e.printStackTrace();
        }
    }
}
