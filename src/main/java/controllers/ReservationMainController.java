package controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class ReservationMainController {

    private void loadScene(String fxmlPath, ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
            Parent root = loader.load();
            Stage stage = (Stage) ((javafx.scene.Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("SmartRH");
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    public void handleAjouter(ActionEvent actionEvent) {
        loadScene("/views/AjouterReservation.fxml", actionEvent);
    }

    @FXML
    public void handleAfficher(ActionEvent actionEvent) {
        loadScene("/views/AfficherReservation.fxml", actionEvent);
    }

    @FXML
    public void handleSupprimer(ActionEvent actionEvent) {
        loadScene("/views/SupprimerReservation.fxml", actionEvent);
    }
}
