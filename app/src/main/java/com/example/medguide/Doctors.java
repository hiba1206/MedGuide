package com.example.medguide;

public class Doctors {
     private String name;
     private String adresse;
     private String numero;
     private String specialite;
    //private String tauxRemboursement;

    // Constructor
    public Doctors(String name, String adresse, String  numero, String specialite) {
        this.name = name;
        this.adresse = adresse;
        this.numero= numero;
        this.specialite = specialite;
        //this.tauxRemboursement = tauxRemboursement;
    }


    // Getters
    public String getName() {
        return name;
    }

    public static String getAdresse() {
        return adresse;
    }

    public String getNumero() {
        return numero;
    }

    public String getSpecialite() {
        return specialite;
    }

    //public String getTauxRemboursement() {
    //  return tauxRemboursement;
    // }

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

    public void setSpecialite(String specialite) {
        this.specialite= specialite;
    }

    /// public void setTauxRemboursement(String tauxRemboursement) {
    //   this.tauxRemboursement = tauxRemboursement;
    //  }

    public boolean matchesSearch(String query) {
        String queryLower = query.toLowerCase().trim();
        return name.toLowerCase().contains(queryLower) ||
                adresse.toLowerCase().contains(queryLower) ||
                // tauxRemboursement.toLowerCase().contains(queryLower) ||
                (numero != null && numero.toLowerCase().contains(queryLower)) ||
                (specialite != null && specialite.toLowerCase().contains(queryLower));
    }


}


