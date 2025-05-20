package com.ismanieji.parkingosystema;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class AddCardActivity extends AppCompatActivity {

    private EditText cardNumberField, expiryField, cvvField;
    private Button saveCardButton;
    private static final String PREFS_NAME = "PaymentPrefs";
    private Button backButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_card);

        cardNumberField = findViewById(R.id.editTextCardNumber);
        expiryField = findViewById(R.id.editTextExpiry);
        cvvField = findViewById(R.id.editTextCVV);
        saveCardButton = findViewById(R.id.buttonSaveCard);

        saveCardButton.setOnClickListener(v -> {
            String cardNumber = cardNumberField.getText().toString().trim();
            String expiry = expiryField.getText().toString().trim();
            String cvv = cvvField.getText().toString().trim();

            if (cardNumber.isEmpty() || expiry.isEmpty() || cvv.isEmpty()) {
                Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show();
            } else {
                SharedPreferences sharedPreferences = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
                sharedPreferences.edit().putBoolean("card_added", true).apply();
                Toast.makeText(this, "Card added successfully!", Toast.LENGTH_SHORT).show();
                finish(); // Return to payment selection screen
            }
        });

        backButton = findViewById(R.id.back_button);
        // Handle back button click
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }
}
