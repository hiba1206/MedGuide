package com.example.medguide;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.medguide.adapters.HistoryItemAdapter;
import com.example.medguide.models.HistoryItem;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import java.util.ArrayList;
import java.util.List;

public class HistoriqueFragment extends Fragment {

    private RecyclerView historyRecyclerView;
    private HistoryItemAdapter adapter;
    private List<HistoryItem> historyList = new ArrayList<>();
    private ProgressBar progressBar;
    private TextView emptyTextView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_historique, container, false);

        historyRecyclerView = rootView.findViewById(R.id.historyRecyclerView);
        progressBar = rootView.findViewById(R.id.progressBar);
        emptyTextView = rootView.findViewById(R.id.emptyTextView);

        // Set up RecyclerView
        historyRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new HistoryItemAdapter(historyList);
        historyRecyclerView.setAdapter(adapter);

        // Retrieve username from arguments
        if (getArguments() != null) {
            String username = getArguments().getString("username");
            if (username != null) {
                fetchHistoryFromFirebase(username);
            } else {
                Toast.makeText(getContext(), "Username is missing.", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(getContext(), "Arguments are missing.", Toast.LENGTH_SHORT).show();
        }

        return rootView;
    }

    private void fetchHistoryFromFirebase(String username) {
        progressBar.setVisibility(View.VISIBLE);

        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("users");

        databaseReference.orderByChild("username").equalTo(username)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        historyList.clear(); // Clear previous data

                        if (dataSnapshot.exists()) {
                            for (DataSnapshot userSnapshot : dataSnapshot.getChildren()) {
                                DataSnapshot historySnapshot = userSnapshot.child("history");
                                if (historySnapshot.exists()) {
                                    for (DataSnapshot itemSnapshot : historySnapshot.getChildren()) {
                                        HistoryItem historyItem = itemSnapshot.getValue(HistoryItem.class);
                                        if (historyItem != null) {
                                            historyList.add(historyItem);
                                        }
                                    }
                                } else {
                                    Toast.makeText(getContext(), "No history found for this user.", Toast.LENGTH_SHORT).show();
                                }
                            }
                        } else {
                            Toast.makeText(getContext(), "User not found.", Toast.LENGTH_SHORT).show();
                        }

                        progressBar.setVisibility(View.GONE);

                        if (historyList.isEmpty()) {
                            emptyTextView.setVisibility(View.VISIBLE);
                            historyRecyclerView.setVisibility(View.GONE);
                        } else {
                            emptyTextView.setVisibility(View.GONE);
                            historyRecyclerView.setVisibility(View.VISIBLE);
                            adapter.notifyDataSetChanged();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        progressBar.setVisibility(View.GONE);
                        Toast.makeText(getContext(), "Failed to load history: " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }
}
