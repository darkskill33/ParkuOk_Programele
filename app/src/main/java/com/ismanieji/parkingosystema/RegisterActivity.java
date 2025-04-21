package com.ismanieji.parkingosystema;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;  // Import Patterns for email validation
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.activity.ComponentActivity;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RegisterActivity extends ComponentActivity {

    private EditText usernameEditText;
    private EditText passwordEditText;
    private EditText firstNameEditText;
    private EditText lastNameEditText;
    private EditText emailEditText;
    private Button registerButton, backButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        usernameEditText = findViewById(R.id.username);
        passwordEditText = findViewById(R.id.password);
        firstNameEditText = findViewById(R.id.first_name);
        lastNameEditText = findViewById(R.id.last_name);
        emailEditText = findViewById(R.id.email);
        registerButton = findViewById(R.id.register_button);
        backButton = findViewById(R.id.back_button);

        registerButton.setOnClickListener(v -> registerUser());

        // Handle Back Button Click
        backButton.setOnClickListener(v -> finish());
    }

    private void registerUser() {
        String username = usernameEditText.getText().toString();
        String password = passwordEditText.getText().toString();
        String firstName = firstNameEditText.getText().toString();
        String lastName = lastNameEditText.getText().toString();
        String email = emailEditText.getText().toString();

        // Check if email is valid
        if (!isValidEmail(email)) {
            emailEditText.setError("Invalid Email Format");
            return;
        }

        // Directly use getApiService() without calling create() again
        ApiService apiService = RetrofitClient.getApiService();
        Call<RegisterResponse> call = apiService.registerUser(new RegisterRequest(username, password, firstName, lastName, email));

        call.enqueue(new Callback<RegisterResponse>() {
            @Override
            public void onResponse(Call<RegisterResponse> call, Response<RegisterResponse> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(RegisterActivity.this, "Registration Success!", Toast.LENGTH_LONG).show();
                    Intent intent = new Intent( RegisterActivity.this, LoginActivity.class);
                    startActivity(intent);
                } else {
                    Toast.makeText(RegisterActivity.this, "Registration Failed: " + response.message(), Toast.LENGTH_SHORT).show();
                    Log.e("Registration Error", response.message());
                }
            }

            @Override
            public void onFailure(Call<RegisterResponse> call, Throwable t) {
                Toast.makeText(RegisterActivity.this, "Error: " + t.getMessage(), Toast.LENGTH_LONG).show();
                Log.e("Registration Failure", t.getMessage());
            }
        });
    }

    // Method to validate email format
    private boolean isValidEmail(String email) {
        return Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }
}
