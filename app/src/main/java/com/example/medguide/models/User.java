package com.example.medguide.models;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class User implements Serializable {
    // Personal Information
    private String username;
    private String nom;
    private String prenom;
    private String email;
    private String phone;
    private String dateNaissance;
    private double taille; // height in cm
    private double poids;  // weight in kg
    private String sexe;   // "Homme" or "Femme"
    private String password;
    private String userId;
    private Map<String, Object> history; // Replace List with Map if Firebase stores history as a map





    // Health Information
    private boolean handicape;
    private boolean diabetique;
    private boolean allergies;
    private String detailsAllergies;
    private String groupeSanguin;
    private static final long serialVersionUID = 1L;




    // Default constructor required for Firebase
    public User() {
    }

    // Constructor
    public User(String username,String nom, String prenom, String email, String phone, String dateNaissance,
                String sexe, String password) {
        this.username = username;
        this.nom = nom;
        this.prenom = prenom;
        this.email = email;
        this.phone = phone;
        this.dateNaissance = dateNaissance;
        this.sexe = sexe;
        this.password = password;
    }

    public Map<String, Object> getHistory() {
        return history;
    }

    public void setHistory(Map<String, Object> history) {
        this.history = history;
    }





    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getUsername() {
        return username;
    }

    public String getNom() {
        return nom;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public String getPrenom() {
        return prenom;
    }

    public void setPrenom(String prenom) {
        this.prenom = prenom;
    }

    public String getEmail() {
        return email;
    }

    public String getPhone() {
        return phone;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getDateNaissance() {
        return dateNaissance;
    }

    public void setDateNaissance(String dateNaissance) {
        this.dateNaissance = dateNaissance;
    }

    public double getTaille() {
        return taille;
    }

    public void setTaille(double taille) {
        this.taille = taille;
    }

    public double getPoids() {
        return poids;
    }

    public void setPoids(double poids) {
        this.poids = poids;
    }

    public String getSexe() {
        return sexe;
    }

    public void setSexe(String sexe) {
        this.sexe = sexe;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public boolean isHandicape() {
        return handicape;
    }

    public void setHandicape(boolean handicape) {
        this.handicape = handicape;
    }

    public boolean isDiabetique() {
        return diabetique;
    }

    public void setDiabetique(boolean diabetique) {
        this.diabetique = diabetique;
    }

    public boolean isAllergies() {
        return allergies;
    }

    public void setAllergies(boolean allergies) {
        this.allergies = allergies;
    }

    public String getDetailsAllergies() {
        return detailsAllergies;
    }

    public void setDetailsAllergies(String detailsAllergies) {
        this.detailsAllergies = detailsAllergies;
    }

    public String getGroupeSanguin() {
        return groupeSanguin;
    }

    public void setGroupeSanguin(String groupeSanguin) {
        this.groupeSanguin = groupeSanguin;
    }

}
