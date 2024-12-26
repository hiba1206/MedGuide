package com.example.medguide;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.medguide.adapters.PharmacyAdapter;
import com.example.medguide.models.Pharmacy;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.util.ArrayList;
import java.util.List;

public class gardeFragment extends Fragment {

    private RecyclerView recyclerView;
    private ProgressBar progressBar;
    private PharmacyAdapter adapter;
    private List<Pharmacy> pharmacyList = new ArrayList<>();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_garde, container, false);

        recyclerView = view.findViewById(R.id.pharmacyRecyclerView);
        progressBar = view.findViewById(R.id.loadingProgressBar);

        adapter = new PharmacyAdapter(pharmacyList);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(adapter);

        loadData();

        return view;
    }

    private void loadData() {
        // Show the progress bar before loading data
        progressBar.setVisibility(View.VISIBLE);
        recyclerView.setVisibility(View.GONE);

        // Load data in a background thread
        new Thread(() -> {
            try {
                // Fetch and parse the data
                Document doc = Jsoup.connect("https://lematin.ma/pharmacie-garde/oujda/jour/oujda").get();
                Elements pharmacies = doc.select("div.ph-record");

                // Clear the list and add new data
                pharmacyList.clear();
                for (int i = 0; i < pharmacies.size(); i++) {
                    String name = pharmacies.get(i).select("div.ph-name").text();
                    String address = pharmacies.get(i).select("div.ph-address").text();
                    pharmacyList.add(new Pharmacy(name, address));
                }

                // Update UI on the main thread
                if (getActivity() != null) {
                    getActivity().runOnUiThread(() -> {
                        progressBar.setVisibility(View.GONE);
                        recyclerView.setVisibility(View.VISIBLE);
                        adapter.notifyDataSetChanged();
                    });
                }
            } catch (Exception e) {
                e.printStackTrace();
                if (getActivity() != null) {
                    getActivity().runOnUiThread(() -> {
                        progressBar.setVisibility(View.GONE);
                        recyclerView.setVisibility(View.VISIBLE);
                        Toast.makeText(getContext(), "Failed to load data", Toast.LENGTH_SHORT).show();
                    });
                }
            }
        }).start();
    }
}
