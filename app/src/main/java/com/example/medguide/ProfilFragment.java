package com.example.medguide;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RadioGroup;
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
    private EditText tvNom, tvPrenom, tvEmail, tvPhone, tvDateNaissance, tvSexe, tvTaille, tvPoids, tvGroupeSanguin, tvDetailsAllergies;

    // TextViews for health information section
    private TextView  tvHandicape, tvDiabetique, tvAllergies ,details_allergies;
    private ImageButton btnEdit;
    private Button btnSave;
    private DatabaseReference databaseReference;
    private String userId;

    private RadioGroup rgHandicape,rgAllergies , rgDiabetique;


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

        details_allergies = view.findViewById(R.id.details_allergies);

        rgAllergies = view.findViewById(R.id.rg_allergies);
        rgDiabetique = view.findViewById(R.id.rg_diabetique);
        rgHandicape = view.findViewById(R.id.rg_handicape);


        btnEdit = view.findViewById(R.id.btnEdit);
        btnSave = view.findViewById(R.id.btnSave);

        // Retrieve username passed from SecondActivity or other source
        if (getArguments() != null) {
            String username = getArguments().getString("username");
            Log.d(TAG, "Username passed to fragment: " + username);  // Log for verification
            if (username != null) {
                loadUserData(username);
            }
        }


        // Edit Button Click Listener
        btnEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tvNom.setEnabled(true);
                tvPrenom.setEnabled(true);
                tvEmail.setEnabled(true);
                tvPhone.setEnabled(true);
                tvDateNaissance.setEnabled(true);
                tvTaille.setEnabled(true);
                tvSexe.setEnabled(true);
                tvPoids.setEnabled(true);
                tvDetailsAllergies.setEnabled(true);
                tvGroupeSanguin.setEnabled(true);

                // Show radio groups and hide text views
                tvHandicape.setVisibility(View.GONE);
                rgHandicape.setVisibility(View.VISIBLE);

                tvAllergies.setVisibility(View.GONE);
                rgAllergies.setVisibility(View.VISIBLE);

                tvDiabetique.setVisibility(View.GONE);
                rgDiabetique.setVisibility(View.VISIBLE);

                btnSave.setVisibility(View.VISIBLE);
            }
        });

        rgAllergies.setOnCheckedChangeListener((group, checkedId) -> {
            if (checkedId == R.id.rb_allergies_yes) {
                // Show and enable details_allergies field
                details_allergies.setVisibility(View.VISIBLE);
                tvDetailsAllergies.setVisibility(View.VISIBLE);
                tvDetailsAllergies.setEnabled(true);
            } else if (checkedId == R.id.rb_allergies_no) {
                // Hide and disable details_allergies field
                details_allergies.setVisibility(View.GONE);
                tvDetailsAllergies.setVisibility(View.GONE);
                tvDetailsAllergies.setEnabled(false);
                tvDetailsAllergies.setText(""); // Clear the field if "Non" is selected
            }
        });


        // Save Button Click Listener
        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveUserData();
            }
        });

        return view;
    }

    // Load user data from Firebase
    private void loadUserData(String username) {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("users");

        databaseReference.orderByChild("username").equalTo(username)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()) {
                            for (DataSnapshot userSnapshot : dataSnapshot.getChildren()) {
                                User user = userSnapshot.getValue(User.class);
                                if (user != null) {
                                    tvNom.setText(user.getNom() != null ? user.getNom() : "N/A");
                                    tvPrenom.setText(user.getPrenom() != null ? user.getPrenom() : "N/A");
                                    tvEmail.setText(user.getEmail() != null ? user.getEmail() : "N/A");
                                    tvPhone.setText(user.getPhone() != null ? user.getPhone() : "N/A");
                                    tvDateNaissance.setText(user.getDateNaissance() != null ? user.getDateNaissance() : "N/A");
                                    tvSexe.setText(user.getSexe() != null ? user.getSexe() : "N/A");
                                    tvTaille.setText(user.getTaille() != -1 ? String.valueOf(user.getTaille()) : "N/A");
                                    tvPoids.setText(user.getPoids() != -1 ? String.valueOf(user.getPoids()) : "N/A");
                                    tvHandicape.setText(user.isHandicape() ? "Oui" : "Non");
                                    tvDiabetique.setText(user.isDiabetique() ? "Oui" : "Non");
                                    tvAllergies.setText(user.isAllergies() ? "Oui" : "Non");

                                    if (user.isAllergies()) {
                                        tvDetailsAllergies.setText(user.getDetailsAllergies() != null ? user.getDetailsAllergies() : "N/A");
                                        tvDetailsAllergies.setVisibility(View.VISIBLE);
                                        details_allergies.setVisibility(View.VISIBLE);
                                    } else {
                                        tvDetailsAllergies.setVisibility(View.GONE);
                                        details_allergies.setVisibility(View.GONE);
                                    }

                                    tvGroupeSanguin.setText(user.getGroupeSanguin() != null ? user.getGroupeSanguin() : "N/A");

                                    userId = userSnapshot.getKey(); // Assign userId
                                    Log.d(TAG, "User ID found: " + userId);

                                    // Initialize databaseReference after userId is fetched
                                    ProfilFragment.this.databaseReference = FirebaseDatabase.getInstance().getReference("users").child(userId);
                                    break;
                                }
                            }
                        } else {
                            Log.d(TAG, "No user found with username: " + username);
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




    private void saveUserData() {
        if (userId == null) {
            Toast.makeText(getContext(), "User ID is not available. Please try again later.", Toast.LENGTH_SHORT).show();
            return;
        }
        boolean isHandicape = rgHandicape.getCheckedRadioButtonId() == R.id.rb_handicape_yes;
        boolean isAllergies = rgAllergies.getCheckedRadioButtonId() == R.id.rb_allergies_yes;
        boolean isDiabetique = rgDiabetique.getCheckedRadioButtonId() == R.id.rb_diabetique_yes;
        String name = tvNom.getText().toString().trim();
        String firstName = tvPrenom.getText().toString().trim();
        String phone = tvPhone.getText().toString().trim();
        String email = tvEmail.getText().toString().trim();
        double taille = Double.parseDouble(tvTaille.getText().toString().trim());
        double poids = Double.parseDouble(tvPoids.getText().toString().trim());
        String sexe = tvSexe.getText().toString().trim();
        String birthdate = tvDateNaissance.getText().toString().trim();
        String group_sanguin = tvGroupeSanguin.getText().toString().trim();
        String details_allergies_text = "";
        if (isAllergies) {
            details_allergies_text = tvDetailsAllergies.getText().toString().trim();
        }






        if (name.isEmpty() || email.isEmpty()) {
            Toast.makeText(getContext(), "Please fill in all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        databaseReference.child("detailsAllergies").setValue(details_allergies_text);
        databaseReference.child("groupeSanguin").setValue(group_sanguin);
        databaseReference.child("handicape").setValue(isHandicape);
        databaseReference.child("allergies").setValue(isAllergies);
        databaseReference.child("diabetique").setValue(isDiabetique);
        databaseReference.child("nom").setValue(name);
        databaseReference.child("prenom").setValue(firstName);
        databaseReference.child("sexe").setValue(sexe);
        databaseReference.child("phone").setValue(phone);
        databaseReference.child("taille").setValue(taille);
        databaseReference.child("poids").setValue(poids);
        databaseReference.child("dateNaissance").setValue(birthdate);
        databaseReference.child("email").setValue(email).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Toast.makeText(getContext(), "Data updated successfully", Toast.LENGTH_SHORT).show();
                tvNom.setEnabled(false);
                tvPrenom.setEnabled(false);
                tvEmail.setEnabled(false);
                tvPhone.setEnabled(false);
                tvDateNaissance.setEnabled(false);
                tvTaille.setEnabled(false);
                tvSexe.setEnabled(false);
                tvPoids.setEnabled(false);
                tvGroupeSanguin.setEnabled(false);
                tvDetailsAllergies.setEnabled(false);
                tvHandicape.setVisibility(View.VISIBLE);
                rgHandicape.setVisibility(View.GONE);

                // Reset views after saving
                tvHandicape.setText(isHandicape ? "Oui" : "Non");
                tvAllergies.setText(isAllergies ? "Oui" : "Non");
                tvDiabetique.setText(isDiabetique ? "Oui" : "Non");


                tvAllergies.setVisibility(View.VISIBLE);
                rgAllergies.setVisibility(View.GONE);

                tvDiabetique.setVisibility(View.VISIBLE);
                rgDiabetique.setVisibility(View.GONE);
                btnSave.setVisibility(View.GONE);
            } else {
                Toast.makeText(getContext(), "Failed to update data", Toast.LENGTH_SHORT).show();
            }
        });
    }

}
