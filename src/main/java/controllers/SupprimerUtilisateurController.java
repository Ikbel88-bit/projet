package controllers;

import entities.User;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import services.ServiceUser;

import java.io.IOException;
import java.sql.SQLException;

public class SupprimerUtilisateurController {

    @FXML
    private TextField emailField;

    @FXML
    private TextField telephoneField;

    @FXML
    private Label errorLabel;

    @FXML
    private Button btnRetour;

    private User selectedUser;

    public void setSelectedUser(User user) {
        this.selectedUser = user;
    }

    @FXML
    void confirmerSuppression(ActionEvent event) {
        String email = emailField.getText();
        String telephone = telephoneField.getText();
        
        if (email.isEmpty() || telephone.isEmpty()) {
            errorLabel.setText("Veuillez remplir tous les champs");
            return;
        }
        
        if (selectedUser == null) {
            errorLabel.setText("Aucun utilisateur sélectionné");
            return;
        }
        
        // Vérifier si les informations correspondent
        if (!selectedUser.getEmail().equals(email) || !selectedUser.getTelephone().equals(telephone)) {
            errorLabel.setText("Les informations ne correspondent pas à l'utilisateur sélectionné");
            return;
        }
        
        try {
            ServiceUser serviceUser = new ServiceUser();
            serviceUser.supprimer(selectedUser.getId());
            
            showAlert("Succès", "L'utilisateur a été supprimé avec succès");
            
            // Retourner à la liste des utilisateurs
            retourPrecedent();
        } catch (SQLException e) {
            errorLabel.setText("Erreur lors de la suppression: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML
    void retourPrecedent() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/AfficherUser.fxml"));
            Parent root = loader.load();
            Scene scene = new Scene(root);
            Stage stage = (Stage) btnRetour.getScene().getWindow();
            stage.setScene(scene);
            stage.setTitle("Gestion des Utilisateurs");
            stage.show();
        } catch (IOException e) {
            errorLabel.setText("Erreur lors du retour à la page précédente: " + e.getMessage());
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
}