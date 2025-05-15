package controller;

import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;

import entities.User;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;
import services.ServiceUser;

public class InscriptionController implements Initializable {
    private static final Logger LOGGER = Logger.getLogger(InscriptionController.class.getName());

    @FXML
    private TextField emailField;
    @FXML
    private PasswordField passwordField;
    @FXML
    private PasswordField confirmPasswordField;
    @FXML
    private TextField nomField;
    @FXML
    private TextField prenomField;
    @FXML
    private Button btn_signup;
    @FXML
    private ComboBox<String> roleComboBox;
    @FXML
    private Label errorLabel;
    @FXML
    private Label nomErrorLabel;
    @FXML
    private Label prenomErrorLabel;
    @FXML
    private Label emailErrorLabel;
    @FXML
    private Label passwordErrorLabel;
    @FXML
    private Label confirmPasswordErrorLabel;
    @FXML
    private Label roleErrorLabel;
    @FXML
    private TextField telephoneField;
    @FXML
    private Label telephoneErrorLabel;
    @FXML
    private Button cancelButton; // Ajout du bouton d'annulation

    private ServiceUser serviceUser;
    private MainController mainController;
    private User currentUser; // Ajout de l'utilisateur courant

    private String mode = "add"; // "add" ou "edit"
    private User userToEdit;

    public void setMode(String mode) {
        this.mode = mode;
    }

    public void setUser(User user) {
        this.userToEdit = user;
        
        // Remplir les champs avec les données de l'utilisateur
        nomField.setText(user.getNom());
        prenomField.setText(user.getPrenom());
        emailField.setText(user.getEmail());
        // Ne pas remplir le mot de passe pour des raisons de sécurité
        roleComboBox.setValue(user.getRole());
        telephoneField.setText(user.getTelephone());
        
        // Changer le texte du bouton selon le mode
        if ("edit".equals(mode)) {
            // Trouver et modifier le bouton d'inscription
            Button signupButton = (Button) nomField.getScene().lookup(".button-primary");
            if (signupButton != null) {
                signupButton.setText("Mettre à jour");
            }
        }
    }

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        serviceUser = new ServiceUser();
        roleComboBox.getItems().addAll(
                "admin",
                "employe"
        );
        
        // Masquer tous les labels d'erreur au démarrage
        hideAllErrorLabels();
        
        // Ajouter des écouteurs pour réinitialiser les styles d'erreur
        nomField.textProperty().addListener((observable, oldValue, newValue) -> {
            nomField.getStyleClass().remove("field-error");
            nomErrorLabel.setVisible(false);
        });
        
        prenomField.textProperty().addListener((observable, oldValue, newValue) -> {
            prenomField.getStyleClass().remove("field-error");
            prenomErrorLabel.setVisible(false);
        });
        
        emailField.textProperty().addListener((observable, oldValue, newValue) -> {
            emailField.getStyleClass().remove("field-error");
            emailErrorLabel.setVisible(false);
        });
        
        passwordField.textProperty().addListener((observable, oldValue, newValue) -> {
            passwordField.getStyleClass().remove("field-error");
            passwordErrorLabel.setVisible(false);
        });
        
        confirmPasswordField.textProperty().addListener((observable, oldValue, newValue) -> {
            confirmPasswordField.getStyleClass().remove("field-error");
            confirmPasswordErrorLabel.setVisible(false);
        });
        
        roleComboBox.valueProperty().addListener((observable, oldValue, newValue) -> {
            roleComboBox.getStyleClass().remove("field-error");
            roleErrorLabel.setVisible(false);
        });
        
        telephoneField.textProperty().addListener((observable, oldValue, newValue) -> {
            telephoneField.getStyleClass().remove("field-error");
            telephoneErrorLabel.setVisible(false);
        });
    }

    public void setMainController(MainController mainController) {
        this.mainController = mainController;
    }
    
    public void setCurrentUser(User user) {
        this.currentUser = user;
    }

    private void hideAllErrorLabels() {
        nomErrorLabel.setVisible(false);
        prenomErrorLabel.setVisible(false);
        emailErrorLabel.setVisible(false);
        passwordErrorLabel.setVisible(false);
        confirmPasswordErrorLabel.setVisible(false);
        roleErrorLabel.setVisible(false);
        telephoneErrorLabel.setVisible(false);
        errorLabel.setText("");
    }

    @FXML
    private void signup(ActionEvent event) {
        // Réinitialiser tous les messages d'erreur
        hideAllErrorLabels();
        
        boolean hasError = false;
        
        String email = emailField.getText().trim();
        String password = passwordField.getText();
        String confirmPassword = confirmPasswordField.getText();
        String nom = nomField.getText().trim();
        String prenom = prenomField.getText().trim();
        String selectedRole = roleComboBox.getValue();
        String telephone = telephoneField.getText().trim();

        // Validation du nom
        if (nom.isEmpty()) {
            nomErrorLabel.setText("Le nom est obligatoire");
            nomErrorLabel.setVisible(true);
            nomField.getStyleClass().add("field-error");
            hasError = true;
        } else if (!nom.matches("^[a-zA-ZÀ-ÿ\\s-]{2,30}$")) {
            nomErrorLabel.setText("Nom invalide (2-30 caractères alphabétiques)");
            nomErrorLabel.setVisible(true);
            nomField.getStyleClass().add("field-error");
            hasError = true;
        }

        // Validation du prénom
        if (prenom.isEmpty()) {
            prenomErrorLabel.setText("Le prénom est obligatoire");
            prenomErrorLabel.setVisible(true);
            prenomField.getStyleClass().add("field-error");
            hasError = true;
        } else if (!prenom.matches("^[a-zA-ZÀ-ÿ\\s-]{2,30}$")) {
            prenomErrorLabel.setText("Prénom invalide (2-30 caractères alphabétiques)");
            prenomErrorLabel.setVisible(true);
            prenomField.getStyleClass().add("field-error");
            hasError = true;
        }

        // Validation de l'email
        String emailRegex = "^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}$";
        if (email.isEmpty()) {
            emailErrorLabel.setText("L'email est obligatoire");
            emailErrorLabel.setVisible(true);
            emailField.getStyleClass().add("field-error");
            hasError = true;
        } else if (!email.matches(emailRegex)) {
            emailErrorLabel.setText("Format d'email invalide");
            emailErrorLabel.setVisible(true);
            emailField.getStyleClass().add("field-error");
            hasError = true;
        } else {
            try {
                // Vérifier si l'email existe déjà en mode ajout ou si l'email a changé en mode édition
                if ("add".equals(mode) || (userToEdit != null && !email.equals(userToEdit.getEmail()))) {
                    boolean emailExists = serviceUser.emailExists(email);
                    if (emailExists) {
                        emailErrorLabel.setText("Cet email est déjà utilisé");
                        emailErrorLabel.setVisible(true);
                        emailField.getStyleClass().add("field-error");
                        hasError = true;
                    }
                }
            } catch (Exception e) {
                // Si l'erreur est due à une table manquante, on peut continuer
                if (e.getMessage().contains("doesn't exist")) {
                    LOGGER.warning("La table users n'existe pas encore, on continue l'inscription");
                } else {
                    LOGGER.severe("Erreur lors de la vérification de l'email : " + e.getMessage());
                    errorLabel.setText("Erreur lors de la vérification de l'email : " + e.getMessage());
                    hasError = true;
                }
            }
        }

        // Validation du mot de passe
        if (password.isEmpty()) {
            passwordErrorLabel.setText("Le mot de passe est obligatoire");
            passwordErrorLabel.setVisible(true);
            passwordField.getStyleClass().add("field-error");
            hasError = true;
        } else if (password.length() < 8) {
            passwordErrorLabel.setText("Minimum 8 caractères requis");
            passwordErrorLabel.setVisible(true);
            passwordField.getStyleClass().add("field-error");
            hasError = true;
        } else if (!password.matches(".*[A-Z].*")) {
            passwordErrorLabel.setText("Au moins une majuscule requise");
            passwordErrorLabel.setVisible(true);
            passwordField.getStyleClass().add("field-error");
            hasError = true;
        } else if (!password.matches(".*[0-9].*")) {
            passwordErrorLabel.setText("Au moins un chiffre requis");
            passwordErrorLabel.setVisible(true);
            passwordField.getStyleClass().add("field-error");
            hasError = true;
        }

        // Validation de la confirmation du mot de passe
        if (confirmPassword.isEmpty()) {
            confirmPasswordErrorLabel.setText("Veuillez confirmer le mot de passe");
            confirmPasswordErrorLabel.setVisible(true);
            confirmPasswordField.getStyleClass().add("field-error");
            hasError = true;
        } else if (!password.equals(confirmPassword)) {
            confirmPasswordErrorLabel.setText("Les mots de passe ne correspondent pas");
            confirmPasswordErrorLabel.setVisible(true);
            confirmPasswordField.getStyleClass().add("field-error");
            hasError = true;
        }

        // Validation du rôle
        if (selectedRole == null || selectedRole.isEmpty()) {
            roleErrorLabel.setText("Veuillez sélectionner un rôle");
            roleErrorLabel.setVisible(true);
            roleComboBox.getStyleClass().add("field-error");
            hasError = true;
        }

        // Validation du téléphone (optionnel mais doit être valide s'il est fourni)
        if (!telephone.isEmpty() && !telephone.matches("^[0-9]{8,15}$")) {
            telephoneErrorLabel.setText("Format de téléphone invalide (8-15 chiffres)");
            telephoneErrorLabel.setVisible(true);
            telephoneField.getStyleClass().add("field-error");
            hasError = true;
        }

        // Si des erreurs sont présentes, ne pas continuer
        if (hasError) {
            return;
        }

        try {
            User user;
            if ("edit".equals(mode) && userToEdit != null) {
                // Mode édition
                user = userToEdit;
                user.setNom(nom);
                user.setPrenom(prenom);
                user.setEmail(email);
                // Ne mettre à jour le mot de passe que s'il a été modifié
                if (!password.isEmpty()) {
                    user.setPassword(password);
                }
                user.setRole(selectedRole);
                user.setTelephone(telephone);
                
                serviceUser.modifier(user);
                showAlert(Alert.AlertType.INFORMATION, "Succès", "Mise à jour réussie",
                        "Les informations de l'utilisateur ont été mises à jour avec succès.");
                
                // Fermer la fenêtre si c'est une fenêtre modale
                Stage stage = (Stage) nomField.getScene().getWindow();
                stage.close();
            } else {
                // Mode ajout
                user = new User();
                user.setEmail(email);
                user.setPassword(password);
                user.setNom(nom);
                user.setPrenom(prenom);
                user.setRole(selectedRole);
                user.setTelephone(telephone);

                serviceUser.ajouter(user);
                showAlert(Alert.AlertType.INFORMATION, "Succès", "Inscription réussie",
                        "Votre compte a été créé avec succès. Vous pouvez maintenant vous connecter.");
                goToLogin(null);
            }
        } catch (SQLException e) {
            LOGGER.severe("Erreur lors de l'opération : " + e.getMessage());
            errorLabel.setText("Erreur lors de l'opération : " + e.getMessage());
        }
    }

    private void showAlert(Alert.AlertType type, String title, String header, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(header);
        alert.setContentText(message);
        alert.showAndWait();
    }

    @FXML
    private void goToLogin(ActionEvent event) {
        try {
            if (mainController != null) {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/Connexion.fxml"));
                Parent root = loader.load();
                LoginController loginController = loader.getController();
                loginController.setMainController(mainController);
                mainController.setCenter(root);
            } else {
                LOGGER.severe("MainController est null, impossible de naviguer vers la page de connexion");
                errorLabel.setText("Erreur lors de la navigation vers la page de connexion");
            }
        } catch (IOException e) {
            LOGGER.severe("Erreur lors du chargement de la page de connexion : " + e.getMessage());
            errorLabel.setText("Erreur lors du chargement de la page de connexion : " + e.getMessage());
        }
    }

    /**
     * Gère le clic sur le bouton Annuler.
     */
    @FXML
    private void handleCancel() {
        if ("add".equals(mode) && mainController != null) {
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
            } catch (IOException e) {
                LOGGER.log(Level.SEVERE, "Erreur lors du retour à la gestion des utilisateurs", e);
                errorLabel.setText("Erreur lors du retour à la gestion des utilisateurs: " + e.getMessage());
            }
        } else {
            // Fermer la fenêtre si c'est une fenêtre modale
            Stage stage = (Stage) nomField.getScene().getWindow();
            stage.close();
        }
    }
}