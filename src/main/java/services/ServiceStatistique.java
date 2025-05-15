package services;

import entities.Condidature;
import entities.Offre;
import entities.StatistiqueRecrutement;
import utils.MyDatabase;

import java.sql.*;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

public class ServiceStatistique {
    private Connection con;
    private ServiceOffre serviceOffre;
    private ServiceCondidature serviceCondidature;

    public ServiceStatistique() {
        con = MyDatabase.getInstance().getCnx();
        serviceOffre = new ServiceOffre();
        serviceCondidature = new ServiceCondidature();
    }

    /**
     * Génère les statistiques pour une offre spécifique
     */
    public StatistiqueRecrutement genererStatistiquePourOffre(int idOffre) throws SQLException {
        // Récupérer l'offre
        Offre offre = serviceOffre.get(idOffre);
        if (offre == null) {
            throw new SQLException("Offre non trouvée avec l'ID: " + idOffre);
        }

        // Récupérer toutes les candidatures pour cette offre
        List<Condidature> candidatures = serviceCondidature.recuperer().stream()
                .filter(c -> c.getIdOffre() == idOffre)
                .collect(Collectors.toList());

        // Calculer les statistiques
        int total = candidatures.size();
        int acceptees = 0;
        int refusees = 0;
        int enAttente = 0;

        for (Condidature c : candidatures) {
            switch (c.getStatut()) {
                case "Acceptée":
                    acceptees++;
                    break;
                case "Refusée":
                    refusees++;
                    break;
                case "en attente":
                    enAttente++;
                    break;
            }
        }

        // Calculer le taux de conversion (candidatures acceptées / total)
        double tauxConversion = total > 0 ? (double) acceptees / total * 100 : 0;

        // Calculer le temps de recrutement (en jours)
        int tempsRecrutement = calculerTempsRecrutement(idOffre);

        // Créer l'objet statistique
        StatistiqueRecrutement stat = new StatistiqueRecrutement();
        stat.setIdOffre(idOffre);
        stat.setTitreOffre(offre.getTitreOffre());
        stat.setNombreCandidatures(total);
        stat.setCandidaturesAcceptees(acceptees);
        stat.setCandidaturesRefusees(refusees);
        stat.setCandidaturesEnAttente(enAttente);
        stat.setTauxConversion(tauxConversion);
        stat.setTempsRecrutementJours(tempsRecrutement);
        stat.setStatutOffre(determinerStatutOffre(acceptees, total));

        // Ne pas enregistrer dans la base de données
        // enregistrerStatistique(stat);

        return stat;
    }

    /**
     * Génère les statistiques pour toutes les offres
     */
    public List<StatistiqueRecrutement> genererStatistiquesPourToutesOffres() throws SQLException {
        List<StatistiqueRecrutement> statistiques = new ArrayList<>();
        List<Offre> offres = serviceOffre.recuperer();

        for (Offre offre : offres) {
            try {
                StatistiqueRecrutement stat = genererStatistiquePourOffre(offre.getIdOffre());
                statistiques.add(stat);
            } catch (SQLException e) {
                System.err.println("Erreur lors de la génération des statistiques pour l'offre " + offre.getIdOffre() + ": " + e.getMessage());
            }
        }

        return statistiques;
    }

    /**
     * Calcule le temps de recrutement pour une offre (en jours)
     */
    private int calculerTempsRecrutement(int idOffre) throws SQLException {
        // Comme nous n'avons pas les dates, on retourne une valeur par défaut
        return 0;
    }

    /**
     * Détermine le statut de l'offre en fonction des candidatures
     */
    private String determinerStatutOffre(int candidaturesAcceptees, int totalCandidatures) {
        if (candidaturesAcceptees > 0) {
            return "Pourvue";
        } else if (totalCandidatures > 0) {
            return "En cours";
        } else {
            return "Sans candidature";
        }
    }

    /**
     * Récupère les statistiques pour une période donnée
     */
    public List<StatistiqueRecrutement> recupererStatistiquesPeriode(Date dateDebut, Date dateFin) throws SQLException {
        // Comme nous n'utilisons plus la table statistique_recrutement, 
        // nous générons les statistiques à la volée
        return genererStatistiquesPourToutesOffres();
    }
}