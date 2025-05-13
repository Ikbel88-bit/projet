package controllers;

import entities.Reservation;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import services.ServiceReservation;

public class AjouterReservationController {

    @FXML
    private TextField txtResource, txtDateDebut, txtDateFin;

    @FXML
    private ChoiceBox<String> choiceStatus;

    @FXML
    private Button btnAjouter;

    private final ServiceReservation service = new ServiceReservation();

    @FXML
    public void initialize() {
        choiceStatus.getItems().addAll("Confirmée", "En attente", "Annulée");
        choiceStatus.setValue("En attente");

        btnAjouter.setOnAction(event -> ajouterReservation());
    }

    private void ajouterReservation() {
        String resource = txtResource.getText();
        String dateDebut = txtDateDebut.getText();
        String dateFin = txtDateFin.getText();
        String status = choiceStatus.getValue();

        if (resource.isEmpty() || dateDebut.isEmpty() || dateFin.isEmpty()) {
            showAlert("Veuillez remplir tous les champs.");
            return;
        }

        Reservation r = new Reservation(0, resource, dateDebut, dateFin, status);
        service.ajouter(r); // ✅ appel correct de la méthode "ajouter"
        showAlert("Réservation ajoutée avec succès.");
        clearForm();
    }

    private void clearForm() {
        txtResource.clear();
        txtDateDebut.clear();
        txtDateFin.clear();
        choiceStatus.setValue("En attente");
    }

    private void showAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    @FXML
    public void handleRetour(ActionEvent actionEvent) {
        try {
            javafx.fxml.FXMLLoader loader = new javafx.fxml.FXMLLoader(getClass().getResource("/path/to/afficherReservations.fxml"));
            javafx.scene.Parent root = loader.load();
            javafx.scene.Scene scene = new javafx.scene.Scene(root);
            javafx.stage.Stage stage = (javafx.stage.Stage) ((javafx.scene.Node) actionEvent.getSource()).getScene().getWindow();
            stage.setScene(scene);
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
