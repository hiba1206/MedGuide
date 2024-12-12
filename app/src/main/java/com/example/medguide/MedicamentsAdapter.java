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

public class MedicamentsAdapter extends RecyclerView.Adapter<MedicamentsAdapter.ViewHolder> implements Filterable {
    private List<Medicament> medicaments;
    private List<Medicament> medicamentsFull; // Full list for searching

    public MedicamentsAdapter(List<Medicament> medicaments) {
        this.medicaments = medicaments;
        medicamentsFull = new ArrayList<>(medicaments);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_medicament, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Medicament medicament = medicaments.get(position);
        holder.nameTextView.setText(medicament.getName());
        holder.priceTextView.setText("Prix: " +medicament.getPrice()+" DH");
        holder.reimbursementTextView.setText("Taux de Remboursement: " +medicament.getTauxRemboursement());
        holder.formeTextView.setText("Forme: " + medicament.getForme());  // Add Forme
        holder.presentationTextView.setText("Présentation: " + medicament.getPresentation());  // Add Presentation
    }


    @Override
    public int getItemCount() {
        return medicaments.size();
    }

    @Override
    public Filter getFilter() {
        return medicamentFilter;
    }

    private Filter medicamentFilter = new Filter() {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            List<Medicament> filteredList = new ArrayList<>();
            if (constraint == null || constraint.length() == 0) {
                filteredList.addAll(medicamentsFull);
            } else {
                String filterPattern = constraint.toString().toLowerCase().trim();
                for (Medicament medicament : medicamentsFull) {
                    if (medicament.matchesSearch(filterPattern)) {
                        filteredList.add(medicament);
                    }
                }
            }
            FilterResults results = new FilterResults();
            results.values = filteredList;
            return results;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            medicaments.clear();
            medicaments.addAll((List) results.values);
            notifyDataSetChanged();
        }
    };

    // Add this method to update the medicaments list
    public void setMedicaments(List<Medicament> newMedicaments) {
        this.medicaments.clear();
        this.medicaments.addAll(newMedicaments);
        this.medicamentsFull.clear();
        this.medicamentsFull.addAll(newMedicaments); // Update the full list for filtering
        notifyDataSetChanged(); // Notify the RecyclerView of data changes
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView nameTextView, priceTextView, reimbursementTextView,formeTextView,presentationTextView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            nameTextView = itemView.findViewById(R.id.textViewName);
            priceTextView = itemView.findViewById(R.id.textViewPrix);
            reimbursementTextView = itemView.findViewById(R.id.textViewTaux);
            formeTextView=itemView.findViewById(R.id.textViewForme);
            presentationTextView=itemView.findViewById(R.id.textViewPresentation);

        }
    }
}
