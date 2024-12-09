package com.example.medguide;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class PersonalInfo_activity extends AppCompatActivity {

    private EditText etNom, etPrenom, etEmailPhone, etDateNaissance, etTaille, etPoids, etPassword, etConfirmPassword;
    private RadioGroup rgSexe;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_personal_info);

        // Références des champs
        etNom = findViewById(R.id.et_nom);
        etPrenom = findViewById(R.id.et_prenom);
        etEmailPhone = findViewById(R.id.et_email_phone);
        etDateNaissance = findViewById(R.id.et_date_naissance);
        etTaille = findViewById(R.id.et_taille);
        etPoids = findViewById(R.id.et_poids);
        etPassword = findViewById(R.id.et_password);
        etConfirmPassword = findViewById(R.id.et_confirm_password);
        rgSexe = findViewById(R.id.rg_sexe);

        Button btnNext = findViewById(R.id.btn_next);

        // Date Picker pour la date de naissance
        etDateNaissance.setOnClickListener(v -> showDatePicker());

        // Bouton Suivant
        btnNext.setOnClickListener(v -> {
            if (validateFields()) {
                Intent intent = new Intent(PersonalInfo_activity.this, HealthyInfoActivity.class);
                startActivity(intent);
            }
        });
    }

    private void showDatePicker() {
        // Initialiser le calendrier
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        // Afficher le sélecteur de date
        DatePickerDialog datePickerDialog = new DatePickerDialog(
                PersonalInfo_activity.this,
                (DatePicker view, int yearSelected, int monthSelected, int daySelected) -> {
                    String date = String.format("%02d/%02d/%04d", daySelected, monthSelected + 1, yearSelected);
                    etDateNaissance.setText(date);
                },
                year, month, day
        );
        datePickerDialog.show();
    }

    private boolean validateFields() {
        if (etNom.getText().toString().isEmpty() || etPrenom.getText().toString().isEmpty() ||
                etEmailPhone.getText().toString().isEmpty() || etDateNaissance.getText().toString().isEmpty() ||
                etPassword.getText().toString().isEmpty() || etConfirmPassword.getText().toString().isEmpty() ||
                rgSexe.getCheckedRadioButtonId() == -1) {
            Toast.makeText(this, "Tous les champs sont obligatoires !", Toast.LENGTH_LONG).show();
            return false;

        }

        if (!etPassword.getText().toString().equals(etConfirmPassword.getText().toString())) {
            Toast.makeText(this, "Les mots de passe ne correspondent pas !", Toast.LENGTH_LONG).show();
            return false;
        }

        return true;
    }

}
