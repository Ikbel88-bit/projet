package controllers;

import entities.Reclamation;
import entities.User;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;
import services.ServiceReclamation;
import services.ServiceUser;

import java.io.IOException;
import java.sql.SQLException;

public class CandidatController {

    @FXML
    private TextField mdpmodif;
    @FXML
    private Button modifier;
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
    @FXML
    private TextArea reclamationTextArea;
    @FXML
    private Label confirmationLabel;
    @FXML
    private Button btnVoirMesReclamations;
    @FXML
    private Button btnRetourPrecedent;

    private User currentUser;

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

    @FXML
    void deposerReclamation(ActionEvent event) {
        String description = reclamationTextArea.getText().trim();
        if (!description.isEmpty() && currentUser != null) {
            try {
                System.out.println("ID utilisateur courant (doit être celui du candidat) : " + currentUser.getId());
                System.out.println("Description : " + description);
                Reclamation reclamation = new Reclamation(currentUser.getId(), description);
                ServiceReclamation service = new ServiceReclamation();
                service.ajouter(reclamation);
                
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Succès");
                alert.setHeaderText(null);
                alert.setContentText("Votre réclamation a été envoyée avec succès !");
                alert.showAndWait();
                
                reclamationTextArea.clear();
            } catch (SQLException e) {
                e.printStackTrace();
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Erreur");
                alert.setHeaderText(null);
                alert.setContentText("Une erreur est survenue lors de l'envoi de la réclamation.\n" + e.getMessage());
                alert.showAndWait();
            }
        } else {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Attention");
            alert.setHeaderText(null);
            alert.setContentText("Veuillez écrire une réclamation.");
            alert.showAndWait();
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

    @FXML
    void voirMesReclamations(ActionEvent event) {
        if (currentUser == null) return;
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/MesReclamations.fxml"));
            Parent root = loader.load();
            MesReclamationsController ctrl = loader.getController();
            ctrl.setUserId(currentUser.getId());
            Stage stage = (Stage) btnVoirMesReclamations.getScene().getWindow();
            stage.setTitle("Mes réclamations");
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    public void retourPrecedent() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/Login.fxml"));
            Parent root = loader.load();
            Scene scene = new Scene(root);
            Stage stage = (Stage) btnRetourPrecedent.getScene().getWindow();
            stage.setScene(scene);
            stage.setTitle("Login");
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
