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
import java.sql.SQLException;
import java.util.List;

public class CondidatureUserController {
    @FXML
    private VBox cardsContainer;
    private int idUser = 1; // À remplacer par l'utilisateur connecté

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

    // Méthodes appelées par les cards pour rafraîchir après modif/suppression
    public void refresh() {
        chargerCandidatures();
    }
}
