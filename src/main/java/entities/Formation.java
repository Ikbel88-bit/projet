package entities;

import java.util.Date;

public class Formation {
    private int id;
    private String titre;
    private String description;
    private Date date_debut;
    private Date date_fin;
    private String formateur;
    private int capacite;

    public Formation(int id, String titre, String description, Date date_debut, Date date_fin, String formateur, int capacite) {
        this.id = id;
        this.titre = titre;
        this.description = description;
        this.date_debut = date_debut;
        this.date_fin = date_fin;
        this.formateur = formateur;
        this.capacite = capacite;
    }

    public Formation(String titre, String description, Date date_debut, Date date_fin, String formateur, int capacite) {
        this.titre = titre;
        this.description = description;
        this.date_debut = date_debut;
        this.date_fin = date_fin;
        this.formateur = formateur;
        this.capacite = capacite;
    }

    public Formation() {
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitre() {
        return titre;
    }

    public void setTitre(String titre) {
        this.titre = titre;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Date getDate_debut() {
        return date_debut;
    }

    public void setDate_debut(Date date_debut) {
        this.date_debut = date_debut;
    }

    public Date getDate_fin() {
        return date_fin;
    }

    public void setDate_fin(Date date_fin) {
        this.date_fin = date_fin;
    }

    public String getFormateur() {
        return formateur;
    }

    public void setFormateur(String formateur) {
        this.formateur = formateur;
    }

    public int getCapacite() {
        return capacite;
    }

    public void setCapacite(int capacite) {
        this.capacite = capacite;
    }

    @Override
    public String toString() {
        return "Formation{" +
                "id=" + id +
                ", titre='" + titre + '\'' +
                ", description='" + description + '\'' +
                ", date_debut=" + date_debut +
                ", date_fin=" + date_fin +
                ", formateur='" + formateur + '\'' +
                ", capacite=" + capacite +
                '}';
    }
}