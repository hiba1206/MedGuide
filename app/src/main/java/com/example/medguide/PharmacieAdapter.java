package com.example.medguide;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class PharmacieAdapter  extends RecyclerView.Adapter<PharmacieAdapter.ViewHolder> implements Filterable {


    private List<Pharmacie> pharmacies;
    private List<Pharmacie> pharmaciesFull; // Full list for searching

    public PharmacieAdapter(List<Pharmacie> pharmacies) {
        this.pharmacies= pharmacies;
        pharmaciesFull = new ArrayList<>(pharmacies);
    }

    @NonNull
    @Override
    public PharmacieAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_pharmacie, parent, false);
        return new PharmacieAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PharmacieAdapter.ViewHolder holder, int position) {
       Pharmacie pharmacie1 = pharmacies.get(position);
        holder.nameTextView.setText(pharmacie1.getName());
        holder.adresseTextView.setText("Adresse: " +pharmacie1.getAdresse());
        holder.numeroTextView.setText("Numero: 0" + pharmacie1.getNumero());  // Add Forme
        holder.horaireTextView.setText("Horaire: " + pharmacie1.getHoraire());  // Add Presentation
    }


    @Override
    public int getItemCount() {
        return pharmacies.size();
    }

    @Override
    public Filter getFilter() {
        return pharmacieFilter;
    }

    private Filter pharmacieFilter = new Filter() {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            List<Pharmacie> filteredList = new ArrayList<>();
            if (constraint == null || constraint.length() == 0) {
                filteredList.addAll(pharmaciesFull);
            } else {
                String filterPattern = constraint.toString().toLowerCase().trim();
                for (Pharmacie pharmacie : pharmaciesFull) {
                    if (pharmacie.matchesSearch(filterPattern)) {

                        filteredList.add(pharmacie);
                    }
                }
            }
            FilterResults results = new FilterResults();
            results.values = filteredList;
            return results;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            pharmacies.clear();
            pharmacies.addAll((List) results.values);
            notifyDataSetChanged();
        }
    };

    // Add this method to update the doctors list
    public void setPharmacies(List<Pharmacie> newPharmacie) {
        this.pharmacies.clear();
        this.pharmacies.addAll(newPharmacie);
        this.pharmaciesFull.clear();
        this.pharmaciesFull.addAll(newPharmacie); // Update the full list for filtering
        notifyDataSetChanged(); // Notify the RecyclerView of data changes
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView nameTextView, adresseTextView, numeroTextView,horaireTextView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            nameTextView = itemView.findViewById(R.id.textViewName);
            adresseTextView = itemView.findViewById(R.id.textViewAdresse);
            numeroTextView = itemView.findViewById(R.id.textViewNumero);
            horaireTextView=itemView.findViewById(R.id.textViewHoraire);

        }
    }
}


