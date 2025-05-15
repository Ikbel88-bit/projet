package controller;

import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;

/**
 * Classe de base pour les contrôleurs avec des méthodes communes
 */
public class BaseController {
    
    /**
     * Affiche une alerte d'information
     * @param title Titre de l'alerte
     * @param message Message de l'alerte
     */
    protected void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
    
    /**
     * Affiche une alerte d'erreur
     * @param title Titre de l'alerte
     * @param message Message de l'alerte
     */
    protected void showErrorAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
    
    /**
     * Affiche une alerte de confirmation et retourne le résultat
     * @param title Titre de l'alerte
     * @param message Message de l'alerte
     * @return true si l'utilisateur a confirmé, false sinon
     */
    protected boolean showConfirmationAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        return alert.showAndWait().get() == ButtonType.OK;
    }
}