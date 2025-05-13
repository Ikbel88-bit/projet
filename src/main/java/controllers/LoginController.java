package controllers;

import entities.User;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import services.ServiceUser;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

public class LoginController {

    @FXML
    private TextField emailOrPhoneField;

    @FXML
    private PasswordField passwordField;

    @FXML
    private Button loginButton;

    @FXML
    private Button registerButton;

    @FXML
    private Label errorLabel;

    @FXML
    void login(ActionEvent event) {
        String emailOrPhone = emailOrPhoneField.getText().trim();
        String password = passwordField.getText();

        try {
            if (emailOrPhone.equals("admin") && password.equals("admin")) {
                openAdminScene();
            } else {
                User user = checkUserInDatabase(emailOrPhone, password);
                if (user != null) {
                    String role = user.getRole().trim();
                    if (role.equalsIgnoreCase("Candidat")) {
                        openCandidatScene(user);
                    } else {
                        openUserScene(user);
                    }
                } else {
                    errorLabel.setText("Identifiants incorrects. Veuillez réessayer.");
                }
            }
        } catch (Exception e) {
            errorLabel.setText("Une erreur est survenue. Veuillez réessayer plus tard.");
            e.printStackTrace();
        }
    }

    private void openAdminScene() {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/AfficherUser.fxml"));
            Parent root = fxmlLoader.load();
            Scene scene = new Scene(root);
            Stage stage = (Stage) emailOrPhoneField.getScene().getWindow();
            stage.setScene(scene);
            stage.setTitle("Gestion des Utilisateurs");
            stage.show();
        } catch (IOException e) {
            errorLabel.setText("Erreur lors du chargement de l'interface administrateur");
            e.printStackTrace();
        }
    }

    private void openUserScene(User user) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/User.fxml"));
            Parent root = loader.load();
            UserController controller = loader.getController();
            controller.setUser(user);
            Stage stage = (Stage) emailOrPhoneField.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("Espace Utilisateur");
            stage.show();
        } catch (IOException e) {
            errorLabel.setText("Erreur lors du chargement de l'interface utilisateur");
            e.printStackTrace();
        }
    }

    private void openCandidatScene(User user) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/Candidat.fxml"));
            Parent root = loader.load();
            CandidatController controller = loader.getController();
            controller.setUser(user);
            Stage stage = (Stage) emailOrPhoneField.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("Espace Candidat");
            stage.show();
        } catch (IOException e) {
            errorLabel.setText("Erreur lors du chargement de l'interface candidat");
            e.printStackTrace();
        }
    }

    private User checkUserInDatabase(String emailOrPhone, String password) {
        ServiceUser serviceUser = new ServiceUser();
        try {
            List<User> users = serviceUser.recuperer();
            for (User user : users) {
                if ((user.getEmail().equals(emailOrPhone) || user.getTelephone().equals(emailOrPhone)) &&
                        user.getPassword() != null &&
                        user.getPassword().equals(password)) {
                    return user;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    @FXML
    void register(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/Register.fxml"));
            Parent root = loader.load();
            Scene scene = new Scene(root);
            Stage stage = (Stage) ((Button) event.getSource()).getScene().getWindow();
            stage.setScene(scene);
            stage.setTitle("Inscription");
            stage.show();
        } catch (IOException e) {
            errorLabel.setText("Erreur lors du chargement de l'interface d'inscription");
            e.printStackTrace();
        }
    }
}