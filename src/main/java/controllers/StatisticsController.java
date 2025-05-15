package controller;

import controllers.MainController;
import entities.User;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.PieChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import services.ServiceProjet;
import services.ServiceTache;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Contrôleur pour l'affichage des statistiques.
 */
public class StatisticsController {
    private static final Logger LOGGER = Logger.getLogger(StatisticsController.class.getName());
    
    @FXML private Label projetsTerminesLabel;
    @FXML private Label projetsEnCoursLabel;
    @FXML private Label projetsEnRetardLabel;
    @FXML private Label tachesTermineesLabel;
    @FXML private Label tachesEnCoursLabel;
    @FXML private Label tachesEnRetardLabel;
    @FXML private Button retourButton;
    @FXML private VBox chartsContainer;
    @FXML private PieChart projetsPieChart;
    @FXML private PieChart tachesPieChart;
    @FXML private BarChart<String, Number> comparaisonBarChart;
    @FXML private CategoryAxis xAxis;
    @FXML private NumberAxis yAxis;
    
    private ServiceProjet serviceProjet;
    private ServiceTache serviceTache;
    private MainController mainController;
    private User currentUser;
    
    /**
     * Initialise le contrôleur.
     */
    @FXML
    public void initialize() {
        LOGGER.info("Initialisation du contrôleur StatisticsController");
        serviceProjet = new ServiceProjet();
        serviceTache = new ServiceTache();
        
        // Configurer les axes du graphique à barres
        if (xAxis != null) {
            xAxis.setLabel("Catégorie");
        }
        if (yAxis != null) {
            yAxis.setLabel("Nombre");
        }
        
        // Charger les statistiques
        loadStatistics();
    }
    
    /**
     * Définit le contrôleur principal.
     * @param mainController le contrôleur principal
     */
    public void setMainController(MainController mainController) {
        this.mainController = mainController;
    }
    
    /**
     * Définit l'utilisateur actuel.
     * @param user l'utilisateur actuel
     */
    public void setCurrentUser(User user) {
        this.currentUser = user;
    }
    
    /**
     * Charge les statistiques des projets et tâches.
     */
    private void loadStatistics() {
        try {
            // Récupérer les statistiques
            List<Integer> projetStats = serviceProjet.getStatistics();
            List<Integer> tacheStats = serviceTache.getStatistics();
            
            // Mettre à jour les labels
            if (projetsTerminesLabel != null) {
                projetsTerminesLabel.setText(String.valueOf(projetStats.get(0)));
            }
            if (projetsEnCoursLabel != null) {
                projetsEnCoursLabel.setText(String.valueOf(projetStats.get(1)));
            }
            if (projetsEnRetardLabel != null) {
                projetsEnRetardLabel.setText(String.valueOf(projetStats.get(2)));
            }
            if (tachesTermineesLabel != null) {
                tachesTermineesLabel.setText(String.valueOf(tacheStats.get(0)));
            }
            if (tachesEnCoursLabel != null) {
                tachesEnCoursLabel.setText(String.valueOf(tacheStats.get(1)));
            }
            if (tachesEnRetardLabel != null) {
                tachesEnRetardLabel.setText(String.valueOf(tacheStats.get(2)));
            }
            
            // Mettre à jour les graphiques
            updateCharts(projetStats, tacheStats);
            
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Erreur lors du chargement des statistiques", e);
            showAlert("Erreur", "Erreur lors du chargement des statistiques : " + e.getMessage());
        }
    }
    
    /**
     * Met à jour les graphiques avec les données statistiques.
     * @param projetStats statistiques des projets
     * @param tacheStats statistiques des tâches
     */
    private void updateCharts(List<Integer> projetStats, List<Integer> tacheStats) {
        // Mettre à jour le graphique en camembert des projets
        if (projetsPieChart != null) {
            ObservableList<PieChart.Data> projetData = FXCollections.observableArrayList(
                new PieChart.Data("Terminés (" + projetStats.get(0) + ")", projetStats.get(0)),
                new PieChart.Data("En cours (" + projetStats.get(1) + ")", projetStats.get(1)),
                new PieChart.Data("En retard (" + projetStats.get(2) + ")", projetStats.get(2))
            );
            projetsPieChart.setData(projetData);
            
            // Appliquer des couleurs personnalisées
            projetData.get(0).getNode().setStyle("-fx-pie-color: #2ecc71;"); // Vert
            projetData.get(1).getNode().setStyle("-fx-pie-color: #3498db;"); // Bleu
            projetData.get(2).getNode().setStyle("-fx-pie-color: #e74c3c;"); // Rouge
        }
        
        // Mettre à jour le graphique en camembert des tâches
        if (tachesPieChart != null) {
            ObservableList<PieChart.Data> tacheData = FXCollections.observableArrayList(
                new PieChart.Data("Terminées (" + tacheStats.get(0) + ")", tacheStats.get(0)),
                new PieChart.Data("En cours (" + tacheStats.get(1) + ")", tacheStats.get(1)),
                new PieChart.Data("En retard (" + tacheStats.get(2) + ")", tacheStats.get(2))
            );
            tachesPieChart.setData(tacheData);
            
            // Appliquer des couleurs personnalisées
            tacheData.get(0).getNode().setStyle("-fx-pie-color: #27ae60;"); // Vert foncé
            tacheData.get(1).getNode().setStyle("-fx-pie-color: #2980b9;"); // Bleu foncé
            tacheData.get(2).getNode().setStyle("-fx-pie-color: #c0392b;"); // Rouge foncé
        }
        
        // Mettre à jour le graphique à barres de comparaison
        if (comparaisonBarChart != null) {
            XYChart.Series<String, Number> projetSeries = new XYChart.Series<>();
            projetSeries.setName("Projets");
            projetSeries.getData().add(new XYChart.Data<>("Terminés", projetStats.get(0)));
            projetSeries.getData().add(new XYChart.Data<>("En cours", projetStats.get(1)));
            projetSeries.getData().add(new XYChart.Data<>("En retard", projetStats.get(2)));
            
            XYChart.Series<String, Number> tacheSeries = new XYChart.Series<>();
            tacheSeries.setName("Tâches");
            tacheSeries.getData().add(new XYChart.Data<>("Terminés", tacheStats.get(0)));
            tacheSeries.getData().add(new XYChart.Data<>("En cours", tacheStats.get(1)));
            tacheSeries.getData().add(new XYChart.Data<>("En retard", tacheStats.get(2)));
            
            comparaisonBarChart.getData().clear();
            comparaisonBarChart.getData().addAll(projetSeries, tacheSeries);
        }
    }
    
    /**
     * Gère le clic sur le bouton Retour.
     * @param event l'événement de clic
     */
    @FXML
    private void handleRetour(ActionEvent event) {
        LOGGER.info("Clic sur le bouton Retour");
        if (mainController != null) {
            try {
                // Utiliser la méthode showDashboard du MainController pour revenir au tableau de bord
                mainController.showDashboard();
                LOGGER.info("Retour au tableau de bord via mainController.showDashboard()");
            } catch (Exception e) {
                LOGGER.log(Level.SEVERE, "Erreur lors du retour au tableau de bord", e);
                
                // Plan B: Charger directement le dashboard admin
                try {
                    FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/AdminDashboard.fxml"));
                    Parent view = loader.load();
                    
                    AdminDashboardController controller = loader.getController();
                    controller.setMainController(mainController);
                    controller.setCurrentUser(currentUser);
                    controller.initData();
                    
                    mainController.setCenter(view);
                    LOGGER.info("Retour au tableau de bord via chargement direct");
                } catch (IOException ex) {
                    LOGGER.log(Level.SEVERE, "Erreur lors du chargement direct du dashboard admin", ex);
                    showAlert("Erreur", "Impossible de charger le dashboard admin: " + ex.getMessage());
                }
            }
        } else {
            LOGGER.warning("mainController est null, impossible de naviguer vers le tableau de bord");
            
            // Si le mainController est null, essayons de trouver la scène et de charger le dashboard
            try {
                Stage stage = (Stage) retourButton.getScene().getWindow();
                if (stage != null) {
                    FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/AdminDashboard.fxml"));
                    Parent root = loader.load();
                    Scene scene = new Scene(root);
                    stage.setScene(scene);
                    LOGGER.info("Navigation vers le dashboard via changement de scène");
                } else {
                    LOGGER.severe("Impossible de trouver la fenêtre pour naviguer");
                }
            } catch (Exception e) {
                LOGGER.log(Level.SEVERE, "Erreur lors de la navigation vers le dashboard", e);
                // Ne pas fermer la fenêtre en cas d'erreur
            }
        }
    }
    
    /**
     * Gère le clic sur le bouton Rafraîchir.
     */
    @FXML
    private void handleRefresh() {
        loadStatistics();
    }
    
    /**
     * Affiche une alerte.
     * @param title le titre de l'alerte
     * @param message le message de l'alerte
     */
    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}