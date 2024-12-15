package com.example.medguide.ui.login;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
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

    GoogleSignInClient gsc;
    LinearLayout googleBtn;
    TextView textView, mdpOublie;
    EditText etCredential, etPassword;
    Button btnLogin;

    DatabaseReference databaseReference;

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
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1000) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                task.getResult(ApiException.class);
                navigateToSecondActivity();
            } catch (ApiException e) {
                Toast.makeText(getContext(), "Something went wrong", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void navigateToSecondActivity() {
        requireActivity().finish();
        Intent intent = new Intent(getActivity(), SecondActivity.class);
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

                    // Log values for debugging
                    Log.d("LoginDebug", "Input Credential: " + credential);
                    Log.d("LoginDebug", "Input Password: " + password);
                    Log.d("LoginDebug", "Stored Username: " + dbUsername);
                    Log.d("LoginDebug", "Stored Email: " + dbEmail);
                    Log.d("LoginDebug", "Stored Phone: " + dbPhone);
                    Log.d("LoginDebug", "Stored Password: " + dbPassword);

                    // Check credentials and password
                    if ((credential.equals(dbUsername) || credential.equals(dbEmail) || credential.equals(dbPhone))
                            && password.equals(dbPassword)) {
                        isUserFound = true;
                        Toast.makeText(getContext(), "Connexion réussie", Toast.LENGTH_LONG).show();
                        navigateToSecondActivity();
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
