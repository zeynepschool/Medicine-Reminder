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
     *
     * @param savedInstanceState Bundle containing the activity's previously saved state.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Create a scrollable container for the UI
        ScrollView scrollView = new ScrollView(this);

        // Root layout with vertical orientation to hold all UI elements
        LinearLayout rootLayout = new LinearLayout(this);
        rootLayout.setOrientation(LinearLayout.VERTICAL);

        // Set padding around the root layout in pixels (converted from dp)
        int padding = dpToPx(16);
        rootLayout.setPadding(padding, padding, padding, padding);

        // Create a horizontal navigation bar layout with back button
        LinearLayout navBar = new LinearLayout(this);
        navBar.setOrientation(LinearLayout.HORIZONTAL);
        navBar.setGravity(Gravity.START | Gravity.CENTER_VERTICAL);
        navBar.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT));

        // Create a "Back" button to close the activity when clicked
        Button backButton = new Button(this);
        backButton.setText("← Back");
        backButton.setOnClickListener(v -> finish());
        navBar.addView(backButton);

        // Add navigation bar to the root layout
        rootLayout.addView(navBar);

        // Add the root layout inside the scroll view
        scrollView.addView(rootLayout);

        // Layout parameters with margin for UI components
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);
        params.setMargins(0, dpToPx(16), 0, 0);

        // Title TextView for the screen
        TextView title = new TextView(this);
        title.setText("Add Medicine");
        title.setTextSize(24);
        title.setGravity(Gravity.CENTER);
        title.setLayoutParams(params);
        rootLayout.addView(title);

        // Input field for medicine name with max length 20 characters
        EditText medNameInput = new EditText(this);
        medNameInput.setHint("Medicine Name");
        medNameInput.setLayoutParams(params);
        medNameInput.setFilters(new InputFilter[]{new InputFilter.LengthFilter(20)});
        rootLayout.addView(medNameInput);

        // Button to open time picker dialog
        Button timeButton = new Button(this);
        timeButton.setText("Select Time");
        timeButton.setLayoutParams(params);
        rootLayout.addView(timeButton);

        // TextView to display the selected time or default message
        TextView selectedTimeText = new TextView(this);
        selectedTimeText.setText("No time selected");
        selectedTimeText.setLayoutParams(params);
        rootLayout.addView(selectedTimeText);

        // Set OnClickListener to open a TimePickerDialog when "Select Time" button is clicked
        timeButton.setOnClickListener(v -> {
            // Get current time as default values in time picker
            Calendar calendar = Calendar.getInstance();
            int hour = calendar.get(Calendar.HOUR_OF_DAY);
            int minute = calendar.get(Calendar.MINUTE);

            // Create and show time picker dialog
            TimePickerDialog dialog = new TimePickerDialog(this,
                    (view, hourOfDay, minute1) -> {
                        // Convert 24-hour time to 12-hour format with AM/PM
                        int hour12 = hourOfDay % 12;
                        if (hour12 == 0) hour12 = 12;
                        String amPm = (hourOfDay >= 12) ? "PM" : "AM";
                        // Update the TextView to show selected time
                        selectedTimeText.setText(String.format("Time: %02d:%02d %s", hour12, minute1, amPm));
                    }, hour, minute, false);
            dialog.show();
        });

        // Input field for number of times to take medicine per day (numeric input)
        EditText timesPerDayInput = new EditText(this);
        timesPerDayInput.setHint("Times per day (e.g., 3)");
        timesPerDayInput.setInputType(android.text.InputType.TYPE_CLASS_NUMBER);
        timesPerDayInput.setLayoutParams(params);
        timesPerDayInput.setFilters(new InputFilter[]{new InputFilter.LengthFilter(3)});
        rootLayout.addView(timesPerDayInput);

        // Label TextView for day selection section
        TextView daysLabel = new TextView(this);
        daysLabel.setText("Select Days:");
        daysLabel.setLayoutParams(params);
        rootLayout.addView(daysLabel);

        // Array of days for checkboxes
        String[] days = {"Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday", "Sunday"};

        // Create checkboxes for each day of the week and add to layout
        CheckBox[] checkBoxes = new CheckBox[days.length];
        for (int i = 0; i < days.length; i++) {
            checkBoxes[i] = new CheckBox(this);
            checkBoxes[i].setText(days[i]);
            checkBoxes[i].setLayoutParams(params);
            rootLayout.addView(checkBoxes[i]);
        }

        // Button to submit the form and add medicine reminder
        Button submitButton = new Button(this);
        submitButton.setText("Add to List");
        submitButton.setLayoutParams(params);
        rootLayout.addView(submitButton);

        // Handle submit button click event
        submitButton.setOnClickListener(v -> {
            // Get input values from UI components
            String name = medNameInput.getText().toString().trim();
            String time = selectedTimeText.getText().toString();
            String timesPerDay = timesPerDayInput.getText().toString().trim();

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
            if (TextUtils.isEmpty(timesPerDay)) {
                Toast.makeText(this, "Please enter times per day", Toast.LENGTH_SHORT).show();
                return;
            }

            // Build a string of selected days separated by spaces
            StringBuilder selectedDaysBuilder = new StringBuilder();
            for (CheckBox cb : checkBoxes) {
                if (cb.isChecked()) {
                    selectedDaysBuilder.append(cb.getText()).append(" ");
                }
            }

            // Validate at least one day is selected
            if (selectedDaysBuilder.length() == 0) {
                Toast.makeText(this, "Please select at least one day", Toast.LENGTH_SHORT).show();
                return;
            }

            // Combine all information into one string for saving
            String fullEntry = name + " at " + time.replace("Time: ", "") +
                    ", " + timesPerDay + " times/day on " + selectedDaysBuilder.toString().trim();

            // Save the medicine reminder info to internal storage file
            saveMedicineToFile(fullEntry);

            // Show confirmation Toast with entered details
            Toast.makeText(this,
                    "Medicine: " + name + "\n" +
                            time + "\n" +
                            "Times/day: " + timesPerDay + "\n" +
                            "Days: " + selectedDaysBuilder.toString(),
                    Toast.LENGTH_LONG).show();
        });

        // Set the entire scrollable layout as the activity content view
        setContentView(scrollView);
    }

    /**
     * Converts density-independent pixels (dp) to pixels (px) for current device.
     *
     * @param dp The value in dp to convert.
     * @return Equivalent pixel value for device density.
     */
    private int dpToPx(int dp) {
        float density = getResources().getDisplayMetrics().density;
        return Math.round((float) dp * density);
    }

    /**
     * Saves medicine information string to a file named "medicines.txt" in append mode.
     *
     * @param medicineInfo String containing medicine reminder details to save.
     */
    private void saveMedicineToFile(String medicineInfo) {
        try {
            // Open file output stream in append mode
            FileOutputStream fos = openFileOutput("medicines.txt", MODE_APPEND);
            // Write the medicine info followed by a newline
            fos.write((medicineInfo + "\n").getBytes());
            fos.close();
        } catch (IOException e) {
            // Print stack trace if an error occurs during file writing
            e.printStackTrace();
        }
    }
}
