package controllers;

import entities.Condidature;
import entities.Offre;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.VBox;
import javafx.scene.control.Button;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import services.ServiceCondidature;
import services.ServiceOffre;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

public class CondidatureUserController {
    @FXML
    private VBox cardsContainer;
    private int idUser = 1; // À remplacer par l'utilisateur connecté

    @FXML
    private Button btnRetour;

    @FXML
    public void initialize() {
        chargerCandidatures();
    }

    public void chargerCandidatures() {
        cardsContainer.getChildren().clear();
        try {
            ServiceCondidature service = new ServiceCondidature();
            List<Condidature> all = service.recuperer();
            ServiceOffre so = new ServiceOffre();
            List<Offre> offres = so.recuperer();
            for (Condidature c : all) {
                if (c.getIdUser() == idUser) {
                    FXMLLoader loader = new FXMLLoader(getClass().getResource("/CondidatureCard.fxml"));
                    Parent card = loader.load();
                    controllers.CondidatureCardController cardController = loader.getController();
                    Offre offre = offres.stream().filter(o -> o.getIdOffre() == c.getIdOffre()).findFirst().orElse(null);
                    cardController.setData(c, offre, this);
                    cardsContainer.getChildren().add(card);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void ajouterCondidature() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/AfficherCondidature.fxml"));
            Parent root = loader.load();
            Stage stage = new Stage();
            stage.setTitle("Ajouter une candidature");
            stage.setScene(new Scene(root));
            stage.showAndWait();
            chargerCandidatures();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void reloadCandidatures() {
        chargerCandidatures();
    }

    // Méthode appelée par les cards pour rafraîchir après modif/suppression
    public void refresh() {
        // Utiliser Platform.runLater pour s'assurer que la mise à jour se fait sur le thread JavaFX
        javafx.application.Platform.runLater(() -> {
            try {
                System.out.println("Rafraîchissement des candidatures...");
                chargerCandidatures();
            } catch (Exception e) {
                System.err.println("Erreur lors du rafraîchissement des candidatures: " + e.getMessage());
                e.printStackTrace();
            }
        });
    }

    /**
     * Retourne à l'écran des offres
     */
    @FXML
    private void retourVersOffres() {
        try {
            // Fermer la fenêtre actuelle
            Stage stage = (Stage) btnRetour.getScene().getWindow();
            stage.close();
            
            // Optionnel : ouvrir l'écran des offres si nécessaire
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/AfficherOffre.fxml"));
            Parent root = loader.load();
            Stage offreStage = new Stage();
            offreStage.setTitle("Offres d'emploi");
            offreStage.setScene(new Scene(root));
            offreStage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
