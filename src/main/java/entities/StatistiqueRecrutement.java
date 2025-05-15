package entities;

import java.util.Date;

public class StatistiqueRecrutement {
    private int idStatistique;
    private int idOffre;
    private String titreOffre;
    private int nombreCandidatures;
    private int candidaturesAcceptees;
    private int candidaturesRefusees;
    private int candidaturesEnAttente;
    private double tauxConversion;
    private int tempsRecrutementJours;
    private Date dateDebut;
    private Date dateFin;
    private String statutOffre;

    public StatistiqueRecrutement() {
    }

    public StatistiqueRecrutement(int idStatistique, int idOffre, String titreOffre, int nombreCandidatures, 
                                 int candidaturesAcceptees, int candidaturesRefusees, int candidaturesEnAttente, 
                                 double tauxConversion, int tempsRecrutementJours, Date dateDebut, Date dateFin, 
                                 String statutOffre) {
        this.idStatistique = idStatistique;
        this.idOffre = idOffre;
        this.titreOffre = titreOffre;
        this.nombreCandidatures = nombreCandidatures;
        this.candidaturesAcceptees = candidaturesAcceptees;
        this.candidaturesRefusees = candidaturesRefusees;
        this.candidaturesEnAttente = candidaturesEnAttente;
        this.tauxConversion = tauxConversion;
        this.tempsRecrutementJours = tempsRecrutementJours;
        this.dateDebut = dateDebut;
        this.dateFin = dateFin;
        this.statutOffre = statutOffre;
    }

    // Getters et Setters
    public int getIdStatistique() {
        return idStatistique;
    }

    public void setIdStatistique(int idStatistique) {
        this.idStatistique = idStatistique;
    }

    public int getIdOffre() {
        return idOffre;
    }

    public void setIdOffre(int idOffre) {
        this.idOffre = idOffre;
    }

    public String getTitreOffre() {
        return titreOffre;
    }

    public void setTitreOffre(String titreOffre) {
        this.titreOffre = titreOffre;
    }

    public int getNombreCandidatures() {
        return nombreCandidatures;
    }

    public void setNombreCandidatures(int nombreCandidatures) {
        this.nombreCandidatures = nombreCandidatures;
    }

    public int getCandidaturesAcceptees() {
        return candidaturesAcceptees;
    }

    public void setCandidaturesAcceptees(int candidaturesAcceptees) {
        this.candidaturesAcceptees = candidaturesAcceptees;
    }

    public int getCandidaturesRefusees() {
        return candidaturesRefusees;
    }

    public void setCandidaturesRefusees(int candidaturesRefusees) {
        this.candidaturesRefusees = candidaturesRefusees;
    }

    public int getCandidaturesEnAttente() {
        return candidaturesEnAttente;
    }

    public void setCandidaturesEnAttente(int candidaturesEnAttente) {
        this.candidaturesEnAttente = candidaturesEnAttente;
    }

    public double getTauxConversion() {
        return tauxConversion;
    }

    public void setTauxConversion(double tauxConversion) {
        this.tauxConversion = tauxConversion;
    }

    public int getTempsRecrutementJours() {
        return tempsRecrutementJours;
    }

    public void setTempsRecrutementJours(int tempsRecrutementJours) {
        this.tempsRecrutementJours = tempsRecrutementJours;
    }

    public Date getDateDebut() {
        return dateDebut;
    }

    public void setDateDebut(Date dateDebut) {
        this.dateDebut = dateDebut;
    }

    public Date getDateFin() {
        return dateFin;
    }

    public void setDateFin(Date dateFin) {
        this.dateFin = dateFin;
    }

    public String getStatutOffre() {
        return statutOffre;
    }

    public void setStatutOffre(String statutOffre) {
        this.statutOffre = statutOffre;
    }

    @Override
    public String toString() {
        return "StatistiqueRecrutement{" +
                "idStatistique=" + idStatistique +
                ", idOffre=" + idOffre +
                ", titreOffre='" + titreOffre + '\'' +
                ", nombreCandidatures=" + nombreCandidatures +
                ", candidaturesAcceptees=" + candidaturesAcceptees +
                ", candidaturesRefusees=" + candidaturesRefusees +
                ", candidaturesEnAttente=" + candidaturesEnAttente +
                ", tauxConversion=" + tauxConversion +
                ", tempsRecrutementJours=" + tempsRecrutementJours +
                ", dateDebut=" + dateDebut +
                ", dateFin=" + dateFin +
                ", statutOffre='" + statutOffre + '\'' +
                '}';
    }
}