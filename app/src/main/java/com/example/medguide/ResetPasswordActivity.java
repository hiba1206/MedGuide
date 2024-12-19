package com.example.medguide;

import android.os.Bundle;
import android.text.TextUtils;
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
    private EditText codeEditText, newPasswordEditText;
    private DatabaseReference usersRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reset_password);

        // Initialize Firebase Realtime Database reference
        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
        usersRef = firebaseDatabase.getReference("users");

        // Initialize views
        codeEditText = findViewById(R.id.codeEditText);
        newPasswordEditText = findViewById(R.id.newPasswordEditText);
        Button resetPasswordButton = findViewById(R.id.resetPasswordButton);

        // Set up the "Réinitialiser le mot de passe" button click listener
        resetPasswordButton.setOnClickListener(v -> {
            String resetCode = codeEditText.getText().toString().trim();
            String newPassword = newPasswordEditText.getText().toString().trim();

            if (TextUtils.isEmpty(resetCode) || TextUtils.isEmpty(newPassword)) {
                Toast.makeText(ResetPasswordActivity.this, "Veuillez remplir tous les champs", Toast.LENGTH_SHORT).show();
            } else {
                // Handle password reset (validate reset code and update password)
                handlePasswordUpdate(resetCode, newPassword);
            }
        });
    }

    /**
     * Handles the password update process by checking if the reset code is correct,
     * and then updating the user's password.
     */
    private void handlePasswordUpdate(String resetCode, String newPassword) {
        usersRef.orderByChild("resetCode").equalTo(resetCode).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    // Reset code is valid, get the user's ID
                    String userId = ((DataSnapshot) dataSnapshot.getChildren().iterator().next()).getKey();

                    // Update the user's password in Firebase
                    usersRef.child(userId).child("password").setValue(newPassword);

                    // Remove the reset code after successful reset
                    usersRef.child(userId).child("resetCode").removeValue();

                    // Inform the user that the password has been reset
                    Toast.makeText(ResetPasswordActivity.this, "Votre mot de passe a été réinitialisé", Toast.LENGTH_LONG).show();

                    // Redirect to the login screen
                    finish();
                } else {
                    // If the reset code is invalid
                    Toast.makeText(ResetPasswordActivity.this, "Code de réinitialisation invalide", Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Handle errors (for example, network issues)
                Toast.makeText(ResetPasswordActivity.this, "Erreur de connexion à la base de données : " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
