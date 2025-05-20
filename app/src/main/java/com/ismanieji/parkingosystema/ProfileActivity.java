package com.ismanieji.parkingosystema;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ProfileActivity extends AppCompatActivity {

    private TextView usernameTextView, emailTextView;
    private Button logoutButton, backButton, editProfileButton;
    private String authToken;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        usernameTextView = findViewById(R.id.usernameTextView);
        emailTextView = findViewById(R.id.emailTextView);
        logoutButton = findViewById(R.id.logoutButton);
        backButton = findViewById(R.id.back_button);
        editProfileButton = findViewById(R.id.editProfileButton);

        backButton.setOnClickListener(v -> finish());

        SharedPreferences sharedPreferences = getSharedPreferences("MyPrefs", MODE_PRIVATE);
        authToken = sharedPreferences.getString("auth_token", null);

        if (authToken == null) {
            startActivity(new Intent(ProfileActivity.this, LoginActivity.class));
            finish();
        } else {
            fetchUserProfile();
        }

        logoutButton.setOnClickListener(v -> performLogout());

        editProfileButton.setOnClickListener(v -> {
            // Pass current email to edit activity
            Intent intent = new Intent(ProfileActivity.this, EditProfileActivity.class);
            intent.putExtra("email", emailTextView.getText().toString().replace("Email: ", ""));
            startActivity(intent);
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Refresh profile info after coming back from edit screen
        fetchUserProfile();
    }

    private void fetchUserProfile() {
        ApiService apiService = RetrofitClient.getApiService();
        Call<ProfileResponse> call = apiService.getProfile("Token " + authToken);

        call.enqueue(new Callback<ProfileResponse>() {
            @Override
            public void onResponse(Call<ProfileResponse> call, Response<ProfileResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    ProfileResponse profile = response.body();
                    usernameTextView.setText("Username: " + profile.getUsername());
                    emailTextView.setText("Email: " + profile.getEmail());
                } else {
                    Toast.makeText(ProfileActivity.this, "Failed to load profile", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ProfileResponse> call, Throwable t) {
                Log.e("Profile Error", t.getMessage());
            }
        });
    }

    private void performLogout() {
        ApiService apiService = RetrofitClient.getApiService();
        Call<Void> call = apiService.logoutUser("Token " + authToken);

        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    SharedPreferences sharedPreferences = getSharedPreferences("MyPrefs", MODE_PRIVATE);
                    sharedPreferences.edit().remove("auth_token").apply();
                    startActivity(new Intent(ProfileActivity.this, MainActivity.class));
                    finish();
                } else {
                    Toast.makeText(ProfileActivity.this, "Logout failed", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Log.e("Logout Error", t.getMessage());
            }
        });
    }
}
