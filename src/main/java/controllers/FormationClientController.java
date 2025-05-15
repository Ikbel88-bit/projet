package controllers;

import entities.Formation;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Alert;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.VBox;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
import services.ServiceFormation;

import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.ResourceBundle;

public class FormationClientController implements Initializable {

    @FXML
    private FlowPane formationContainer;

    @FXML
    private VBox emptyState;

    private ServiceFormation serviceFormation;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        serviceFormation = new ServiceFormation();
        loadFormations();
    }

    @FXML
    public void openReservationList() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/ReservationList.fxml"));
            Parent root = loader.load();

            Scene scene = new Scene(root);
            scene.getStylesheets().add(getClass().getResource("/ReservationList.css").toExternalForm());

            Stage stage = new Stage();
            stage.setTitle("Liste des Réservations");
            stage.setScene(scene);
            stage.showAndWait();
        } catch (IOException e) {
            showAlert(Alert.AlertType.ERROR, "Erreur", "Erreur lors de l'ouverture de la liste des réservations: " + e.getMessage());
        }
    }

    public void loadFormations() {
        formationContainer.getChildren().clear();
        List<Formation> formations = getFormations();

        if (formations != null) {
            if (formations.isEmpty()) {
                emptyState.setVisible(true);
                emptyState.setManaged(true);
                formationContainer.setVisible(false);
                formationContainer.setManaged(false);
            } else {
                emptyState.setVisible(false);
                emptyState.setManaged(false);
                formationContainer.setVisible(true);
                formationContainer.setManaged(true);

                for (Formation formation : formations) {
                    VBox card = createFormationCard(formation);
                    formationContainer.getChildren().add(card);
                }
            }
        }
    }

    private VBox createFormationCard(Formation formation) {
        VBox card = new VBox(10);
        card.getStyleClass().add("formation-item");

        // En-tête de la carte
        HBox header = new HBox(10);
        header.getStyleClass().add("card-header");

        // Titre de la formation
        Label titleLabel = new Label(formation.getTitre());
        titleLabel.getStyleClass().add("formation-title");

        // Badge de statut
        String status = formation.getDate_fin().before(new java.util.Date()) ? "Terminé" : "En cours";
        Label statusBadge = new Label(status);
        statusBadge.getStyleClass().addAll("status-badge", "status-" + (status.equals("En cours") ? "en-attente" : "confirme"));

        header.getChildren().addAll(titleLabel, statusBadge);

        // Container des informations
        VBox infoContainer = new VBox(8);
        infoContainer.getStyleClass().add("info-container");

        // Formateur
        HBox formateurBox = new HBox(5);
        Label formateurLabel = new Label("Formateur:");
        formateurLabel.getStyleClass().add("info-label");
        Label formateurValue = new Label(formation.getFormateur());
        formateurValue.getStyleClass().add("info-value");
        formateurBox.getChildren().addAll(formateurLabel, formateurValue);

        // Capacité
        HBox capaciteBox = new HBox(5);
        Label capaciteLabel = new Label("Capacité:");
        capaciteLabel.getStyleClass().add("info-label");
        Label capaciteValue = new Label(String.valueOf(formation.getCapacite()));
        capaciteValue.getStyleClass().add("info-value");
        capaciteBox.getChildren().addAll(capaciteLabel, capaciteValue);

        // Date de début
        HBox dateDebutBox = new HBox(5);
        Label dateDebutLabel = new Label("Date de début:");
        dateDebutLabel.getStyleClass().add("info-label");
        Label dateDebutValue = new Label(new SimpleDateFormat("dd/MM/yyyy").format(formation.getDate_debut()));
        dateDebutValue.getStyleClass().add("info-value");
        dateDebutBox.getChildren().addAll(dateDebutLabel, dateDebutValue);

        // Date de fin
        HBox dateFinBox = new HBox(5);
        Label dateFinLabel = new Label("Date de fin:");
        dateFinLabel.getStyleClass().add("info-label");
        Label dateFinValue = new Label(new SimpleDateFormat("dd/MM/yyyy").format(formation.getDate_fin()));
        dateFinValue.getStyleClass().add("info-value");
        dateFinBox.getChildren().addAll(dateFinLabel, dateFinValue);

        infoContainer.getChildren().addAll(formateurBox, capaciteBox, dateDebutBox, dateFinBox);

        // Boutons d'action
        HBox actions = new HBox(10);
        actions.getStyleClass().add("actions-container");

        Button reserveButton = new Button("Réserver");
        reserveButton.getStyleClass().addAll("action-button", "reserve-button");
        reserveButton.setOnAction(e -> openReservationForm(formation));

        actions.getChildren().add(reserveButton);

        // Assembler la carte
        card.getChildren().addAll(header, infoContainer, actions);

        return card;
    }

    private void openReservationForm(Formation formation) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/AddReservation.fxml"));
            Parent root = loader.load();

            Controllers.AddReservationController controller = loader.getController();
            controller.setFormationId(formation.getId()); // Pass the formation ID

            Scene scene = new Scene(root);
            scene.getStylesheets().add(getClass().getResource("/AddReservation.css").toExternalForm());

            Stage stage = new Stage();
            stage.setTitle("Réserver une Formation");
            stage.setScene(scene);
            stage.showAndWait();

            loadFormations(); // Refresh in case reservation affects display (optional)
        } catch (IOException e) {
            showAlert(Alert.AlertType.ERROR, "Erreur", "Erreur lors de l'ouverture du formulaire de réservation: " + e.getMessage());
        }
    }

    private List<Formation> getFormations() {
        try {
            return serviceFormation.recuperer();
        } catch (SQLException e) {
            showAlert(Alert.AlertType.ERROR, "Erreur", "Erreur lors du chargement des formations: " + e.getMessage());
            return null;
        }
    }

    private void showAlert(Alert.AlertType type, String title, String content) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setContentText(content);
        alert.showAndWait();
    }
}