package controllers;

import entities.Reservation;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.TextField;
import services.ServiceReservation;

public class SupprimerReservationController {

    @FXML
    private TextField txtId;

    private final ServiceReservation service = new ServiceReservation();

    @FXML
    public void supprimerReservation() {
        try {
            int id = Integer.parseInt(txtId.getText());
            Reservation reservation = service.recupererParId(id);

            if (reservation != null) {
                service.supprimer(reservation);
                showAlert("Réservation supprimée avec succès.");
            } else {
                showAlert("Aucune réservation trouvée avec cet ID.");
            }
        } catch (NumberFormatException e) {
            showAlert("Veuillez entrer un ID numérique valide.");
        } catch (Exception e) {
            showAlert("Erreur lors de la suppression : " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void showAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Information");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
