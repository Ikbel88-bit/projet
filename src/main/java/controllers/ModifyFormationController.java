package Controllers;

import entities.Formation;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;
import services.ServiceFormation;

import java.sql.SQLException;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;

public class ModifyFormationController {

    @FXML private TextField titreField;
    @FXML private Label titreError;
    @FXML private TextArea descriptionField;
    @FXML private Label descriptionError;
    @FXML private DatePicker dateDebutPicker;
    @FXML private Label dateDebutError;
    @FXML private DatePicker dateFinPicker;
    @FXML private Label dateFinError;
    @FXML private TextField formateurField;
    @FXML private Label formateurError;
    @FXML private TextField capaciteField;
    @FXML private Label capaciteError;

    private ServiceFormation serviceFormation = new ServiceFormation();
    private Formation formationToModify;

    public void setFormation(Formation formation) {
        this.formationToModify = formation;

        // Fill form with existing data
        titreField.setText(formation.getTitre());
        descriptionField.setText(formation.getDescription());
        dateDebutPicker.setValue(formation.getDate_debut() != null
                ? formation.getDate_debut().toInstant().atZone(ZoneId.systemDefault()).toLocalDate()
                : null);
        dateFinPicker.setValue(formation.getDate_fin() != null
                ? formation.getDate_fin().toInstant().atZone(ZoneId.systemDefault()).toLocalDate()
                : null);
        formateurField.setText(formation.getFormateur());
        capaciteField.setText(String.valueOf(formation.getCapacite()));
    }

    @FXML
    public void handleModifyFormation() throws SQLException {
        // Reset error messages
        resetErrorStyles();

        boolean hasError = false;

        // Validate fields
        String titre = titreField.getText();
        if (titre == null || titre.trim().isEmpty()) {
            showFieldError(titreField, titreError, "Le titre est obligatoire");
            hasError = true;
        }

        String description = descriptionField.getText();
        if (description == null || description.trim().isEmpty()) {
            showFieldError(descriptionField, descriptionError, "La description est obligatoire");
            hasError = true;
        }

        LocalDate dateDebut = dateDebutPicker.getValue();
        if (dateDebut == null) {
            showFieldError(dateDebutPicker, dateDebutError, "La date de début est obligatoire");
            hasError = true;
        }

        LocalDate dateFin = dateFinPicker.getValue();
        if (dateFin == null) {
            showFieldError(dateFinPicker, dateFinError, "La date de fin est obligatoire");
            hasError = true;
        } else if (dateDebut != null && dateFin.isBefore(dateDebut)) {
            showFieldError(dateFinPicker, dateFinError, "La date de fin doit être après la date de début");
            hasError = true;
        }

        String formateur = formateurField.getText();
        if (formateur == null || formateur.trim().isEmpty()) {
            showFieldError(formateurField, formateurError, "Le formateur est obligatoire");
            hasError = true;
        }

        int capacite = 0;
        try {
            capacite = Integer.parseInt(capaciteField.getText());
            if (capacite <= 0) {
                showFieldError(capaciteField, capaciteError, "La capacité doit être supérieure à 0");
                hasError = true;
            }
        } catch (NumberFormatException e) {
            showFieldError(capaciteField, capaciteError, "Veuillez entrer un nombre valide");
            hasError = true;
        }

        if (hasError) {
            return;
        }

        // Update formation
        formationToModify.setTitre(titre.trim());
        formationToModify.setDescription(description.trim());
        formationToModify.setDate_debut(Date.from(dateDebut.atStartOfDay(ZoneId.systemDefault()).toInstant()));
        formationToModify.setDate_fin(Date.from(dateFin.atStartOfDay(ZoneId.systemDefault()).toInstant()));
        formationToModify.setFormateur(formateur.trim());
        formationToModify.setCapacite(capacite);

        serviceFormation.modifier(formationToModify);

        ((Stage) titreField.getScene().getWindow()).close();
    }

    @FXML
    public void handleCancel() {
        ((Stage) titreField.getScene().getWindow()).close();
    }

    private void resetErrorStyles() {
        titreField.setStyle(null);
        titreError.setVisible(false);
        titreError.setManaged(false);

        descriptionField.setStyle(null);
        descriptionError.setVisible(false);
        descriptionError.setManaged(false);

        dateDebutPicker.setStyle(null);
        dateDebutError.setVisible(false);
        dateDebutError.setManaged(false);

        dateFinPicker.setStyle(null);
        dateFinError.setVisible(false);
        dateFinError.setManaged(false);

        formateurField.setStyle(null);
        formateurError.setVisible(false);
        formateurError.setManaged(false);

        capaciteField.setStyle(null);
        capaciteError.setVisible(false);
        capaciteError.setManaged(false);
    }

    private void showFieldError(Control field, Label errorLabel, String message) {
        field.setStyle("-fx-border-color: #dc3545;");
        errorLabel.setText(message);
        errorLabel.setVisible(true);
        errorLabel.setManaged(true);
    }
}