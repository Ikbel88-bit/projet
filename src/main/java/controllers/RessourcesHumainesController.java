package controller;

import api.RessourcesHumainesAPI;
import entities.User;
import entities.Projet;
import entities.Tache;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.PieChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;

import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Contrôleur pour la gestion des ressources humaines
 */
public class RessourcesHumainesController {
    private static final Logger LOGGER = Logger.getLogger(RessourcesHumainesController.class.getName());
    
    @FXML private ComboBox<User> employeComboBox;
    @FXML private TableView<Tache> tachesTableView;
    @FXML private TableView<Projet> projetsTableView;
    @FXML private Label chargeLabel;
    @FXML private Label performanceLabel;
    @FXML private BarChart<String, Number> performanceChart;
    @FXML private PieChart workloadChart;
    
    private RessourcesHumainesAPI rhAPI;
    
    @FXML
    public void initialize() {
        LOGGER.info("Initialisation de RessourcesHumainesController");
        rhAPI = new RessourcesHumainesAPI();
        
        // Initialiser les tableaux
        initTachesTable();
        initProjetsTable();
        
        // Charger les employés dans le ComboBox
        loadEmployees();
        
        // Ajouter un écouteur pour le changement d'employé
        employeComboBox.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null) {
                loadEmployeeData(newVal.getId());
            }
        });
        
        // Charger les statistiques globales
        loadStatistics();
    }
    
    private void initTachesTable() {
        // Configurer les colonnes du tableau des tâches
        TableColumn<Tache, String> nomCol = new TableColumn<>("Nom");
        nomCol.setCellValueFactory(new PropertyValueFactory<>("nom_tache"));
        
        TableColumn<Tache, String> projetCol = new TableColumn<>("Projet");
        projetCol.setCellValueFactory(new PropertyValueFactory<>("nomProjet"));
        
        TableColumn<Tache, String> etatCol = new TableColumn<>("État");
        etatCol.setCellValueFactory(new PropertyValueFactory<>("etat"));
        
        TableColumn<Tache, String> dateDebutCol = new TableColumn<>("Début");
        dateDebutCol.setCellValueFactory(new PropertyValueFactory<>("date_debut"));
        
        TableColumn<Tache, String> dateFinCol = new TableColumn<>("Fin");
        dateFinCol.setCellValueFactory(new PropertyValueFactory<>("date_fin"));
        
        tachesTableView.getColumns().addAll(nomCol, projetCol, etatCol, dateDebutCol, dateFinCol);
    }
    
    private void initProjetsTable() {
        // Configurer les colonnes du tableau des projets
        TableColumn<Projet, String> nomCol = new TableColumn<>("Nom");
        nomCol.setCellValueFactory(new PropertyValueFactory<>("nom_projet"));
        
        TableColumn<Projet, String> etatCol = new TableColumn<>("État");
        etatCol.setCellValueFactory(new PropertyValueFactory<>("etat"));
        
        TableColumn<Projet, String> dateDebutCol = new TableColumn<>("Début");
        dateDebutCol.setCellValueFactory(new PropertyValueFactory<>("date_debut"));
        
        TableColumn<Projet, String> dateFinCol = new TableColumn<>("Fin");
        dateFinCol.setCellValueFactory(new PropertyValueFactory<>("date_fin"));
        
        projetsTableView.getColumns().addAll(nomCol, etatCol, dateDebutCol, dateFinCol);
    }
    
    private void loadEmployees() {
        try {
            List<User> employees = rhAPI.getAllEmployees();
            employeComboBox.setItems(FXCollections.observableArrayList(employees));
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Erreur lors du chargement des employés", e);
            showAlert("Erreur", "Impossible de charger la liste des employés: " + e.getMessage());
        }
    }
    
    private void loadEmployeeData(int idEmploye) {
        try {
            // Charger les tâches de l'employé
            List<Tache> taches = rhAPI.getTasksByEmployee(idEmploye);
            tachesTableView.setItems(FXCollections.observableArrayList(taches));
            
            // Charger les projets de l'employé
            List<Projet> projets = rhAPI.getProjectsByEmployee(idEmploye);
            projetsTableView.setItems(FXCollections.observableArrayList(projets));
            
            // Afficher la charge de travail
            int workload = rhAPI.getEmployeeWorkload(idEmploye);
            chargeLabel.setText("Charge de travail: " + workload + " tâches");
            
            // Afficher la performance
            double performance = rhAPI.getEmployeePerformance(idEmploye);
            performanceLabel.setText("Performance: " + String.format("%.1f", performance) + "%");
            
            // Mettre à jour les graphiques
            updateCharts(idEmploye);
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Erreur lors du chargement des données de l'employé", e);
            showAlert("Erreur", "Impossible de charger les données de l'employé: " + e.getMessage());
        }
    }
    
    private void updateCharts(int idEmploye) {
        try {
            // Mettre à jour le graphique de performance
            updatePerformanceChart(idEmploye);
            
            // Mettre à jour le graphique de charge de travail
            updateWorkloadChart(idEmploye);
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Erreur lors de la mise à jour des graphiques", e);
        }
    }
    
    private void updatePerformanceChart(int idEmploye) {
        try {
            performanceChart.getData().clear();
            
            XYChart.Series<String, Number> series = new XYChart.Series<>();
            series.setName("Performance");
            
            // Ajouter la performance de l'employé sélectionné
            double performance = rhAPI.getEmployeePerformance(idEmploye);
            series.getData().add(new XYChart.Data<>("Employé sélectionné", performance));
            
            // Ajouter la performance moyenne de tous les employés
            Map<Integer, Double> performanceReport = rhAPI.generatePerformanceReport();
            double averagePerformance = performanceReport.values().stream()
                    .mapToDouble(Double::doubleValue)
                    .average()
                    .orElse(0.0);
            series.getData().add(new XYChart.Data<>("Moyenne", averagePerformance));
            
            performanceChart.getData().add(series);
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Erreur lors de la mise à jour du graphique de performance", e);
        }
    }
    
    private void updateWorkloadChart(int idEmploye) {
        try {
            workloadChart.getData().clear();
            
            // Obtenir la charge de travail de l'employé
            int workload = rhAPI.getEmployeeWorkload(idEmploye);
            
            // Créer les données pour le graphique
            ObservableList<PieChart.Data> pieChartData = FXCollections.observableArrayList(
                    new PieChart.Data("Tâches actives", workload),
                    new PieChart.Data("Capacité disponible", Math.max(10 - workload, 0))
            );
            
            workloadChart.setData(pieChartData);
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Erreur lors de la mise à jour du graphique de charge de travail", e);
        }
    }
    
    private void loadStatistics() {
        try {
            // Charger les statistiques globales
            Map<Integer, Double> performanceReport = rhAPI.generatePerformanceReport();
            Map<Integer, Integer> workloadReport = rhAPI.generateWorkloadReport();
            
            // Afficher les statistiques dans les graphiques
            if (!performanceReport.isEmpty() && !workloadReport.isEmpty()) {
                // Trouver l'employé le plus performant
                Map.Entry<Integer, Double> bestPerformer = performanceReport.entrySet().stream()
                        .max(Map.Entry.comparingByValue())
                        .orElse(null);
                
                if (bestPerformer != null) {
                    // Sélectionner l'employé le plus performant dans le ComboBox
                    for (User user : employeComboBox.getItems()) {
                        if (user.getId() == bestPerformer.getKey()) {
                            employeComboBox.getSelectionModel().select(user);
                            break;
                        }
                    }
                }
            }
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Erreur lors du chargement des statistiques", e);
        }
    }
    
    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
    
    @FXML
    private void handleRefresh() {
        loadEmployees();
        loadStatistics();
        
        User selectedEmployee = employeComboBox.getSelectionModel().getSelectedItem();
        if (selectedEmployee != null) {
            loadEmployeeData(selectedEmployee.getId());
        }
    }
    
    @FXML
    private void handleAssignTask() {
        User selectedEmployee = employeComboBox.getSelectionModel().getSelectedItem();
        if (selectedEmployee == null) {
            showAlert("Erreur", "Veuillez sélectionner un employé");
            return;
        }
        
        // Ici, vous pourriez ouvrir une boîte de dialogue pour sélectionner une tâche à assigner
        // Pour l'exemple, nous allons simplement afficher un message
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Assignation de tâche");
        alert.setHeaderText(null);
        alert.setContentText("Fonctionnalité d'assignation de tâche à implémenter");
        alert.showAndWait();
    }
    
    @FXML
    private void handleGenerateReport() {
        try {
            // Générer un rapport complet
            Map<Integer, Double> performanceReport = rhAPI.generatePerformanceReport();
            Map<Integer, Integer> workloadReport = rhAPI.generateWorkloadReport();
            
            StringBuilder report = new StringBuilder();
            report.append("Rapport de ressources humaines\n\n");
            report.append("Performance des employés:\n");
            
            for (User user : employeComboBox.getItems()) {
                Double performance = performanceReport.get(user.getId());
                Integer workload = workloadReport.get(user.getId());
                
                report.append(String.format("- %s %s: Performance %.1f%%, Charge de travail: %d tâches\n", 
                        user.getNom(), user.getPrenom(), 
                        performance != null ? performance : 0.0, 
                        workload != null ? workload : 0));
            }
            
            // Afficher le rapport
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Rapport RH");
            alert.setHeaderText("Rapport de ressources humaines");
            
            TextArea textArea = new TextArea(report.toString());
            textArea.setEditable(false);
            textArea.setWrapText(true);
            textArea.setPrefHeight(300);
            textArea.setPrefWidth(500);
            
            alert.getDialogPane().setContent(textArea);
            alert.showAndWait();
            
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Erreur lors de la génération du rapport", e);
            showAlert("Erreur", "Impossible de générer le rapport: " + e.getMessage());
        }
    }
}