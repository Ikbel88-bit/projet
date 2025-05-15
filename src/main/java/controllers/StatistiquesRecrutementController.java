package controllers;

import entities.StatistiqueRecrutement;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.chart.PieChart;
import javafx.scene.control.*;
import javafx.stage.Stage;
import services.ServiceStatistique;
import services.ServiceOffre;

import java.sql.SQLException;
import java.text.DecimalFormat;

public class StatistiquesRecrutementController {

    @FXML
    private Label lblTitreOffre;
    @FXML
    private Label lblTauxAcceptation;
    @FXML
    private Label lblTauxRefus;
    @FXML
    private Label lblNombreCandidatures;
    @FXML
    private PieChart pieChartStatuts;
    @FXML
    private Button btnRetour;

    private ServiceStatistique serviceStatistique;
    private ServiceOffre serviceOffre;
    private DecimalFormat df = new DecimalFormat("#.##");
    private int idOffre;

    @FXML
    public void initialize() {
        serviceStatistique = new ServiceStatistique();
        serviceOffre = new ServiceOffre();
    }

    /**
     * Définit l'ID de l'offre pour laquelle générer les statistiques
     * @param idOffre l'ID de l'offre
     */
    public void setIdOffre(int idOffre) {
        this.idOffre = idOffre;
        System.out.println("ID Offre défini dans StatistiquesRecrutementController: " + idOffre);
    }

    /**
     * Charge les statistiques pour l'offre spécifiée
     */
    public void chargerStatistiques() {
        try {
            if (idOffre <= 0) {
                System.err.println("Erreur: ID d'offre non valide");
                return;
            }
            
            // Générer les statistiques
            StatistiqueRecrutement stat = serviceStatistique.genererStatistiquePourOffre(idOffre);
            
            // Mettre à jour l'interface
            mettreAJourStatistiques(stat);
            
        } catch (SQLException e) {
            e.printStackTrace();
            // Afficher un message d'erreur si nécessaire
            afficherErreur("Erreur", "Impossible de générer les statistiques: " + e.getMessage());
        }
    }

    /**
     * Met à jour l'interface avec les statistiques fournies
     */
    private void mettreAJourStatistiques(StatistiqueRecrutement stat) {
        // Mettre à jour le titre de l'offre
        lblTitreOffre.setText(stat.getTitreOffre());
        
        // Mettre à jour les labels
        lblNombreCandidatures.setText(String.valueOf(stat.getNombreCandidatures()));
        
        // Calculer les taux
        double tauxAcceptation = 0;
        double tauxRefus = 0;
        
        if (stat.getNombreCandidatures() > 0) {
            tauxAcceptation = (double) stat.getCandidaturesAcceptees() / stat.getNombreCandidatures() * 100;
            tauxRefus = (double) stat.getCandidaturesRefusees() / stat.getNombreCandidatures() * 100;
        }
        
        lblTauxAcceptation.setText(df.format(tauxAcceptation) + "%");
        lblTauxRefus.setText(df.format(tauxRefus) + "%");
        
        // Mettre à jour le graphique en camembert
        ObservableList<PieChart.Data> pieChartData = FXCollections.observableArrayList(
            new PieChart.Data("Acceptées (" + stat.getCandidaturesAcceptees() + ")", stat.getCandidaturesAcceptees()),
            new PieChart.Data("Refusées (" + stat.getCandidaturesRefusees() + ")", stat.getCandidaturesRefusees()),
            new PieChart.Data("En attente (" + stat.getCandidaturesEnAttente() + ")", stat.getCandidaturesEnAttente())
        );
        pieChartStatuts.setData(pieChartData);
        
        // Ajouter des couleurs au graphique
        applyCustomColorSequence(pieChartData);
    }
    
    /**
     * Applique une séquence de couleurs personnalisée au graphique en camembert
     */
    private void applyCustomColorSequence(ObservableList<PieChart.Data> pieChartData) {
        String[] pieColors = {"#4CAF50", "#F44336", "#FFC107"};
        int i = 0;
        for (PieChart.Data data : pieChartData) {
            String color = pieColors[i % pieColors.length];
            data.getNode().setStyle("-fx-pie-color: " + color + ";");
            i++;
        }
    }
    
    private void afficherErreur(String titre, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(titre);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    /**
     * Retourne à l'écran des candidatures
     */
    @FXML
    private void retourVersOffres() {
        // Fermer la fenêtre actuelle
        Stage stage = (Stage) btnRetour.getScene().getWindow();
        stage.close();
    }
}
