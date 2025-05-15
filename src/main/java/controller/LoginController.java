package controller;

import entities.User;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import services.ServiceUser;

import java.io.IOException;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class LoginController {
    private static final Logger LOGGER = Logger.getLogger(LoginController.class.getName());

    @FXML private TextField emailField;
    @FXML private PasswordField passwordField;
    @FXML private Button loginButton;
    @FXML private Button registerButton;
    @FXML private Label errorLabel;
    @FXML private Hyperlink forgotPasswordLink;

    private ServiceUser serviceUser;
    private controllers.MainController mainController;

    @FXML
    public void initialize() {
        serviceUser = new ServiceUser();
        errorLabel.setVisible(false);
        
        // Ajouter un écouteur pour permettre la connexion en appuyant sur Entrée
        passwordField.setOnKeyPressed(this::handleEnterKey);
    }

    public void setMainController(controllers.MainController mainController) {
        this.mainController = mainController;
    }

    @FXML
    private void handleLogin(ActionEvent event) {
        String email = emailField.getText().trim();
        String password = passwordField.getText();
        
        // Validation basique
        if (email.isEmpty() || password.isEmpty()) {
            showError("Veuillez remplir tous les champs");
            return;
        }
        
        try {
            // Authentification de l'utilisateur
            User user = serviceUser.authenticate(email, password);
            if (user != null) {
                // Authentification réussie
                errorLabel.setVisible(false);
                mainController.setCurrentUser(user);
                mainController.switchToDashboard(user);
            } else {
                // Échec de l'authentification
                showError("Email ou mot de passe incorrect");
            }
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Erreur lors de l'authentification", e);
            showError("Erreur de connexion à la base de données");
        }
    }

    @FXML
    private void handleRegister(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/Inscription.fxml"));
            Parent root = loader.load();
            
            controller.InscriptionController controller = loader.getController();
            controller.setMainController(mainController);
            controller.setMode("add");
            
            mainController.setCenter(root);
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Erreur lors du chargement de la page d'inscription", e);
            showError("Erreur lors du chargement de la page d'inscription");
        }
    }

    @FXML
    private void handleForgotPassword(ActionEvent event) {
        // Implémenter la fonctionnalité de récupération de mot de passe
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Récupération de mot de passe");
        alert.setHeaderText(null);
        alert.setContentText("Un email de récupération a été envoyé à votre adresse email si elle existe dans notre système.");
        alert.showAndWait();
    }

    private void handleEnterKey(KeyEvent event) {
        if (event.getCode() == KeyCode.ENTER) {
            loginButton.fire();
        }
    }

    private void showError(String message) {
        errorLabel.setText(message);
        errorLabel.setVisible(true);
    }
}