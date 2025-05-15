package controllers;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.scene.layout.HBox;
import javafx.scene.text.Font;
import javafx.scene.paint.Color;
import javafx.scene.control.Alert.AlertType;
import javafx.stage.Stage;
import services.ServiceCondidature;
import services.ServiceUtilisateur;
import entities.Condidature;
import entities.Utilisateur;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

public class VoirCandidaturesController {
    @FXML
    private VBox candidaturesBox;
    @FXML
    private ScrollPane scrollPane;
    @FXML
    private ComboBox<String> filtreStatutComboBox;
    @FXML
    private Label compteurLabel;
    @FXML
    private Button btnStatistiques;

    private ServiceCondidature serviceCondidature;
    private ServiceUtilisateur serviceUtilisateur;
    private int idOffre;

    public void initialize() {
        serviceCondidature = new ServiceCondidature();
        serviceUtilisateur = new ServiceUtilisateur();
        
        // Initialiser le ComboBox de filtre
        if (filtreStatutComboBox != null) {
            filtreStatutComboBox.getItems().addAll("Tous", "en attente", "Acceptée", "Refusée");
            filtreStatutComboBox.setValue("Tous");
            filtreStatutComboBox.valueProperty().addListener((obs, oldVal, newVal) -> {
                if (newVal != null) {
                    chargerCandidatures();
                }
            });
        } else {
            System.err.println("ERREUR: filtreStatutComboBox est null dans initialize()");
        }
        
        // S'assurer que le bouton des statistiques est visible
        if (btnStatistiques != null) {
            btnStatistiques.setVisible(true);
        }
        
        // Initialiser le compteur
        if (compteurLabel != null) {
            compteurLabel.setText("Aucune candidature à afficher");
        }
    }

    public void setIdOffre(int idOffre) {
        this.idOffre = idOffre;
        System.out.println("ID Offre défini: " + idOffre);
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

    private void mettreAJourCompteur(List<Condidature> candidatures) {
        if (compteurLabel == null) return;
        
        int total = candidatures.size();
        int enAttente = 0;
        int acceptees = 0;
        int refusees = 0;
        
        for (Condidature c : candidatures) {
            switch (c.getStatut()) {
                case "en attente":
                    enAttente++;
                    break;
                case "Acceptée":
                    acceptees++;
                    break;
                case "Refusée":
                    refusees++;
                    break;
            }
        }
        
        String texteCompteur = String.format("Total: %d candidatures | En attente: %d | Acceptées: %d | Refusées: %d", 
                                             total, enAttente, acceptees, refusees);
        compteurLabel.setText(texteCompteur);
    }

    private void chargerCandidatures() {
        if (candidaturesBox == null) {
            System.err.println("ERREUR: candidaturesBox est null dans chargerCandidatures()");
            return;
        }
        
        candidaturesBox.getChildren().clear();
        try {
            List<Condidature> toutesLesCandidatures = serviceCondidature.recuperer();
            String filtreStatut = filtreStatutComboBox != null ? filtreStatutComboBox.getValue() : "Tous";
            
            // Filtrer les candidatures pour cette offre
            List<Condidature> candidaturesOffre = toutesLesCandidatures.stream()
                .filter(c -> c.getIdOffre() == idOffre)
                .collect(java.util.stream.Collectors.toList());
            
            // Mettre à jour le compteur avec toutes les candidatures de l'offre
            mettreAJourCompteur(candidaturesOffre);
            
            // Filtrer selon le statut sélectionné
            candidaturesOffre.stream()
                .filter(c -> filtreStatut.equals("Tous") || c.getStatut().equals(filtreStatut))
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

                            // Boutons de modification du statut et suppression
                            HBox buttonBox = new HBox(10);
                            Button accepterBtn = new Button("Accepter");
                            Button refuserBtn = new Button("Refuser");
                            Button supprimerBtn = new Button("Supprimer");
                            
                            accepterBtn.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white; -fx-background-radius: 5;");
                            refuserBtn.setStyle("-fx-background-color: #f44336; -fx-text-fill: white; -fx-background-radius: 5;");
                            supprimerBtn.setStyle("-fx-background-color: #FF9800; -fx-text-fill: white; -fx-background-radius: 5;");
                            
                            accepterBtn.setOnAction(e -> modifierStatut(c, "Acceptée"));
                            refuserBtn.setOnAction(e -> modifierStatut(c, "Refusée"));
                            supprimerBtn.setOnAction(e -> supprimerCandidature(c));
                            
                            buttonBox.getChildren().addAll(accepterBtn, refuserBtn, supprimerBtn);
                            
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

    private void supprimerCandidature(Condidature candidature) {
        Alert confirmAlert = new Alert(AlertType.CONFIRMATION);
        confirmAlert.setTitle("Confirmation de suppression");
        confirmAlert.setHeaderText("Supprimer la candidature");
        confirmAlert.setContentText("Êtes-vous sûr de vouloir supprimer cette candidature ?");
        
        confirmAlert.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                try {
                    serviceCondidature.supprimer(candidature);
                    chargerCandidatures(); // Recharger la liste
                    afficherMessage("Succès", "La candidature a été supprimée avec succès");
                } catch (SQLException e) {
                    afficherMessage(AlertType.ERROR, "Erreur", "Impossible de supprimer la candidature");
                    e.printStackTrace();
                }
            }
        });
    }

    @FXML
    private void resetFiltre() {
        filtreStatutComboBox.setValue("Tous");
    }

    @FXML
    private void ouvrirStatistiques() {
        try {
            // Vérifier que l'ID de l'offre est bien défini
            if (idOffre <= 0) {
                afficherMessage(AlertType.WARNING, "Attention", "Veuillez d'abord sélectionner une offre");
                return;
            }
            
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/StatistiquesRecrutement.fxml"));
            Parent root = loader.load();
            
            // Récupérer le contrôleur et lui passer l'ID de l'offre
            StatistiquesRecrutementController controller = loader.getController();
            controller.setIdOffre(idOffre);
            controller.chargerStatistiques();
            
            Stage stage = new Stage();
            stage.setTitle("Statistiques de recrutement");
            stage.setScene(new Scene(root));
            stage.show();
            
            System.out.println("Ouverture des statistiques pour l'offre ID: " + idOffre);
        } catch (IOException e) {
            e.printStackTrace();
            afficherMessage(AlertType.ERROR, "Erreur", "Impossible d'ouvrir les statistiques: " + e.getMessage());
        }
    }
}
