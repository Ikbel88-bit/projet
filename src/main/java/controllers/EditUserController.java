package controller;

import entities.User;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.*;
import services.ServiceUser;

import java.io.IOException;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Contrôleur pour l'édition d'un utilisateur
 */
public class EditUserController {
    private static final Logger LOGGER = Logger.getLogger(EditUserController.class.getName());
    
    @FXML private TextField nomField;
    @FXML private TextField prenomField;
    @FXML private TextField emailField;
    @FXML private PasswordField passwordField;
    @FXML private PasswordField confirmPasswordField;
    @FXML private ComboBox<String> roleComboBox;
    @FXML private TextField telephoneField;
    @FXML private Label errorLabel;
    
    private MainController mainController;
    private User currentUser;
    private User userToEdit;
    private ServiceUser serviceUser;
    
    /**
     * Initialise le contrôleur
     */
    @FXML
    public void initialize() {
        serviceUser = new ServiceUser();
        
        // Initialiser les rôles
        roleComboBox.getItems().addAll("admin", "employe");
    }
    
    /**
     * Définit le contrôleur principal
     * @param mainController le contrôleur principal
     */
    public void setMainController(MainController mainController) {
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
     * Définit l'utilisateur à éditer
     * @param user l'utilisateur à éditer
     */
    public void setUserToEdit(User user) {
        this.userToEdit = user;
    }
    
    /**
     * Initialise les données
     */
    public void initData() {
        if (userToEdit != null) {
            nomField.setText(userToEdit.getNom());
            prenomField.setText(userToEdit.getPrenom());
            emailField.setText(userToEdit.getEmail());
            roleComboBox.setValue(userToEdit.getRole());
            telephoneField.setText(userToEdit.getTelephone());
        }
    }
    
    /**
     * Gestionnaire pour le bouton Sauvegarder
     */
    @FXML
    private void handleSave() {
        try {
            // Valider les champs
            if (!validateFields()) {
                return;
            }
            
            // Mettre à jour l'utilisateur
            userToEdit.setNom(nomField.getText().trim());
            userToEdit.setPrenom(prenomField.getText().trim());
            userToEdit.setEmail(emailField.getText().trim());
            userToEdit.setRole(roleComboBox.getValue());
            userToEdit.setTelephone(telephoneField.getText().trim());
            
            // Mettre à jour le mot de passe si nécessaire
            if (!passwordField.getText().isEmpty()) {
                userToEdit.setPassword(passwordField.getText());
            }
            
            // Sauvegarder l'utilisateur
            serviceUser.modifier(userToEdit);
            
            // Afficher un message de succès
            showAlert("Succès", "L'utilisateur a été mis à jour avec succès.");
            
            // Retourner à la liste des utilisateurs
            handleCancel();
            
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Erreur SQL lors de la mise à jour de l'utilisateur", e);
            errorLabel.setText("Erreur lors de la mise à jour de l'utilisateur: " + e.getMessage());
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Erreur inattendue", e);
            errorLabel.setText("Une erreur inattendue s'est produite: " + e.getMessage());
        }
    }
    
    /**
     * Gestionnaire pour le bouton Annuler
     */
    @FXML
    private void handleCancel() {
        try {
            // Retourner à la liste des utilisateurs
             FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/GestionUtilisateur.fxml"));
            Parent view = loader.load();
            
            GestionUtilisateurController controller = loader.getController();
            controller.setMainController(mainController);
            if (currentUser != null) {
                controller.setCurrentUser(currentUser);
            }
            
            mainController.setCenter(view);
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Erreur lors du retour à la liste des utilisateurs", e);
            errorLabel.setText("Erreur lors du retour à la liste des utilisateurs: " + e.getMessage());
        }
    }
    
    /**
     * Valide les champs du formulaire
     * @return true si tous les champs sont valides, false sinon
     */
    private boolean validateFields() {
        boolean isValid = true;
        
        // Réinitialiser l'erreur
        errorLabel.setText("");
        
        // Valider le nom
        if (nomField.getText().trim().isEmpty()) {
            errorLabel.setText("Le nom est obligatoire");
            isValid = false;
        }
        
        // Valider le prénom
        if (prenomField.getText().trim().isEmpty()) {
            errorLabel.setText("Le prénom est obligatoire");
            isValid = false;
        }
        
        // Valider l'email
        if (emailField.getText().trim().isEmpty()) {
            errorLabel.setText("L'email est obligatoire");
            isValid = false;
        }
        
        // Valider le rôle
        if (roleComboBox.getValue() == null) {
            errorLabel.setText("Le rôle est obligatoire");
            isValid = false;
        }
        
        // Valider le mot de passe si renseigné
        if (!passwordField.getText().isEmpty() && !passwordField.getText().equals(confirmPasswordField.getText())) {
            errorLabel.setText("Les mots de passe ne correspondent pas");
            isValid = false;
        }
        
        return isValid;
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
}