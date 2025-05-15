package controllers;

import entities.Tache;
import entities.User;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.effect.DropShadow;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import services.ServiceTache;
import services.ServiceUser;

import java.io.IOException;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Contrôleur pour la gestion des tâches.
 */
public class GestionTacheController {
    private static final Logger LOGGER = Logger.getLogger(GestionTacheController.class.getName());
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    
    @FXML private TextField nomTacheField;
    @FXML private TextArea descriptionTacheField;
    @FXML private DatePicker dateDebutField;
    @FXML private DatePicker dateFinTacheField;
    @FXML private ChoiceBox<String> etatTacheField;
    @FXML private TextField projetIdField;
    @FXML private TextField employeIdField;
    @FXML private TextField searchField;
    @FXML private TableView<Tache> tacheTableView;
    
    @FXML private Label nomTacheErrorLabel;
    @FXML private Label descriptionTacheErrorLabel;
    @FXML private Label dateDebutTacheErrorLabel;
    @FXML private Label dateFinTacheErrorLabel;
    @FXML private Label etatTacheErrorLabel;
    @FXML private Label projetIdErrorLabel;
    @FXML private Label employeIdErrorLabel;
    
    @FXML private Label tachesTermineesLabel;
    @FXML private Label tachesEnCoursLabel;
    @FXML private Label tachesEnRetardLabel;
    
    @FXML private FlowPane tasksContainer;

    private ServiceTache serviceTache;
    private Tache tache;
    private boolean isAdmin;
    private controllers.MainController mainController;
    private User currentUser;

    /**
     * Définit si l'utilisateur est un administrateur
     * @param isAdmin true si l'utilisateur est admin, false sinon
     */
    public void setIsAdmin(boolean isAdmin) {
        this.isAdmin = isAdmin;
        LOGGER.info("setIsAdmin appelé avec: " + isAdmin);
    }

    /**
     * Définit le contrôleur principal
     * @param mainController le contrôleur principal
     */
    public void setMainController(controllers.MainController mainController) {
        this.mainController = mainController;
    }

    /**
     * Définit l'utilisateur actuel
     * @param user l'utilisateur actuel
     */
    public void setCurrentUser(User user) {
        this.currentUser = user;
    }

    /**
     * Définit la tâche à éditer
     * @param tache Tâche à éditer
     */
    public void setTache(Tache tache) {
        this.tache = tache;
        
        // Remplir les champs avec les données de la tâche
        if (tache != null) {
            nomTacheField.setText(tache.getNom_tache());
            descriptionTacheField.setText(tache.getDescription());
            dateDebutField.setValue(tache.getDate_debut());
            dateFinTacheField.setValue(tache.getDate_fin());
            etatTacheField.setValue(tache.getEtat());
            projetIdField.setText(String.valueOf(tache.getId_projet()));
            employeIdField.setText(String.valueOf(tache.getId_employe()));
        }
    }

    @FXML
    public void initialize() {
        serviceTache = new ServiceTache();
        
        // Initialiser le tableau des tâches
        initializeTableView();
        
        // Initialiser les choix d'état
        if (etatTacheField != null) {
            etatTacheField.setItems(FXCollections.observableArrayList(
                "EN_COURS", "TERMINE", "EN_RETARD"
            ));
            etatTacheField.setValue("EN_COURS");
        }
        
        // Masquer tous les labels d'erreur
        hideAllErrorLabels();
        
        // Ajouter des écouteurs pour réinitialiser les styles d'erreur
        setupFieldListeners();
        
        // Initialiser avec une nouvelle tâche
        handleNouvelleTache();
        
        // Charger les tâches existantes
        loadTaches();
        
        // Charger les statistiques
        loadStatistics();
        
        // Configurer la recherche
        if (searchField != null) {
            searchField.textProperty().addListener((observable, oldValue, newValue) -> {
                try {
                    if (newValue.isEmpty()) {
                        loadTaches();
                    } else {
                        List<Tache> filteredTaches = serviceTache.rechercher(newValue);
                        
                        // Mettre à jour le tableau
                        if (tacheTableView != null) {
                            tacheTableView.setItems(FXCollections.observableArrayList(filteredTaches));
                        }
                        
                        // Mettre à jour le conteneur de cartes
                        if (tasksContainer != null) {
                            displayTaches(filteredTaches);
                        }
                    }
                } catch (SQLException e) {
                    LOGGER.severe("Erreur lors de la recherche : " + e.getMessage());
                }
            });
        }
        
        // Configuration des DatePickers
        if (dateDebutField != null) {
            // Définir la date de début par défaut à aujourd'hui
            dateDebutField.setValue(LocalDate.now());
            
            // Empêcher la sélection de dates passées
            dateDebutField.setDayCellFactory(picker -> new DateCell() {
                @Override
                public void updateItem(LocalDate date, boolean empty) {
                    super.updateItem(date, empty);
                    setDisable(empty || date.isBefore(LocalDate.now()));
                }
            });
            
            // Mettre à jour la date de fin lorsque la date de début change
            dateDebutField.valueProperty().addListener((observable, oldValue, newValue) -> {
                if (dateFinTacheField != null) {
                    // Si la date de fin est avant la nouvelle date de début, la mettre à jour
                    if (dateFinTacheField.getValue() == null || dateFinTacheField.getValue().isBefore(newValue)) {
                        dateFinTacheField.setValue(newValue.plusDays(7));
                    }
                    
                    // Mettre à jour les contraintes de la date de fin
                    dateFinTacheField.setDayCellFactory(picker -> new DateCell() {
                        @Override
                        public void updateItem(LocalDate date, boolean empty) {
                            super.updateItem(date, empty);
                            setDisable(empty || date.isBefore(newValue));
                        }
                    });
                }
            });
        }
        
        if (dateFinTacheField != null) {
            // Définir la date de fin par défaut à une semaine après aujourd'hui
            dateFinTacheField.setValue(LocalDate.now().plusDays(7));
            
            // Empêcher la sélection de dates avant la date de début
            dateFinTacheField.setDayCellFactory(picker -> new DateCell() {
                @Override
                public void updateItem(LocalDate date, boolean empty) {
                    super.updateItem(date, empty);
                    LocalDate minDate = dateDebutField != null ? dateDebutField.getValue() : LocalDate.now();
                    setDisable(empty || date.isBefore(minDate));
                }
            });
        }
    }

    /**
     * Valide les champs du formulaire
     * @return true si tous les champs sont valides, false sinon
     */
    private boolean validateFields() {
        boolean isValid = true;
        
        // Réinitialiser tous les messages d'erreur
        hideAllErrorLabels();
        
        // Valider le nom de la tâche
        if (nomTacheField.getText().trim().isEmpty()) {
            nomTacheErrorLabel.setText("Le nom de la tâche est obligatoire");
            nomTacheErrorLabel.setVisible(true);
            nomTacheField.getStyleClass().add("field-error");
            isValid = false;
        }
        
        // Valider la description
        if (descriptionTacheField.getText().trim().isEmpty()) {
            descriptionTacheErrorLabel.setText("La description est obligatoire");
            descriptionTacheErrorLabel.setVisible(true);
            descriptionTacheField.getStyleClass().add("field-error");
            isValid = false;
        }
        
        // Valider la date de début
        if (dateDebutField.getValue() == null) {
            dateDebutTacheErrorLabel.setText("La date de début est obligatoire");
            dateDebutTacheErrorLabel.setVisible(true);
            dateDebutField.getStyleClass().add("field-error");
            isValid = false;
        }
        
        // Valider la date de fin
        if (dateFinTacheField.getValue() == null) {
            dateFinTacheErrorLabel.setText("La date de fin est obligatoire");
            dateFinTacheErrorLabel.setVisible(true);
            dateFinTacheField.getStyleClass().add("field-error");
            isValid = false;
        } else if (dateDebutField.getValue() != null && 
                   dateFinTacheField.getValue().isBefore(dateDebutField.getValue())) {
            dateFinTacheErrorLabel.setText("La date de fin doit être après la date de début");
            dateFinTacheErrorLabel.setVisible(true);
            dateFinTacheField.getStyleClass().add("field-error");
            isValid = false;
        }
        
        // Valider l'état
        if (etatTacheField.getValue() == null || etatTacheField.getValue().isEmpty()) {
            etatTacheErrorLabel.setText("L'état est obligatoire");
            etatTacheErrorLabel.setVisible(true);
            etatTacheField.getStyleClass().add("field-error");
            isValid = false;
        }
        
        // Valider l'ID du projet
        try {
            if (projetIdField.getText().trim().isEmpty()) {
                projetIdErrorLabel.setText("L'ID du projet est obligatoire");
                projetIdErrorLabel.setVisible(true);
                projetIdField.getStyleClass().add("field-error");
                isValid = false;
            } else {
                Integer.parseInt(projetIdField.getText().trim());
            }
        } catch (NumberFormatException e) {
            projetIdErrorLabel.setText("L'ID du projet doit être un nombre");
            projetIdErrorLabel.setVisible(true);
            projetIdField.getStyleClass().add("field-error");
            isValid = false;
        }
        
        // Valider l'ID de l'employé
        try {
            if (employeIdField.getText().trim().isEmpty()) {
                employeIdErrorLabel.setText("L'ID de l'employé est obligatoire");
                employeIdErrorLabel.setVisible(true);
                employeIdField.getStyleClass().add("field-error");
                isValid = false;
            } else {
                Integer.parseInt(employeIdField.getText().trim());
            }
        } catch (NumberFormatException e) {
            employeIdErrorLabel.setText("L'ID de l'employé doit être un nombre");
            employeIdErrorLabel.setVisible(true);
            employeIdField.getStyleClass().add("field-error");
            isValid = false;
        }
        
        return isValid;
    }

    /**
     * Cache tous les labels d'erreur
     */
    private void hideAllErrorLabels() {
        if (nomTacheErrorLabel != null) nomTacheErrorLabel.setVisible(false);
        if (descriptionTacheErrorLabel != null) descriptionTacheErrorLabel.setVisible(false);
        if (dateDebutTacheErrorLabel != null) dateDebutTacheErrorLabel.setVisible(false);
        if (dateFinTacheErrorLabel != null) dateFinTacheErrorLabel.setVisible(false);
        if (etatTacheErrorLabel != null) etatTacheErrorLabel.setVisible(false);
        if (projetIdErrorLabel != null) projetIdErrorLabel.setVisible(false);
        if (employeIdErrorLabel != null) employeIdErrorLabel.setVisible(false);
        
        // Supprimer la classe d'erreur des champs
        if (nomTacheField != null) nomTacheField.getStyleClass().remove("field-error");
        if (descriptionTacheField != null) descriptionTacheField.getStyleClass().remove("field-error");
        if (dateDebutField != null) dateDebutField.getStyleClass().remove("field-error");
        if (dateFinTacheField != null) dateFinTacheField.getStyleClass().remove("field-error");
        if (etatTacheField != null) etatTacheField.getStyleClass().remove("field-error");
        if (projetIdField != null) projetIdField.getStyleClass().remove("field-error");
        if (employeIdField != null) employeIdField.getStyleClass().remove("field-error");
    }
    
    private void setupFieldListeners() {
        if (nomTacheField != null) {
            nomTacheField.textProperty().addListener((observable, oldValue, newValue) -> {
                nomTacheField.getStyleClass().remove("field-error");
                if (nomTacheErrorLabel != null) {
                    nomTacheErrorLabel.setVisible(false);
                }
            });
        }
        
        // Ajoutez des écouteurs similaires pour les autres champs
        // ...
    }
    
    private void loadTaches() {
        try {
            // Récupérer la liste des tâches
            List<Tache> tachesList = serviceTache.recuperer();
            // Convertir en ObservableList
            ObservableList<Tache> taches = FXCollections.observableArrayList(tachesList);
            
            // Mettre à jour le tableau si disponible
            if (tacheTableView != null) {
                tacheTableView.setItems(taches);
            }
            
            // Mettre à jour le conteneur de cartes si disponible
            if (tasksContainer != null) {
                displayTaches(tachesList);
            }
            
            // Mettre à jour les statistiques
            loadStatistics();
        } catch (SQLException e) {
            LOGGER.severe("Erreur lors du chargement des tâches : " + e.getMessage());
            showAlert("Erreur", "Impossible de charger les tâches : " + e.getMessage());
        }
    }
    
    private void displayTaches(List<Tache> taches) {
        if (tasksContainer != null) {
            tasksContainer.getChildren().clear();
            
            for (Tache t : taches) {
                VBox taskCard = createTaskCard(t);
                tasksContainer.getChildren().add(taskCard);
            }
        }
    }
    
    private VBox createTaskCard(Tache tache) {
        VBox card = new VBox();
        card.getStyleClass().addAll("task-card", "card");
        card.setPadding(new Insets(15));
        card.setSpacing(10);
        card.setMinWidth(300);
        card.setMaxWidth(350);
        
        // Appliquer une couleur de fond selon l'état
        switch (tache.getEtat()) {
            case "TERMINE":
                card.setStyle("-fx-background-color: #d5f5e3;"); // Vert clair
                break;
            case "EN_RETARD":
                card.setStyle("-fx-background-color: #fadbd8;"); // Rouge clair
                break;
            default: // EN_COURS
                card.setStyle("-fx-background-color: #ebf5fb;"); // Bleu clair
                break;
        }
        
        // Titre de la tâche
        Label titleLabel = new Label(tache.getNom_tache());
        titleLabel.getStyleClass().add("card-title");
        titleLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 16px;");
        
        // Description
        Label descLabel = new Label(tache.getDescription());
        descLabel.getStyleClass().add("card-description");
        descLabel.setWrapText(true);
        descLabel.setStyle("-fx-font-size: 14px;");
        
        // Dates
        HBox datesBox = new HBox(10);
        
        Label dateDebutLabel = new Label("Début: " + 
            (tache.getDate_debut() != null ? tache.getDate_debut().format(DATE_FORMATTER) : "N/A"));
        dateDebutLabel.setStyle("-fx-font-size: 12px;");
        
        Label dateFinLabel = new Label("Fin: " + 
            (tache.getDate_fin() != null ? tache.getDate_fin().format(DATE_FORMATTER) : "N/A"));
        dateFinLabel.setStyle("-fx-font-size: 12px;");
        
        datesBox.getChildren().addAll(dateDebutLabel, dateFinLabel);
        
        // État
        Label etatLabel = new Label(tache.getEtat());
        etatLabel.getStyleClass().add("state-badge");
        etatLabel.getStyleClass().add(tache.getEtat().toLowerCase().replace("_", "-"));
        etatLabel.setPadding(new Insets(5, 10, 5, 10));
        etatLabel.setStyle("-fx-background-radius: 15; -fx-font-weight: bold; -fx-font-size: 12px;");
        
        // Projet
        Label projetLabel = new Label("Projet: " + (tache.getNomProjet() != null ? tache.getNomProjet() : "N/A"));
        projetLabel.setStyle("-fx-font-size: 12px; -fx-font-style: italic;");
        
        // Boutons d'action
        HBox actionsBox = new HBox(10);
        actionsBox.setAlignment(Pos.CENTER_RIGHT);
        
        Button editButton = new Button("Modifier");
        editButton.getStyleClass().add("btn-primary");
        editButton.setOnAction(e -> populateFields(tache));
        
        Button deleteButton = new Button("Supprimer");
        deleteButton.getStyleClass().add("btn-danger");
        deleteButton.setOnAction(e -> {
            this.tache = tache;
            handleSupprimerTache();
        });
        
        actionsBox.getChildren().addAll(editButton, deleteButton);
        
        // Ajouter tous les éléments à la carte
        card.getChildren().addAll(titleLabel, descLabel, datesBox, etatLabel, projetLabel, actionsBox);
        
        // Ajouter un effet d'ombre
        DropShadow shadow = new DropShadow();
        shadow.setRadius(5.0);
        shadow.setOffsetX(3.0);
        shadow.setOffsetY(3.0);
        shadow.setColor(Color.color(0.4, 0.4, 0.4, 0.3));
        card.setEffect(shadow);
        
        return card;
    }
    
    private void loadStatistics() {
        try {
            List<Integer> stats = serviceTache.getStatistics();
            if (tachesTermineesLabel != null) {
                tachesTermineesLabel.setText(String.valueOf(stats.get(0)));
            }
            if (tachesEnCoursLabel != null) {
                tachesEnCoursLabel.setText(String.valueOf(stats.get(1)));
            }
            if (tachesEnRetardLabel != null) {
                tachesEnRetardLabel.setText(String.valueOf(stats.get(2)));
            }
        } catch (SQLException e) {
            LOGGER.severe("Erreur lors du chargement des statistiques : " + e.getMessage());
            showAlert("Erreur", "Impossible de charger les statistiques: " + e.getMessage());
        }
    }
    
    private void populateFields(Tache tache) {
        this.tache = tache;
        if (nomTacheField != null) nomTacheField.setText(tache.getNom_tache());
        if (descriptionTacheField != null) descriptionTacheField.setText(tache.getDescription());
        if (dateDebutField != null) dateDebutField.setValue(tache.getDate_debut());
        if (dateFinTacheField != null) dateFinTacheField.setValue(tache.getDate_fin());
        if (etatTacheField != null) etatTacheField.setValue(tache.getEtat());
        if (projetIdField != null) projetIdField.setText(String.valueOf(tache.getId_projet()));
        if (employeIdField != null) employeIdField.setText(String.valueOf(tache.getId_employe()));
    }
    
    @FXML
    private void handleSauvegarderTache() {
        // Vérifier si l'utilisateur est admin
        if (currentUser == null || !"ADMIN".equalsIgnoreCase(currentUser.getRole())) {
            showAlert("Erreur", "Seul un administrateur peut modifier les tâches.");
            return;
        }
        
        // Validation des champs
        if (!validateFields()) {
            return;
        }
        
        try {
            Tache tacheToSave = (tache != null) ? tache : new Tache();
            tacheToSave.setNom_tache(nomTacheField.getText().trim());
            tacheToSave.setDescription(descriptionTacheField.getText().trim());
            tacheToSave.setDate_debut(dateDebutField.getValue());
            tacheToSave.setDate_fin(dateFinTacheField.getValue());
            tacheToSave.setEtat(etatTacheField.getValue());
            tacheToSave.setId_projet(Integer.parseInt(projetIdField.getText().trim()));
            tacheToSave.setId_employe(Integer.parseInt(employeIdField.getText().trim()));

            if (tache == null || tache.getId() == 0) {
                serviceTache.ajouter(tacheToSave);
                showAlert("Succès", "Tâche ajoutée avec succès.");
            } else {
                serviceTache.modifier(tacheToSave);
                showAlert("Succès", "Tâche modifiée avec succès.");
            }

            // Rafraîchir les données
            loadTaches();
            loadStatistics();
            clearFields();
        } catch (SQLException e) {
            LOGGER.severe("Erreur lors de la sauvegarde : " + e.getMessage());
            showAlert("Erreur", "Erreur lors de la sauvegarde : " + e.getMessage());
        }
    }
    
    @FXML
    private void handleSupprimerTache() {
        // Vérifier si l'utilisateur est admin
        if (currentUser == null || !"ADMIN".equalsIgnoreCase(currentUser.getRole())) {
            showAlert("Erreur", "Seul un administrateur peut supprimer les tâches.");
            return;
        }
        
        if (tache == null || tache.getId() == 0) {
            showAlert("Erreur", "Veuillez d'abord sélectionner une tâche à supprimer.");
            return;
        }
        
        try {
            // Demander confirmation avant de supprimer
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Confirmation de suppression");
            alert.setHeaderText(null);
            alert.setContentText("Êtes-vous sûr de vouloir supprimer cette tâche ?");
            
            if (alert.showAndWait().get() == ButtonType.OK) {
                serviceTache.supprimer(tache.getId());
                showAlert("Succès", "La tâche a été supprimée avec succès.");
                loadTaches();
                loadStatistics();
                clearFields();
                tache = null;
            }
        } catch (SQLException e) {
            LOGGER.severe("Erreur lors de la suppression de la tâche : " + e.getMessage());
            showAlert("Erreur", "Impossible de supprimer la tâche: " + e.getMessage());
        }
    }
    
    @FXML
    private void handleNouvelleTache() {
        clearFields();
        tache = null;
    }
    
    private void clearFields() {
        if (nomTacheField != null) nomTacheField.clear();
        if (descriptionTacheField != null) descriptionTacheField.clear();
        if (dateDebutField != null) dateDebutField.setValue(LocalDate.now());
        if (dateFinTacheField != null) dateFinTacheField.setValue(LocalDate.now().plusDays(7));
        if (etatTacheField != null) etatTacheField.setValue("EN_COURS");
        if (projetIdField != null) projetIdField.clear();
        if (employeIdField != null) employeIdField.clear();
        hideAllErrorLabels();
    }
    
    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    /**
     * Gestionnaire pour le bouton Retour
     */
    @FXML
    public void handleRetour() {
        try {
            // Vérifier si nous avons un contrôleur principal
            if (mainController != null) {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/AdminDashboard.fxml"));
                Parent view = loader.load();
                
                controller.AdminDashboardController controller = loader.getController();
                controller.setMainController(mainController);
                if (currentUser != null) {
                    controller.setCurrentUser(currentUser);
                }
                controller.initData();
                
                mainController.setCenter(view);
            } else {
                LOGGER.warning("mainController est null, impossible de naviguer vers le tableau de bord");
                // Essayer de naviguer en utilisant la scène actuelle
                try {
                    Stage stage = (Stage) nomTacheField.getScene().getWindow();
                    if (stage != null) {
                        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/AdminDashboard.fxml"));
                        Parent root = loader.load();
                        Scene scene = new Scene(root);
                        stage.setScene(scene);
                        LOGGER.info("Navigation vers le dashboard via changement de scène");
                    }
                } catch (Exception ex) {
                    LOGGER.log(Level.SEVERE, "Erreur lors de la navigation alternative", ex);
                }
            }
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Erreur lors du chargement du tableau de bord", e);
            showAlert("Erreur", "Impossible de charger le tableau de bord: " + e.getMessage());
        }
    }

    /**
     * Initialise le tableau des tâches
     */
    private void initializeTableView() {
        if (tacheTableView != null) {
            // Créer les colonnes
            TableColumn<Tache, Integer> idCol = new TableColumn<>("ID");
            idCol.setCellValueFactory(new PropertyValueFactory<>("id"));
            
            TableColumn<Tache, String> nomCol = new TableColumn<>("Nom");
            nomCol.setCellValueFactory(new PropertyValueFactory<>("nom_tache"));
            
            TableColumn<Tache, String> descCol = new TableColumn<>("Description");
            descCol.setCellValueFactory(new PropertyValueFactory<>("description"));
            descCol.setPrefWidth(150);
            
            TableColumn<Tache, LocalDate> dateDebutCol = new TableColumn<>("Date début");
            dateDebutCol.setCellValueFactory(new PropertyValueFactory<>("date_debut"));
            
            TableColumn<Tache, LocalDate> dateFinCol = new TableColumn<>("Date fin");
            dateFinCol.setCellValueFactory(new PropertyValueFactory<>("date_fin"));
            
            TableColumn<Tache, String> etatCol = new TableColumn<>("État");
            etatCol.setCellValueFactory(new PropertyValueFactory<>("etat"));
            
            TableColumn<Tache, Integer> projetIdCol = new TableColumn<>("ID Projet");
            projetIdCol.setCellValueFactory(new PropertyValueFactory<>("id_projet"));
            
            TableColumn<Tache, Integer> employeIdCol = new TableColumn<>("ID Employé");
            employeIdCol.setCellValueFactory(new PropertyValueFactory<>("id_employe"));
            
            // Ajouter les colonnes au tableau
            tacheTableView.getColumns().addAll(idCol, nomCol, descCol, dateDebutCol, 
                                              dateFinCol, etatCol, projetIdCol, employeIdCol);
            
            // Ajouter un écouteur de sélection
            tacheTableView.getSelectionModel().selectedItemProperty().addListener(
                (observable, oldValue, newValue) -> {
                    if (newValue != null) {
                        populateFields(newValue);
                    }
                }
            );
        }
    }
}
