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
        labelStatut.setText(condidature.getStatut());
    }

    @FXML
    private void modifierCard() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/AfficherCondidature.fxml"));
            Parent root = loader.load();
            AfficherCondidatureController controller = loader.getController();
            if (offre != null) controller.setOffre(offre);
            controller.lettreMotivationField.setText(condidature.getLettreDeMotivation());
            controller.urlCvField.setText(condidature.getUrlCv());
            Stage stage = new Stage();
            stage.setTitle("Modifier la candidature");
            stage.setScene(new Scene(root));
            stage.showAndWait();
            // TODO: Ajouter la logique de mise à jour en base si besoin
            parentController.refresh();
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

    public void initialize() {
        try {
            Image editImage = new Image("file:/C:/xampp/htdocs/img/Capture_d_ecran_2025-02-11_194347.png");
            Image deleteImage = new Image(getClass().getResource("/Image/delete.png").toExternalForm());

            editImageView.setImage(editImage);
            deleteImageView.setImage(deleteImage);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

} 