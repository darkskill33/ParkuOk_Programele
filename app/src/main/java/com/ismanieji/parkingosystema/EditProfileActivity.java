package com.ismanieji.parkingosystema;


import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import android.util.Log;
import androidx.appcompat.app.AppCompatActivity;


public class EditProfileActivity extends AppCompatActivity {

    private EditText emailEditText, passwordEditText;
    private Button saveButton, backButton;
    private String authToken;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        emailEditText = findViewById(R.id.editEmail);
        passwordEditText = findViewById(R.id.editPassword);
        saveButton = findViewById(R.id.saveProfileButton);
        backButton = findViewById(R.id.back_button);
        backButton.setOnClickListener(v -> finish());

        SharedPreferences sharedPreferences = getSharedPreferences("MyPrefs", MODE_PRIVATE);
        authToken = sharedPreferences.getString("auth_token", null);

        // Prefill if passed from ProfileActivity
        emailEditText.setText(getIntent().getStringExtra("email"));

        saveButton.setOnClickListener(v -> {
            String email = emailEditText.getText().toString().trim();
            String password = passwordEditText.getText().toString().trim();

            String originalEmail = getIntent().getStringExtra("email");

            boolean emailUnchanged = email.equals(originalEmail) || email.isEmpty();
            boolean passwordEmpty = password.isEmpty();

            if (emailUnchanged && passwordEmpty) {
                Toast.makeText(this, "Nothing to update", Toast.LENGTH_SHORT).show();
            } else {
                // For fields that are unchanged or empty, send null to avoid unnecessary updates
                String emailToUpdate = emailUnchanged ? null : email;
                String passwordToUpdate = passwordEmpty ? null : password;

                updateProfile(emailToUpdate, passwordToUpdate);
            }
        });

    }

    private void updateProfile(String email, String password) {
        ApiService apiService = RetrofitClient.getApiService();
        UpdateProfileRequest request = new UpdateProfileRequest(email, password);

        Call<Void> call = apiService.updateProfile("Token " + authToken, request);
        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(EditProfileActivity.this, "Profile updated", Toast.LENGTH_SHORT).show();
                    finish(); // go back to ProfileActivity
                } else {
                    Toast.makeText(EditProfileActivity.this, "Update failed", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Log.e("UpdateError", t.getMessage());
            }
        });
    }
}
