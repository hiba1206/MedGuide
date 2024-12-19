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

        // Initialisation de la référence Firebase
        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
        usersRef = firebaseDatabase.getReference("users");

        // Initialisation des vues
        emailEditText = findViewById(R.id.emailEditText);
        Button submitButton = findViewById(R.id.submitButton);
        TextView loginHereText = findViewById(R.id.loginHereText);

        // Écouteur pour le bouton "Réinitialiser le mot de passe"
        submitButton.setOnClickListener(v -> {
            String email = emailEditText.getText().toString().trim();

            // Vérifier si l'email est vide ou non valide
            if (TextUtils.isEmpty(email)) {
                Toast.makeText(ForgotPasswordActivity.this, "Veuillez entrer votre adresse email", Toast.LENGTH_SHORT).show();
            } else if (!isValidEmail(email)) {
                Toast.makeText(ForgotPasswordActivity.this, "L'email entré n'est pas valide", Toast.LENGTH_SHORT).show();
            } else {
                // Traiter la réinitialisation du mot de passe
                handlePasswordReset(email);
            }
        });

        // Écouteur pour le lien "Se connecter ici"
        loginHereText.setOnClickListener(v -> {
            // Rediriger vers l'écran de connexion
            Intent intent = new Intent(ForgotPasswordActivity.this, MainActivity.class);
            startActivity(intent);
            finish();
        });
    }

    /**
     * Fonction pour vérifier la validité de l'email.
     */
    private boolean isValidEmail(String email) {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }

    /**
     * Fonction pour traiter la réinitialisation du mot de passe.
     */
    private void handlePasswordReset(String email) {
        // Vérifier si l'email existe dans la base de données Firebase
        usersRef.orderByChild("email").equalTo(email).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    // Utilisateur trouvé, générer un code de réinitialisation
                    String resetCode = generateResetCode();

                    // Obtenir l'ID de l'utilisateur et stocker le code de réinitialisation dans Firebase
                    String userId = ((DataSnapshot) dataSnapshot.getChildren().iterator().next()).getKey();
                    usersRef.child(userId).child("resetCode").setValue(resetCode);

                    // Envoyer un email réel avec SendGrid
                    SendGridHelper.sendResetPasswordEmail(email, resetCode);

                    // Afficher un message de confirmation à l'utilisateur
                    Toast.makeText(ForgotPasswordActivity.this, "Un email de réinitialisation a été envoyé à " + email, Toast.LENGTH_LONG).show();
                } else {
                    // Si l'email n'est pas trouvé dans la base de données
                    Toast.makeText(ForgotPasswordActivity.this, "Aucun utilisateur trouvé avec cet email", Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Gestion des erreurs de connexion Firebase
                Toast.makeText(ForgotPasswordActivity.this, "Erreur de connexion à la base de données : " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    /**
     * Fonction pour générer un code de réinitialisation aléatoire (6 chiffres).
     */
    private String generateResetCode() {
        Random random = new Random();
        return String.format("%06d", random.nextInt(1000000));  // Génère un code aléatoire à 6 chiffres
    }
}
