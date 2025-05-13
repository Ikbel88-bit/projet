package controllers;

import entities.Reclamation;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.stage.Stage;
import services.ServiceReclamation;

import java.io.IOException;

public class RepondreReclamationController {
    @FXML
    private TextArea reclamationText;
    @FXML
    private TextArea reponseText;
    @FXML
    private Button btnEnvoyer;

    private Reclamation reclamation;

    public void setReclamation(Reclamation reclamation) {
        this.reclamation = reclamation;
        reclamationText.setText(reclamation.getDescription());
        reponseText.setText(reclamation.getReponse() != null ? reclamation.getReponse() : "");
    }

    @FXML
    void envoyerReponse() {
        String reponse = reponseText.getText().trim();
        if (reponse.isEmpty()) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Attention");
            alert.setHeaderText(null);
            alert.setContentText("Veuillez saisir une réponse.");
            alert.showAndWait();
            return;
        }
        try {
            ServiceReclamation service = new ServiceReclamation();
            service.repondreAReclamation(reclamation.getReclamationId(), reponse);
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Succès");
            alert.setHeaderText(null);
            alert.setContentText("Réponse envoyée avec succès !");
            alert.showAndWait();
            // Retourner à la liste des réclamations
            retourPrecedent();
        } catch (Exception e) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Erreur");
            alert.setHeaderText(null);
            alert.setContentText("Erreur lors de l'envoi de la réponse.");
            alert.showAndWait();
        }
    }

    @FXML
    void retourPrecedent() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/AfficherReclamation.fxml"));
            Parent root = loader.load();
            Scene scene = new Scene(root);
            Stage stage = (Stage) btnEnvoyer.getScene().getWindow();
            stage.setScene(scene);
            stage.setTitle("Gestion des Réclamations");
            stage.show();
        } catch (IOException e) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Erreur");
            alert.setHeaderText(null);
            alert.setContentText("Erreur lors du retour à la liste des réclamations.");
            alert.showAndWait();
            e.printStackTrace();
        }
    }
} 