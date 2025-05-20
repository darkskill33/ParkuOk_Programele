package com.ismanieji.parkingosystema;

import android.graphics.Color;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.View;
import android.widget.Button;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class PaymentHistoryActivity extends AppCompatActivity {

    private List<PaymentRecord> paymentHistory = new ArrayList<>();
    private Button backButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment_history);
        backButton = findViewById(R.id.back_button);

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        TableLayout tableLayout = findViewById(R.id.history_table);

        loadPaymentHistory();

        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault());

        // Header row
        TableRow headerRow = new TableRow(this);
        addTextToRow(headerRow, "Date/Time", true);
        addTextToRow(headerRow, "Location", true);
        addTextToRow(headerRow, "Payment Method", true);
        addTextToRow(headerRow, "Cost (€)", true);
        addTextToRow(headerRow, "Plates", true);
        tableLayout.addView(headerRow);

        // Data rows
        for (PaymentRecord record : paymentHistory) {
            TableRow row = new TableRow(this);

            String formattedDate = dateFormat.format(record.getTimestamp());
            addTextToRow(row, formattedDate, false);
            addTextToRow(row, record.getLocation(), false);
            addTextToRow(row, record.getMethod(), false);
            addTextToRow(row, String.valueOf(record.getCost()), false);
            addTextToRow(row, record.getCarPlate(), false);
            tableLayout.addView(row);
        }
    }

    private void addTextToRow(TableRow row, String text, boolean isHeader) {
        TextView textView = new TextView(this);
        textView.setText(text);
        textView.setPadding(8, 8, 8, 8);
        textView.setTextColor(Color.BLACK);
        textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);
        if (isHeader) {
            textView.setBackgroundColor(Color.LTGRAY);
            textView.setTypeface(null, android.graphics.Typeface.BOLD);
        } else {
            textView.setBackgroundColor(Color.WHITE);
        }
        row.addView(textView);
    }

    private void loadPaymentHistory() {
        paymentHistory.clear();

        Map<String, ?> allEntries = getSharedPreferences("PaymentHistory", MODE_PRIVATE).getAll();

        for (Map.Entry<String, ?> entry : allEntries.entrySet()) {
            try {
                long timestamp = Long.parseLong(entry.getKey());
                String value = (String) entry.getValue();
                // Format: method,cost,location
                // Format now: method,cost,location,carPlate
                String[] parts = value.split(",", 4);
                if (parts.length == 4) {
                    String method = parts[0];
                    int cost = Integer.parseInt(parts[1]);
                    String location = parts[2];
                    String carPlate = parts[3];
                    paymentHistory.add(new PaymentRecord(method, cost, location, carPlate, timestamp));
                }
            } catch (Exception e) {
                // Ignore malformed entries
                e.printStackTrace();
            }
        }

        // Sort by timestamp descending (most recent first)
        paymentHistory.sort((a, b) -> Long.compare(b.getTimestamp(), a.getTimestamp()));
    }
}
