package com.ismanieji.parkingosystema;

import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.*;
import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import com.google.android.material.navigation.NavigationView;
import org.osmdroid.config.Configuration;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.Marker;
import android.preference.PreferenceManager;
import java.util.*;
import android.os.Handler;
import android.os.Looper;

public class MainUserActivity extends AppCompatActivity {

    private DrawerLayout drawerLayout;
    private MapView mapView;
    private EditText searchBar;
    private ImageButton searchButton;
    private Button reserveButton;
    private TextView activeReminder;
    private ImageButton favoritesButton, plateButton;
    private ArrayList<Marker> parkingMarkers = new ArrayList<>();
    private List<Reservation> reservationHistory = new ArrayList<>();
    private Reservation activeReservation = null;
    private Handler timerHandler = new Handler(Looper.getMainLooper());
    private Runnable timerRunnable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_user);

        Context ctx = getApplicationContext();
        Configuration.getInstance().setUserAgentValue(getPackageName());
        Configuration.getInstance().load(ctx, PreferenceManager.getDefaultSharedPreferences(ctx));

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        drawerLayout = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar,
                R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(item -> {
            handleNavigationItem(item);
            return true;
        });

        mapView = findViewById(R.id.mapView);
        mapView.setTileSource(TileSourceFactory.MAPNIK);
        mapView.setBuiltInZoomControls(true);
        mapView.setMultiTouchControls(true);

        GeoPoint startPoint = new GeoPoint(54.6872, 25.2797);
        mapView.getController().setZoom(14);
        mapView.getController().setCenter(startPoint);

        searchBar = findViewById(R.id.search_bar);
        searchButton = findViewById(R.id.search_button);
        activeReminder = findViewById(R.id.active_reservation_reminder);
        favoritesButton = findViewById(R.id.favorites_button);

        searchButton.setOnClickListener(v -> {
            if (searchBar.getVisibility() == View.GONE) {
                searchBar.setVisibility(View.VISIBLE);
                searchBar.requestFocus();
            } else {
                searchBar.setVisibility(View.GONE);
            }
        });

        searchBar.addTextChangedListener(new android.text.TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) {
                filterMarkers(s.toString());
            }
            @Override public void afterTextChanged(android.text.Editable s) {}
        });

        favoritesButton.setOnClickListener(v -> {
            if (favoritesButton.isSelected()) {
                favoritesButton.setSelected(false);
                favoritesButton.setBackgroundTintList(ColorStateList.valueOf(
                        ContextCompat.getColor(this, R.color.black)));
                favoritesButton.setColorFilter(ContextCompat.getColor(this, R.color.black), PorterDuff.Mode.SRC_IN);
                showAllMarkers();
            } else {
                if (activeReservation != null) {
                    favoritesButton.setSelected(true);
                    favoritesButton.setBackgroundTintList(ColorStateList.valueOf(
                            ContextCompat.getColor(this, R.color.teal_700)));
                    favoritesButton.setColorFilter(ContextCompat.getColor(this, R.color.gray), PorterDuff.Mode.SRC_IN);
                    filterToActiveReservation();
                } else {
                    Toast.makeText(this, "No active reservation to show.", Toast.LENGTH_SHORT).show();
                }
            }
        });


        showPredefinedParkingSpots();

        if (activeReservation != null) {
            activeReminder.setVisibility(View.VISIBLE);
            startReservationTimer();
            Toast.makeText(this, "Reminder: Active reservation at " + activeReservation.getLocationName(), Toast.LENGTH_LONG).show();
        }

        plateButton = findViewById(R.id.plate_button);
        plateButton.setOnClickListener(v -> {
            Intent intent = new Intent(this, CarPlatesActivity.class);
            startActivity(intent);
        });

    }

    private void handleNavigationItem(@NonNull MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.nav_my_account) {
            startActivity(new Intent(this, ProfileActivity.class));
        } else if (id == R.id.nav_payment_methods) {
            startActivity(new Intent(this, PaymentMethodsActivity.class));
        } else if (id == R.id.nav_payment_history) {
            startActivity(new Intent(this, PaymentHistoryActivity.class));
        } else if (id == R.id.nav_logout) {
            Toast.makeText(this, "Logged Out", Toast.LENGTH_SHORT).show();
            finish();
        } else if (id == R.id.nav_reservation_history) {
            ReservationHistoryActivity.history = new ArrayList<>(reservationHistory);
            startActivity(new Intent(this, ReservationHistoryActivity.class));
        }

        drawerLayout.closeDrawer(GravityCompat.START);
    }

    private void showPredefinedParkingSpots() {
        List<ParkingLocation> parkingLocations = new ArrayList<>();
        parkingLocations.add(new ParkingLocation("City Center Parking", 54.6892, 25.2798));
        parkingLocations.add(new ParkingLocation("Green Street Lot", 54.6905, 25.2850));
        parkingLocations.add(new ParkingLocation("Old Town Garage", 54.6860, 25.2740));
        parkingLocations.add(new ParkingLocation("Cathedral Square Parking", 54.6865, 25.2872));
        parkingLocations.add(new ParkingLocation("Business District Garage", 54.6920, 25.2765));
        parkingLocations.add(new ParkingLocation("Riverside Parking Lot", 54.6843, 25.2810));
        parkingLocations.add(new ParkingLocation("University Campus Parking", 54.6898, 25.2716));
        parkingLocations.add(new ParkingLocation("Panorama Shopping Parking", 54.7001, 25.2623));
        parkingLocations.add(new ParkingLocation("Railway Station Lot", 54.6741, 25.2857));
        parkingLocations.add(new ParkingLocation("Vingis Park Entrance", 54.6832, 25.2511));

        for (ParkingLocation location : parkingLocations) {
            Marker marker = new Marker(mapView);
            marker.setPosition(new GeoPoint(location.latitude, location.longitude));
            marker.setTitle(location.name);
            marker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM);
            marker.setOnMarkerClickListener((m, map) -> {
                showReservationDialog(location.name);
                return true;
            });
            mapView.getOverlays().add(marker);
            parkingMarkers.add(marker);
        }
        mapView.invalidate();
    }

    private void showReservationDialog(String locationName) {
        LinearLayout parkingInfoPanel = findViewById(R.id.parking_info_panel);
        TextView nameText = parkingInfoPanel.findViewById(R.id.parking_name);
        TextView addressText = parkingInfoPanel.findViewById(R.id.parking_address);
        ImageView parkingImage = parkingInfoPanel.findViewById(R.id.parking_image);
        Button cancelButton = parkingInfoPanel.findViewById(R.id.cancel_button);
        reserveButton = parkingInfoPanel.findViewById(R.id.reserve_button);
        TextView activeReminder = parkingInfoPanel.findViewById(R.id.active_reservation_reminder);

        nameText.setText(locationName);
        addressText.setText("Address: " + getMockAddress(locationName));
        switch (locationName) {
            case "City Center Parking":
                parkingImage.setImageResource(R.drawable.parkingas_ged);
                break;
            case "Green Street Lot":
                parkingImage.setImageResource(R.drawable.parkingas_zal);
                break;
            case "Old Town Garage":
                parkingImage.setImageResource(R.drawable.parkingas_pil);
                break;
            case "Cathedral Square Parking":
                parkingImage.setImageResource(R.drawable.parkingas_kat);
                break;
            case "Business District Garage":
                parkingImage.setImageResource(R.drawable.parkingas_ver);
                break;
            case "Riverside Parking Lot":
                parkingImage.setImageResource(R.drawable.parkingas_upe);
                break;
            case "University Campus Parking":
                parkingImage.setImageResource(R.drawable.parkingas_stud);
                break;
            case "Panorama Shopping Parking":
                parkingImage.setImageResource(R.drawable.parkingas_pano);
                break;
            case "Railway Station Lot":
                parkingImage.setImageResource(R.drawable.parkingas_gel);
                break;
            case "Vingis Park Entrance":
                parkingImage.setImageResource(R.drawable.parking_ving);
                break;
            default:
                parkingImage.setImageResource(R.drawable.parkingas_1);
                break;
        }

        parkingInfoPanel.setVisibility(View.VISIBLE);

        cancelButton.setOnClickListener(v -> parkingInfoPanel.setVisibility(View.GONE));

        if (activeReservation != null) {
            if (activeReservation.getLocationName().equals(locationName)) {
                reserveButton.setText("End Reservation");
                reserveButton.setVisibility(View.VISIBLE);
                reserveButton.setOnClickListener(v -> showEndReservationDialog());
            } else {
                reserveButton.setVisibility(View.GONE);
                Toast.makeText(this, "You already have an active reservation at " + activeReservation.getLocationName(), Toast.LENGTH_SHORT).show();
            }
        } else {
            reserveButton.setVisibility(View.VISIBLE);
            reserveButton.setText("Reserve");
            reserveButton.setOnClickListener(v -> {
                long now = System.currentTimeMillis();
                activeReservation = new Reservation(locationName, now);
                activeReminder.setVisibility(View.VISIBLE);
                reserveButton.setText("End Reservation");
                startReservationTimer();
                scheduleReminderNotification();
                Toast.makeText(this, "Reservation made at " + locationName, Toast.LENGTH_SHORT).show();
                parkingInfoPanel.setVisibility(View.GONE);
            });
        }
    }


    private String getMockAddress(String name) {
        switch (name) {
            case "City Center Parking": return "Gedimino Ave 10, Vilnius";
            case "Green Street Lot": return "Žalia Gatvė 12, Vilnius";
            case "Old Town Garage": return "Pilies St 3, Vilnius";
            case "Cathedral Square Parking": return "Katedros aikštė, Vilnius";
            case "Business District Garage": return "Verslo g. 5, Vilnius";
            case "Riverside Parking Lot": return "Upės g. 2, Vilnius";
            case "University Campus Parking": return "Studentų g. 1, Vilnius";
            case "Panorama Shopping Parking": return "Saltoniškių g. 9, Vilnius";
            case "Railway Station Lot": return "Geležinkelio g. 16, Vilnius";
            case "Vingis Park Entrance": return "Vingio Parkas, Vilnius";
            default: return "Vilnius";
        }
    }


    private void showEndReservationDialog() {
        long endTime = System.currentTimeMillis();
        long durationMinutes = (endTime - activeReservation.getStartTime()) / (1000 * 60);
        int pricePerMinute = 1; // Define your rate here
        int totalCost = (int) Math.max(1, durationMinutes * pricePerMinute); // Minimum $1

        new AlertDialog.Builder(this)
                .setTitle("End Reservation")
                .setMessage("End reservation at " + activeReservation.getLocationName() + "?\nEstimated Cost: €" + totalCost)
                .setPositiveButton("End", (dialog, which) -> {
                    Reservation completed = new Reservation(
                            activeReservation.getLocationName(),
                            activeReservation.getStartTime(),
                            endTime
                    );
                    reservationHistory.add(completed);

                    SharedPreferences prefs = getSharedPreferences("PaymentPrefs", MODE_PRIVATE);
                    String paymentMethod = prefs.getString("selected_payment_method", "Card");
                    savePaymentHistory(paymentMethod, totalCost, completed.getLocationName());

                    activeReservation = null;
                    stopReservationTimer();
                    cancelReminderNotification();
                    reserveButton.setVisibility(View.GONE);

                })
                .setNegativeButton("Cancel", null)
                .show();
    }



    private void showSelectParkingPrompt() {
        new AlertDialog.Builder(this)
                .setTitle("No Parking Selected")
                .setMessage("Please tap on a parking location on the map to reserve.")
                .setPositiveButton("OK", null)
                .show();
    }

    private void filterMarkers(String query) {
        for (Marker marker : parkingMarkers) {
            if (marker.getTitle().toLowerCase().contains(query.toLowerCase())) {
                marker.setVisible(true);
            } else {
                marker.setVisible(false);
            }
        }
        mapView.invalidate();
    }

    private void startReservationTimer() {
        timerRunnable = new Runnable() {
            @Override
            public void run() {
                if (activeReservation != null) {
                    long elapsedMillis = System.currentTimeMillis() - activeReservation.getStartTime();
                    int seconds = (int) (elapsedMillis / 1000) % 60;
                    int minutes = (int) ((elapsedMillis / (1000 * 60)) % 60);
                    int hours = (int) (elapsedMillis / (1000 * 60 * 60));

                    String timerText = String.format(Locale.getDefault(),
                            "Active reservation at %s (%02d:%02d:%02d)",
                            activeReservation.getLocationName(), hours, minutes, seconds);

                    activeReminder.setText(timerText);
                    activeReminder.setVisibility(View.VISIBLE);
                    timerHandler.postDelayed(this, 1000);
                }
            }
        };
        timerHandler.post(timerRunnable);
    }

    private void stopReservationTimer() {
        timerHandler.removeCallbacks(timerRunnable);
        activeReminder.setVisibility(View.GONE);
    }

    private static class ParkingLocation {
        String name;
        double latitude;
        double longitude;

        ParkingLocation(String name, double latitude, double longitude) {
            this.name = name;
            this.latitude = latitude;
            this.longitude = longitude;
        }
    }

    private void scheduleReminderNotification() {
        Intent intent = new Intent(this, NotificationReceiver.class);
        intent.putExtra("location", activeReservation.getLocationName());
        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 1001, intent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);

        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        long interval = 1 * 60 * 1000; // 1 minutes
        long triggerAt = System.currentTimeMillis() + interval;

        alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, triggerAt, interval, pendingIntent);
    }

    private void cancelReminderNotification() {
        Intent intent = new Intent(this, NotificationReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 1001, intent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);
        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        alarmManager.cancel(pendingIntent);
    }

    private void showAllMarkers() {
        mapView.getOverlays().clear();
        for (Marker marker : parkingMarkers) {
            mapView.getOverlays().add(marker);
        }
        mapView.invalidate();
    }

    private void filterToActiveReservation() {
        mapView.getOverlays().clear();

        for (Marker marker : parkingMarkers) {
            if (marker.getTitle().equals(activeReservation.getLocationName())) {
                mapView.getOverlays().add(marker);
                mapView.getController().setCenter(marker.getPosition());
                break;
            }
        }

        mapView.invalidate();
    }

    private void savePaymentHistory(String method, int cost, String location) {
        SharedPreferences prefs = getSharedPreferences("PaymentHistory", MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();

        String entry = method + "," + cost + "," + location;
        long timestamp = System.currentTimeMillis();

        editor.putString(String.valueOf(timestamp), entry);
        editor.apply();
    }


}
