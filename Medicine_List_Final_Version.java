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

    @Override
    protected void onCreate(Bundle saved_instance_state) {
        super.onCreate(saved_instance_state);

        ScrollView scroll_view = new ScrollView(this);

        LinearLayout root_layout = new LinearLayout(this);
        root_layout.setOrientation(LinearLayout.VERTICAL);

        int padding = dp_to_px(16);
        root_layout.setPadding(padding, padding, padding, padding);

        LinearLayout nav_bar = new LinearLayout(this);
        nav_bar.setOrientation(LinearLayout.HORIZONTAL);
        nav_bar.setGravity(Gravity.START | Gravity.CENTER_VERTICAL);
        nav_bar.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT));

        Button back_button = new Button(this);
        back_button.setText("← Back");
        back_button.setOnClickListener(v -> finish());
        nav_bar.addView(back_button);

        root_layout.addView(nav_bar);

        scroll_view.addView(root_layout);

        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);
        params.setMargins(0, dp_to_px(16), 0, 0);

        TextView title = new TextView(this);
        title.setText("Add Medicine");
        title.setTextSize(24);
        title.setGravity(Gravity.CENTER);
        title.setLayoutParams(params);
        root_layout.addView(title);

        EditText med_name_input = new EditText(this);
        med_name_input.setHint("Medicine Name");
        med_name_input.setLayoutParams(params);
        med_name_input.setFilters(new InputFilter[] { new InputFilter.LengthFilter(20) });
        root_layout.addView(med_name_input);

        Button time_button = new Button(this);
        time_button.setText("Select Time");
        time_button.setLayoutParams(params);
        root_layout.addView(time_button);

        TextView selected_time_text = new TextView(this);
        selected_time_text.setText("No time selected");
        selected_time_text.setLayoutParams(params);
        root_layout.addView(selected_time_text);

        time_button.setOnClickListener(v -> {
            Calendar calendar = Calendar.getInstance();
            int hour = calendar.get(Calendar.HOUR_OF_DAY);
            int minute = calendar.get(Calendar.MINUTE);

            TimePickerDialog dialog = new TimePickerDialog(this,
                    (view, hour_of_day, minute1) -> {
                        int hour12 = hour_of_day % 12;
                        if (hour12 == 0) hour12 = 12;
                        String am_pm = (hour_of_day >= 12) ? "PM" : "AM";
                        selected_time_text.setText(String.format("Time: %02d:%02d %s", hour12, minute1, am_pm));
                    }, hour, minute, false);
            dialog.show();
        });

        EditText times_per_day_input = new EditText(this);
        times_per_day_input.setHint("Times per day (e.g., 3)");
        times_per_day_input.setInputType(android.text.InputType.TYPE_CLASS_NUMBER);
        times_per_day_input.setLayoutParams(params);
        times_per_day_input.setFilters(new InputFilter[] { new InputFilter.LengthFilter(3) });
        root_layout.addView(times_per_day_input);

        TextView days_label = new TextView(this);
        days_label.setText("Select Days:");
        days_label.setLayoutParams(params);
        root_layout.addView(days_label);

        String[] days = {"Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday", "Sunday"};
        CheckBox[] checkboxes = new CheckBox[days.length];
        for (int i = 0; i < days.length; i++) {
            checkboxes[i] = new CheckBox(this);
            checkboxes[i].setText(days[i]);
            checkboxes[i].setLayoutParams(params);
            root_layout.addView(checkboxes[i]);
        }

        Button submit_button = new Button(this);
        submit_button.setText("Add to List");
        submit_button.setLayoutParams(params);
        root_layout.addView(submit_button);

        submit_button.setOnClickListener(v -> {
            String name = med_name_input.getText().toString().trim();
            String time = selected_time_text.getText().toString();
            String times_per_day = times_per_day_input.getText().toString().trim();

            if (TextUtils.isEmpty(name)) {
                Toast.makeText(this, "Please enter medicine name", Toast.LENGTH_SHORT).show();
                return;
            }
            if (time.equals("No time selected")) {
                Toast.makeText(this, "Please select a time", Toast.LENGTH_SHORT).show();
                return;
            }
            if (TextUtils.isEmpty(times_per_day)) {
                Toast.makeText(this, "Please enter times per day", Toast.LENGTH_SHORT).show();
                return;
            }

            StringBuilder selected_days_builder = new StringBuilder();
            for (CheckBox cb : checkboxes) {
                if (cb.isChecked()) {
                    selected_days_builder.append(cb.getText()).append(" ");
                }
            }

            if (selected_days_builder.length() == 0) {
                Toast.makeText(this, "Please select at least one day", Toast.LENGTH_SHORT).show();
                return;
            }

            String full_entry = name + " at " + time.replace("Time: ", "") +
                    ", " + times_per_day + " times/day on " + selected_days_builder.toString().trim();

            save_medicine_to_file(full_entry);

            Toast.makeText(this,
                    "Medicine: " + name + "\n" +
                            time + "\n" +
                            "Times/day: " + times_per_day + "\n" +
                            "Days: " + selected_days_builder.toString(),
                    Toast.LENGTH_LONG).show();
        });

        setContentView(scroll_view);
    }

    /**
     * Converts dp units to pixel units based on device density.
     *
     * @param dp The value in density-independent pixels to convert.
     * @return The equivalent pixel value as an integer.
     */
    private int dp_to_px(int dp) {
        float density = getResources().getDisplayMetrics().density;
        return Math.round((float) dp * density);
    }

    /**
     * Saves the given medicine information string to internal file "medicines.txt".
     * Appends new entries to the file.
     *
     * @param medicine_info String containing medicine details to save.
     */
    private void save_medicine_to_file(String medicine_info) {
        try {
            FileOutputStream fos = openFileOutput("medicines.txt", MODE_APPEND);
            fos.write((medicine_info + "\n").getBytes());
            fos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
