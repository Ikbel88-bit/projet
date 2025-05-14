package controllers;

import entities.Offre;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.control.Label;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class OffreItemController implements Initializable {

    @FXML
    private Label ItemEntreprise;

    private Offre offre; // l'offre courante
    private AfficherOffreController parentController;

    public void setParentController(AfficherOffreController controller) {
        this.parentController = controller;
    }

    public void setData(Offre offre) {
        this.offre = offre;
        ItemEntreprise.setText(offre.getNomEntreprise());
    }

    @FXML
    private void VoirPlus(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/OffreDetail.fxml"));
            Parent detailPane = loader.load();

            OffreDetailController detailController = loader.getController();
            detailController.setData(offre);

            if (parentController != null) {
                parentController.afficherDetail(detailPane);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void initialize(URL url, ResourceBundle rb) {}
}
