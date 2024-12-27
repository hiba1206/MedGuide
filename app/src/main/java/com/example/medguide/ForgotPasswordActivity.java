package com.example.medguide;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import okhttp3.*;
import java.io.IOException;

public class ForgotPasswordActivity extends AppCompatActivity {
    private static final String TAG = "ForgotPasswordActivity";
    private EditText emailEditText;
    private OkHttpClient client;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);

        // Initialize views
        emailEditText = findViewById(R.id.emailEditText);
        Button submitButton = findViewById(R.id.submitButton);
        TextView loginHereText = findViewById(R.id.loginHereText);

        // Initialize OkHttpClient
        client = new OkHttpClient();

        // Set up the "Réinitialiser le mot de passe" button click listener
        submitButton.setOnClickListener(v -> {
            String email = emailEditText.getText().toString().trim();

            // Check if the email field is empty
            if (TextUtils.isEmpty(email)) {
                Toast.makeText(ForgotPasswordActivity.this, "Veuillez entrer votre adresse email", Toast.LENGTH_SHORT).show();
                Log.d(TAG, "Email field is empty");
            } else {
                // Create a request object and make the API call
                Log.d(TAG, "Email entered: " + email);
                handlePasswordReset(email);
            }
        });

        // Set up the "Se connecter ici" text click listener
        loginHereText.setOnClickListener(v -> {
            // Redirect to LoginActivity
            Intent intent = new Intent(ForgotPasswordActivity.this, MainActivity.class);
            startActivity(intent);
            finish();
        });
    }

    /**
     * Send a request to the backend to initiate the password reset process.
     */
    private void handlePasswordReset(String email) {
        // Create a JSON body for the request
        MediaType JSON = MediaType.parse("application/json; charset=utf-8");
        String json = "{\"email\": \"" + email + "\"}";

        RequestBody body = RequestBody.create(json, JSON);

        // Create the request
        Request request = new Request.Builder()
                .url("https://10.0.2.2:5001/reset-password?token={token}") // Replace with your server URL
                .post(body)
                .build();

        // Log the request URL and body
        Log.d(TAG, "Sending request to: " + request.url());
        Log.d(TAG, "Request body: " + json);

        // Make the request asynchronously
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                // Log failure details
                Log.e(TAG, "Request failed", e);
                runOnUiThread(() -> {
                    Toast.makeText(ForgotPasswordActivity.this, "Problème de connexion", Toast.LENGTH_SHORT).show();
                });
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    // Log the successful response
                    String responseMessage = response.body().string();
                    Log.d(TAG, "Response successful: " + responseMessage);
                    runOnUiThread(() -> {
                        Toast.makeText(ForgotPasswordActivity.this, responseMessage, Toast.LENGTH_LONG).show();
                        emailEditText.setText(""); // Clear the email field
                    });
                } else {
                    // Log the error response
                    Log.e(TAG, "Error response: " + response.code() + " - " + response.message());
                    runOnUiThread(() -> {
                        Toast.makeText(ForgotPasswordActivity.this, "Erreur lors de la réinitialisation", Toast.LENGTH_LONG).show();
                    });
                }
            }
        });
    }
}
