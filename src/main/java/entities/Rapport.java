package entities;

import java.time.LocalDate;

/**
 * Entité représentant un rapport de projet ou de tâche
 */
public class Rapport {
    private int id;
    private String titre;
    private String contenu;
    private LocalDate dateCreation;
    private int idProjet;
    private int idTache;
    private int idAuteur;
    private String statut;

    public Rapport() {
        this.dateCreation = LocalDate.now();
        this.statut = "BROUILLON";
    }

    public Rapport(int id, String titre, String contenu, LocalDate dateCreation, int idProjet, int idTache, int idAuteur, String statut) {
        this.id = id;
        this.titre = titre;
        this.contenu = contenu;
        this.dateCreation = dateCreation;
        this.idProjet = idProjet;
        this.idTache = idTache;
        this.idAuteur = idAuteur;
        this.statut = statut;
    }

    public Rapport(String titre, String contenu, int idProjet, int idAuteur) {
        this.titre = titre;
        this.contenu = contenu;
        this.dateCreation = LocalDate.now();
        this.idProjet = idProjet;
        this.idTache = 0; // Pas de tâche associée
        this.idAuteur = idAuteur;
        this.statut = "BROUILLON";
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

    public String getContenu() {
        return contenu;
    }

    public void setContenu(String contenu) {
        this.contenu = contenu;
    }

    public LocalDate getDateCreation() {
        return dateCreation;
    }

    public void setDateCreation(LocalDate dateCreation) {
        this.dateCreation = dateCreation;
    }

    public int getIdProjet() {
        return idProjet;
    }

    public void setIdProjet(int idProjet) {
        this.idProjet = idProjet;
    }

    public int getIdTache() {
        return idTache;
    }

    public void setIdTache(int idTache) {
        this.idTache = idTache;
    }

    public int getIdAuteur() {
        return idAuteur;
    }

    public void setIdAuteur(int idAuteur) {
        this.idAuteur = idAuteur;
    }

    public String getStatut() {
        return statut;
    }

    public void setStatut(String statut) {
        this.statut = statut;
    }

    @Override
    public String toString() {
        return "Rapport{" +
                "id=" + id +
                ", titre='" + titre + '\'' +
                ", contenu='" + contenu + '\'' +
                ", dateCreation=" + dateCreation +
                ", idProjet=" + idProjet +
                ", idTache=" + idTache +
                ", idAuteur=" + idAuteur +
                ", statut='" + statut + '\'' +
                '}';
    }
}