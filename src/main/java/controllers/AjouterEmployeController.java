package controllers;

import entities.User;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import services.ServiceUser;

import java.io.IOException;
import java.sql.SQLException;

public class AjouterEmployeController {

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
    void ajouterEmploye(ActionEvent event) {
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

        if (!isValidPhoneNumber(telephone)) {
            showAlert("Erreur", getPhoneNumberErrorMessage(telephone));
            return;
        }

        if (!isValidEmail(email)) {
            showAlert("Erreur", "Format d'email invalide. Exemple: nom.prenom@gmail.com");
            return;
        }

        try {
            // Créer un nouvel employé
            User newUser = new User();
            newUser.setNom(nom);
            newUser.setPrenom(prenom);
            newUser.setEmail(email);
            newUser.setTelephone(telephone);
            newUser.setPassword(password);
            newUser.setRole("Employe");

            // Ajouter à la base de données
            ServiceUser serviceUser = new ServiceUser();
            serviceUser.ajouter(newUser);

            showAlert("Succès", "Employé ajouté avec succès!");
            
            // Clear fields
            clearFields();
            
            // Retourner à la page précédente
            retourPrecedent();
        } catch (SQLException e) {
            showAlert("Erreur", "Erreur lors de l'ajout de l'employé: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML
    void retourPrecedent() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/AfficherUser.fxml"));
            Parent root = loader.load();
            Scene scene = new Scene(root);
            Stage stage = (Stage) nomField.getScene().getWindow();
            stage.setScene(scene);
            stage.setTitle("Gestion des Utilisateurs");
            stage.show();
        } catch (IOException e) {
            showAlert("Erreur", "Erreur lors du retour à la page précédente: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private boolean isValidPhoneNumber(String phone) {
        // Vérifie si le numéro est un numéro tunisien valide
        // Format: 8 chiffres commençant par 2, 3, 4, 5, 7, 8 ou 9
        return phone.matches("^[2-59][0-9]{7}$");
    }

    private String getPhoneNumberErrorMessage(String phone) {
        if (phone.length() != 8) {
            return "Le numéro de téléphone doit contenir exactement 8 chiffres";
        }
        if (!phone.matches("^[2-59][0-9]{7}$")) {
            return "Le numéro de téléphone doit commencer par 2, 3, 4, 5, 7, 8 ou 9";
        }
        return "Format de numéro de téléphone invalide";
    }

    private boolean isValidEmail(String email) {
        // Expression régulière pour valider le format d'email
        String emailRegex = "^[A-Za-z0-9+_.-]+@(.+)$";
        return email.matches(emailRegex);
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
    }
} 