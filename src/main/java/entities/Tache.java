package entities;

import java.time.LocalDate;

public class Tache {
    private int id;
    private int id_tache; // Ajout pour compatibilité
    private String nom_tache;
    private String description;
    private LocalDate date_debut;
    private LocalDate date_fin;
    private String etat;
    private int id_projet;
    private int id_employe;
    private String nomProjet; // Ajout pour compatibilité
    private boolean notified; // Ajout pour compatibilité
    private String commentaire; // Ajout pour le commentaire/excuse

    public Tache() {
        // Constructeur par défaut
        this.date_debut = LocalDate.now();
        this.etat = "EN_COURS";
        this.notified = false;
    }

    public Tache(int id, String nom_tache, String description, LocalDate date_debut, 
                LocalDate date_fin, String etat, int id_projet, int id_employe) {
        this.id = id;
        this.id_tache = id; // Pour compatibilité
        this.nom_tache = nom_tache;
        this.description = description;
        this.date_debut = date_debut;
        this.date_fin = date_fin;
        this.etat = etat;
        this.id_projet = id_projet;
        this.id_employe = id_employe;
        this.notified = false;
    }

    /**
     * Retourne l'ID de la tâche
     * @return L'ID de la tâche
     */
    public int getId() {
        return id_tache;
    }

    /**
     * Définit l'ID de la tâche
     * @param id_tache Le nouvel ID de la tâche
     */
    public void setId(int id_tache) {
        this.id_tache = id_tache;
    }
    
    // Méthodes pour compatibilité
    public int getId_tache() {
        return id_tache;
    }

    public void setId_tache(int id_tache) {
        this.id_tache = id_tache;
        this.id = id_tache; // Pour compatibilité
    }

    public String getNom_tache() {
        return nom_tache;
    }

    public void setNom_tache(String nom_tache) {
        this.nom_tache = nom_tache;
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

    public int getId_projet() {
        return id_projet;
    }

    public void setId_projet(int id_projet) {
        this.id_projet = id_projet;
    }

    public int getId_employe() {
        return id_employe;
    }

    public void setId_employe(int id_employe) {
        this.id_employe = id_employe;
    }
    
    // Méthodes pour compatibilité
    public String getNomProjet() {
        return nomProjet;
    }
    
    public void setNomProjet(String nomProjet) {
        this.nomProjet = nomProjet;
    }
    
    public boolean isNotified() {
        return notified;
    }
    
    public void setNotified(boolean notified) {
        this.notified = notified;
    }

    public String getCommentaire() {
        return commentaire;
    }

    public void setCommentaire(String commentaire) {
        this.commentaire = commentaire;
    }

    @Override
    public String toString() {
        return "Tache{" +
                "id=" + id +
                ", nom_tache='" + nom_tache + '\'' +
                ", description='" + description + '\'' +
                ", date_debut=" + date_debut +
                ", date_fin=" + date_fin +
                ", etat='" + etat + '\'' +
                ", id_projet=" + id_projet +
                ", id_employe=" + id_employe +
                ", nomProjet='" + nomProjet + '\'' +
                ", notified=" + notified +
                ", commentaire='" + commentaire + '\'' +
                '}';
    }
}