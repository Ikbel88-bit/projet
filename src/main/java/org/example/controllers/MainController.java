package org.example.controllers;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

import java.io.IOException;

public class MainController {
    @FXML
    public void initialize() {
        navigateTo("accueil"); // Affiche la page d'accueil par défaut
    }

    public static void navigateTo(String view) {
        try {
            String fxmlPath = "/org/example/views/" + view + ".fxml";
            FXMLLoader loader = new FXMLLoader(MainController.class.getResource(fxmlPath));
            Parent viewContent = loader.load();
            // Lookup dynamique du StackPane central
            Scene scene = Stage.getWindows().stream().filter(Stage.class::isInstance).map(Stage.class::cast).findFirst().map(Stage::getScene).orElse(null);
            if (scene != null) {
                StackPane contentPane = (StackPane) scene.lookup(".content-pane");
                if (contentPane != null) {
                    contentPane.getChildren().setAll(viewContent);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
            showError("Erreur de navigation", "Impossible de charger la vue demandée.");
        }
    }

    @FXML
    private void handleAccueil() {
        navigateTo("accueil");
    }

    @FXML
    private void handleRessources() {
        navigateTo("ressources");
    }

    @FXML
    private void handleReservations() {
        navigateTo("reservations");
    }

    private static void showError(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
} 