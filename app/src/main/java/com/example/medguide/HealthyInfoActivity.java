package com.example.medguide;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class HealthyInfoActivity extends AppCompatActivity {

    private RadioGroup rgHandicape, rgDiabetique, rgAllergies;
    private EditText etDetailsAllergies, etGroupeSanguin;
    private Button btnSoumettre;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_healthy_info);

        // Initialisation des vues
        rgHandicape = findViewById(R.id.rg_handicape);
        rgDiabetique = findViewById(R.id.rg_diabetique);
        rgAllergies = findViewById(R.id.rg_allergies);
        etDetailsAllergies = findViewById(R.id.et_details_allergies);
        etGroupeSanguin = findViewById(R.id.groupesang);
        btnSoumettre = findViewById(R.id.btn_soumettre);

        // Gestion de la visibilité du champ de détails des allergies
        rgAllergies.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if (checkedId == R.id.rb_allergies_oui) {
                    etDetailsAllergies.setVisibility(View.VISIBLE);
                } else {
                    etDetailsAllergies.setVisibility(View.GONE);
                    etDetailsAllergies.setText("");
                }
            }
        });

        // Gestion du clic sur le bouton Soumettre
        btnSoumettre.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Validation des réponses
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

                // Récupération des réponses
                boolean estHandicape = rgHandicape.getCheckedRadioButtonId() == R.id.rb_handicape_oui;
                boolean estDiabetique = rgDiabetique.getCheckedRadioButtonId() == R.id.rb_diabetique_oui;
                boolean aAllergies = rgAllergies.getCheckedRadioButtonId() == R.id.rb_allergies_oui;
                String detailsAllergies = etDetailsAllergies.getText().toString().trim();
                String groupeSanguin = etGroupeSanguin.getText().toString().trim();

                // Affichage des réponses (ou traitement supplémentaire)
                String message = "Handicapé : " + (estHandicape ? "Oui" : "Non") + "\n" +
                        "Diabétique : " + (estDiabetique ? "Oui" : "Non") + "\n" +
                        "Allergies : " + (aAllergies ? "Oui" : "Non") + "\n";
                if (aAllergies) {
                    message += "Détails des allergies : " + detailsAllergies + "\n";
                }
                message += "Groupe sanguin : " + groupeSanguin;

                Toast.makeText(HealthyInfoActivity.this, message, Toast.LENGTH_LONG).show();

                // Ici, vous pouvez ajouter le code pour enregistrer ou envoyer les réponses
            }
        });
    }
}
