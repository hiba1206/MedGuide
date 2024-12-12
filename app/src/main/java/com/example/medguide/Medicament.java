package com.example.medguide;

public class Medicament {
    private String name;
    private String forme;
    private String presentation;
    private String prix;
    private String tauxRemboursement;

    // Constructor
    public Medicament(String name, String forme, String presentation, String prix, String tauxRemboursement) {
        this.name = name;
        this.forme = forme;
        this.presentation = presentation;
        this.prix = prix;
        this.tauxRemboursement = tauxRemboursement;
    }

    // Getters
    public String getName() {
        return name;
    }

    public String getForme() {
        return forme;
    }

    public String getPresentation() {
        return presentation;
    }

    public String getPrice() {
        return prix;
    }

    public String getTauxRemboursement() {
        return tauxRemboursement;
    }

    // Setters (if needed)
    public void setName(String name) {
        this.name = name;
    }

    public void setForme(String forme) {
        this.forme = forme;
    }

    public void setPresentation(String presentation) {
        this.presentation = presentation;
    }

    public void setPrix(String prix) {
        this.prix = prix;
    }

    public void setTauxRemboursement(String tauxRemboursement) {
        this.tauxRemboursement = tauxRemboursement;
    }

    public boolean matchesSearch(String query) {
        String queryLower = query.toLowerCase().trim();
        return name.toLowerCase().contains(queryLower) ||
                prix.toLowerCase().contains(queryLower) ||
                tauxRemboursement.toLowerCase().contains(queryLower) ||
                (forme != null && forme.toLowerCase().contains(queryLower)) ||
                (presentation != null && presentation.toLowerCase().contains(queryLower));
    }
}
