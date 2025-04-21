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
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.ArrayList;
import java.util.concurrent.BlockingQueue;

public class ReservationHistoryActivity extends AppCompatActivity {

    public static List<Reservation> history = new ArrayList<>();
    private Button backButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reservation_history);

        backButton = findViewById(R.id.back_button);
        backButton.setOnClickListener(v -> finish());

        TableLayout tableLayout = findViewById(R.id.history_table);

        // Date formatter
        SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault());

        // Populate rows
        for (Reservation res : history) {
            TableRow row = new TableRow(this);
            row.setBackgroundColor(0xFFFFFFFF);

            String locationName = res.getLocationName();
            String[] words = locationName.split(" ");
            String finalLocationName = "";

            for(int i = 0; i < words.length; i++){
                if(words[i].length() <= 20 && (finalLocationName + " " + words[i]).length() <= 20){
                    finalLocationName += " " + words[i];
                } else {
                    finalLocationName += '\n' + words[i];
                }
            }

            TextView locationView = new TextView(this);
            locationView.setText(finalLocationName);
            locationView.setPadding(8, 8, 8, 8);
            locationView.setBackgroundColor(0xFFFFFFFF);
            locationView.setTextColor(Color.BLACK);
            locationView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);

            long startTime = res.getStartTime();
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
            SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm", Locale.getDefault());

            Date startDate = new Date(startTime);
            String date = dateFormat.format(startDate);
            String time = timeFormat.format(startDate);

            String formattedDateTime = date + "\n" + time;

            TextView startView = new TextView(this);
            startView.setText(formattedDateTime);
            startView.setPadding(8, 8, 8, 8);
            startView.setBackgroundColor(0xFFFFFFFF);
            startView.setTextColor(Color.BLACK);
            startView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);


            long endTime = res.getEndTime();
            String formattedDateTimeEnd = "";
            if(endTime != 0){
                Date endDate = new Date(endTime);
                String date1 = dateFormat.format(endDate);
                String time1 = timeFormat.format(endDate);
                formattedDateTimeEnd = date1 + "\n" + time1;
            }

            TextView endView = new TextView(this);
            endView.setText(formattedDateTimeEnd != "" ?
                    formattedDateTimeEnd : "Active");
            endView.setPadding(8, 8, 8, 8);
            endView.setBackgroundColor(0xFFFFFFFF);
            endView.setTextColor(Color.BLACK);
            endView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);

            row.addView(locationView);
            row.addView(startView);
            row.addView(endView);

            tableLayout.addView(row);
        }
    }
}
