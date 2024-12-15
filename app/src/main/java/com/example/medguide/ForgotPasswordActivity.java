package com.example.medguide;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class ForgotPasswordActivity extends AppCompatActivity{
    private EditText emailEditText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);

        // Initialize views
        emailEditText = findViewById(R.id.emailEditText);
        Button submitButton = findViewById(R.id.submitButton);
        TextView loginHereText = findViewById(R.id.loginHereText);

        // Set up the "Réinitialiser le mot de passe" button click listener
        submitButton.setOnClickListener(v -> {
            String email = emailEditText.getText().toString().trim();

            // Check if the email field is empty
            if (TextUtils.isEmpty(email)) {
                Toast.makeText(ForgotPasswordActivity.this, "Veuillez entrer votre adresse email", Toast.LENGTH_SHORT).show();
            } else {
                // Handle password reset (e.g., send reset email)
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
     * Simulate the password reset process.
     * In a real app, you'd send an email or communicate with the backend server here.
     */
    private void handlePasswordReset(String email) {
        // Simulated feedback
        Toast.makeText(this, "Un email de réinitialisation a été envoyé à " + email, Toast.LENGTH_LONG).show();

        // Optionally, navigate to another screen or reset the form
        emailEditText.setText(""); // Clear the email field
    }
}
