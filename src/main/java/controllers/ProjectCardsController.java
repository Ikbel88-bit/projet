package controller;

import entities.Projet;
import entities.Tache;
import entities.User;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.VBox;
import javafx.scene.layout.HBox;
import javafx.scene.control.ProgressBar;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.event.ActionEvent;
import javafx.stage.Stage;
import services.ServiceProjet;
import services.ServiceTache;

import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ProjectCardsController {
    private static final Logger LOGGER = Logger.getLogger(ProjectCardsController.class.getName());

    @FXML private FlowPane projetsContainer;
    @FXML private FlowPane tachesContainer;
    @FXML private Button refreshButton;
    @FXML private Button retourButton;

    private ServiceProjet serviceProjet;
    private ServiceTache serviceTache;
    private MainController mainController;

    @FXML
    public void initialize() {
        serviceProjet = new ServiceProjet();
        serviceTache = new ServiceTache();
        
        // Charger les projets et tâches
        loadProjets();
        loadTaches();
    }
    
    public void setMainController(MainController mainController) {
        this.mainController = mainController;
    }
    
    private void loadProjets() {
        try {
            List<Projet> projets = serviceProjet.recuperer();
            projetsContainer.getChildren().clear();
            
            if (projets.isEmpty()) {
                Label emptyLabel = new Label("Aucun projet disponible");
                emptyLabel.getStyleClass().add("empty-message");
                projetsContainer.getChildren().add(emptyLabel);
                return;
            }
            
            for (Projet projet : projets) {
                VBox projetCard = createProjetCard(projet);
                projetsContainer.getChildren().add(projetCard);
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Erreur lors du chargement des projets", e);
        }
    }
    
    private void loadTaches() {
        try {
            List<Tache> taches = serviceTache.recuperer();
            tachesContainer.getChildren().clear();
            
            if (taches.isEmpty()) {
                Label emptyLabel = new Label("Aucune tâche disponible");
                emptyLabel.getStyleClass().add("empty-message");
                tachesContainer.getChildren().add(emptyLabel);
                return;
            }
            
            for (Tache tache : taches) {
                VBox tacheCard = createTacheCard(tache);
                tachesContainer.getChildren().add(tacheCard);
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Erreur lors du chargement des tâches", e);
        }
    }
    
    private VBox createProjetCard(Projet projet) {
        VBox card = new VBox(10);
        card.getStyleClass().add("project-card");
        card.setPrefWidth(300);
        card.setPadding(new Insets(15));
        
        // Statut du projet
        HBox statusBox = new HBox(10);
        statusBox.setAlignment(Pos.CENTER_LEFT);
        
        Circle statusCircle = new Circle(6);
        if ("TERMINE".equals(projet.getEtat())) {
            statusCircle.setFill(Color.web("#2ecc71"));
        } else if ("EN_RETARD".equals(projet.getEtat())) {
            statusCircle.setFill(Color.web("#e74c3c"));
        } else {
            statusCircle.setFill(Color.web("#3498db"));
        }
        
        Label statusLabel = new Label(projet.getEtat());
        statusLabel.getStyleClass().add("status-label");
        
        statusBox.getChildren().addAll(statusCircle, statusLabel);
        
        // Titre du projet
        Label titleLabel = new Label(projet.getNom_projet());
        titleLabel.getStyleClass().add("card-title");
        
        // Description du projet
        Label descLabel = new Label(projet.getDescription());
        descLabel.getStyleClass().add("card-description");
        descLabel.setWrapText(true);
        
        // Dates du projet
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        String dateDebut = projet.getDate_debut() != null ? projet.getDate_debut().format(formatter) : "Non définie";
        String dateFin = projet.getDate_fin() != null ? projet.getDate_fin().format(formatter) : "Non définie";
        
        Label dateLabel = new Label("Du " + dateDebut + " au " + dateFin);
        dateLabel.getStyleClass().add("card-date");
        
        // Lieu du projet
        Label lieuLabel = new Label("Lieu: " + (projet.getLieu() != null ? projet.getLieu() : "Non défini"));
        lieuLabel.getStyleClass().add("card-location");
        
        // Boutons d'action
        HBox actionsBox = new HBox(10);
        actionsBox.setAlignment(Pos.CENTER_RIGHT);
        
        Button detailsButton = new Button("Détails");
        detailsButton.getStyleClass().add("btn-info");
        detailsButton.setOnAction(e -> showProjetDetails(projet));
        
        Button editButton = new Button("Modifier");
        editButton.getStyleClass().add("btn-primary");
        editButton.setOnAction(e -> editProjet(projet));
        
        actionsBox.getChildren().addAll(detailsButton, editButton);
        
        // Ajouter tous les éléments à la carte
        card.getChildren().addAll(statusBox, titleLabel, descLabel, dateLabel, lieuLabel, actionsBox);
        
        return card;
    }
    
    private VBox createTacheCard(Tache tache) {
        VBox card = new VBox(10);
        card.getStyleClass().add("task-card");
        card.setPrefWidth(300);
        card.setPadding(new Insets(15));
        
        // Statut de la tâche
        HBox statusBox = new HBox(10);
        statusBox.setAlignment(Pos.CENTER_LEFT);
        
        Circle statusCircle = new Circle(6);
        if ("TERMINE".equals(tache.getEtat())) {
            statusCircle.setFill(Color.web("#2ecc71"));
        } else if ("EN_RETARD".equals(tache.getEtat())) {
            statusCircle.setFill(Color.web("#e74c3c"));
        } else {
            statusCircle.setFill(Color.web("#3498db"));
        }
        
        Label statusLabel = new Label(tache.getEtat());
        statusLabel.getStyleClass().add("status-label");
        
        statusBox.getChildren().addAll(statusCircle, statusLabel);
        
        // Titre de la tâche
        Label titleLabel = new Label(tache.getNom_tache());
        titleLabel.getStyleClass().add("card-title");
        
        // Description de la tâche
        Label descLabel = new Label(tache.getDescription());
        descLabel.getStyleClass().add("card-description");
        descLabel.setWrapText(true);
        
        // Dates de la tâche
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        String dateDebut = tache.getDate_debut() != null ? tache.getDate_debut().format(formatter) : "Non définie";
        String dateFin = tache.getDate_fin() != null ? tache.getDate_fin().format(formatter) : "Non définie";
        
        Label dateLabel = new Label("Du " + dateDebut + " au " + dateFin);
        dateLabel.getStyleClass().add("card-date");
        
        // Progression de la tâche
        VBox progressBox = new VBox(5);
        Label progressLabel = new Label("Progression:");
        ProgressBar progressBar = new ProgressBar();
        
        // Calculer la progression en fonction de l'état
        double progress = 0.0;
        if ("TERMINE".equals(tache.getEtat())) {
            progress = 1.0;
        } else if ("EN_COURS".equals(tache.getEtat())) {
            // Calculer la progression en fonction des dates
            if (tache.getDate_debut() != null && tache.getDate_fin() != null) {
                LocalDate now = LocalDate.now();
                LocalDate start = tache.getDate_debut();
                LocalDate end = tache.getDate_fin();
                
                if (now.isAfter(start) && now.isBefore(end)) {
                    long totalDays = java.time.temporal.ChronoUnit.DAYS.between(start, end);
                    long daysPassed = java.time.temporal.ChronoUnit.DAYS.between(start, now);
                    
                    if (totalDays > 0) {
                        progress = (double) daysPassed / totalDays;
                    }
                } else if (now.isAfter(end)) {
                    progress = 0.8; // Presque terminé mais pas marqué comme tel
                }
            } else {
                progress = 0.5; // Valeur par défaut pour "EN_COURS"
            }
        }
        
        progressBar.setProgress(progress);
        progressBar.setPrefWidth(Double.MAX_VALUE);
        
        progressBox.getChildren().addAll(progressLabel, progressBar);
        
        // Boutons d'action
        HBox actionsBox = new HBox(10);
        actionsBox.setAlignment(Pos.CENTER_RIGHT);
        
        Button detailsButton = new Button("Détails");
        detailsButton.getStyleClass().add("btn-info");
        detailsButton.setOnAction(e -> showTacheDetails(tache));
        
        Button editButton = new Button("Modifier");
        editButton.getStyleClass().add("btn-primary");
        editButton.setOnAction(e -> editTache(tache));
        
        actionsBox.getChildren().addAll(detailsButton, editButton);
        
        // Ajouter tous les éléments à la carte
        card.getChildren().addAll(statusBox, titleLabel, descLabel, dateLabel, progressBox, actionsBox);
        
        return card;
    }
    
    private void showProjetDetails(Projet projet) {
        // Implémenter l'affichage des détails du projet
        LOGGER.info("Affichage des détails du projet: " + projet.getNom_projet());
        
        // TODO: Implémenter l'affichage des détails
    }
    
    private void editProjet(Projet projet) {
        // Implémenter la modification du projet
        LOGGER.info("Modification du projet: " + projet.getNom_projet());
        
        if (mainController != null) {
            try {
                // Rediriger vers la page de gestion de projet avec le projet sélectionné
                mainController.loadProjetForEdit(projet);
            } catch (Exception e) {
                LOGGER.log(Level.SEVERE, "Erreur lors de la redirection vers la page de modification", e);
            }
        }
    }
    
    private void showTacheDetails(Tache tache) {
        // Implémenter l'affichage des détails de la tâche
        LOGGER.info("Affichage des détails de la tâche: " + tache.getNom_tache());
        
        // TODO: Implémenter l'affichage des détails
    }
    
    private void editTache(Tache tache) {
        // Implémenter la modification de la tâche
        LOGGER.info("Modification de la tâche: " + tache.getNom_tache());
        
        if (mainController != null) {
            try {
                // Vérifier si l'utilisateur est admin avant de permettre la modification
                User currentUser = mainController.getCurrentUser();
                if (currentUser != null && "ADMIN".equalsIgnoreCase(currentUser.getRole())) {
                    // Rediriger vers la page de gestion de tâche avec la tâche sélectionnée
                    mainController.loadTacheForEdit(tache);
                } else {
                    // Afficher un message d'erreur si l'utilisateur n'est pas admin
                    Alert alert = new Alert(Alert.AlertType.WARNING);
                    alert.setTitle("Accès refusé");
                    alert.setHeaderText(null);
                    alert.setContentText("Seuls les administrateurs peuvent modifier les tâches.");
                    alert.showAndWait();
                }
            } catch (Exception e) {
                LOGGER.log(Level.SEVERE, "Erreur lors de la redirection vers la page de modification", e);
            }
        }
    }
    
    @FXML
    private void handleRefresh(ActionEvent event) {
        loadProjets();
        loadTaches();
    }
    
    @FXML
    private void handleRetour(ActionEvent event) {
        if (mainController != null) {
            mainController.showDashboard();
        } else {
            // Si le mainController n'est pas disponible, fermer la fenêtre
            Stage stage = (Stage) retourButton.getScene().getWindow();
            stage.close();
        }
    }
}