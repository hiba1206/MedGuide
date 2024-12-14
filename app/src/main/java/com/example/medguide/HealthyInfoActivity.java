package com.example.medguide;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.medguide.models.User;
import com.google.firebase.FirebaseApp;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class HealthyInfoActivity extends AppCompatActivity {

    private RadioGroup rgHandicape, rgDiabetique, rgAllergies;
    private EditText etDetailsAllergies, etGroupeSanguin, etTaille, etPoids;
    // Firebase Database Reference
    private DatabaseReference databaseReference;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_healthy_info);

        // Initialize Firebase
        FirebaseApp.initializeApp(this);

        // Initialize Firebase Database
        databaseReference = FirebaseDatabase.getInstance().getReference();

        // Initialize views
        etTaille = findViewById(R.id.et_taille);
        etPoids = findViewById(R.id.et_poids);
        rgHandicape = findViewById(R.id.rg_handicape);
        rgDiabetique = findViewById(R.id.rg_diabetique);
        rgAllergies = findViewById(R.id.rg_allergies);
        etDetailsAllergies = findViewById(R.id.et_details_allergies);
        etGroupeSanguin = findViewById(R.id.et_groupesang);
        Button btnSoumettre = findViewById(R.id.btn_soumettre);

        // Handle visibility of allergy details input
        rgAllergies.setOnCheckedChangeListener((group, checkedId) -> {
            if (checkedId == R.id.rb_allergies_oui) {
                etDetailsAllergies.setVisibility(View.VISIBLE);
            } else {
                etDetailsAllergies.setVisibility(View.GONE);
                etDetailsAllergies.setText("");
            }
        });

        // Handle submit button click
        btnSoumettre.setOnClickListener(v -> {
            // Validate answers
            if (rgHandicape.getCheckedRadioButtonId() == -1) {
                Toast.makeText(HealthyInfoActivity.this, "Veuillez répondre à la question sur le handicap.", Toast.LENGTH_SHORT).show();
                return;
            }

            if (rgDiabetique.getCheckedRadioButtonId() == -1) {
                Toast.makeText(HealthyInfoActivity.this, "Veuillez répondre à la question sur le diabète.", Toast.LENGTH_SHORT).show();
                return;
            }

            if (rgAllergies.getCheckedRadioButtonId() == -1) {
                Toast.makeText(HealthyInfoActivity.this, "Veuillez répondre à la question sur les allergies.", Toast.LENGTH_SHORT).show();
                return;
            }

            if (TextUtils.isEmpty(etGroupeSanguin.getText().toString().trim())) {
                Toast.makeText(HealthyInfoActivity.this, "Veuillez indiquer votre groupe sanguin.", Toast.LENGTH_SHORT).show();
                return;
            }

            // Retrieve the answers
            boolean estHandicape = rgHandicape.getCheckedRadioButtonId() == R.id.rb_handicape_oui;
            boolean estDiabetique = rgDiabetique.getCheckedRadioButtonId() == R.id.rb_diabetique_oui;
            boolean aAllergies = rgAllergies.getCheckedRadioButtonId() == R.id.rb_allergies_oui;
            String detailsAllergies = etDetailsAllergies.getText().toString().trim();
            String groupeSanguin = etGroupeSanguin.getText().toString().trim();

            // Show the answers as a message
            String message = "Handicapé : " + (estHandicape ? "Oui" : "Non") + "\n" +
                    "Diabétique : " + (estDiabetique ? "Oui" : "Non") + "\n" +
                    "Allergies : " + (aAllergies ? "Oui" : "Non") + "\n";
            if (aAllergies) {
                message += "Détails des allergies : " + detailsAllergies + "\n";
            }
            message += "Groupe sanguin : " + groupeSanguin;

            Toast.makeText(HealthyInfoActivity.this, message, Toast.LENGTH_LONG).show();

            User user = (User) getIntent().getSerializableExtra("user");

            // Update health info
            if (user == null) {
                Toast.makeText(this, "Erreur : les informations utilisateur sont manquantes.", Toast.LENGTH_LONG).show();
                return;
            }

            user.setTaille(Double.parseDouble(etTaille.getText().toString().trim()));
            user.setPoids(Double.parseDouble(etPoids.getText().toString().trim()));
            user.setHandicape(rgHandicape.getCheckedRadioButtonId() == R.id.rb_handicape_oui);
            user.setDiabetique(rgDiabetique.getCheckedRadioButtonId() == R.id.rb_diabetique_oui);
            user.setAllergies(rgAllergies.getCheckedRadioButtonId() == R.id.rb_allergies_oui);
            user.setDetailsAllergies(etDetailsAllergies.getText().toString().trim());
            user.setGroupeSanguin(etGroupeSanguin.getText().toString().trim());

            // Save to Firebase
            saveUserToFirebase(user);

            finish();

            // Go to the next activity
            Intent nextIntent = new Intent(HealthyInfoActivity.this, MainActivity.class);
            startActivity(nextIntent);
        });
    }

    private void saveUserToFirebase(User user) {
        // Generate a unique key for the user
        String userId = databaseReference.push().getKey();

        // Check if the generated key is valid
        if (userId == null) {
            Toast.makeText(this, "Erreur lors de la génération de l'identifiant utilisateur.", Toast.LENGTH_LONG).show();
            return;
        }

        // Save user data under the generated key
        databaseReference.child("users").child(userId).setValue(user)
                .addOnSuccessListener(aVoid ->
                        Toast.makeText(HealthyInfoActivity.this, "Données enregistrées avec succès.", Toast.LENGTH_LONG).show())
                .addOnFailureListener(e ->
                        Toast.makeText(HealthyInfoActivity.this, "Erreur d'enregistrement : " + e.getMessage(), Toast.LENGTH_LONG).show());
    }
}
