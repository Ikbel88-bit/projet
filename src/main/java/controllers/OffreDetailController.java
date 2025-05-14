package controllers;

import entities.Offre;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;

import javafx.event.ActionEvent;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.stage.Stage;
import javafx.scene.Scene;
import java.io.IOException;

public class OffreDetailController implements Initializable {

    @FXML
    private Label CONTRAT;

    @FXML
    private Label DECRIPTION;

    @FXML
    private Label TITTRE;

    @FXML
    private Label TYPE;

    private Offre offre;

    public void setData(Offre offre) {
        this.offre = offre;
        TITTRE.setText(offre.getTitreOffre());
        DECRIPTION.setText(offre.getDescriptionOffre());
        CONTRAT.setText(offre.getTypeContrat());
        TYPE.setText(offre.getNomEntreprise());
    }

    @FXML
    private void consulter(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/AfficherCondidature.fxml"));
            Parent root = loader.load();
            controllers.AfficherCondidatureController controller = loader.getController();
            controller.setOffre(offre);
            Stage stage = new Stage();
            stage.setTitle("Candidater à l'offre");
            stage.setScene(new Scene(root));
            stage.show();
            Stage oldStage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            oldStage.close();
        } catch (IOException e) {
            e.printStackTrace();

        }
    }
        @FXML
        public void initialize (URL url, ResourceBundle resourceBundle){
        }
    }
