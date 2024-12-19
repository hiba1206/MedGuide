package com.example.medguide;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import com.example.medguide.models.User;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class ProfilFragment extends Fragment {

    private static final String TAG = "ProfilFragment";

    // TextViews for personal data section
    private TextView tvNom, tvPrenom, tvEmail, tvPhone, tvDateNaissance, tvSexe;

    // TextViews for health information section
    private TextView tvTaille, tvPoids, tvHandicape, tvDiabetique, tvAllergies, tvGroupeSanguin, tvDetailsAllergies;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profil, container, false);

        // Initialize TextViews for personal data
        tvNom = view.findViewById(R.id.tv_nom);
        tvPrenom = view.findViewById(R.id.tv_prenom);
        tvEmail = view.findViewById(R.id.tv_email);
        tvPhone = view.findViewById(R.id.tv_phone);
        tvDateNaissance = view.findViewById(R.id.tv_date_naissance);
        tvSexe = view.findViewById(R.id.tv_sexe);

        // Initialize TextViews for health information
        tvTaille = view.findViewById(R.id.tv_taille);
        tvPoids = view.findViewById(R.id.tv_poids);
        tvHandicape = view.findViewById(R.id.tv_handicape);
        tvDiabetique = view.findViewById(R.id.tv_diabetique);
        tvAllergies = view.findViewById(R.id.tv_allergies);
        tvGroupeSanguin = view.findViewById(R.id.tv_groupe_sanguin);
        tvDetailsAllergies = view.findViewById(R.id.tv_details_allergies);

        // Retrieve username passed from SecondActivity or other source
        if (getArguments() != null) {
            String username = getArguments().getString("username");
            Log.d(TAG, "Username passed to fragment: " + username);  // Log for verification
            if (username != null) {
                loadUserData(username);
            }
        }

        return view;
    }

    // Load user data from Firebase
    private void loadUserData(String username) {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("users");

        // Search for user by "username"
        databaseReference.orderByChild("username").equalTo(username)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()) {
                            for (DataSnapshot userSnapshot : dataSnapshot.getChildren()) {
                                // Retrieve the User object
                                User user = userSnapshot.getValue(User.class);

                                if (user != null) {
                                    // Display personal data in TextViews
                                    tvNom.setText(user.getNom() != null ? user.getNom() : "N/A");
                                    tvPrenom.setText(user.getPrenom() != null ? user.getPrenom() : "N/A");
                                    tvEmail.setText(user.getEmail() != null ? user.getEmail() : "N/A");
                                    tvPhone.setText(user.getPhone() != null ? user.getPhone() : "N/A");
                                    tvDateNaissance.setText(user.getDateNaissance() != null ? user.getDateNaissance() : "N/A");
                                    tvSexe.setText(user.getSexe() != null ? user.getSexe() : "N/A");

                                    // Display health information
                                    tvTaille.setText(user.getTaille() != -1 ? String.valueOf(user.getTaille()) : "N/A");
                                    tvPoids.setText(user.getPoids() != -1 ? String.valueOf(user.getPoids()) : "N/A");
                                    tvHandicape.setText(user.isHandicape() ? "Oui" : "Non");
                                    tvDiabetique.setText(user.isDiabetique() ? "Oui" : "Non");
                                    tvAllergies.setText(user.isAllergies() ? "Oui" : "Non");

                                    // Show allergy details only if allergies is true
                                    if (user.isAllergies()) {
                                        tvDetailsAllergies.setText(user.getDetailsAllergies() != null ? user.getDetailsAllergies() : "N/A");
                                        tvDetailsAllergies.setVisibility(View.VISIBLE);
                                    } else {
                                        tvDetailsAllergies.setVisibility(View.GONE);
                                    }

                                    // Display blood group
                                    tvGroupeSanguin.setText(user.getGroupeSanguin() != null ? user.getGroupeSanguin() : "N/A");

                                    Log.d(TAG, "User found: " + user.getNom() + " " + user.getPrenom());
                                    break; // Exit after finding the user
                                }
                            }
                        } else {
                            Log.d(TAG, "No user found with this username.");
                            Toast.makeText(getContext(), "Utilisateur non trouvé", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        Log.w(TAG, "Error reading data", databaseError.toException());
                        Toast.makeText(getContext(), "Erreur lors de la lecture des données", Toast.LENGTH_SHORT).show();
                    }
                });
    }
}
