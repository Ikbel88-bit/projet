/*package controllers;

import entities.Offre;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.Alert;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import services.ServiceOffre;

import java.io.IOException;
import java.sql.SQLException;

public class SupprimerOffreController {

    @javafx.fxml.FXML
    private TextField fxSupprimer;

    @javafx.fxml.FXML
    public void supprimer(ActionEvent actionEvent) {
        ServiceOffre serviceOffre = new ServiceOffre();
        try{
            Offre offre =new Offre(Integer.parseInt(fxSupprimer.getText()));
            serviceOffre.supprimer(offre);
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Supprimer Offre");
            alert.setContentText("Offre supprimee");
            alert.showAndWait();
            Stage oldStage = (Stage) ((Node) actionEvent.getSource()).getScene().getWindow();
            oldStage.close();

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @javafx.fxml.FXML
    public void afficher(ActionEvent actionEvent) {
        Stage oldStage = (Stage) ((Node) actionEvent.getSource()).getScene().getWindow();
        oldStage.close();
    }

}
*/
