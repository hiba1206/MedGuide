package com.example.medguide;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
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

import java.util.HashMap;
import java.util.Map;

public class ProfilFragment extends Fragment {

    private static final String TAG = "ProfilFragment";

    // Editable fields for user data
    private EditText etNom, etPrenom, etEmail, etPhone, etDateNaissance, etSexe;
    private EditText etTaille, etPoids, etGroupeSanguin, etDetailsAllergies;
    private Button btnSave;

    // Checkboxes for additional user data
    private CheckBox cbHandicape, cbDiabetique, cbAllergie;

    private String username;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profil, container, false);

        // Initialize fields
        etNom = view.findViewById(R.id.tv_nom);
        etPrenom = view.findViewById(R.id.tv_prenom);
        etEmail = view.findViewById(R.id.tv_email);
        etPhone = view.findViewById(R.id.tv_phone);
        etDateNaissance = view.findViewById(R.id.tv_date_naissance);
        etSexe = view.findViewById(R.id.tv_sexe);

        etTaille = view.findViewById(R.id.tv_taille);
        etPoids = view.findViewById(R.id.tv_poids);
        etGroupeSanguin = view.findViewById(R.id.tv_groupe_sanguin);
        etDetailsAllergies = view.findViewById(R.id.tv_details_allergies);

        btnSave = view.findViewById(R.id.btn_save);

        // Initialize checkboxes
        cbHandicape = view.findViewById(R.id.cb_handicape);
        cbDiabetique = view.findViewById(R.id.cb_diabetique);
        cbAllergie = view.findViewById(R.id.cb_allergie);

        // Retrieve username
        if (getArguments() != null) {
            username = getArguments().getString("username");
            if (username != null) {
                loadUserData(username);
            }
        }

        // Save button click listener
        btnSave.setOnClickListener(v -> saveUserData());

        return view;
    }

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
                                    // Populate fields
                                    etNom.setText(user.getNom());
                                    etPrenom.setText(user.getPrenom());
                                    etEmail.setText(user.getEmail());
                                    etPhone.setText(user.getPhone());
                                    etDateNaissance.setText(user.getDateNaissance());
                                    etSexe.setText(user.getSexe());
                                    etTaille.setText(String.valueOf(user.getTaille()));
                                    etPoids.setText(String.valueOf(user.getPoids()));
                                    etGroupeSanguin.setText(user.getGroupeSanguin());
                                    etDetailsAllergies.setText(user.getDetailsAllergies());

                                    // Display the checkbox values
                                    cbHandicape.setChecked(user.isHandicape());
                                    cbDiabetique.setChecked(user.isDiabetique());
                                    cbAllergie.setChecked(user.isAllergies());

                                    // Show or hide allergy details based on the allergies field
                                    etDetailsAllergies.setVisibility(user.isAllergies() ? View.VISIBLE : View.GONE);
                                    break;
                                }
                            }
                        } else {
                            Toast.makeText(getContext(), "Utilisateur non trouvé", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        Toast.makeText(getContext(), "Erreur lors de la lecture des données", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private boolean isValidDouble(String str) {
        try {
            Double.parseDouble(str);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    private void saveUserData() {
        try {
            String tailleStr = etTaille.getText().toString();
            String poidsStr = etPoids.getText().toString();

            if (!isValidDouble(tailleStr) || !isValidDouble(poidsStr)) {
                Toast.makeText(getContext(), "Veuillez entrer des valeurs numériques valides pour la taille et le poids", Toast.LENGTH_SHORT).show();
                return;
            }

            double taille = Double.parseDouble(tailleStr);
            double poids = Double.parseDouble(poidsStr);

            // Vérifiez que les champs obligatoires ne sont pas vides
            if (etNom.getText().toString().isEmpty() || etPrenom.getText().toString().isEmpty() ||
                    etEmail.getText().toString().isEmpty() || etPhone.getText().toString().isEmpty() ||
                    etDateNaissance.getText().toString().isEmpty() || etSexe.getText().toString().isEmpty() ||
                    etGroupeSanguin.getText().toString().isEmpty()) {

                Toast.makeText(getContext(), "Veuillez remplir tous les champs", Toast.LENGTH_SHORT).show();
                return;
            }

            // Get the new checkbox values
            boolean handicape = cbHandicape.isChecked();
            boolean diabetique = cbDiabetique.isChecked();
            boolean allergies = cbAllergie.isChecked();

            // Création d'un utilisateur mis à jour
            User updatedUser = new User();
            updatedUser.setUsername(username);
            updatedUser.setNom(etNom.getText().toString());
            updatedUser.setPrenom(etPrenom.getText().toString());
            updatedUser.setEmail(etEmail.getText().toString());
            updatedUser.setPhone(etPhone.getText().toString());
            updatedUser.setDateNaissance(etDateNaissance.getText().toString());
            updatedUser.setSexe(etSexe.getText().toString());
            updatedUser.setTaille(taille);
            updatedUser.setPoids(poids);
            updatedUser.setGroupeSanguin(etGroupeSanguin.getText().toString());
            updatedUser.setDetailsAllergies(etDetailsAllergies.getText().toString());
            updatedUser.setAllergies(allergies);
            updatedUser.setHandicape(handicape);
            updatedUser.setDiabetique(diabetique);

            // Log des données envoyées
            Log.d(TAG, "Données envoyées : " + updatedUser.toString());

            // Mise à jour dans Firebase
            DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("users").child(username);

            Map<String, Object> updates = new HashMap<>();
            updates.put("nom", updatedUser.getNom());
            updates.put("prenom", updatedUser.getPrenom());
            updates.put("email", updatedUser.getEmail());
            updates.put("phone", updatedUser.getPhone());
            updates.put("dateNaissance", updatedUser.getDateNaissance());
            updates.put("sexe", updatedUser.getSexe());
            updates.put("taille", updatedUser.getTaille());
            updates.put("poids", updatedUser.getPoids());
            updates.put("groupeSanguin", updatedUser.getGroupeSanguin());
            updates.put("detailsAllergies", updatedUser.getDetailsAllergies());
            updates.put("allergies", updatedUser.isAllergies());
            updates.put("handicape", updatedUser.isHandicape());
            updates.put("diabetique", updatedUser.isDiabetique());

            databaseReference.updateChildren(updates).addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    Toast.makeText(getContext(), "Modifications enregistrées avec succès", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getContext(), "Erreur lors de l'enregistrement", Toast.LENGTH_SHORT).show();
                }
            });
        } catch (Exception e) {
            Toast.makeText(getContext(), "Erreur inattendue : " + e.getMessage(), Toast.LENGTH_SHORT).show();
            Log.e(TAG, "Erreur lors de la sauvegarde des données", e);
        }
    }
}
