package com.example.medguide;

import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.appcompat.widget.SearchView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import java.util.List;


public class medicamentsFragment extends Fragment {
    private RecyclerView recyclerView;
    private SearchView searchView;
    private MedicamentsAdapter adapter;
    private databaseHelper DatabaseHelper;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_medicaments, container, false);

        searchView = view.findViewById(R.id.searchView_medicaments);
        recyclerView = view.findViewById(R.id.recyclerView_medicaments);

        DatabaseHelper = new databaseHelper(requireContext());
        adapter = new MedicamentsAdapter(new ArrayList<>());

        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(adapter);

        loadMedicaments();

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                searchMedicaments(query);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                if (newText.isEmpty()) {
                    loadMedicaments();
                }
                return false;
            }
        });

        return view;
    }

    private void loadMedicaments() {
        Cursor cursor = null;
        try {
            cursor = DatabaseHelper.getMedicamentsForDisplay();
            if (cursor == null) {
                Log.e("Database", "Failed to get cursor for medicaments.");
                return;
            }

            List<Medicament> medicaments = new ArrayList<>();
            if (cursor != null) {
                int nomIndex = cursor.getColumnIndex("NOM");
                int formeIndex = cursor.getColumnIndex("FORME");
                int presentationIndex = cursor.getColumnIndex("PRESENTATION");
                int prixIndex = cursor.getColumnIndex("PRIX_BR");
                int tauxIndex = cursor.getColumnIndex("TAUX_REMBOURSEMENT");

                if (nomIndex == -1 || formeIndex == -1 || presentationIndex == -1 || prixIndex == -1 || tauxIndex == -1) {
                    throw new IllegalStateException("Column not found in the database query.");
                }

                while (cursor.moveToNext()) {
                    medicaments.add(new Medicament(
                            cursor.getString(nomIndex),
                            cursor.getString(formeIndex),
                            cursor.getString(presentationIndex),
                            cursor.getString(prixIndex),
                            cursor.getString(tauxIndex)
                    ));
                }
            }
            adapter.setMedicaments(medicaments);
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
    }

    private void searchMedicaments(String name) {
        Cursor cursor = DatabaseHelper.searchMedicamentByName(name);
        List<Medicament> medicaments = new ArrayList<>();
        if (cursor != null) {
            int nomIndex = cursor.getColumnIndex("NOM");
            int formeIndex = cursor.getColumnIndex("FORME");
            int presentationIndex = cursor.getColumnIndex("PRESENTATION");
            int prixIndex = cursor.getColumnIndex("PRIX_BR");
            int tauxIndex = cursor.getColumnIndex("TAUX_REMBOURSEMENT");

            if (nomIndex == -1 || formeIndex == -1 || presentationIndex == -1 || prixIndex == -1 || tauxIndex == -1) {
                throw new IllegalStateException("Column not found in the database query.");
            }

            while (cursor.moveToNext()) {
                medicaments.add(new Medicament(
                        cursor.getString(nomIndex),
                        cursor.getString(formeIndex),
                        cursor.getString(presentationIndex),
                        cursor.getString(prixIndex),
                        cursor.getString(tauxIndex)
                ));
            }
            cursor.close();
        }
        adapter.setMedicaments(medicaments);
    }


}