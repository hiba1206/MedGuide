package com.example.medguide;

public class Pharmacie {

    private String name;
    private String adresse;
    private String numero;
    private String horaire;


    // Constructor
    public Pharmacie(String name, String adresse, String numeron, String horaire) {
        this.name = name;
        this.adresse = adresse;
        this.numero = numeron;
        this.horaire = horaire;

    }

    // Getters
    public String getName() {
        return name;
    }

    public String getAdresse() {
        return numero;
    }

    public String getNumero() {
        return numero;
    }

    public String getHoraire() {
        return horaire;
    }



    // Setters (if needed)
    public void setName(String name) {
        this.name = name;
    }

    public void setAdresse(String adresse) {
        this.adresse = adresse;
    }

    public void setNumero(String numero) {
        this.numero = numero;
    }

    public void setHoraire(String horaire) {
        this.horaire= horaire;
    }


    public boolean matchesSearch(String query) {
        String queryLower = query.toLowerCase().trim();
        return name.toLowerCase().contains(queryLower) ||
                adresse.toLowerCase().contains(queryLower) ||
                //tauxRemboursement.toLowerCase().contains(queryLower) ||
                (numero != null && numero.toLowerCase().contains(queryLower)) ||
                (horaire != null && horaire.toLowerCase().contains(queryLower));
    }
}
