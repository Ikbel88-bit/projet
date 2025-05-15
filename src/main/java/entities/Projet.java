package entities;

import java.time.LocalDate;

public class Projet {
    private int id_projet;
    private String nom_projet;
    private String description;
    private LocalDate date_debut;
    private LocalDate date_fin;
    private String etat;
    private int id_admin;
    private String lieu;

    public Projet() {
        // Constructeur par défaut
        this.date_debut = LocalDate.now();
        this.etat = "EN_COURS";
    }

    public Projet(int id_projet, String nom_projet, String description, LocalDate date_debut, 
                 LocalDate date_fin, String etat, int id_admin, String lieu) {
        this.id_projet = id_projet;
        this.nom_projet = nom_projet;
        this.description = description;
        this.date_debut = date_debut;
        this.date_fin = date_fin;
        this.etat = etat;
        this.id_admin = id_admin;
        this.lieu = lieu;
    }

    public int getId_projet() {
        return id_projet;
    }

    public void setId_projet(int id_projet) {
        this.id_projet = id_projet;
    }

    public String getNom_projet() {
        return nom_projet;
    }

    public void setNom_projet(String nom_projet) {
        this.nom_projet = nom_projet;
    }
    
    // Méthode pour compatibilité
    public void setNom(String nom) {
        this.nom_projet = nom;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public LocalDate getDate_debut() {
        return date_debut;
    }

    public void setDate_debut(LocalDate date_debut) {
        this.date_debut = date_debut;
    }

    public LocalDate getDate_fin() {
        return date_fin;
    }

    public void setDate_fin(LocalDate date_fin) {
        this.date_fin = date_fin;
    }

    public String getEtat() {
        return etat;
    }

    public void setEtat(String etat) {
        this.etat = etat;
    }

    public int getId_admin() {
        return id_admin;
    }

    public void setId_admin(int id_admin) {
        this.id_admin = id_admin;
    }

    public String getLieu() {
        return lieu;
    }

    public void setLieu(String lieu) {
        this.lieu = lieu;
    }

    @Override
    public String toString() {
        return "Projet{" +
                "id_projet=" + id_projet +
                ", nom_projet='" + nom_projet + '\'' +
                ", description='" + description + '\'' +
                ", date_debut=" + date_debut +
                ", date_fin=" + date_fin +
                ", etat='" + etat + '\'' +
                ", id_admin=" + id_admin +
                ", lieu='" + lieu + '\'' +
                '}';
    }
}
