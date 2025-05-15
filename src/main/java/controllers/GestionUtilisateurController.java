package controller;

import entities.User;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.Callback;
import services.ServiceUser;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;

public class  GestionUtilisateurController {
    private static final Logger LOGGER = Logger.getLogger(GestionUtilisateurController.class.getName());
    
    @FXML private TableView<User> userTable;
    @FXML private TableColumn<User, Integer> idColumn;
    @FXML private TableColumn<User, String> nomColumn;
    @FXML private TableColumn<User, String> prenomColumn;
    @FXML private TableColumn<User, String> emailColumn;
    @FXML private TableColumn<User, String> roleColumn;
    @FXML private TableColumn<User, String> telephoneColumn;
    @FXML private TableColumn<User, Void> actionsColumn;
    @FXML private TextField searchField;
    
    private ServiceUser serviceUser;
    private ObservableList<User> userList = FXCollections.observableArrayList();
    private MainController mainController;
    private User currentUser;

    /**
     * Définit l'utilisateur actuel
     * @param user l'utilisateur actuel
     */
    public void setCurrentUser(User user) {
        this.currentUser = user;
    }
    
    @FXML
    public void initialize() {
        serviceUser = new ServiceUser();
        
        // Initialiser les colonnes
        idColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        nomColumn.setCellValueFactory(new PropertyValueFactory<>("nom"));
        prenomColumn.setCellValueFactory(new PropertyValueFactory<>("prenom"));
        emailColumn.setCellValueFactory(new PropertyValueFactory<>("email"));
        roleColumn.setCellValueFactory(new PropertyValueFactory<>("role"));
        telephoneColumn.setCellValueFactory(new PropertyValueFactory<>("telephone"));
        
        // Configurer la colonne d'actions
        setupActionsColumn();
        
        // Charger les données
        loadUsers();
    }
    
    private void setupActionsColumn() {
        Callback<TableColumn<User, Void>, TableCell<User, Void>> cellFactory = new Callback<>() {
            @Override
            public TableCell<User, Void> call(final TableColumn<User, Void> param) {
                return new TableCell<>() {
                    private final Button editBtn = new Button("Modifier");
                    private final Button deleteBtn = new Button("Supprimer");
                    private final HBox pane = new HBox(5, editBtn, deleteBtn);
                    
                    {
                        editBtn.getStyleClass().add("button-edit");
                        deleteBtn.getStyleClass().add("button-delete");
                        
                        editBtn.setOnAction(event -> {
                            User user = getTableView().getItems().get(getIndex());
                            handleEditUser(user);
                        });
                        
                        deleteBtn.setOnAction(event -> {
                            User user = getTableView().getItems().get(getIndex());
                            handleDeleteUser(user);
                        });
                    }
                    
                    @Override
                    protected void updateItem(Void item, boolean empty) {
                        super.updateItem(item, empty);
                        setGraphic(empty ? null : pane);
                    }
                };
            }
        };
        
        actionsColumn.setCellFactory(cellFactory);
    }
    
    private void loadUsers() {
        try {
            List<User> users = serviceUser.recuperer();
            userList.clear();
            userList.addAll(users);
            userTable.setItems(userList);
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Erreur lors du chargement des utilisateurs", e);
            showAlert(Alert.AlertType.ERROR, "Erreur", "Erreur lors du chargement des utilisateurs", e.getMessage());
        }
    }
    
    @FXML
    private void handleSearch() {
        String searchTerm = searchField.getText().trim();
        if (searchTerm.isEmpty()) {
            loadUsers();
            return;
        }
        
        try {
            List<User> users = serviceUser.rechercher(searchTerm);
            userList.clear();
            userList.addAll(users);
            userTable.setItems(userList);
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Erreur lors de la recherche d'utilisateurs", e);
            showAlert(Alert.AlertType.ERROR, "Erreur", "Erreur lors de la recherche d'utilisateurs", e.getMessage());
        }
    }
    
    @FXML
    private void handleRefresh() {
        searchField.clear();
        loadUsers();
    }
    
    @FXML
    private void handleAddUser() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/Inscription.fxml"));
            Parent root = loader.load();
            
            InscriptionController controller = loader.getController();
            controller.setMainController(mainController);
            controller.setMode("add");
            
            Stage stage = new Stage();
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setTitle("Ajouter un utilisateur");
            stage.setScene(new Scene(root));
            stage.showAndWait();
            
            // Rafraîchir la liste après l'ajout
            loadUsers();
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Erreur lors de l'ouverture du formulaire d'ajout", e);
            showAlert(Alert.AlertType.ERROR, "Erreur", "Erreur lors de l'ouverture du formulaire d'ajout", e.getMessage());
        }
    }
    
    /**
     * Gestionnaire pour le bouton Modifier
     */
    private void handleEditUser(User user) {
        try {
            // Charger la vue d'édition d'utilisateur
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/EditUser.fxml"));
            Parent view = loader.load();
            
            // Configurer le contrôleur
            EditUserController controller = loader.getController();
            controller.setMainController(mainController);
            controller.setCurrentUser(currentUser);
            controller.setUserToEdit(user);
            controller.initData();
            
            // Afficher la vue
            mainController.setCenter(view);
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Erreur lors du chargement de la vue d'édition", e);
            showAlert("Erreur", "Impossible de charger la vue d'édition: " + e.getMessage());
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Erreur inattendue", e);
            showAlert("Erreur", "Une erreur inattendue s'est produite: " + e.getMessage());
        }
    }
    
    /**
     * Gestionnaire pour le bouton Supprimer
     */
    private void handleDeleteUser(User user) {
        try {
            // Demander confirmation
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Confirmation de suppression");
            alert.setHeaderText("Supprimer l'utilisateur");
            alert.setContentText("Êtes-vous sûr de vouloir supprimer l'utilisateur " + user.getNom() + " " + user.getPrenom() + " ?");
            
            if (alert.showAndWait().get() == ButtonType.OK) {
                // Supprimer l'utilisateur
                serviceUser.supprimer(user);
                
                // Rafraîchir la table
                handleRefresh();
                
                // Afficher un message de succès
                showAlert("Succès", "L'utilisateur a été supprimé avec succès.");
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Erreur SQL lors de la suppression de l'utilisateur", e);
            showAlert("Erreur", "Impossible de supprimer l'utilisateur: " + e.getMessage());
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Erreur inattendue", e);
            showAlert("Erreur", "Une erreur inattendue s'est produite: " + e.getMessage());
        }
    }
    
    private void showAlert(Alert.AlertType type, String title, String header, String content) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(header);
        alert.setContentText(content);
        alert.showAndWait();
    }
    
    /**
     * Affiche une alerte
     * @param title le titre de l'alerte
     * @param message le message de l'alerte
     */
    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
    
    public void setMainController(MainController mainController) {
        this.mainController = mainController;
    }

    /**
     * Gère le clic sur le bouton Retour.
     */
    @FXML
    private void handleRetour() {
        if (mainController != null) {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/AdminDashboard.fxml"));
                Parent view = loader.load();
                
                AdminDashboardController controller = loader.getController();
                controller.setMainController(mainController);
                controller.setCurrentUser(currentUser);
                controller.initData();
                
                mainController.setCenter(view);
            } catch (IOException e) {
                LOGGER.log(Level.SEVERE, "Erreur lors du chargement du dashboard admin", e);
                showAlert(Alert.AlertType.ERROR, "Erreur", "Impossible de charger le dashboard admin", e.getMessage());
            }
        }
    }
}
