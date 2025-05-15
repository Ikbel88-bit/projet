package org.example.models;

import java.time.LocalDate;

public class Reservation {
    private int idReservations;
    private int idRessources;
    private int idEmploye;
    private LocalDate dateDebut;
    private LocalDate dateFin;
    private String motif;
    private String statut;

    public Reservation() {}

    public Reservation(int idReservations, int idRessources, int idEmploye, LocalDate dateDebut, LocalDate dateFin, String motif, String statut) {
        this.idReservations = idReservations;
        this.idRessources = idRessources;
        this.idEmploye = idEmploye;
        this.dateDebut = dateDebut;
        this.dateFin = dateFin;
        this.motif = motif;
        this.statut = statut;
    }

    // Getters and Setters
    public int getIdReservations() { return idReservations; }
    public void setIdReservations(int idReservations) { this.idReservations = idReservations; }
    public int getIdRessources() { return idRessources; }
    public void setIdRessources(int idRessources) { this.idRessources = idRessources; }
    public int getIdEmploye() { return idEmploye; }
    public void setIdEmploye(int idEmploye) { this.idEmploye = idEmploye; }
    public LocalDate getDateDebut() { return dateDebut; }
    public void setDateDebut(LocalDate dateDebut) { this.dateDebut = dateDebut; }
    public LocalDate getDateFin() { return dateFin; }
    public void setDateFin(LocalDate dateFin) { this.dateFin = dateFin; }
    public String getMotif() { return motif; }
    public void setMotif(String motif) { this.motif = motif; }
    public String getStatut() { return statut; }
    public void setStatut(String statut) { this.statut = statut; }
} 