package com.example.medguide;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import java.util.Properties;
import javax.mail.Authenticator;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import androidx.appcompat.app.AppCompatActivity;

import java.util.UUID;

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
                sendResetCode(email);

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

    private void sendResetCode(String userEmail) {
        DatabaseReference usersRef = FirebaseDatabase.getInstance().getReference("users");

        usersRef.orderByChild("email").equalTo(userEmail).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    for (DataSnapshot userSnapshot : snapshot.getChildren()) {
                        String userId = userSnapshot.getKey();

                        // Generate reset code and expiry time
                        String resetCode = UUID.randomUUID().toString().substring(0, 6);
                        long expiryTime = System.currentTimeMillis() + 3600000; // 1 hour from now

                        // Update database
                        DatabaseReference userRef = usersRef.child(userId);
                        userRef.child("resetCode").setValue(resetCode);
                        userRef.child("resetCodeExpiry").setValue(expiryTime);

                        // Send email
                        sendEmail(userEmail, resetCode);
                        Intent intent = new Intent(ForgotPasswordActivity.this, ResetPasswordActivity.class);
                        intent.putExtra("USER_EMAIL", userEmail);
                        intent.putExtra("RESET_CODE", resetCode);
                        startActivity(intent);
                        finish();
                        Toast.makeText(getApplicationContext(), "Reset code sent to your email.", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(getApplicationContext(), "Email not found.", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("FirebaseError", error.getMessage());
            }
        });
    }

    private void sendEmail(String email, String resetCode) {
        // Email credentials
        final String senderEmail = "kimafaf@gmail.com";
        final String senderPassword = "ocgz gfaa lfid lcjx";

        // Email properties
        Properties props = new Properties();
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.host", "smtp.gmail.com");
        props.put("mail.smtp.port", "587");

        // Session
        Session session = Session.getInstance(props, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(senderEmail, senderPassword);
            }
        });

        // Run email-sending logic in a separate thread
        new Thread(() -> {
            try {
                // Create the email message
                Message message = new MimeMessage(session);
                message.setFrom(new InternetAddress(senderEmail));
                message.setRecipients(
                        Message.RecipientType.TO,
                        InternetAddress.parse(email)
                );
                message.setSubject("Password Reset Request");
                message.setText("Dear User,\n\n"
                        + "You have requested to reset your password. Please use the following reset code:\n\n"
                        + resetCode + "\n\n"
                        + "This code is valid for one hour. If you did not request a password reset, please ignore this email.\n\n"
                        + "Best regards,\n"
                        + "Your App Team");

                // Send the email
                Transport.send(message);
                Log.d("Email", "Reset code sent successfully to: " + email);

            } catch (MessagingException e) {
                e.printStackTrace();
                Log.e("EmailError", "Failed to send email: " + e.getMessage());
            }
        }).start();
    }

}
