/*package controllers;

import entities.Offre;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.Alert;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import services.ServiceOffre;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ModifierOffreController {
    @FXML private TextField titreField;
    @FXML private TextField entrepriseField;
    @FXML private ComboBox<String> contratCombo;
    @FXML private TextArea descriptionArea;
    @FXML private Button btnEnregistrer;
    @FXML private Button btnAnnuler;

    private ServiceOffre serviceOffre;
    private Offre offre;

    public void initialize() {
        serviceOffre = new ServiceOffre();
        setupContratCombo();
    }

    public void setOffre(Offre offre) {
        this.offre = offre;
        titreField.setText(offre.getTitre());
        entrepriseField.setText(offre.getEntreprise());
        contratCombo.setValue(offre.getContrat());
        descriptionArea.setText(offre.getDescription());
    }

    private void setupContratCombo() {
        contratCombo.getItems().addAll(
            "CDI",
            "CDD",
            "Stage",
            "Alternance",
            "Freelance"
        );
    }

    @FXML
    private void handleEnregistrer() {
        String titre = titreField.getText();
        String entreprise = entrepriseField.getText();
        String contrat = contratCombo.getValue();
        String description = descriptionArea.getText();

        if (validateInput(titre, entreprise, contrat, description)) {
            offre.setTitre(titre);
            offre.setEntreprise(entreprise);
            offre.setContrat(contrat);
            offre.setDescription(description);

            try {
                serviceOffre.modifier(offre);
                Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                alert.setTitle("Confirmation");
                alert.setContentText("l'offre est modifiee avec succes");
                alert.showAndWait();
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }
    }

    @FXML
    private void handleAnnuler() {
        closeWindow();
    }

    private boolean validateInput(String titre, String entreprise, String contrat, String description) {
        if (titre == null || titre.trim().isEmpty()) {
            showError("Le titre est requis");
            return false;
        }
        if (entreprise == null || entreprise.trim().isEmpty()) {
            showError("L'entreprise est requise");
            return false;
        }
        if (contrat == null || contrat.trim().isEmpty()) {
            showError("Le type de contrat est requis");
            return false;
        }
        if (description == null || description.trim().isEmpty()) {
            showError("La description est requise");
            return false;
        }
        return true;
    }

    private void showError(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Erreur de validation");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void closeWindow() {
        Stage stage = (Stage) titreField.getScene().getWindow();
        stage.close();
    }
}


 */
