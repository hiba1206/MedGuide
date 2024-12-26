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

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.medguide.models.User;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

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
                String email = etEmail.getText().toString().trim();
                String username = etUsername.getText().toString().trim();
                String phone = etPhone.getText().toString().trim();

                // Check for uniqueness of email, username, and phone
                checkForExistingUser(email, username, phone);
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

        // Validate username length (must be at least 5 characters)
        if (etUsername.getText().toString().length() < 5) {
            Toast.makeText(this, "Le nom d'utilisateur doit contenir au moins 5 caractères !", Toast.LENGTH_LONG).show();
            return false;
        }

        // Validate email format
        if (!isValidEmail(etEmail.getText().toString().trim())) {
            Toast.makeText(this, "L'email n'est pas valide !", Toast.LENGTH_LONG).show();
            return false;
        }

        // Validate phone number (must contain exactly 10 digits)
        String phoneNumber = etPhone.getText().toString().trim();
        if (phoneNumber.length() != 10 || !phoneNumber.matches("[0-9]+")) {
            Toast.makeText(this, "Le numéro de téléphone doit contenir exactement 10 chiffres !", Toast.LENGTH_LONG).show();
            return false;
        }

        // Validate password (at least 8 characters, contains at least one uppercase letter, one special character, and one number)
        String password = etPassword.getText().toString().trim();
        if (password.length() < 8 || !password.matches(".*[A-Z].*") || !password.matches(".*[0-9].*") || !password.matches(".*[!@#$%^&*(),.?\":{}|<>].*")) {
            Toast.makeText(this, "Le mot de passe doit contenir au moins 8 caractères, un chiffre, une lettre majuscule et un caractère spécial !", Toast.LENGTH_LONG).show();
            return false;
        }

        // Check if passwords match
        if (!etPassword.getText().toString().equals(etConfirmPassword.getText().toString())) {
            Toast.makeText(this, "Les mots de passe ne correspondent pas !", Toast.LENGTH_LONG).show();
            return false;
        }

        return true;
    }


    private boolean isValidEmail(String email) {
        return email != null && Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }

    private void checkForExistingUser(String email, String username, String phone) {
        DatabaseReference usersRef = FirebaseDatabase.getInstance().getReference("users");

        usersRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                boolean isEmailTaken = false;
                boolean isUsernameTaken = false;
                boolean isPhoneTaken = false;

                for (DataSnapshot userSnapshot : snapshot.getChildren()) {
                    String dbEmail = userSnapshot.child("email").getValue(String.class);
                    String dbUsername = userSnapshot.child("username").getValue(String.class);
                    String dbPhone = userSnapshot.child("phone").getValue(String.class);

                    if (email.equals(dbEmail)) {
                        isEmailTaken = true;
                    }
                    if (username.equals(dbUsername)) {
                        isUsernameTaken = true;
                    }
                    if (phone.equals(dbPhone)) {
                        isPhoneTaken = true;
                    }
                }

                if (isEmailTaken) {
                    Toast.makeText(PersonalInfoActivity.this, "L'email est déjà utilisé!", Toast.LENGTH_SHORT).show();
                } else if (isUsernameTaken) {
                    Toast.makeText(PersonalInfoActivity.this, "Le nom d'utilisateur est déjà pris!", Toast.LENGTH_SHORT).show();
                } else if (isPhoneTaken) {
                    Toast.makeText(PersonalInfoActivity.this, "Le numéro de téléphone est déjà utilisé!", Toast.LENGTH_SHORT).show();
                } else {
                    // Proceed with the registration since no duplicates were found
                    proceedWithRegistration(email, username, phone);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(PersonalInfoActivity.this, "Erreur de vérification: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void proceedWithRegistration(String email, String username, String phone) {
        // Create a new User object
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

        // Pass the user object to the next activity
        Intent intent = new Intent(PersonalInfoActivity.this, HealthyInfoActivity.class);
        intent.putExtra("user", user);
        startActivity(intent);
        finish();
    }
}
