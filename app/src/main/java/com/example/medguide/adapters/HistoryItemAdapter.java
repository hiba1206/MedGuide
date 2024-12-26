package com.example.medguide.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.medguide.R;
import com.example.medguide.models.HistoryItem;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class HistoryItemAdapter extends RecyclerView.Adapter<HistoryItemAdapter.HistoryViewHolder> {

    private List<HistoryItem> historyList;

    public HistoryItemAdapter(List<HistoryItem> historyList) {
        this.historyList = historyList;
    }

    @NonNull
    @Override
    public HistoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_history, parent, false);
        return new HistoryViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull HistoryViewHolder holder, int position) {
        HistoryItem historyItem = historyList.get(position);

        // Convert timestamp to human-readable date
        long timestampMillis = Long.parseLong(historyItem.getTimestamp());
        String readableDate = convertTimestampToDate(timestampMillis);

        holder.symptomsTextView.setText("Symptômes: " + historyItem.getSymptoms());
        holder.timestampTextView.setText(readableDate);
        holder.responseTextView.setText("Réponse: " + historyItem.getResponse());

        // Initially hide the response and set "View More"
        holder.responseTextView.setVisibility(View.GONE);
        holder.viewMoreTextView.setText("Voir Plus");

        holder.viewMoreTextView.setOnClickListener(view -> {
            if (holder.responseTextView.getVisibility() == View.GONE) {
                // Show response and change text to "Hide"
                holder.responseTextView.setVisibility(View.VISIBLE);
                holder.viewMoreTextView.setText("Voir Moins");
            } else {
                // Hide response and change text to "View More"
                holder.responseTextView.setVisibility(View.GONE);
                holder.viewMoreTextView.setText("Voir Plus");
            }
        });
    }


    @Override
    public int getItemCount() {
        return historyList.size();
    }

    static class HistoryViewHolder extends RecyclerView.ViewHolder {
        TextView symptomsTextView, responseTextView, timestampTextView, viewMoreTextView;

        public HistoryViewHolder(@NonNull View itemView) {
            super(itemView);
            symptomsTextView = itemView.findViewById(R.id.symptomsTextView);
            responseTextView = itemView.findViewById(R.id.responseTextView);
            timestampTextView = itemView.findViewById(R.id.timestampTextView);
            viewMoreTextView = itemView.findViewById(R.id.viewMoreTextView);
        }
    }


    private String convertTimestampToDate(long timestampMillis) {
        // Convert timestamp (in milliseconds) to a human-readable date
        SimpleDateFormat sdf = new SimpleDateFormat("dd MMM yyyy HH:mm:ss", Locale.getDefault());
        Date date = new Date(timestampMillis);
        return sdf.format(date);
    }
}
