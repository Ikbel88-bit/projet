/*package controllers;

import entities.Condidature;
import entities.Offre;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.Alert;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import services.ServiceCondidature;
import services.ServiceOffre;

import java.awt.event.ActionEvent;
import java.io.IOException;
import java.sql.SQLException;

public class SupprimerCondidatureController {
    @javafx.fxml.FXML
    private TextField fxSupprimerId;
    @javafx.fxml.FXML
    public void supprimer(javafx.event.ActionEvent actionEvent) {
        ServiceCondidature servicecondidature = new ServiceCondidature();
        try{
            Condidature offre =new Condidature(Integer.parseInt(fxSupprimerId.getText()));
            servicecondidature.supprimer(offre);
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Supprimer Condidature");
            alert.setContentText("Condidature supprimee");
            alert.showAndWait();
            Stage oldStage = (Stage) ((Node) actionEvent.getSource()).getScene().getWindow();
            oldStage.close();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
    @javafx.fxml.FXML

    public void afficher(javafx.event.ActionEvent actionEvent) {
            Stage oldStage = (Stage) ((Node) actionEvent.getSource()).getScene().getWindow();
            oldStage.close();
    }
}
*/
