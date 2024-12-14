package com.example.medguide;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.example.medguide.models.User;

import androidx.appcompat.app.AppCompatActivity;

import java.util.Calendar;

public class PersonalInfoActivity extends AppCompatActivity {

    private EditText etNom, etPrenom, etEmail, etDateNaissance, etPassword, etConfirmPassword, etUsername, etPhone;
    private RadioGroup rgSexe;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_personal_info);


        // References to views
        etUsername = findViewById(R.id.et_username);
        etNom = findViewById(R.id.et_nom);
        etPrenom = findViewById(R.id.et_prenom);
        etEmail = findViewById(R.id.et_email);
        etPhone = findViewById(R.id.et_phone);
        etDateNaissance = findViewById(R.id.et_date_naissance);
        etPassword = findViewById(R.id.et_password);
        etConfirmPassword = findViewById(R.id.et_confirm_password);
        rgSexe = findViewById(R.id.rg_sexe);

        Button btnNext = findViewById(R.id.btn_next);

        // Date Picker for birthdate
        etDateNaissance.setOnClickListener(v -> showDatePicker());

        // Next button click
        btnNext.setOnClickListener(v -> {
            if (validateFields()) {
                User user = new User(
                        etUsername.getText().toString().trim(),
                        etNom.getText().toString().trim(),
                        etPrenom.getText().toString().trim(),
                        etEmail.getText().toString().trim(),
                        etPhone.getText().toString().trim(),
                        etDateNaissance.getText().toString().trim(),
                        (rgSexe.getCheckedRadioButtonId() == R.id.rb_male ? "Homme" : "Femme"),
                        etPassword.getText().toString()
                );

                Intent intent = new Intent(PersonalInfoActivity.this, HealthyInfoActivity.class);
                intent.putExtra("user", user);
                startActivity(intent);
                finish();
            }
        });
    }

    private void showDatePicker() {
        // Initialize the calendar
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        // Show the date picker
        DatePickerDialog datePickerDialog = new DatePickerDialog(
                PersonalInfoActivity.this,
                (DatePicker view, int yearSelected, int monthSelected, int daySelected) -> {
                    String date = String.format("%02d/%02d/%04d", daySelected, monthSelected + 1, yearSelected);
                    etDateNaissance.setText(date);
                },
                year, month, day
        );
        datePickerDialog.show();
    }

    private boolean validateFields() {
        // Validate all required fields
        EditText[] fields = {etUsername, etNom, etPrenom, etEmail, etPhone, etDateNaissance, etPassword, etConfirmPassword};
        for (EditText field : fields) {
            if (TextUtils.isEmpty(field.getText().toString().trim())) {
                Toast.makeText(this, "Tous les champs sont obligatoires !", Toast.LENGTH_LONG).show();
                return false;
            }
        }

        // Validate email format
        if (!isValidEmail(etEmail.getText().toString().trim())) {
            Toast.makeText(this, "L'email n'est pas valide !", Toast.LENGTH_LONG).show();
            return false;
        }

        // Check password match
        if (!etPassword.getText().toString().equals(etConfirmPassword.getText().toString())) {
            Toast.makeText(this, "Les mots de passe ne correspondent pas !", Toast.LENGTH_LONG).show();
            return false;
        }

        return true;
    }

    private boolean isValidEmail(String email) {
        return email != null && Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }
}
