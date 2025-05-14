package controllers;

import javafx.fxml.FXML;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.VBox;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.text.Font;
import javafx.scene.paint.Color;
import javafx.scene.control.Button;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import services.ServiceCondidature;
import services.ServiceUtilisateur;
import entities.Condidature;
import entities.Utilisateur;
import java.sql.SQLException;
import java.util.List;

public class VoirCandidaturesController {
    @FXML
    private VBox candidaturesBox;
    @FXML
    private ScrollPane scrollPane;

    private ServiceCondidature serviceCondidature;
    private ServiceUtilisateur serviceUtilisateur;
    private int idOffre;

    public void initialize() {
        serviceCondidature = new ServiceCondidature();
        serviceUtilisateur = new ServiceUtilisateur();
    }

    public void setIdOffre(int idOffre) {
        this.idOffre = idOffre;
        chargerCandidatures();
    }

    private void modifierStatut(Condidature candidature, String nouveauStatut) {
        try {
            candidature.setStatut(nouveauStatut);
            serviceCondidature.modifier(candidature);
            chargerCandidatures(); // Recharger la liste
            afficherMessage("Succès", "Le statut a été modifié avec succès");
        } catch (SQLException e) {
            afficherMessage(AlertType.ERROR, "Erreur", "Impossible de modifier le statut");
            e.printStackTrace();
        }
    }

    private void afficherMessage(String titre, String message) {
        Alert alert = new Alert(AlertType.INFORMATION);
        alert.setTitle(titre);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void afficherMessage(AlertType type, String titre, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(titre);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void chargerCandidatures() {
        candidaturesBox.getChildren().clear();
        try {
            List<Condidature> candidatures = serviceCondidature.recuperer();
            candidatures.stream()
                .filter(c -> c.getIdOffre() == idOffre)
                .forEach(c -> {
                    try {
                        Utilisateur candidat = serviceUtilisateur.recupererParId(c.getIdUser());
                        if (candidat != null) {
                            VBox candidatureBox = new VBox(15);
                            candidatureBox.setStyle("-fx-background-color: white; -fx-padding: 20; -fx-background-radius: 10; -fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.1), 10, 0, 0, 10);");

                            // En-tête avec nom du candidat
                            Label nomLabel = new Label(candidat.getPrenom() + " " + candidat.getNom());
                            nomLabel.setFont(Font.font("Barlow Condensed ExtraBold Italic", 18));
                            nomLabel.setTextFill(Color.web("#2196F3"));

                            // Informations du candidat
                            VBox infoBox = new VBox(8);
                            Label emailLabel = new Label("Email : " + candidat.getEmail());
                            Label telLabel = new Label("Téléphone : " + candidat.getTelephone());
                            
                            Font infoFont = Font.font("Arial Narrow", 14);
                            emailLabel.setFont(infoFont);
                            telLabel.setFont(infoFont);
                            
                            emailLabel.setTextFill(Color.web("#666666"));
                            telLabel.setTextFill(Color.web("#666666"));

                            // Informations de la candidature
                            VBox candidatureInfoBox = new VBox(8);
                            Label statutLabel = new Label("Statut : " + c.getStatut());
                            Label cvLabel = new Label("CV : " + c.getUrlCv());
                            Label lettreLabel = new Label("Lettre de motivation : " + c.getLettreDeMotivation());
                            
                            statutLabel.setFont(infoFont);
                            cvLabel.setFont(infoFont);
                            lettreLabel.setFont(infoFont);
                            
                            statutLabel.setTextFill(Color.web("#4CAF50"));
                            cvLabel.setTextFill(Color.web("#666666"));
                            lettreLabel.setTextFill(Color.web("#666666"));

                            // Boutons de modification du statut
                            HBox buttonBox = new HBox(10);
                            Button accepterBtn = new Button("Accepter");
                            Button refuserBtn = new Button("Refuser");
                            
                            accepterBtn.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white; -fx-background-radius: 5;");
                            refuserBtn.setStyle("-fx-background-color: #f44336; -fx-text-fill: white; -fx-background-radius: 5;");
                            
                            accepterBtn.setOnAction(e -> modifierStatut(c, "Acceptée"));
                            refuserBtn.setOnAction(e -> modifierStatut(c, "Refusée"));
                            
                            buttonBox.getChildren().addAll(accepterBtn, refuserBtn);
                            
                            candidatureInfoBox.getChildren().addAll(statutLabel, cvLabel, lettreLabel, buttonBox);
                            candidatureBox.getChildren().addAll(nomLabel, infoBox, candidatureInfoBox);
                            
                            candidaturesBox.getChildren().add(candidatureBox);
                        }
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
                });
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
