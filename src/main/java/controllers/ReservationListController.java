package controllers;

import entities.Reservation;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.VBox;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
import services.ServiceReservation;

import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;

public class ReservationListController implements Initializable {

    @FXML
    private FlowPane reservationContainer;

    @FXML
    private VBox emptyState;

    private ServiceReservation serviceReservation;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        serviceReservation = new ServiceReservation();
        loadReservations();
    }

    @FXML
    public void openAddReservation() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/AddReservation.fxml"));
            Parent root = loader.load();

            Scene scene = new Scene(root);
            scene.getStylesheets().add(getClass().getResource("/AddReservation.css").toExternalForm());

            Stage stage = new Stage();
            stage.setTitle("Nouvelle Réservation");
            stage.setScene(scene);
            stage.showAndWait();

            loadReservations();
        } catch (IOException e) {
            showAlert(Alert.AlertType.ERROR, "Erreur", "Erreur lors de l'ouverture du formulaire: " + e.getMessage());
        }
    }

    public void loadReservations() {
        reservationContainer.getChildren().clear();
        List<Reservation> reservations = getReservations();

        if (reservations != null) {
            if (reservations.isEmpty()) {
                emptyState.setVisible(true);
                emptyState.setManaged(true);
                reservationContainer.setVisible(false);
                reservationContainer.setManaged(false);
            } else {
                emptyState.setVisible(false);
                emptyState.setManaged(false);
                reservationContainer.setVisible(true);
                reservationContainer.setManaged(true);

                for (Reservation reservation : reservations) {
                    VBox card = createReservationCard(reservation);
                    reservationContainer.getChildren().add(card);
                }
            }
        }
    }

    private VBox createReservationCard(Reservation reservation) {
        VBox card = new VBox(10);
        card.getStyleClass().add("reservation-item");

        HBox header = new HBox(10);
        header.getStyleClass().add("card-header");

        Label titleLabel = new Label("Réservation #" + reservation.getId());
        titleLabel.getStyleClass().add("reservation-title");

        header.getChildren().add(titleLabel);

        VBox infoContainer = new VBox(8);
        infoContainer.getStyleClass().add("info-container");

        HBox participantBox = new HBox(5);
        Label participantLabel = new Label("Participant:");
        participantLabel.getStyleClass().add("info-label");
        Label participantValue = new Label(reservation.getNom_participant());
        participantValue.getStyleClass().add("info-value");
        participantBox.getChildren().addAll(participantLabel, participantValue);

        HBox emailBox = new HBox(5);
        Label emailLabel = new Label("Email:");
        emailLabel.getStyleClass().add("info-label");
        Label emailValue = new Label(reservation.getEmail_participant());
        emailValue.getStyleClass().add("info-value");
        emailBox.getChildren().addAll(emailLabel, emailValue);

        HBox dateBox = new HBox(5);
        Label dateLabel = new Label("Date:");
        dateLabel.getStyleClass().add("info-label");
        Label dateValue = new Label(new SimpleDateFormat("dd/MM/yyyy").format(reservation.getDate_reservation()));
        dateValue.getStyleClass().add("info-value");
        dateBox.getChildren().addAll(dateLabel, dateValue);

        HBox formationBox = new HBox(5);
        Label formationLabel = new Label("Formation ID:");
        formationLabel.getStyleClass().add("info-label");
        Label formationValue = new Label(String.valueOf(reservation.getId_formation()));
        formationValue.getStyleClass().add("info-value");
        formationBox.getChildren().addAll(formationLabel, formationValue);

        infoContainer.getChildren().addAll(participantBox, emailBox, dateBox, formationBox);

        HBox actions = new HBox(10);
        actions.getStyleClass().add("actions-container");

        Button deleteButton = new Button("Supprimer");
        deleteButton.getStyleClass().addAll("action-button", "delete-button");
        deleteButton.setOnAction(e -> deleteReservation(reservation));

        actions.getChildren().add(deleteButton);

        card.getChildren().addAll(header, infoContainer, actions);

        return card;
    }

    private void deleteReservation(Reservation reservation) {
        Alert confirmDialog = new Alert(Alert.AlertType.CONFIRMATION);
        confirmDialog.setTitle("Confirmation de suppression");
        confirmDialog.setHeaderText("Supprimer la réservation");
        confirmDialog.setContentText("Êtes-vous sûr de vouloir supprimer cette réservation ?");

        Optional<ButtonType> result = confirmDialog.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            try {
                serviceReservation.supprimer(reservation);
                reservationContainer.getChildren().removeIf(node ->
                        node instanceof VBox && node.getUserData() == reservation);
                showAlert(Alert.AlertType.INFORMATION, "Succès", "Réservation supprimée avec succès!");
                loadReservations();
            } catch (SQLException e) {
                showAlert(Alert.AlertType.ERROR, "Erreur", "Erreur lors de la suppression: " + e.getMessage());
            }
        }
    }

    private List<Reservation> getReservations() {
        try {
            return serviceReservation.recuperer();
        } catch (SQLException e) {
            showAlert(Alert.AlertType.ERROR, "Erreur", "Erreur lors du chargement des réservations: " + e.getMessage());
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