package controller;

import controllers.MainController;
import entities.User;
import entities.Projet;
import entities.Notification;
import entities.Tache;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.geometry.Insets;
import javafx.scene.paint.Color;
import services.ServiceProjet;
import services.ServiceTache;
import services.ServiceUser;
import services.ServiceNotification;
import services.ServiceTacheNotification;
import controller.GestionProjetController;
import controllers.GestionTacheController;
import controller.GestionUtilisateurController;
import controller.StatisticsController;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Contrôleur pour le tableau de bord Admin.
 */
public class AdminDashboardController {
    private static final Logger LOGGER = Logger.getLogger(AdminDashboardController.class.getName());
    
    @FXML private Label welcomeLabel;
    @FXML private Label projetsActifsLabel;
    @FXML private Label tachesTermineesLabel;
    @FXML private Label employesActifsLabel;
    @FXML private Label tachesEnRetardLabel;
    @FXML private Label tachesEnCoursLabel;
    @FXML private FlowPane tachesGrid;
    @FXML private VBox notificationsContainer;
    @FXML private ScrollPane notificationsScrollPane;
    @FXML private VBox mainContent;
    
    private MainController mainController;
    private User currentUser;
    private ServiceProjet serviceProjet;
    private ServiceTache serviceTache;
    private ServiceUser serviceUser;
    private ServiceNotification serviceNotification;
    private ServiceTacheNotification serviceTacheNotification;
    
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
    
    @FXML
    public void initialize() {
        serviceProjet = new ServiceProjet();
        serviceTache = new ServiceTache();
        serviceUser = new ServiceUser();
        serviceNotification = new ServiceNotification();
        serviceTacheNotification = new ServiceTacheNotification();
        
        // Vérifier les tâches en retard au démarrage
        serviceTacheNotification.verifierTachesEnRetard();
    }
    
    public void setMainController(MainController mainController) {
        this.mainController = mainController;
        if (mainController != null) {
            this.currentUser = mainController.getCurrentUser();
        }
    }
    
    public void initData() {
        if (currentUser != null) {
            if (welcomeLabel != null) {
                welcomeLabel.setText("Bienvenue, " + currentUser.getPrenom() + " " + currentUser.getNom() + " (Admin)");
            } else {
                LOGGER.warning("welcomeLabel est null, impossible d'afficher le message de bienvenue");
            }
            
            // Charger les statistiques
            loadStatistics();
            
            // Charger les tâches récentes
            loadRecentTasks();
            
            // Charger les notifications
            loadNotifications();
        }
    }
    
    private void loadStatistics() {
        try {
            // Récupérer les statistiques des tâches
            List<Integer> stats = serviceTache.getStatistics();
            
            // Vérifier si les statistiques sont disponibles
            if (stats != null && !stats.isEmpty()) {
                // Tâches terminées
                if (tachesTermineesLabel != null && stats.size() > 0) {
                    tachesTermineesLabel.setText(String.valueOf(stats.get(0)));
                }
                
                // Tâches en cours
                if (tachesEnCoursLabel != null && stats.size() > 1) {
                    tachesEnCoursLabel.setText(String.valueOf(stats.get(1)));
                }
                
                // Tâches en retard
                if (tachesEnRetardLabel != null && stats.size() > 2) {
                    tachesEnRetardLabel.setText(String.valueOf(stats.get(2)));
                }
            }
            
            // Projets actifs
            if (projetsActifsLabel != null) {
                try {
                    int projetsActifs = serviceProjet.countActiveProjects();
                    projetsActifsLabel.setText(String.valueOf(projetsActifs));
                } catch (Exception e) {
                    LOGGER.log(Level.WARNING, "Erreur lors du comptage des projets actifs", e);
                    projetsActifsLabel.setText("0");
                }
            }
            
            // Employés actifs
            if (employesActifsLabel != null) {
                try {
                    // Utiliser une méthode de ServiceUser pour compter les employés
                    int employesActifs = serviceUser.countEmployes();
                    employesActifsLabel.setText(String.valueOf(employesActifs));
                } catch (Exception e) {
                    LOGGER.log(Level.WARNING, "Erreur lors du comptage des employés actifs", e);
                    employesActifsLabel.setText("0");
                }
            }
            
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Erreur lors du chargement des statistiques", e);
            // Ne pas propager l'exception pour éviter de bloquer l'interface
        }
    }
    
    private void loadRecentTasks() {
        try {
            List<Tache> recentTasks = serviceTache.getRecentTasks(5);
            tachesGrid.getChildren().clear();
            
            for (Tache tache : recentTasks) {
                VBox taskCard = createTaskCard(tache);
                tachesGrid.getChildren().add(taskCard);
            }
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Erreur lors du chargement des tâches récentes", e);
        }
    }
    
    private VBox createTaskCard(Tache tache) {
        VBox card = new VBox(10);
        card.getStyleClass().add("task-card");
        card.setPadding(new Insets(15));
        card.setMinWidth(250);
        card.setMaxWidth(300);
        
        // Titre de la tâche
        Label titleLabel = new Label(tache.getNom_tache());
        titleLabel.getStyleClass().add("task-title");
        
        // Description
        Label descriptionLabel = new Label(tache.getDescription());
        descriptionLabel.setWrapText(true);
        descriptionLabel.getStyleClass().add("task-description");
        
        // Dates
        HBox datesBox = new HBox(10);
        datesBox.getChildren().addAll(
            createDateLabel("Début: " + tache.getDate_debut().format(DateTimeFormatter.ofPattern("dd/MM/yyyy"))),
            createDateLabel("Fin: " + tache.getDate_fin().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")))
        );
        
        // État
        Label stateLabel = new Label(tache.getEtat());
        stateLabel.getStyleClass().addAll("state-badge", tache.getEtat().toLowerCase().replace("_", "-"));
        
        // Employé assigné
        Label employeeLabel = new Label("Chargement...");
        try {
            User employe = serviceUser.recupererParId(tache.getId_employe());
            if (employe != null) {
                employeeLabel.setText("Assigné à: " + employe.getPrenom() + " " + employe.getNom());
            } else {
                employeeLabel.setText("Non assigné");
            }
        } catch (Exception e) {
            LOGGER.log(Level.WARNING, "Erreur lors de la récupération de l'employé", e);
            employeeLabel.setText("Erreur de chargement");
        }
        
        card.getChildren().addAll(titleLabel, descriptionLabel, datesBox, stateLabel, employeeLabel);
        return card;
    }
    
    private Label createDateLabel(String text) {
        Label label = new Label(text);
        label.getStyleClass().add("date-label");
        return label;
    }
    
    private void loadNotifications() {
        try {
            if (notificationsContainer == null) {
                return; // Sortir si le conteneur n'est pas disponible
            }
            
            // Récupérer les notifications de l'utilisateur
            List<Notification> notifications = serviceNotification.getNotificationsByUser(currentUser.getId());
            
            // Afficher les notifications
            notificationsContainer.getChildren().clear();
            
            if (notifications.isEmpty()) {
                Label emptyLabel = new Label("Aucune notification");
                emptyLabel.getStyleClass().add("empty-message");
                notificationsContainer.getChildren().add(emptyLabel);
            } else {
                for (Notification notification : notifications) {
                    VBox notifCard = createNotificationCard(notification);
                    notificationsContainer.getChildren().add(notifCard);
                }
            }
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Erreur lors du chargement des notifications", e);
            // Ne pas propager l'exception pour éviter de bloquer l'interface
        }
    }
    
    private VBox createNotificationCard(Notification notification) {
        VBox card = new VBox(10);
        card.getStyleClass().add("notification-card");
        card.setPadding(new Insets(10));
        
        // Icône selon le type
        Label iconLabel = new Label();
        if ("TACHE_TERMINEE".equals(notification.getType())) {
            iconLabel.setText("✓");
            iconLabel.setTextFill(Color.GREEN);
        } else if ("TACHE_RETARD".equals(notification.getType())) {
            iconLabel.setText("⚠");
            iconLabel.setTextFill(Color.RED);
        } else {
            iconLabel.setText("ℹ");
            iconLabel.setTextFill(Color.BLUE);
        }
        iconLabel.setStyle("-fx-font-size: 16px;");
        
        // Contenu de la notification
        VBox contentBox = new VBox(5);
        HBox.setHgrow(contentBox, Priority.ALWAYS); // Correction ici
        
        Label messageLabel = new Label(notification.getMessage());
        messageLabel.setWrapText(true);
        
        LocalDateTime dateTime = notification.getDateCreation().toLocalDateTime();
        String formattedDate = dateTime.format(DATE_FORMATTER);
        Label dateLabel = new Label(formattedDate);
        dateLabel.getStyleClass().add("notification-date");
        
        contentBox.getChildren().addAll(messageLabel, dateLabel);
        
        // Bouton pour marquer comme lu
        Button markReadBtn = new Button("Lu");
        markReadBtn.getStyleClass().add("mark-read-btn");
        markReadBtn.setVisible(!notification.isLue());
        markReadBtn.setOnAction(e -> {
            try {
                serviceNotification.marquerCommeLue(notification.getId());
                card.getStyleClass().remove("unread");
                markReadBtn.setVisible(false);
            } catch (Exception ex) {
                LOGGER.log(Level.SEVERE, "Erreur lors du marquage de la notification comme lue", ex);
            }
        });
        
        card.getChildren().addAll(iconLabel, contentBox, markReadBtn);
        return card;
    }
    
    @FXML
    private void handleDashboard() {
        // Recharger le tableau de bord principal
        if (mainController != null) {
            // Utiliser la méthode showDashboard du MainController
            mainController.showDashboard();
        } else {
            // Si mainController est null, simplement réinitialiser les données
            initData();
            LOGGER.info("Tableau de bord rechargé localement (mainController est null)");
        }
    }
    
    @FXML
    private void handleRefresh() {
        // Vérifier les tâches en retard
        int notificationsEnvoyees = serviceTacheNotification.verifierTachesEnRetard();
        if (notificationsEnvoyees > 0) {
            showAlert("Notifications", notificationsEnvoyees + " nouvelle(s) notification(s) de tâches en retard.");
        }
        
        // Recharger les données
        initData();
    }
    
    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
    
    /**
     * Gestionnaire pour le bouton Gestion Projets
     */
    @FXML
    public void handleGestionProjets() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/GestionProjetScrollable.fxml"));
            Parent view = loader.load();
            
            GestionProjetController controller = loader.getController();
            controller.setMainController(mainController);
            if (currentUser != null) {
                controller.setCurrentUser(currentUser);
            }
            
            mainController.setCenter(view);
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Erreur lors du chargement de la gestion des projets", e);
            showAlert("Erreur", "Impossible de charger la gestion des projets: " + e.getMessage());
        }
    }
    
    /**
     * Gestionnaire pour le bouton Gestion Tâches
     */
    @FXML
    public void handleGestionTaches() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/GestionTacheScrollable.fxml"));
            Parent view = loader.load();
            
            GestionTacheController controller = loader.getController();
            controller.setMainController(mainController);
            if (currentUser != null) {
                controller.setCurrentUser(currentUser);
            }
            
            mainController.setCenter(view);
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Erreur lors du chargement de la gestion des tâches", e);
            showAlert("Erreur", "Impossible de charger la gestion des tâches: " + e.getMessage());
        }
    }
    
    /**
     * Gestionnaire pour le bouton Gestion Utilisateurs
     */
    @FXML
    public void handleGestionUtilisateurs() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/GestionUtilisateur.fxml"));
            Parent view = loader.load();
            
            GestionUtilisateurController controller = loader.getController();
            controller.setMainController(mainController);
            if (currentUser != null) {
                controller.setCurrentUser(currentUser);
            }
            
            mainController.setCenter(view);
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Erreur lors du chargement de la gestion des utilisateurs", e);
            showAlert("Erreur", "Impossible de charger la gestion des utilisateurs: " + e.getMessage());
        }
    }
    
    @FXML
    private void handleStatistiques() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/Statistics.fxml"));
            Parent view = loader.load();
            
            StatisticsController controller = loader.getController();
            if (controller != null) {
                controller.setMainController(mainController);
                controller.setCurrentUser(currentUser);
            } else {
                LOGGER.severe("Le contrôleur StatisticsController n'a pas pu être chargé");
            }
            
            if (mainController != null) {
                mainController.setCenter(view);
            } else {
                LOGGER.severe("mainController est null, impossible de charger la vue des statistiques");
            }
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Erreur lors du chargement de la vue des statistiques", e);
            showError("Erreur", "Impossible de charger la vue des statistiques: " + e.getMessage());
        }
    }
    
    @FXML
    private void handleLogout() {
        mainController.handleLogout();
    }
    
    @FXML
    private void handleAjouterProjet() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/GestionProjet.fxml"));
            Parent view = loader.load();
            
            GestionProjetController controller = loader.getController();
            if (controller != null) {
                controller.setAdmin(true); // Définir explicitement comme admin
                
                // Passer l'utilisateur actuel pour avoir l'ID admin
                if (currentUser != null) {
                    // Créer un projet temporaire avec l'ID admin
                    Projet projet = new Projet();
                    projet.setId_admin(currentUser.getId());
                    controller.setProjet(projet);
                }
            }
            
            if (mainController != null) {
                mainController.setCenter(view);
            }
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Erreur lors du chargement de la vue GestionProjet", e);
            showError("Erreur", "Impossible de charger la vue de gestion des projets: " + e.getMessage());
        }
    }
    
    @FXML
    private void handleAjouterTache() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/GestionTache.fxml"));
            Parent view = loader.load();
            
            // Configurer le contrôleur de gestion des tâches
            GestionTacheController controller = loader.getController();
            if (controller != null) {
                controller.setMainController(mainController);
                
                // Passer l'utilisateur actuel pour vérifier son rôle
                if (currentUser != null) {
                    controller.setCurrentUser(currentUser);
                }
            }
            
            if (mainController != null) {
                mainController.setCenter(view);
            }
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Erreur lors du chargement de la vue GestionTache", e);
            showError("Erreur", "Impossible de charger la vue de gestion des tâches: " + e.getMessage());
        }
    }
    
    /**
     * Méthode pour gérer le clic sur le bouton de gestion des employés
     */
    @FXML
    private void handleGestionEmployes() {
        loadView("/fxml/GestionEmploye.fxml");
    }
    
    private void loadView(String fxmlPath) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
            if (loader.getLocation() == null) {
                showError("Fichier non trouvé", "Le fichier FXML '" + fxmlPath + "' n'a pas été trouvé.");
                return;
            }
            
            Parent view = loader.load();
            
            // Utiliser le mainPane du MainController
            if (mainController != null) {
                mainController.setCenter(view);
            } else {
                LOGGER.warning("mainController est null, impossible de charger la vue");
                showError("Erreur", "Impossible de charger la vue: le contrôleur principal est null");
            }
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Erreur lors du chargement de la vue " + fxmlPath, e);
            showError("Erreur", "Impossible de charger la vue: " + e.getMessage());
        }
    }
    
    private void showError(String title, String message) {
        if (mainController != null) {
            mainController.showAlert(title, message);
        } else {
            LOGGER.severe(title + ": " + message);
        }
    }
    
    /**
     * Définit l'utilisateur actuel
     * @param user l'utilisateur actuel
     */
    public void setCurrentUser(User user) {
        this.currentUser = user;
    }

    @FXML
    private void handleRessourcesHumaines() {
        try {
            mainController.loadView("/fxml/RessourcesHumaines.fxml");
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Erreur lors du chargement de la vue des ressources humaines", e);
            showAlert("Erreur", "Impossible de charger la vue des ressources humaines: " + e.getMessage());
        }
    }

    @FXML
    private void handleShowProjectCards() {
        if (mainController != null) {
            mainController.showProjectCards();
        }
    }
}
