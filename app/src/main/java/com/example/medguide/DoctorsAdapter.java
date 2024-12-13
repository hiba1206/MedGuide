package com.example.medguide;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.text.BreakIterator;
import java.util.ArrayList;
import java.util.List;

public class DoctorsAdapter extends RecyclerView.Adapter<DoctorsAdapter.ViewHolder> implements Filterable{


    private List<Doctors> doctors;
    private List<Doctors> doctorsFull; // Full list for searching

    public DoctorsAdapter(List<Medicament> medicaments) {
            this.doctors = doctors;
            doctorsFull = new ArrayList<>(doctors);
        }

        @NonNull
        @Override
        public DoctorsAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_doctors, parent, false);
            return new DoctorsAdapter.ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
            Doctors doctors1 = doctors.get(position);
            holder.nameTextView.setText(doctors1.getName());
            holder.AdresseTextView.setText("Adresse: " +doctors1.getAdresse());
           // holdeTextView.setText("Taux de Remboursement: " +doctors.getTauxRemboursement());
            holder.NumeroTextView.setText("numero: " + doctors1.getNumero());  // Add Forme
            holder.SpecialiteTextView.setText("specialite: " + doctors1.getSpecialite());  // Add Presentation
        }


        @Override
        public int getItemCount() {
            return doctors.size();
        }

        @Override
        public Filter getFilter() {
            return doctorsFilter;
        }

        private Filter doctorsFilter = new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                List<Doctors> filteredList = new ArrayList<>();
                if (constraint == null || constraint.length() == 0) {
                    filteredList.addAll(doctorsFull);
                } else {
                    String filterPattern = constraint.toString().toLowerCase().trim();
                    for (Doctors doctors : doctorsFull) {
                        if (doctors.matchesSearch(filterPattern)) {

                            filteredList.add(doctors);
                        }
                    }
                }
                FilterResults results = new FilterResults();
                results.values = filteredList;
                return results;
            }

            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                doctors.clear();
                doctors.addAll((List) results.values);
                notifyDataSetChanged();
            }
        };

    // Add this method to update the doctors list
    public void setDoctors(List<Doctors> newDoctors) {
        this.doctors.clear();
        this.doctors.addAll(newDoctors);
        this.doctorsFull.clear();
        this.doctorsFull.addAll(newDoctors); // Update the full list for filtering
        notifyDataSetChanged(); // Notify the RecyclerView of data changes
    }

        public class ViewHolder extends RecyclerView.ViewHolder {
            public BreakIterator SpecialiteTextView;
            public BreakIterator AdresseTextView;
            public BreakIterator NumeroTextView;
            TextView nameTextView, adresseTextView, numeroTextView,specialiteTextView;

            public ViewHolder(@NonNull View itemView) {
                super(itemView);
                nameTextView = itemView.findViewById(R.id.textViewName);
                adresseTextView = itemView.findViewById(R.id.textViewAdresse);
                numeroTextView = itemView.findViewById(R.id.textViewNumero);
                specialiteTextView=itemView.findViewById(R.id.textViewSpecialite);
                //presentationTextView=itemView.findViewById(R.id.textViewPresentation);

            }
        }
    }
