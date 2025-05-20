package com.ismanieji.parkingosystema;


import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.InputType;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import java.util.ArrayList;
import java.util.Arrays;

public class CarPlatesActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private ListView platesListView;
    private Button addPlateButton, backButton;

    private ArrayList<String> platesList;
    private ArrayAdapter<String> adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_car_plates);

        toolbar = findViewById(R.id.toolbar_car_plates);
        platesListView = findViewById(R.id.plates_list_view);
        addPlateButton = findViewById(R.id.add_plate_button);
        backButton = findViewById(R.id.back_button);
        backButton.setOnClickListener(v -> finish());

        setSupportActionBar(toolbar);
        // Enable back button
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        // Load saved plates (from SharedPreferences or start empty)
        platesList = loadSavedPlates();

        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_multiple_choice, platesList);
        platesListView.setAdapter(adapter);
        platesListView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);

        // Restore checked states if saved (optional)
        restoreCheckedPlates();

        // Add new plate button click
        addPlateButton.setOnClickListener(v -> showAddPlateDialog());
    }

    // Back button behavior (toolbar)
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish(); // close activity and go back
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private ArrayList<String> loadSavedPlates() {
        SharedPreferences prefs = getSharedPreferences("user_prefs", MODE_PRIVATE);
        String platesString = prefs.getString("car_plates", "");
        ArrayList<String> list = new ArrayList<>();
        if (!platesString.isEmpty()) {
            String[] platesArray = platesString.split(",");
            list.addAll(Arrays.asList(platesArray));
        }
        return list;
    }

    private void savePlates() {
        SharedPreferences prefs = getSharedPreferences("user_prefs", MODE_PRIVATE);
        StringBuilder sb = new StringBuilder();
        for (String plate : platesList) {
            sb.append(plate).append(",");
        }
        if (sb.length() > 0) {
            sb.deleteCharAt(sb.length() - 1); // remove last comma
        }
        prefs.edit().putString("car_plates", sb.toString()).apply();
    }

    private void restoreCheckedPlates() {
        // If you want to restore checked state (optional)
        // For example, you could save selected plates separately
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
                adapter.notifyDataSetChanged();
                savePlates();
            } else {
                Toast.makeText(this, "Plate is empty or already exists", Toast.LENGTH_SHORT).show();
            }
        });

        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.cancel());

        builder.show();
    }

    @Override
    protected void onPause() {
        super.onPause();
        savePlates();
    }
}
