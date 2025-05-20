package com.ismanieji.parkingosystema;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class PaymentMethodsActivity extends AppCompatActivity {

    private RadioGroup paymentMethodGroup;
    private SharedPreferences sharedPreferences;
    private static final String PREFS_NAME = "PaymentPrefs";
    private static final String KEY_PAYMENT_METHOD = "SelectedPaymentMethod";
    private Button backButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment_methods);

        paymentMethodGroup = findViewById(R.id.payment_method_group);
        sharedPreferences = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);

        String savedMethod = sharedPreferences.getString(KEY_PAYMENT_METHOD, null);
        if (savedMethod != null) {
            selectSavedMethod(savedMethod);
        }

        paymentMethodGroup.setOnCheckedChangeListener((group, checkedId) -> {
            RadioButton selectedButton = findViewById(checkedId);
            String selectedMethod = selectedButton.getText().toString();

            if (selectedMethod.equals("Payment Card") && !isCardAdded()) {
                Toast.makeText(this, "No card added. Redirecting to add card screen...", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(this, AddCardActivity.class));
                return;
            }

            sharedPreferences.edit().putString(KEY_PAYMENT_METHOD, selectedMethod).apply();
            Toast.makeText(this, "Payment method selected: " + selectedMethod, Toast.LENGTH_SHORT).show();
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

    private void selectSavedMethod(String method) {
        for (int i = 0; i < paymentMethodGroup.getChildCount(); i++) {
            if (paymentMethodGroup.getChildAt(i) instanceof RadioButton) {
                RadioButton rb = (RadioButton) paymentMethodGroup.getChildAt(i);
                if (rb.getText().toString().equals(method)) {
                    rb.setChecked(true);
                    break;
                }
            }
        }
    }

    private boolean isCardAdded() {
        // Mock check - replace with actual logic if you store cards
        return sharedPreferences.getBoolean("card_added", false);
    }

}
