package org.example.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.Label;

public class AccueilController {
    @FXML private Label ressourcesCount;
    @FXML private Label reservationsCount;

    @FXML
    public void initialize() {
        // Ces valeurs peuvent être récupérées dynamiquement depuis la base ou un service
        ressourcesCount.setText("12");
        reservationsCount.setText("5");
    }

    @FXML
    private void handleNouvelleRessource() {
        MainController.navigateTo("ressources");
    }

    @FXML
    private void handleNouvelleReservation() {
        MainController.navigateTo("reservations");
    }
} 