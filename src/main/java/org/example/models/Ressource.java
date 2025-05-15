package org.example.models;

public class Ressource {
    private int idRessource;
    private String nom;
    private String type;
    private String description;
    private boolean disponible;
    private String emplacement;
    private String etat;

    public Ressource() {}

    public Ressource(int idRessource, String nom, String type, String description, boolean disponible, String emplacement, String etat) {
        this.idRessource = idRessource;
        this.nom = nom;
        this.type = type;
        this.description = description;
        this.disponible = disponible;
        this.emplacement = emplacement;
        this.etat = etat;
    }

    public int getIdRessource() { return idRessource; }
    public void setIdRessource(int idRessource) { this.idRessource = idRessource; }
    public String getNom() { return nom; }
    public void setNom(String nom) { this.nom = nom; }
    public String getType() { return type; }
    public void setType(String type) { this.type = type; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public boolean isDisponible() { return disponible; }
    public void setDisponible(boolean disponible) { this.disponible = disponible; }
    public String getEmplacement() { return emplacement; }
    public void setEmplacement(String emplacement) { this.emplacement = emplacement; }
    public String getEtat() { return etat; }
    public void setEtat(String etat) { this.etat = etat; }

    @Override
    public String toString() {
        return "Ressource{" +
                "idRessource=" + idRessource +
                ", nom='" + nom + '\'' +
                ", type='" + type + '\'' +
                ", description='" + description + '\'' +
                ", disponible=" + disponible +
                ", emplacement='" + emplacement + '\'' +
                ", etat='" + etat + '\'' +
                '}';
    }
} 