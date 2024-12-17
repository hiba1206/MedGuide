package com.example.medguide;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import java.util.Random;

public class ForgotPasswordActivity extends AppCompatActivity {
    private EditText emailEditText;
    private DatabaseReference usersRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);

        // Initialize Firebase Realtime Database reference
        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
        usersRef = firebaseDatabase.getReference("users");

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
                // Handle password reset (check if email exists and send reset code)
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
     * Handles the password reset process by checking if the email exists,
     * generating a reset code, and storing it in Firebase.
     */
    private void handlePasswordReset(String email) {
        // Look for the user by email
        usersRef.orderByChild("email").equalTo(email).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    // User exists, generate a reset code
                    String resetCode = generateResetCode();

                    // Get the user's ID (key) and store the reset code in the database
                    String userId = ((DataSnapshot) dataSnapshot.getChildren().iterator().next()).getKey();
                    usersRef.child(userId).child("resetCode").setValue(resetCode);

                    // Simulate sending an email with the reset code (here, just display it)
                    sendResetEmail(email, resetCode);

                    // Inform the user that the reset code has been sent
                    Toast.makeText(ForgotPasswordActivity.this, "Un email de réinitialisation a été envoyé à " + email, Toast.LENGTH_LONG).show();
                } else {
                    // If the email does not exist in the database
                    Toast.makeText(ForgotPasswordActivity.this, "Aucun utilisateur trouvé avec cet email", Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Handle errors (for example, network issues)
                Toast.makeText(ForgotPasswordActivity.this, "Erreur de connexion à la base de données : " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    /**
     * Generates a random 6-digit reset code.
     */
    private String generateResetCode() {
        Random random = new Random();
        return String.format("%06d", random.nextInt(1000000));  // Generates a random 6-digit code
    }

    /**
     * Simulate sending an email with the reset code.
     * In a real app, you would use an external service like Firebase Functions, SendGrid, etc.
     */
    private void sendResetEmail(String email, String resetCode) {
        // For demonstration purposes, we'll simulate sending an email
        String resetLink = "https://votreapp.com/reset?code=" + resetCode;
        System.out.println("Envoyer un e-mail à " + email + " avec le lien de réinitialisation : " + resetLink);
        // Here, you would use a real email sending service (e.g., Firebase Functions or an external API)
    }
}
