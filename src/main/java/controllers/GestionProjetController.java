package controller;

import entities.Projet;
import entities.User;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.scene.web.WebView;
import javafx.scene.web.WebEngine;
import services.ServiceProjet;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Contrôleur pour la gestion des projets.
 */
public class GestionProjetController {
    private static final Logger LOGGER = Logger.getLogger(GestionProjetController.class.getName());
    
    // Champs FXML
    @FXML private TextField nomProjetField;
    @FXML private TextArea descriptionProjetField;
    @FXML private TextField lieuField;
    @FXML private DatePicker dateFinField;
    @FXML private ChoiceBox<String> etatField;
    @FXML private Button sauvegarderButton;
    @FXML private TextField id_admin;
    @FXML private WebView mapWebView;
    @FXML private VBox mapContainer;
    
    // Labels d'erreur
    @FXML private Label nomProjetErrorLabel;
    @FXML private Label descriptionErrorLabel;
    @FXML private Label lieuErrorLabel;
    @FXML private Label dateFinErrorLabel;
    @FXML private Label adminIdErrorLabel;
    
    // Labels pour les statistiques
    @FXML private Label projetsTerminesLabel;
    @FXML private Label projetsEnCoursLabel;
    @FXML private Label projetsEnRetardLabel;

    // Services et données
    private ServiceProjet serviceProjet;
    private Projet projet;
    private boolean isAdmin;
    private boolean editMode = false;
    private User currentUser;

    // Référence au contrôleur principal
    private MainController mainController;

    /**
     * Définit le contrôleur principal
     * @param mainController le contrôleur principal
     */
    public void setMainController(MainController mainController) {
        this.mainController = mainController;
        if (mainController != null) {
            this.currentUser = mainController.getCurrentUser();
            if (currentUser != null && id_admin != null) {
                id_admin.setText(String.valueOf(currentUser.getId()));
            }
        }
    }

    /**
     * Définit l'utilisateur actuel
     * @param user l'utilisateur actuel
     */
    public void setCurrentUser(User user) {
        this.currentUser = user;
        if (user != null && id_admin != null) {
            id_admin.setText(String.valueOf(user.getId()));
        }
    }

    @FXML
    public void initialize() {
        LOGGER.info("Initialisation de GestionProjetController");
        serviceProjet = new ServiceProjet();
        
        // Vérifier la structure de la table projet
        try {
            Connection connection = utils.MyDatabase.getInstance().getConnection();
            utils.DatabaseChecker.checkTableStructure("projet", connection);
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Erreur lors de la vérification de la structure de la table", e);
        }
        
        // Initialiser les états possibles
        if (etatField != null) {
            etatField.getItems().addAll("EN_COURS", "TERMINE");
            etatField.setValue("EN_COURS");
        } else {
            LOGGER.severe("etatField est null. Vérifiez le fichier FXML.");
        }
        
        // Masquer tous les labels d'erreur
        hideAllErrorLabels();
        
        // Ajouter des écouteurs pour réinitialiser les styles d'erreur
        setupFieldListeners();
        
        // Initialiser avec un nouveau projet
        handleNouveau();
        
        // Charger les statistiques
        loadStatistics();
        
        // Définir l'admin par défaut à true pour le débogage
        isAdmin = true;
        LOGGER.info("GestionProjetController initialisé, isAdmin = " + isAdmin);
    }

    private void hideAllErrorLabels() {
        if (nomProjetErrorLabel != null) nomProjetErrorLabel.setVisible(false);
        if (descriptionErrorLabel != null) descriptionErrorLabel.setVisible(false);
        if (lieuErrorLabel != null) lieuErrorLabel.setVisible(false);
        if (dateFinErrorLabel != null) dateFinErrorLabel.setVisible(false);
        if (adminIdErrorLabel != null) adminIdErrorLabel.setVisible(false);
    }
    
    private void setupFieldListeners() {
        if (nomProjetField != null) {
            nomProjetField.textProperty().addListener((observable, oldValue, newValue) -> {
                nomProjetField.getStyleClass().remove("field-error");
                if (nomProjetErrorLabel != null) {
                    nomProjetErrorLabel.setVisible(false);
                }
            });
        }
        
        if (descriptionProjetField != null) {
            descriptionProjetField.textProperty().addListener((observable, oldValue, newValue) -> {
                descriptionProjetField.getStyleClass().remove("field-error");
                if (descriptionErrorLabel != null) {
                    descriptionErrorLabel.setVisible(false);
                }
            });
        }
        
        if (lieuField != null) {
            lieuField.textProperty().addListener((observable, oldValue, newValue) -> {
                lieuField.getStyleClass().remove("field-error");
                if (lieuErrorLabel != null) {
                    lieuErrorLabel.setVisible(false);
                }
            });
        }
        
        if (dateFinField != null) {
            dateFinField.valueProperty().addListener((observable, oldValue, newValue) -> {
                dateFinField.getStyleClass().remove("field-error");
                if (dateFinErrorLabel != null) {
                    dateFinErrorLabel.setVisible(false);
                }
            });
        }
        
        if (id_admin != null) {
            id_admin.textProperty().addListener((observable, oldValue, newValue) -> {
                id_admin.getStyleClass().remove("field-error");
                if (adminIdErrorLabel != null) {
                    adminIdErrorLabel.setVisible(false);
                }
            });
        }
    }
    
    private void loadStatistics() {
        try {
            List<Integer> stats = serviceProjet.getStatistics();
            if (projetsTerminesLabel != null) {
                projetsTerminesLabel.setText(String.valueOf(stats.get(0)));
            }
            if (projetsEnCoursLabel != null) {
                projetsEnCoursLabel.setText(String.valueOf(stats.get(1)));
            }
            if (projetsEnRetardLabel != null) {
                projetsEnRetardLabel.setText(String.valueOf(stats.get(2)));
            }
        } catch (SQLException e) {
            LOGGER.severe("Erreur lors du chargement des statistiques : " + e.getMessage());
            showAlert("Erreur", "Impossible de charger les statistiques: " + e.getMessage());
        }
    }

    /**
     * Gestionnaire pour le bouton Sauvegarder
     */
    @FXML
    private void handleSauvegarder() {
        try {
            // Valider les champs
            if (!validateFields()) {
                return;
            }
            
            // Créer ou mettre à jour le projet
            if (projet == null) {
                // Nouveau projet
                projet = new Projet();
            }
            
            // Remplir les données du projet
            projet.setNom(nomProjetField.getText());
            projet.setDescription(descriptionProjetField.getText());
            projet.setLieu(lieuField.getText());
            projet.setDate_fin(dateFinField.getValue());
            projet.setEtat(etatField.getValue());
            
            try {
                projet.setId_admin(Integer.parseInt(id_admin.getText()));
            } catch (NumberFormatException e) {
                adminIdErrorLabel.setText("L'ID admin doit être un nombre");
                adminIdErrorLabel.setVisible(true);
                id_admin.getStyleClass().add("field-error");
                return;
            }
            
            // Sauvegarder le projet
            if (editMode) {
                serviceProjet.modifier(projet);
                showAlert("Succès", "Le projet a été mis à jour avec succès.");
            } else {
                serviceProjet.ajouter(projet);
                showAlert("Succès", "Le projet a été créé avec succès.");
            }
            
            // Réinitialiser le formulaire
            clearFields();
            
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Erreur SQL lors de la sauvegarde du projet", e);
            showAlert("Erreur", "Impossible de sauvegarder le projet: " + e.getMessage());
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Erreur inattendue", e);
            showAlert("Erreur", "Une erreur inattendue s'est produite: " + e.getMessage());
        }
    }
    
    /**
     * Gestionnaire pour le bouton Supprimer
     */
    @FXML
    private void handleSupprimer() {
        try {
            // Vérifier si un projet est sélectionné
            if (projet == null) {
                showAlert("Erreur", "Aucun projet sélectionné.");
                return;
            }
            
            // Demander confirmation
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Confirmation de suppression");
            alert.setHeaderText("Supprimer le projet");
            alert.setContentText("Êtes-vous sûr de vouloir supprimer le projet " + projet.getNom_projet() + " ?");
            
            if (alert.showAndWait().get() == ButtonType.OK) {
                // Supprimer le projet
                serviceProjet.supprimer(projet.getId_projet());
                
                // Réinitialiser le formulaire
                clearFields();
                
                // Afficher un message de succès
                showAlert("Succès", "Le projet a été supprimé avec succès.");
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Erreur SQL lors de la suppression du projet", e);
            showAlert("Erreur", "Impossible de supprimer le projet: " + e.getMessage());
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Erreur inattendue", e);
            showAlert("Erreur", "Une erreur inattendue s'est produite: " + e.getMessage());
        }
    }
    
    @FXML
    private void handleNouveau() {
        clearFields();
        projet = null;
        editMode = false;
        
        // Si l'utilisateur actuel est défini, utiliser son ID
        if (currentUser != null && id_admin != null) {
            id_admin.setText(String.valueOf(currentUser.getId()));
        }
    }
    
    private void clearFields() {
        if (nomProjetField != null) nomProjetField.clear();
        if (descriptionProjetField != null) descriptionProjetField.clear();
        if (lieuField != null) lieuField.clear();
        if (dateFinField != null) dateFinField.setValue(LocalDate.now().plusDays(30));
        if (etatField != null) etatField.setValue("EN_COURS");
        if (id_admin != null) {
            if (currentUser != null) {
                id_admin.setText(String.valueOf(currentUser.getId()));
            } else {
                id_admin.clear();
            }
        }
        hideAllErrorLabels();
    }

    /**
     * Définit le projet à éditer
     * @param projet le projet à éditer
     */
    public void setProjet(Projet projet) {
        this.projet = projet;
        if (projet != null) {
            editMode = projet.getId_projet() != 0;
            
            if (nomProjetField != null) nomProjetField.setText(projet.getNom_projet());
            if (descriptionProjetField != null) descriptionProjetField.setText(projet.getDescription());
            if (lieuField != null) lieuField.setText(projet.getLieu());
            if (dateFinField != null) dateFinField.setValue(projet.getDate_fin());
            if (etatField != null) etatField.setValue(projet.getEtat());
            if (id_admin != null) id_admin.setText(String.valueOf(projet.getId_admin()));
        } else {
            editMode = false;
        }
    }
    
    /**
     * Méthode de débogage pour vérifier l'état du contrôleur
     */
    private void debugState() {
        LOGGER.info("=== État du contrôleur GestionProjet ===");
        LOGGER.info("isAdmin: " + isAdmin);
        LOGGER.info("editMode: " + editMode);
        LOGGER.info("projet: " + (projet != null ? "ID=" + projet.getId_projet() : "null"));
        LOGGER.info("currentUser: " + (currentUser != null ? "ID=" + currentUser.getId() : "null"));
        LOGGER.info("serviceProjet: " + (serviceProjet != null ? "initialisé" : "null"));
        LOGGER.info("nomProjetField: " + (nomProjetField != null ? nomProjetField.getText() : "null"));
        LOGGER.info("descriptionProjetField: " + (descriptionProjetField != null ? "initialisé" : "null"));
        LOGGER.info("lieuField: " + (lieuField != null ? lieuField.getText() : "null"));
        LOGGER.info("dateFinField: " + (dateFinField != null ? dateFinField.getValue() : "null"));
        LOGGER.info("etatField: " + (etatField != null ? etatField.getValue() : "null"));
        LOGGER.info("id_admin: " + (id_admin != null ? id_admin.getText() : "null"));
        LOGGER.info("======================================");
    }

    /**
     * Définit si l'utilisateur est un administrateur
     * @param isAdmin true si l'utilisateur est admin, false sinon
     */
    public void setAdmin(boolean isAdmin) {
        this.isAdmin = isAdmin;
        LOGGER.info("setAdmin appelé avec: " + isAdmin);
    }
    
    /**
     * Affiche une alerte d'information
     * @param title titre de l'alerte
     * @param message message de l'alerte
     */
    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
    
    /**
     * Affiche une alerte d'erreur
     * @param title titre de l'alerte
     * @param message message de l'alerte
     */
    private void showError(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    /**
     * Gère le clic sur le bouton Annuler
     */
    @FXML
    private void handleAnnuler() {
        // Réinitialiser les champs
        clearFields();
        
        // Si nous avons un contrôleur principal, retourner au tableau de bord
        if (mainController != null) {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/AdminDashboard.fxml"));
                Parent view = loader.load();
                
                AdminDashboardController controller = loader.getController();
                controller.setMainController(mainController);
                controller.initData();
                
                mainController.setCenter(view);
            } catch (IOException e) {
                LOGGER.severe("Erreur lors du chargement du tableau de bord: " + e.getMessage());
                showAlert("Erreur", "Impossible de charger le tableau de bord: " + e.getMessage());
            }
        }
    }

    /**
     * Affiche la carte pour le lieu spécifié
     */
    @FXML
    private void handleShowMap() {
        String lieu = lieuField.getText().trim();
        if (lieu.isEmpty()) {
            showAlert("Erreur", "Veuillez d'abord entrer un lieu.");
            return;
        }
        
        try {
            // Rendre le conteneur de carte visible
            mapContainer.setVisible(true);
            mapContainer.setManaged(true);
            
            // Encoder le lieu pour l'URL
            String encodedLieu = URLEncoder.encode(lieu, StandardCharsets.UTF_8.toString());
            
            // Charger la carte
            WebEngine webEngine = mapWebView.getEngine();
            
            // HTML personnalisé pour intégrer OpenStreetMap sans clé API
            String mapHtml = "<!DOCTYPE html>"
                    + "<html>"
                    + "<head>"
                    + "    <meta charset=\"UTF-8\">"
                    + "    <title>Carte</title>"
                    + "    <style>"
                    + "        body, html, #map { width: 100%; height: 100%; margin: 0; padding: 0; }"
                    + "    </style>"
                    + "    <link rel=\"stylesheet\" href=\"https://unpkg.com/leaflet@1.7.1/dist/leaflet.css\"/>"
                    + "    <script src=\"https://unpkg.com/leaflet@1.7.1/dist/leaflet.js\"></script>"
                    + "</head>"
                    + "<body>"
                    + "    <div id=\"map\"></div>"
                    + "    <script>"
                    + "        var map = L.map('map').setView([0, 0], 2);"
                    + "        L.tileLayer('https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png', {"
                    + "            attribution: '&copy; <a href=\"https://www.openstreetmap.org/copyright\">OpenStreetMap</a> contributors'"
                    + "        }).addTo(map);"
                    + "        // Utiliser un service de géocodage pour trouver les coordonnées"
                    + "        fetch('https://nominatim.openstreetmap.org/search?format=json&q=" + encodedLieu + "')"
                    + "            .then(response => response.json())"
                    + "            .then(data => {"
                    + "                if (data && data.length > 0) {"
                    + "                    var lat = parseFloat(data[0].lat);"
                    + "                    var lon = parseFloat(data[0].lon);"
                    + "                    map.setView([lat, lon], 13);"
                    + "                    L.marker([lat, lon]).addTo(map)"
                    + "                        .bindPopup('Lieu: " + lieu + "')"
                    + "                        .openPopup();"
                    + "                } else {"
                    + "                    alert('Lieu non trouvé sur la carte');"
                    + "                }"
                    + "            })"
                    + "            .catch(error => {"
                    + "                console.error('Erreur:', error);"
                    + "                alert('Erreur lors de la recherche du lieu');"
                    + "            });"
                    + "    </script>"
                    + "</body>"
                    + "</html>";
            
            webEngine.loadContent(mapHtml);
            
            LOGGER.info("Carte chargée pour le lieu: " + lieu);
        } catch (Exception e) {
            LOGGER.severe("Erreur lors de l'affichage de la carte : " + e.getMessage());
            showAlert("Erreur", "Impossible d'afficher la carte : " + e.getMessage());
        }
    }

    /**
     * Valide les champs du formulaire
     * @return true si tous les champs sont valides, false sinon
     */
    private boolean validateFields() {
        boolean isValid = true;
        
        // Valider le nom du projet
        if (nomProjetField.getText().trim().isEmpty()) {
            nomProjetField.getStyleClass().add("field-error");
            nomProjetErrorLabel.setText("Le nom du projet est obligatoire");
            nomProjetErrorLabel.setVisible(true);
            isValid = false;
        }
        
        // Valider la description
        if (descriptionProjetField.getText().trim().isEmpty()) {
            descriptionProjetField.getStyleClass().add("field-error");
            descriptionErrorLabel.setText("La description est obligatoire");
            descriptionErrorLabel.setVisible(true);
            isValid = false;
        }
        
        // Valider le lieu
        if (lieuField.getText().trim().isEmpty()) {
            lieuField.getStyleClass().add("field-error");
            lieuErrorLabel.setText("Le lieu est obligatoire");
            lieuErrorLabel.setVisible(true);
            isValid = false;
        }
        
        // Valider la date de fin
        if (dateFinField.getValue() == null) {
            dateFinField.getStyleClass().add("field-error");
            dateFinErrorLabel.setText("La date de fin est obligatoire");
            dateFinErrorLabel.setVisible(true);
            isValid = false;
        }
        
        // Valider l'ID admin
        if (id_admin.getText().trim().isEmpty()) {
            id_admin.getStyleClass().add("field-error");
            adminIdErrorLabel.setText("L'ID admin est obligatoire");
            adminIdErrorLabel.setVisible(true);
            isValid = false;
        }
        
        return isValid;
    }
}
