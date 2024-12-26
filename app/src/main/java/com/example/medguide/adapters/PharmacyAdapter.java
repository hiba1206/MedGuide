package com.example.medguide.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.medguide.R;
import com.example.medguide.models.Pharmacy;

import java.util.List;

public class PharmacyAdapter extends RecyclerView.Adapter<PharmacyAdapter.PharmacyViewHolder> {
    private final List<Pharmacy> pharmacyList;

    public PharmacyAdapter(List<Pharmacy> pharmacyList) {
        this.pharmacyList = pharmacyList;
    }

    @NonNull
    @Override
    public PharmacyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Inflate the item layout
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_pharmacy, parent, false);
        return new PharmacyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PharmacyViewHolder holder, int position) {
        // Bind data to the views
        Pharmacy pharmacy = pharmacyList.get(position);
        holder.nameTextView.setText(pharmacy.getName());
        holder.addressTextView.setText(pharmacy.getAddress());
    }

    @Override
    public int getItemCount() {
        return pharmacyList.size();
    }

    static class PharmacyViewHolder extends RecyclerView.ViewHolder {
        TextView nameTextView;
        TextView addressTextView;

        PharmacyViewHolder(View itemView) {
            super(itemView);
            nameTextView = itemView.findViewById(R.id.pharmacyName);
            addressTextView = itemView.findViewById(R.id.pharmacyAddress);
        }
    }
}
