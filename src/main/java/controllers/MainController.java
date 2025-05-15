package controller;

import entities.User;
import entities.Projet;
import entities.Tache;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import services.ServiceUser;

import java.io.IOException;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class MainController {
    private static final Logger LOGGER = Logger.getLogger(MainController.class.getName());

    @FXML private BorderPane mainPane;
    private ServiceUser serviceUser;
    private User currentUser;

    public MainController() {
        // Constructeur par défaut
    }

    @FXML
    public void initialize() {
        serviceUser = new ServiceUser();
        loadLoginScreen();
    }

    public void loadLoginScreen() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/Connexion.fxml"));
            Parent root = loader.load();
            LoginController loginController = loader.getController();
            loginController.setMainController(this);
            mainPane.setCenter(root);
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Erreur lors du chargement de l'écran de connexion", e);
            showAlert("Erreur", "Erreur lors du chargement de l'écran de connexion : " + e.getMessage());
        }
    }

    public void switchToDashboard(User user) {
        this.currentUser = user;
        try {
            String fxmlFile = "admin".equals(user.getRole()) ? "/fxml/AdminDashboard.fxml" : "/fxml/EmployeeDashboard.fxml";
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlFile));
            Parent root = loader.load();

            if ("admin".equals(user.getRole())) {
                AdminDashboardController adminController = loader.getController();
                adminController.setMainController(this);
                adminController.initData();
            } else {
                EmployeeDashboardController employeeController = loader.getController();
                employeeController.setMainController(this);
                employeeController.initData();
            }

            mainPane.setCenter(root);
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Erreur lors du chargement du tableau de bord", e);
            showAlert("Erreur", "Erreur lors du chargement du tableau de bord : " + e.getMessage());
        }
    }

    public void handleLogout() {
        currentUser = null;
        loadLoginScreen();
    }

    public void showAlert(String title, String message) {
        Alert alert = new Alert(AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    public void showInfo(String title, String message) {
        Alert alert = new Alert(AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    public void showConfirmation(String title, String message) {
        Alert alert = new Alert(AlertType.CONFIRMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    public User getCurrentUser() {
        return currentUser;
    }

    public void setCurrentUser(User user) {
        this.currentUser = user;
    }

    public ServiceUser getServiceUser() {
        return serviceUser;
    }

    public void loadView(String fxmlPath) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
            Parent root = loader.load();
            mainPane.setCenter(root);
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Erreur lors du chargement de la vue", e);
            showAlert("Erreur", "Erreur lors du chargement de la vue : " + e.getMessage());
        }
    }

    public void loadView(String fxmlPath, Object controller) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
            if (controller != null) {
                loader.setController(controller);
            }
            Parent root = loader.load();
            mainPane.setCenter(root);
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Erreur lors du chargement de la vue avec contrôleur", e);
            showAlert("Erreur", "Erreur lors du chargement de la vue : " + e.getMessage());
        }
    }

    public void changeScene(String fxmlPath) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
            Parent root = loader.load();
            Stage stage = (Stage) mainPane.getScene().getWindow();
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Erreur lors du changement de scène", e);
            showAlert("Erreur", "Erreur lors du changement de scène : " + e.getMessage());
        }
    }

    /**
     * Définit le contenu central du BorderPane.
     * @param node Le nœud à placer au centre
     */
    public void setCenter(Parent node) {
        mainPane.setCenter(node);
    }

    /**
     * Retourne le BorderPane principal
     * @return le BorderPane principal
     */
    public BorderPane getMainPane() {
        return mainPane;
    }

    /**
     * Affiche le tableau de bord
     */
    public void showDashboard() {
        try {
            // Déterminer quel tableau de bord charger en fonction du rôle de l'utilisateur
            String fxmlPath = "/fxml/AdminDashboard.fxml";
            if (currentUser != null && !"admin".equals(currentUser.getRole())) {
                fxmlPath = "/fxml/EmployeeDashboard.fxml";
            }

            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
            Parent view = loader.load();

            if ("/fxml/AdminDashboard.fxml".equals(fxmlPath)) {
                AdminDashboardController controller = loader.getController();
                if (controller != null) {
                    controller.setMainController(this);
                    controller.setCurrentUser(currentUser);
                    controller.initData();
                }
            } else {
                EmployeeDashboardController controller = loader.getController();
                if (controller != null) {
                    controller.setMainController(this);
                    // Assurez-vous que cette méthode existe dans EmployeeDashboardController
                    if (currentUser != null) {
                        controller.setCurrentUser(currentUser);
                    }
                    controller.initData();
                }
            }

            setCenter(view);
            LOGGER.info("Tableau de bord chargé avec succès");
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Erreur lors du chargement du tableau de bord", e);
            showAlert("Erreur", "Impossible de charger le tableau de bord: " + e.getMessage());
        }
    }

    /**
     * Charge un projet pour modification
     * @param projet Le projet à modifier
     */
    public void loadProjetForEdit(Projet projet) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/GestionProjet.fxml"));
            Parent view = loader.load();

            GestionProjetController controller = loader.getController();
            if (controller != null) {
                controller.setMainController(this);
                controller.setProjet(projet);
                controller.setAdmin(true); // Supposons que l'utilisateur est admin
            }

            setCenter(view);
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Erreur lors du chargement de la page de gestion de projet", e);
            showAlert("Erreur", "Impossible de charger la page de gestion de projet: " + e.getMessage());
        }
    }

    /**
     * Charge une tâche pour modification
     * @param tache La tâche à modifier
     */
    public void loadTacheForEdit(Tache tache) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/GestionTache.fxml"));
            Parent view = loader.load();

            GestionTacheController controller = loader.getController();
            if (controller != null) {
                controller.setMainController(this);
                controller.setCurrentUser(getCurrentUser());
                controller.setTache(tache);
            }

            setCenter(view);
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Erreur lors du chargement de la page de gestion de tâche", e);
            showAlert("Erreur", "Impossible de charger la page de gestion de tâche: " + e.getMessage());
        }
    }

    /**
     * Affiche la vue des projets et tâches sous forme de cartes
     */
    public void showProjectCards() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/ProjectCards.fxml"));
            Parent view = loader.load();

            ProjectCardsController controller = loader.getController();
            if (controller != null) {
                controller.setMainController(this);
            }

            setCenter(view);
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Erreur lors du chargement de la vue des projets et tâches", e);
            showAlert("Erreur", "Impossible de charger la vue des projets et tâches: " + e.getMessage());
        }
    }
}
