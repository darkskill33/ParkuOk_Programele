package com.ismanieji.parkingosystema;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.InputType;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import java.util.ArrayList;
import java.util.Arrays;

public class CarPlatesActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private RadioGroup plateRadioGroup;
    private Button addPlateButton;
    private ArrayList<String> platesList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_car_plates);

        toolbar = findViewById(R.id.toolbar_car_plates);
        plateRadioGroup = findViewById(R.id.plate_radio_group);
        addPlateButton = findViewById(R.id.add_plate_button);

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        platesList = loadSavedPlates();
        refreshPlateRadioButtons();

        addPlateButton.setOnClickListener(v -> showAddPlateDialog());

        plateRadioGroup.setOnCheckedChangeListener((group, checkedId) -> {
            RadioButton selected = findViewById(checkedId);
            if (selected != null) {
                saveSelectedPlate(selected.getText().toString());
                Toast.makeText(this, "Selected: " + selected.getText(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    protected void onPause() {
        super.onPause();
        savePlates();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish(); // Toolbar back button
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private ArrayList<String> loadSavedPlates() {
        SharedPreferences prefs = getSharedPreferences("user_prefs", MODE_PRIVATE);
        String platesString = prefs.getString("car_plates", "");
        ArrayList<String> list = new ArrayList<>();
        if (!platesString.isEmpty()) {
            list.addAll(Arrays.asList(platesString.split(",")));
        }
        return list;
    }

    private void savePlates() {
        SharedPreferences prefs = getSharedPreferences("user_prefs", MODE_PRIVATE);
        StringBuilder sb = new StringBuilder();
        for (String plate : platesList) {
            sb.append(plate).append(",");
        }
        if (!platesList.isEmpty()) {
            sb.deleteCharAt(sb.length() - 1);
        }
        prefs.edit().putString("car_plates", sb.toString()).apply();
    }

    private void saveSelectedPlate(String plate) {
        SharedPreferences prefs = getSharedPreferences("user_prefs", MODE_PRIVATE);
        prefs.edit().putString("selected_plate", plate).apply();
    }

    private void refreshPlateRadioButtons() {
        plateRadioGroup.removeAllViews();
        for (int i = 0; i < platesList.size(); i++) {
            String plate = platesList.get(i);
            RadioButton radioButton = new RadioButton(this);
            radioButton.setText(plate);
            radioButton.setId(View.generateViewId());

            // Long press to delete
            radioButton.setOnLongClickListener(v -> {
                new AlertDialog.Builder(CarPlatesActivity.this)
                        .setTitle("Remove Plate")
                        .setMessage("Do you want to remove " + plate + "?")
                        .setPositiveButton("Yes", (dialog, which) -> {
                            platesList.remove(plate);
                            refreshPlateRadioButtons();
                            savePlates();
                        })
                        .setNegativeButton("No", null)
                        .show();
                return true;
            });

            plateRadioGroup.addView(radioButton);
        }

        // Restore previously selected plate (optional)
        SharedPreferences prefs = getSharedPreferences("user_prefs", MODE_PRIVATE);
        String selected = prefs.getString("selected_plate", null);
        if (selected != null) {
            for (int i = 0; i < plateRadioGroup.getChildCount(); i++) {
                RadioButton rb = (RadioButton) plateRadioGroup.getChildAt(i);
                if (rb.getText().toString().equals(selected)) {
                    rb.setChecked(true);
                    break;
                }
            }
        }
    }

    private void showAddPlateDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Add Car Plate");

        final EditText input = new EditText(this);
        input.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_CAP_CHARACTERS);
        builder.setView(input);

        builder.setPositiveButton("Add", (dialog, which) -> {
            String newPlate = input.getText().toString().trim();
            if (!newPlate.isEmpty() && !platesList.contains(newPlate)) {
                platesList.add(newPlate);
                refreshPlateRadioButtons();
                savePlates();
            } else {
                Toast.makeText(this, "Plate is empty or already exists", Toast.LENGTH_SHORT).show();
            }
        });

        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.cancel());

        builder.show();
    }
}
