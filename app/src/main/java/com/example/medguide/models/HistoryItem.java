package com.example.medguide.models;

public class HistoryItem {
    private String symptoms;
    private String response;
    private String timestamp; // To track when the entry was added

    // Default constructor for Firebase
    public HistoryItem() {}

    // Constructor
    public HistoryItem(String symptoms, String response, String timestamp) {
        this.symptoms = symptoms;
        this.response = response;
        this.timestamp = timestamp;
    }

    // Getters and Setters
    public String getSymptoms() {
        return symptoms;
    }

    public void setSymptoms(String symptoms) {
        this.symptoms = symptoms;
    }

    public String getResponse() {
        return response;
    }

    public void setResponse(String response) {
        this.response = response;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }
}
