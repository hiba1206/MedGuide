package com.example.medguide;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class ResetPasswordActivity extends AppCompatActivity {

    private EditText codeEditText;
    private Button verifyCodeButton;
    private String userEmail;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reset_password);

        // Initialize views
        codeEditText = findViewById(R.id.codeEditText);
        verifyCodeButton = findViewById(R.id.verifyCodeButton);
        userEmail = getIntent().getStringExtra("USER_EMAIL");


        // Set up button click listener
        verifyCodeButton.setOnClickListener(v -> {
            String enteredCode = codeEditText.getText().toString().trim();

            if (enteredCode.isEmpty()) {
                Toast.makeText(this, "Veuillez entrer le code de réinitialisation.", Toast.LENGTH_SHORT).show();
            } else {
                verifyResetCode(userEmail, enteredCode);
            }
        });
    }

    private void verifyResetCode(String userEmail, String enteredCode) {
        DatabaseReference usersRef = FirebaseDatabase.getInstance().getReference("users");

        usersRef.orderByChild("email").equalTo(userEmail).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    for (DataSnapshot userSnapshot : snapshot.getChildren()) {
                        String resetCode = userSnapshot.child("resetCode").getValue(String.class);
                        Long expiryTime = userSnapshot.child("resetCodeExpiry").getValue(Long.class);

                        if (resetCode != null && resetCode.equals(enteredCode)) {
                            if (expiryTime != null && System.currentTimeMillis() < expiryTime) {
                                // Code is valid, proceed to reset password
                                showResetPasswordLayout(userSnapshot.getKey());
                            } else {
                                Toast.makeText(getApplicationContext(), "Ce code a expiré.", Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            Toast.makeText(getApplicationContext(), "Code Invalide.", Toast.LENGTH_SHORT).show();
                        }
                    }
                } else {
                    Toast.makeText(getApplicationContext(), "Email non trouvé.", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("FirebaseError", error.getMessage());
            }
        });
    }

    private void showResetPasswordLayout(String userId) {
        // Pass userId to the new activity or fragment to reset the password
        Intent intent = new Intent(this, NewPasswordActivity.class);
        intent.putExtra("USER_ID", userId);
        startActivity(intent);
        finish();
    }
}
