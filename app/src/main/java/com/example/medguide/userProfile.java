package com.example.medguide;

class userProfile {
    // Private fields
    private String nom;
    private String prenom;
    private String username;
    private String phoneNumber;
    private String email;
    private String birthday;
    private String sexe;
    private String password;
    private String taille;
    private String poids;
    private String grpSanguin;
    private Boolean isHandicap;
    private Boolean isDiabetique;
    private Boolean isAllergic;
    private String allergiesDetails;

    // Constructor
    public userProfile(String nom, String prenom, String username, String phoneNumber, String email, String birthday,
                       String sexe, String password, String taille, String poids, String grpSanguin, Boolean isHandicap,
                       Boolean isDiabetique, Boolean isAllergic, String allergiesDetails) {
        this.nom = nom;
        this.prenom = prenom;
        this.username = username;
        this.phoneNumber = phoneNumber;
        this.email = email;
        this.birthday = birthday;
        this.sexe = sexe;
        this.password = password;
        this.taille = taille;
        this.poids = poids;
        this.grpSanguin = grpSanguin;
        this.isHandicap = isHandicap;
        this.isDiabetique = isDiabetique;
        this.isAllergic = isAllergic;
        this.allergiesDetails = allergiesDetails;
    }

    // Getters and Setters
    public String getNom() {
        return nom;
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

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getBirthday() {
        return birthday;
    }

    public void setBirthday(String birthday) {
        this.birthday = birthday;
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

    public String getTaille() {
        return taille;
    }

    public void setTaille(String taille) {
        this.taille = taille;
    }

    public String getPoids() {
        return poids;
    }

    public void setPoids(String poids) {
        this.poids = poids;
    }

    public String getGrpSanguin() {
        return grpSanguin;
    }

    public void setGrpSanguin(String grpSanguin) {
        this.grpSanguin = grpSanguin;
    }

    public Boolean getIsHandicap() {
        return isHandicap;
    }

    public void setIsHandicap(Boolean isHandicap) {
        this.isHandicap = isHandicap;
    }

    public Boolean getIsDiabetique() {
        return isDiabetique;
    }

    public void setIsDiabetique(Boolean isDiabetique) {
        this.isDiabetique = isDiabetique;
    }

    public Boolean getIsAllergic() {
        return isAllergic;
    }

    public void setIsAllergic(Boolean isAllergic) {
        this.isAllergic = isAllergic;
    }

    public String getAllergiesDetails() {
        return allergiesDetails;
    }

    public void setAllergiesDetails(String allergiesDetails) {
        this.allergiesDetails = allergiesDetails;
    }
}
