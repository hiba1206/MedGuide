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

public class pharmacieFragment extends Fragment{
private RecyclerView recyclerView;
private SearchView searchView;
private PharmacieAdapter adapter;
private databaseHelper DatabaseHelper;

@Nullable
@Override
public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
    View view = inflater.inflate(R.layout.fragment_pharmacie, container, false);

    searchView = view.findViewById(R.id.searchView_pharmacie);
    recyclerView = view.findViewById(R.id.recyclerView_pharmacie);

    DatabaseHelper = new databaseHelper(requireContext());
    adapter = new PharmacieAdapter(new ArrayList<>());

    recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
    recyclerView.setAdapter(adapter);

    loadPharmacie();

    searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
        @Override
        public boolean onQueryTextSubmit(String query) {
            searchPharmacie(query);
            return false;
        }

        @Override
        public boolean onQueryTextChange(String newText) {
            if (newText.isEmpty()) {
                loadPharmacie();
            }
            return false;
        }
    });

    return view;
}

private void loadPharmacie() {
    Cursor cursor = null;
    Cursor testCursor = DatabaseHelper.getReadableDatabase().rawQuery("SELECT * FROM pharmacies LIMIT 1", null);
    if (testCursor != null && testCursor.moveToFirst()) {
        String nom= testCursor.getString(testCursor.getColumnIndex("nom"));
        Log.d("Database", "First pharmacie: " +nom);
    }
    if (testCursor != null) {
        testCursor.close();
    }

    try {
        cursor = DatabaseHelper.getPharmacieForDisplay();
        if (cursor == null) {
            Log.e("Database", "Failed to get cursor for doctors.");
            return;
        }

        List<Pharmacie>pharmacie = new ArrayList<>();
        if (cursor != null) {
            int nomIndex = cursor.getColumnIndex("nom");
            int adresseIndex = cursor.getColumnIndex("adresse");
            int numeroIndex = cursor.getColumnIndex("numero");
            int horaireIndex = cursor.getColumnIndex("horaire");

            if (nomIndex == -1 || adresseIndex == -1 || numeroIndex == -1 || horaireIndex == -1 ) {
                Log.e("Database", "One or more columns not found in the database query.");
                return;
            }

            while (cursor.moveToNext()) {
                Log.d("Database", "Doctor found: " + cursor.getString(nomIndex));
                pharmacie.add(new Pharmacie(
                        cursor.getString(nomIndex),
                        cursor.getString(adresseIndex),
                        cursor.getString(numeroIndex),
                        cursor.getString(horaireIndex)
                ));

            };
        }
        adapter.setPharmacies(pharmacie);
    } finally {
        if (cursor != null) {
            cursor.close();
        }
    }
}

private void searchPharmacie(String name) {
    Cursor cursor = DatabaseHelper.searchPharmacieByName(name);
    List<Pharmacie> pharmacie = new ArrayList<>();
    if (cursor != null) {
        int nomIndex = cursor.getColumnIndex("nom");
        int adresseIndex = cursor.getColumnIndex("adresse");
        int numeroIndex = cursor.getColumnIndex("numero");
        int horaireIndex = cursor.getColumnIndex("horaire");

        if (nomIndex == -1 || adresseIndex == -1 || numeroIndex == -1 || horaireIndex == -1 ) {
            throw new IllegalStateException("Column not found in the database query.");
        }

        while (cursor.moveToNext()) {
            pharmacie.add(new Pharmacie(
                    cursor.getString(nomIndex),
                    cursor.getString(adresseIndex),
                    cursor.getString(numeroIndex),
                    cursor.getString(horaireIndex)

            ));
        }
        cursor.close();
    }
    adapter.setPharmacies(pharmacie);
}

}


