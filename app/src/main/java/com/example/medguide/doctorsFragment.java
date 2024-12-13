package com.example.medguide;

import android.database.Cursor;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;


public class doctorsFragment extends Fragment{
private RecyclerView recyclerView;
private SearchView searchView;
private MedicamentsAdapter adapter;
private databaseHelper DatabaseHelper;

@Nullable
@Override
public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
    View view = inflater.inflate(R.layout.fragment_doctors, container, false);

    searchView = view.findViewById(R.id.searchView_doctors);
    recyclerView = view.findViewById(R.id.recyclerView_doctors);

    DatabaseHelper = new databaseHelper(requireContext());
    adapter = new MedicamentsAdapter(new ArrayList<>());

    recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
    recyclerView.setAdapter(adapter);

    loadDoctors();

    searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
        @Override
        public boolean onQueryTextSubmit(String query) {
            searchDoctors(query);
            return false;
        }

        @Override
        public boolean onQueryTextChange(String newText) {
            if (newText.isEmpty()) {
                loadDoctors();
            }
            return false;
        }
    });

    return view;
}

private void loadDoctors() {
    Cursor cursor = null;
    try {
        cursor = DatabaseHelper.getDoctorsForDisplay();
        if (cursor == null) {
            Log.e("Database", "Failed to get cursor for doctors.");
            return;
        }

        List<Doctors> doctors = new ArrayList<>();
        if (cursor != null) {
            int nomIndex = cursor.getColumnIndex("NOM");
            int adresseIndex = cursor.getColumnIndex("ADRESSE");
            int numeroIndex = cursor.getColumnIndex("NUMERO");
            int specialiteIndex = cursor.getColumnIndex("SPECIALITE");
            //int tauxIndex = cursor.getColumnIndex("TAUX_REMBOURSEMENT");

            if (nomIndex == -1 || adresseIndex == -1 || numeroIndex == -1 || specialiteIndex == -1 ) {
                throw new IllegalStateException("Column not found in the database query.");
            }

            while (cursor.moveToNext()) {
                doctors.add(new Medicament(
                        cursor.getString(nomIndex),
                        cursor.getString(adresseIndex),
                        cursor.getString(numeroIndex),
                        cursor.getString(specialiteIndex)
                )

            };
        }
        adapter.setMedicaments(doctors);
    } finally {
        if (cursor != null) {
            cursor.close();
        }
    }
}

private void searchDoctors(String name) {
    Cursor cursor = DatabaseHelper.searchDoctorsByName(name);
    List<Doctors> doctors = new ArrayList<>();
    if (cursor != null) {
        int nomIndex = cursor.getColumnIndex("NOM");
        int adresseIndex = cursor.getColumnIndex("ADRESSE");
        int numeroIndex = cursor.getColumnIndex("NUMERO");
        int specialiteIndex = cursor.getColumnIndex("SPECIALITE");
        //int tauxIndex = cursor.getColumnIndex("TAUX_REMBOURSEMENT");

        if (nomIndex == -1 || adresseIndex == -1 || numeroIndex == -1 || specialiteIndex == -1 ) {
            throw new IllegalStateException("Column not found in the database query.");
        }

        while (cursor.moveToNext()) {
            doctors.add(new Medicament(
                    cursor.getString(nomIndex),
                    cursor.getString(adresseIndex),
                    cursor.getString(numeroIndex),
                    cursor.getString(specialiteIndex)

            ));
        }
        cursor.close();
    }
    adapter.setMedicaments(doctors);
}


}


