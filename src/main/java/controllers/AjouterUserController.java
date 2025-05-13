package controllers;

import entities.User;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TextField;
import javafx.scene.control.MenuButton;
import javafx.scene.control.MenuItem;
import services.ServiceUser;

import java.sql.SQLException;

public class AjouterUserController {

    @FXML
    private TextField nomField;

    @FXML
    private TextField prenomField;

    @FXML
    private TextField emailField;

    @FXML
    private TextField telephoneField;

    @FXML
    private TextField passwordField;

    @FXML
    private MenuButton roleMenuButton;

    private String selectedRole;

    @FXML
    void initialize() {
        // Set up role selection handlers
        roleMenuButton.getItems().forEach(item -> {
            item.setOnAction(event -> {
                selectedRole = item.getText();
                roleMenuButton.setText(selectedRole);
            });
        });
    }

    @FXML
    void ajouteruser(ActionEvent event) {
        // Récupérer les informations des champs
        String nom = nomField.getText();
        String prenom = prenomField.getText();
        String email = emailField.getText();
        String telephone = telephoneField.getText();
        String password = passwordField.getText();

        if (nom.isEmpty() || prenom.isEmpty() || email.isEmpty() || telephone.isEmpty() || password.isEmpty()) {
            showAlert("Erreur", "Veuillez remplir tous les champs.");
            return;
        }

        try {
            // Créer un nouvel utilisateur
            User newUser = new User();
            newUser.setNom(nom);
            newUser.setPrenom(prenom);
            newUser.setEmail(email);
            newUser.setTelephone(telephone);
            newUser.setPassword(password);
            newUser.setRole("Employe"); // Rôle par défaut

            // Ajouter à la base de données
            ServiceUser serviceUser = new ServiceUser();
            serviceUser.ajouter(newUser);

            showAlert("Succès", "Utilisateur ajouté avec succès!");
            
            // Clear fields
            clearFields();
        } catch (SQLException e) {
            showAlert("Erreur", "Erreur lors de l'ajout de l'utilisateur: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void showAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }

    private void clearFields() {
        nomField.clear();
        prenomField.clear();
        emailField.clear();
        telephoneField.clear();
        passwordField.clear();
        roleMenuButton.setText("Sélectionner un rôle");
        selectedRole = null;
    }
}
