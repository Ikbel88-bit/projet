package controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.Label;
import javafx.scene.control.Alert;
import entities.User;
import entities.Reclamation;
import services.ServiceUser;
import services.ServiceReclamation;
import java.sql.SQLException;
import java.io.IOException;
import javafx.stage.Stage;

public class UserController {

    @FXML
    private TextField mdpmodif;

    @FXML
    private Button modifier;

    @FXML
    private TextArea reclamationuser;

    @FXML
    private Label nomLabel;
    @FXML
    private Label prenomLabel;
    @FXML
    private Label emailLabel;
    @FXML
    private Label telephoneLabel;
    @FXML
    private Label roleLabel;
    @FXML
    private Button btnRetour;

    private User currentUser;
    private ServiceReclamation serviceReclamation;

    @FXML
    public void initialize() {
        serviceReclamation = new ServiceReclamation();
    }

    // Action pour déposer une réclamation
    @FXML
    void deposer(ActionEvent event) {
        String reclamationText = reclamationuser.getText();
        if (!reclamationText.isEmpty() && currentUser != null) {
            try {
                Reclamation reclamation = new Reclamation(currentUser.getId(), reclamationText);
                serviceReclamation.ajouter(reclamation);
                
                // Afficher un message de succès
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Succès");
                alert.setHeaderText(null);
                alert.setContentText("Votre réclamation a été envoyée avec succès !");
                alert.showAndWait();
                
                // Réinitialiser le champ de réclamation
                reclamationuser.clear();
            } catch (SQLException e) {
                e.printStackTrace();
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Erreur");
                alert.setHeaderText(null);
                alert.setContentText("Une erreur est survenue lors de l'envoi de la réclamation.");
                alert.showAndWait();
            }
        } else {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Attention");
            alert.setHeaderText(null);
            alert.setContentText("Veuillez entrer une réclamation.");
            alert.showAndWait();
        }
    }

    // Action pour modifier le mot de passe
    @FXML
    void modifierMdp(ActionEvent event) {
        String newPassword = mdpmodif.getText();
        if (!newPassword.isEmpty() && currentUser != null) {
            try {
                currentUser.setPassword(newPassword);
                ServiceUser serviceUser = new ServiceUser();
                serviceUser.modifier(currentUser);
                
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Succès");
                alert.setHeaderText(null);
                alert.setContentText("Mot de passe modifié avec succès !");
                alert.showAndWait();
                
                mdpmodif.clear();
            } catch (SQLException e) {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Erreur");
                alert.setHeaderText(null);
                alert.setContentText("Une erreur est survenue lors de la modification du mot de passe.");
                alert.showAndWait();
                e.printStackTrace();
            }
        } else {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Attention");
            alert.setHeaderText(null);
            alert.setContentText("Veuillez entrer un nouveau mot de passe.");
            alert.showAndWait();
        }
    }

    public void setUser(User user) {
        this.currentUser = user;
        afficherInfosUtilisateur();
    }

    private void afficherInfosUtilisateur() {
        if (currentUser != null) {
            nomLabel.setText("Nom: " + currentUser.getNom());
            prenomLabel.setText("Prénom: " + currentUser.getPrenom());
            emailLabel.setText("Email: " + currentUser.getEmail());
            telephoneLabel.setText("Téléphone: " + currentUser.getTelephone());
            roleLabel.setText("Rôle: " + currentUser.getRole());
        }
    }

    @FXML
    public void retourLogin() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/Login.fxml"));
            Parent root = loader.load();
            Scene scene = new Scene(root);
            Stage stage = (Stage) btnRetour.getScene().getWindow();
            stage.setScene(scene);
            stage.setTitle("Login");
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}