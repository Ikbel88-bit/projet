package controllers;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.stage.Stage;
import utils.MyDatabase;

import java.io.IOException;

public class MainViewController {

    private Stage entretiensStage = null;
    private Stage evaluationsStage = null;
    private Stage listeEntretiensStage = null;

    public void handleEntretiens() {
        if (!checkDatabaseConnection()) return;

        if (entretiensStage != null && entretiensStage.isShowing()) {
            entretiensStage.toFront();
            entretiensStage.requestFocus();
            return;
        }
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/EntretienView.fxml"));
            Parent root = loader.load();
            Scene scene = new Scene(root);
            entretiensStage = new Stage();
            entretiensStage.setScene(scene);
            // Définir une taille raisonnable pour voir les bords de la fenêtre
            entretiensStage.setWidth(900);
            entretiensStage.setHeight(700);
            entretiensStage.setResizable(true);
            entretiensStage.setTitle("Gestion des Entretiens");
            entretiensStage.setOnHidden(e -> entretiensStage = null);
            entretiensStage.show();
            // Centrer la fenêtre sur l'écran
            entretiensStage.centerOnScreen();
        } catch (IOException e) {
            showError("Erreur", "Impossible de charger la vue des entretiens : " + e.getMessage());
        }
    }

    public void handleEvaluations() {
        if (!checkDatabaseConnection()) return;

        if (evaluationsStage != null && evaluationsStage.isShowing()) {
            evaluationsStage.toFront();
            evaluationsStage.requestFocus();
            return;
        }
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/EvaluationView.fxml"));
            Parent root = loader.load();
            Scene scene = new Scene(root);
            evaluationsStage = new Stage();
            evaluationsStage.setScene(scene);
            // Définir une taille raisonnable pour voir les bords de la fenêtre
            evaluationsStage.setWidth(900);
            evaluationsStage.setHeight(700);
            evaluationsStage.setResizable(true); // Permettre le redimensionnement
            evaluationsStage.setTitle("Gestion des Évaluations");
            evaluationsStage.setOnHidden(e -> evaluationsStage = null);
            evaluationsStage.show();
            // Centrer la fenêtre sur l'écran
            evaluationsStage.centerOnScreen();
        } catch (IOException e) {
            showError("Erreur", "Impossible de charger la vue des évaluations : " + e.getMessage());
        }
    }

    public void handleListeEntretiens() {
        if (!checkDatabaseConnection()) return;

        if (listeEntretiensStage != null && listeEntretiensStage.isShowing()) {
            listeEntretiensStage.toFront();
            listeEntretiensStage.requestFocus();
            return;
        }
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/ListeEntretiensView.fxml"));
            Parent root = loader.load();
            Scene scene = new Scene(root);
            listeEntretiensStage = new Stage();
            listeEntretiensStage.setScene(scene);
            // Définir une taille raisonnable pour voir les bords de la fenêtre
            listeEntretiensStage.setWidth(950);
            listeEntretiensStage.setHeight(650);
            listeEntretiensStage.setResizable(true);
            listeEntretiensStage.setTitle("Liste des Entretiens");
            listeEntretiensStage.setOnHidden(e -> listeEntretiensStage = null);
            listeEntretiensStage.show();
            // Centrer la fenêtre sur l'écran
            listeEntretiensStage.centerOnScreen();
        } catch (IOException e) {
            showError("Erreur", "Impossible de charger la liste des entretiens : " + e.getMessage());
        }
    }

    private boolean checkDatabaseConnection() {
        if (!MyDatabase.getInstance().isConnected()) {
            showError("Erreur de connexion",
                "Impossible de se connecter à la base de données.\n" +
                "Veuillez vérifier que :\n" +
                "1. Le serveur MySQL est démarré\n" +
                "2. La base de données 'workshop' existe\n" +
                "3. Les paramètres de connexion sont corrects");
            return false;
        }
        return true;
    }

    private void showError(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
}