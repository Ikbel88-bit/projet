package controllers;

import entities.Condidature;
import entities.Offre;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.Button;
import services.ServiceCondidature;
import javafx.stage.Stage;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import javax.swing.*;
import java.net.URL;

public class CondidatureCardController {
    @FXML
    private ImageView editImageView;
    @FXML
    private ImageView deleteImageView;

    @FXML
    private Label labelEntreprise;
    @FXML
    private Label labelTitre;
    @FXML
    private Label labelContrat;
    @FXML
    private Label labelLettre;
    @FXML
    private Label labelStatut;

    private Condidature condidature;
    private Offre offre;
    private CondidatureUserController parentController;

    public void setData(Condidature condidature, Offre offre, CondidatureUserController parent) {
        this.condidature = condidature;
        this.offre = offre;
        this.parentController = parent;
        if (offre != null) {
            labelEntreprise.setText(offre.getNomEntreprise());
            labelTitre.setText(offre.getTitreOffre());
            labelContrat.setText(offre.getTypeContrat());
        } else {
            labelEntreprise.setText("");
            labelTitre.setText("");
            labelContrat.setText("");
        }
        labelLettre.setText(condidature.getLettreDeMotivation());
        
        // Vérifier que le statut n'est pas null et l'afficher
        String statut = condidature.getStatut();
        if (statut != null && !statut.isEmpty()) {
            labelStatut.setText(statut);
            
            // Appliquer un style différent selon le statut
            if ("Acceptée".equals(statut)) {
                labelStatut.setStyle("-fx-text-fill: #4CAF50; -fx-font-weight: bold;");
            } else if ("Refusée".equals(statut)) {
                labelStatut.setStyle("-fx-text-fill: #F44336; -fx-font-weight: bold;");
            } else {
                // Pour "en attente" ou autres statuts
                labelStatut.setStyle("-fx-text-fill: #FF9800; -fx-font-weight: bold;");
            }
        } else {
            labelStatut.setText("en attente");
            labelStatut.setStyle("-fx-text-fill: #FF9800; -fx-font-weight: bold;");
        }
        
        // Afficher des informations de débogage
        System.out.println("Statut de la candidature #" + condidature.getIdCondidature() + ": " + statut);
    }

    @FXML
    private void modifierCard() {
        try {
            // Préparer la nouvelle vue
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/AfficherCondidature.fxml"));
            Parent root = loader.load();
            AfficherCondidatureController controller = loader.getController();
            
            // Configurer le contrôleur
            if (offre != null) controller.setOffre(offre);
            controller.lettreMotivationField.setText(condidature.getLettreDeMotivation());
            controller.urlCvField.setText(condidature.getUrlCv());
            controller.setCondidature(condidature);
            
            // Récupérer la scène actuelle
            Scene currentScene = editImageView.getScene();
            Stage currentStage = (Stage) currentScene.getWindow();
            
            // Configurer le contrôleur pour le retour
            controller.setPreviousScene(currentScene);
            controller.setPreviousRoot(currentScene.getRoot());
            controller.setParentController(parentController);
            
            // Ajouter une transition de fondu
            root.setOpacity(0);
            
            // Remplacer le contenu de la scène actuelle
            currentStage.setTitle("Modifier la candidature");
            currentScene.setRoot(root);
            
            // Animation de fondu à l'entrée
            javafx.animation.FadeTransition fadeIn = new javafx.animation.FadeTransition(javafx.util.Duration.millis(300), root);
            fadeIn.setFromValue(0);
            fadeIn.setToValue(1);
            fadeIn.play();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void supprimerCard() {
        try {
            ServiceCondidature service = new ServiceCondidature();
            service.supprimer(condidature);
            parentController.refresh();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    public void initialize() {
        try {
            // Charger les images depuis les ressources
            URL editImageUrl = getClass().getResource("/Image/edit.png");
            URL deleteImageUrl = getClass().getResource("/Image/delete.png");
            
            // Si les images sont trouvées dans les ressources, les utiliser
            if (editImageUrl != null) {
                editImageView.setImage(new Image(editImageUrl.toExternalForm()));
            } else {
                // Fallback vers le chemin local si l'image n'est pas trouvée dans les ressources
                try {
                    editImageView.setImage(new Image("file:/C:/bureau/xampp/htdocs/img/Capture d'écran 2025-02-11 194347.png"));
                } catch (Exception e) {
                    System.err.println("Impossible de charger l'image d'édition: " + e.getMessage());
                }
            }
            
            if (deleteImageUrl != null) {
                deleteImageView.setImage(new Image(deleteImageUrl.toExternalForm()));
            } else {
                System.err.println("Image de suppression non trouvée dans les ressources");
            }
            
        } catch (Exception e) {
            System.err.println("Erreur lors du chargement des images: " + e.getMessage());
            e.printStackTrace();
        }
    }

} 