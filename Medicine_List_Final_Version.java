package com.example.myapplication1;

import android.app.TimePickerDialog;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Gravity;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Calendar;
import android.text.InputFilter;

public class MainActivity2 extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Root ScrollView with a vertical LinearLayout inside
        ScrollView scrollView = new ScrollView(this);

        LinearLayout rootLayout = new LinearLayout(this);
        rootLayout.setOrientation(LinearLayout.VERTICAL);
        int padding = dpToPx(16);
        rootLayout.setPadding(padding, padding, padding, padding);

        //Back Navigation Bar
        LinearLayout navbar = new LinearLayout(this);
        navbar.setOrientation(LinearLayout.HORIZONTAL);
        navbar.setGravity(Gravity.START | Gravity.CENTER_VERTICAL);
        navbar.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT));

        Button backButton = new Button(this);
        backButton.setText("← Back");
        backButton.setOnClickListener(v -> finish());  // Finish this activity
        navbar.addView(backButton);

        // Add navbar to the top of the layout
        rootLayout.addView(navbar);

        scrollView.addView(rootLayout);

        // Layout with margin
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);
        params.setMargins(0, dpToPx(16), 0, 0);

        // Title TextView
        TextView title = new TextView(this);
        title.setText("Add Medicine");
        title.setTextSize(24);
        title.setGravity(Gravity.CENTER);
        title.setLayoutParams(params);
        rootLayout.addView(title);

        // Medicine Name input
        EditText medNameInput = new EditText(this);
        medNameInput.setHint("Medicine Name");
        medNameInput.setLayoutParams(params);
        rootLayout.addView(medNameInput);

        // Limit input to 20 characters
        medNameInput.setFilters(new InputFilter[] { new InputFilter.LengthFilter(20) });

        // Time Picker button + display text
        Button timeButton = new Button(this);
        timeButton.setText("Select Time");
        timeButton.setLayoutParams(params);
        rootLayout.addView(timeButton);

        TextView selectedTimeText = new TextView(this);
        selectedTimeText.setText("No time selected");
        selectedTimeText.setLayoutParams(params);
        rootLayout.addView(selectedTimeText);

        timeButton.setOnClickListener(v -> {
            Calendar calendar = Calendar.getInstance();
            int hour = calendar.get(Calendar.HOUR_OF_DAY);
            int minute = calendar.get(Calendar.MINUTE);

            TimePickerDialog dialog = new TimePickerDialog(this,
                    (view, hourOfDay, minute1) -> {
                        int hour12 = hourOfDay % 12;
                        if (hour12 == 0) hour12 = 12;
                        String amPm = (hourOfDay >= 12) ? "PM" : "AM";
                        selectedTimeText.setText(String.format("Time: %02d:%02d %s", hour12, minute1, amPm));
                    }, hour, minute, false);
            dialog.show();
        });

        // Times per day input
        EditText timesPerDayInput = new EditText(this);
        timesPerDayInput.setHint("Times per day (e.g., 3)");
        timesPerDayInput.setInputType(android.text.InputType.TYPE_CLASS_NUMBER);
        timesPerDayInput.setLayoutParams(params);

        // Limit to max 3 digits
        timesPerDayInput.setFilters(new InputFilter[] { new InputFilter.LengthFilter(3) });

        // Days of week label
        TextView daysLabel = new TextView(this);
        daysLabel.setText("Select Days:");
        daysLabel.setLayoutParams(params);
        rootLayout.addView(daysLabel);

        // Days checkboxes
        String[] days = {"Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday", "Sunday"};
        CheckBox[] checkboxes = new CheckBox[days.length];
        for (int i = 0; i < days.length; i++) {
            checkboxes[i] = new CheckBox(this);
            checkboxes[i].setText(days[i]);
            checkboxes[i].setLayoutParams(params);
            rootLayout.addView(checkboxes[i]);
        }

        // Submit button
        Button submitButton = new Button(this);
        submitButton.setText("Add to List");
        submitButton.setLayoutParams(params);
        rootLayout.addView(submitButton);

        submitButton.setOnClickListener(v -> {
            String name = medNameInput.getText().toString().trim();
            String time = selectedTimeText.getText().toString();
            String timesPerDay = timesPerDayInput.getText().toString().trim();

            if (TextUtils.isEmpty(name)) {
                Toast.makeText(this, "Please enter medicine name", Toast.LENGTH_SHORT).show();
                return;
            }
            if (time.equals("No time selected")) {
                Toast.makeText(this, "Please select a time", Toast.LENGTH_SHORT).show();
                return;
            }
            if (TextUtils.isEmpty(timesPerDay)) {
                Toast.makeText(this, "Please enter times per day", Toast.LENGTH_SHORT).show();
                return;
            }

            StringBuilder selectedDaysBuilder = new StringBuilder();
            for (CheckBox cb : checkboxes) {
                if (cb.isChecked()) {
                    selectedDaysBuilder.append(cb.getText()).append(" ");
                }
            }

            if (selectedDaysBuilder.length() == 0) {
                Toast.makeText(this, "Please select at least one day", Toast.LENGTH_SHORT).show();
                return;
            }

            String fullEntry = name + " at " + time.replace("Time: ", "") +
                    ", " + timesPerDay + " times/day on " + selectedDaysBuilder.toString().trim();

            // Save to file
            saveMedicineToFile(fullEntry);

            Toast.makeText(this,
                    "Medicine: " + name + "\n" +
                            time + "\n" +
                            "Times/day: " + timesPerDay + "\n" +
                            "Days: " + selectedDaysBuilder.toString(),
                    Toast.LENGTH_LONG).show();
        });

        setContentView(scrollView);
    }


    private int dpToPx(int dp) {
        float density = getResources().getDisplayMetrics().density;
        return Math.round((float) dp * density);
    }

    // Save medicine info to internal storage file
    private void saveMedicineToFile(String medicineInfo) {
        try {
            FileOutputStream fos = openFileOutput("medicines.txt", MODE_APPEND);
            fos.write((medicineInfo + "\n").getBytes());
            fos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
