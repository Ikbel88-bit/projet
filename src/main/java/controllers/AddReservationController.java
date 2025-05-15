package controllers;

import entities.Reservation;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;
import services.ServiceReservation;

import java.sql.SQLException;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;

public class AddReservationController {

    @FXML private TextField nomParticipantField;
    @FXML private Label nomParticipantError;
    @FXML private TextField emailParticipantField;
    @FXML private Label emailParticipantError;
    @FXML private DatePicker dateReservationPicker;
    @FXML private Label dateReservationError;
    @FXML private TextField formationIdField;
    @FXML private Label formationIdError;
    @FXML private Button saveButton;

    private ServiceReservation serviceReservation = new ServiceReservation();
    private int formationId;

    @FXML
    private void handleSaveReservation() {
        resetErrorStyles();

        boolean hasError = false;

        String nomParticipant = nomParticipantField.getText();
        if (nomParticipant == null || nomParticipant.trim().isEmpty()) {
            showFieldError(nomParticipantField, nomParticipantError, "Le nom du participant est obligatoire");
            hasError = true;
        }

        String emailParticipant = emailParticipantField.getText();
        if (emailParticipant == null || emailParticipant.trim().isEmpty()) {
            showFieldError(emailParticipantField, emailParticipantError, "L'email du participant est obligatoire");
            hasError = true;
        }

        LocalDate dateReservation = dateReservationPicker.getValue();
        if (dateReservation == null) {
            showFieldError(dateReservationPicker, dateReservationError, "La date de réservation est obligatoire");
            hasError = true;
        }

        if (hasError) {
            return;
        }

        Date dateReservationDate = Date.from(dateReservation.atStartOfDay(ZoneId.systemDefault()).toInstant());
        Reservation reservation = new Reservation(formationId, nomParticipant.trim(), emailParticipant.trim(), dateReservationDate);

        try {
            serviceReservation.ajouter(reservation);
            Stage stage = (Stage) saveButton.getScene().getWindow();
            stage.close();
        } catch (SQLException e) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Erreur");
            alert.setContentText("Erreur lors de la réservation: " + e.getMessage());
            alert.showAndWait();
        }
    }

    @FXML
    private void handleCancel() {
        Stage stage = (Stage) nomParticipantField.getScene().getWindow();
        stage.close();
    }

    public void setFormationId(int formationId) {
        this.formationId = formationId;
        formationIdField.setText(String.valueOf(formationId));
    }

    private void resetErrorStyles() {
        nomParticipantField.setStyle(null);
        nomParticipantError.setVisible(false);
        nomParticipantError.setManaged(false);

        emailParticipantField.setStyle(null);
        emailParticipantError.setVisible(false);
        emailParticipantError.setManaged(false);

        dateReservationPicker.setStyle(null);
        dateReservationError.setVisible(false);
        dateReservationError.setManaged(false);
    }

    private void showFieldError(Control field, Label errorLabel, String message) {
        field.setStyle("-fx-border-color: #dc3545;");
        errorLabel.setText(message);
        errorLabel.setVisible(true);
        errorLabel.setManaged(true);
    }
}