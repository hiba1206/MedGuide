package com.example.medguide.ui.login;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import com.example.medguide.ForgotPasswordActivity;
import com.example.medguide.PersonalInfoActivity;
import com.example.medguide.R;
import com.example.medguide.SecondActivity;
import com.example.medguide.models.User;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class LoginFragment extends Fragment {

    private GoogleSignInClient gsc;
    private LinearLayout googleBtn;
    private TextView textView, mdpOublie;
    private EditText etCredential, etPassword;
    private Button btnLogin;

    private DatabaseReference databaseReference;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_login, container, false);

        // Firebase reference
        databaseReference = FirebaseDatabase.getInstance().getReference("users");

        // Initialize Views
        googleBtn = view.findViewById(R.id.google_btn);
        textView = view.findViewById(R.id.signup_text);
        etCredential = view.findViewById(R.id.username);
        etPassword = view.findViewById(R.id.password);
        btnLogin = view.findViewById(R.id.login_button);
        mdpOublie = view.findViewById(R.id.forgot_password);

        // Google Sign-In Options
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .requestProfile()
                .build();
        gsc = GoogleSignIn.getClient(requireContext(), gso);

        // Google Sign-In Button
        googleBtn.setOnClickListener(v -> signIn());

        // Redirect to Registration
        textView.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), PersonalInfoActivity.class);
            startActivity(intent);
        });

        // Forgot password clickable textView
        mdpOublie.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), ForgotPasswordActivity.class);
            startActivity(intent);
        });

        // Manual Login Button
        btnLogin.setOnClickListener(v -> {
            String credential = etCredential.getText().toString().trim();
            String password = etPassword.getText().toString().trim();

            if (credential.isEmpty() || password.isEmpty()) {
                Toast.makeText(getContext(), "Veuillez remplir tous les champs", Toast.LENGTH_SHORT).show();
            } else {
                verifyLogin(credential, password);
            }
        });

        return view;
    }

    private void signIn() {
        Intent signInIntent = gsc.getSignInIntent();
        startActivityForResult(signInIntent, 1000);
        SharedPreferences prefs = getContext().getSharedPreferences("userPrefs", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString("auth_method", "google"); // Or "firebase", "google", depending on how they logged in
        editor.apply();

    }

    String firstName ;
    String lastName;
    String email ;
    String userId;

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1000) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                GoogleSignInAccount account = task.getResult(ApiException.class);
                firstName = account.getGivenName();
                lastName = account.getFamilyName();
                email = account.getEmail();
                userId = account.getId();

                // Check if the user has already been redirected and saved their data
                SharedPreferences prefs = getContext().getSharedPreferences("userPrefs", Context.MODE_PRIVATE);
                boolean hasCompletedInfo = prefs.getBoolean("hasCompletedInfo", false);

                if (!hasCompletedInfo) {
                     //First-time login, save user info and redirect to the personal info screen
                    saveUserInfoToFirebase(firstName, lastName, email, userId);
                } else {
                    // User has already completed the info screen, you can just navigate directly
                   navigateToSecondActivity(null);
                 }
                task.getResult(ApiException.class);

            } catch (ApiException e) {
                Toast.makeText(getContext(), "Something went wrong", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void saveUserInfoToFirebase(String firstName, String lastName, String email, String userId) {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference userRef = database.getReference("users").child(userId);

        // Create a User object to store the details
        User user = new User(firstName, lastName, email);

        // Save to Firebase Database
        userRef.setValue(user).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                // Mark the user as having completed the info screen
                SharedPreferences prefs = getContext().getSharedPreferences("userPrefs", Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = prefs.edit();
               editor.putBoolean("hasCompletedInfo", true);  // Mark as completed
                editor.apply();

                // Redirect the user to the personal info screen
                redirectToInfoScreen();
            } else {
                Toast.makeText(getContext(), "Failed to save user information.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void redirectToInfoScreen() {
        Intent intent = new Intent(getActivity(), PersonalInfoActivity.class);
        intent.putExtra("isGoogleSignIn", true);
        intent.putExtra("firstName", firstName); // Replace with actual variable holding the data
        intent.putExtra("lastName", lastName);
        intent.putExtra("email", email);
        intent.putExtra("userId", userId);
        startActivity(intent);
        requireActivity().finish();
    }

        private void navigateToSecondActivity(String username) {
        requireActivity().finish();
        Intent intent = new Intent(getActivity(), SecondActivity.class);
        if (username != null) {
            intent.putExtra("username", username);
        }
        startActivity(intent);
    }

    private void verifyLogin(String credential, String password) {
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                boolean isUserFound = false;

                for (DataSnapshot userSnapshot : snapshot.getChildren()) {
                    String dbUsername = userSnapshot.child("username").getValue(String.class);
                    String dbEmail = userSnapshot.child("email").getValue(String.class);
                    String dbPhone = userSnapshot.child("phone").getValue(String.class);
                    String dbPassword = userSnapshot.child("password").getValue(String.class);

                    // Check credentials and password
                    if ((credential.equals(dbUsername) || credential.equals(dbEmail) || credential.equals(dbPhone))
                            && password.equals(dbPassword)) {
                        isUserFound = true;
                        Toast.makeText(getContext(), "Connexion réussie", Toast.LENGTH_LONG).show();

                        SharedPreferences prefs = getContext().getSharedPreferences("userPrefs", Context.MODE_PRIVATE);
                        SharedPreferences.Editor editor = prefs.edit();
                        editor.putString("auth_method", "firebase"); // Since you are using Firebase auth
                        editor.apply();
                        // Navigate to SecondActivity with the username
                        navigateToSecondActivity(dbUsername);
                        break;
                    }
                }

                if (!isUserFound) {
                    Toast.makeText(getContext(), "Identifiants invalides", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getContext(), "Erreur : " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
