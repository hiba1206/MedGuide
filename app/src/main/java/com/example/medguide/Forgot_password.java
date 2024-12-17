package com.example.medguide;
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

public class Forgot_password extends AppCompatActivity  {




        private EditText emailEditText;
        private Button submitButton;
        private TextView rememberPasswordText, loginHereText;
        private FirebaseDatabase firebaseDatabase;
        private DatabaseReference databaseReference;

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_forgot_password); // Utilisez votre fichier XML ici

            // Initialiser les vues
            emailEditText = findViewById(R.id.emailEditText);
            submitButton = findViewById(R.id.submitButton);
            rememberPasswordText = findViewById(R.id.rememberPasswordText);
            loginHereText = findViewById(R.id.loginHereText);

            // Initialiser Firebase
            firebaseDatabase = FirebaseDatabase.getInstance();
            databaseReference = firebaseDatabase.getReference("users"); // Supposons que les utilisateurs sont dans la référence "users"

            // Événement pour la soumission du formulaire
            submitButton.setOnClickListener(v -> {
                String email = emailEditText.getText().toString().trim();

                // Vérification si l'email est vide
                if (TextUtils.isEmpty(email)) {
                    emailEditText.setError("L'email est requis");
                    return;
                }

                // Recherche de l'email dans la base de données
                resetPassword(email);
            });
        }

        private void resetPassword(String email) {
            // Vérifier si l'email existe dans la base de données Firebase
            databaseReference.orderByChild("email").equalTo(email).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {
                        // Si l'email existe, envoyer un message de réinitialisation ou une action
                        Toast.makeText(Forgot_password.this, "Email trouvé. Réinitialisation du mot de passe en cours...", Toast.LENGTH_SHORT).show();

                        // Ici, vous pouvez envoyer un e-mail de réinitialisation via votre serveur ou générer un lien spécial

                        // Exemple d'envoi d'une notification ou mise à jour d'un champ pour la réinitialisation
                        String resetLink = "https://votreapp.com/reset?email=" + email; // Exemple de lien pour réinitialiser
                       // Vous pouvez aussi stocker un code de réinitialisation dans la base de données

                        // Simuler l'envoi du lien de réinitialisation
                        sendResetLink(email, resetLink);
                    } else {
                        // Si l'email n'est pas trouvé dans la base de données
                        Toast.makeText(Forgot_password.this, "Cet email n'existe pas", Toast.LENGTH_LONG).show();
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    Toast.makeText(Forgot_password.this, "Erreur : " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        }

        private void sendResetLink(String email, String resetLink) {
            // Simuler l'envoi d'un email ou d'un lien de réinitialisation
            // Vous pouvez utiliser un service comme Firebase Functions ou un serveur externe pour envoyer l'email.
            // Ici, nous allons juste afficher un toast pour simuler cela.

            Toast.makeText(this, "Lien de réinitialisation envoyé à : " + email, Toast.LENGTH_SHORT).show();
        }
    }


