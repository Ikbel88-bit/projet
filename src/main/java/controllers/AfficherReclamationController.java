// AfficherReclamationController.java
package controllers;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import entities.Reclamation;
import entities.User;
import services.ServiceReclamation;
import services.ServiceUser;
import javafx.stage.Stage;
import java.sql.SQLException;
import java.io.IOException;
import javafx.scene.layout.VBox;
import javafx.scene.control.TextArea;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;

public class AfficherReclamationController {

    @FXML
    private TableView<Reclamation> tableView;

    @FXML
    private TableColumn<Reclamation, Integer> id;

    @FXML
    private TableColumn<Reclamation, Integer> userId;

    @FXML
    private TableColumn<Reclamation, String> description;

    @FXML
    private TableColumn<Reclamation, String> reponse;

    @FXML
    private Button btnRetour;

    @FXML
    private Button btnSupprimer;

    @FXML
    private VBox reclamationsBox;

    @FXML
    private Button btnRetourPrecedent;

    private ObservableList<Reclamation> reclamations;
    private ServiceReclamation serviceReclamation;
    private ServiceUser serviceUser;

    @FXML
    public void initialize() {
        serviceReclamation = new ServiceReclamation();
        serviceUser = new ServiceUser();
        try {
            // Affichage dynamique dans la VBox
            reclamationsBox.getChildren().clear();
            for (Reclamation rec : serviceReclamation.recupererToutes()) {
                // Créer un conteneur pour chaque réclamation
                VBox reclamationContainer = new VBox(5);
                reclamationContainer.setStyle("-fx-padding: 10; -fx-background-color: white; -fx-background-radius: 8; -fx-border-radius: 8; -fx-border-color: #bdc3c7;");

                // Récupérer les informations du candidat
                User candidat = serviceUser.recupererParId(rec.getUserId());
                if (candidat != null) {
                    Label candidatLabel = new Label("Candidat : " + candidat.getNom() + " " + candidat.getPrenom());
                    candidatLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 14;");
                    reclamationContainer.getChildren().add(candidatLabel);
                }

                // Ajouter la réclamation
                TextArea ta = new TextArea();
                ta.setText(rec.getDescription());
                ta.setEditable(false);
                ta.setWrapText(true);
                ta.setStyle("-fx-font-size: 14; -fx-background-radius: 8; -fx-border-radius: 8; -fx-border-color: #bdc3c7; -fx-padding: 8;");
                ta.setPrefHeight(80);
                ta.setOnMouseClicked((MouseEvent e) -> {
                    ouvrirFenetreReponse(rec);
                });
                reclamationContainer.getChildren().add(ta);

                // Ajouter le conteneur à la VBox principale
                reclamationsBox.getChildren().add(reclamationContainer);
            }
        } catch (SQLException e) {
            showError("Erreur", "Impossible de charger les réclamations", e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML
    public void retourAdmin() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/AfficherUser.fxml"));
            Parent root = loader.load();
            Scene scene = new Scene(root);
            Stage stage = (Stage) btnRetour.getScene().getWindow();
            stage.setScene(scene);
            stage.setTitle("Gestion des Utilisateurs");
            stage.show();
        } catch (IOException e) {
            showError("Erreur", "Impossible de retourner à la page précédente", e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML
    public void supprimerReclamation() {
        Reclamation selected = tableView.getSelectionModel().getSelectedItem();
        if (selected != null) {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Confirmation");
            alert.setHeaderText(null);
            alert.setContentText("Voulez-vous vraiment supprimer cette réclamation ?");

            alert.showAndWait().ifPresent(response -> {
                if (response == javafx.scene.control.ButtonType.OK) {
                    try {
                        serviceReclamation.supprimer(selected.getReclamationId());
                        reclamations.remove(selected);
                        showSuccess("Succès", "Réclamation supprimée avec succès");
                    } catch (SQLException e) {
                        showError("Erreur", "Impossible de supprimer la réclamation", e.getMessage());
                        e.printStackTrace();
                    }
                }
            });
        } else {
            showWarning("Attention", "Veuillez sélectionner une réclamation à supprimer");
        }
    }

    @FXML
    public void retourPrecedent() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/AfficherUser.fxml"));
            Parent root = loader.load();
            Scene scene = new Scene(root);
            Stage stage = (Stage) btnRetourPrecedent.getScene().getWindow();
            stage.setScene(scene);
            stage.setTitle("Gestion des Utilisateurs");
            stage.show();
        } catch (IOException e) {
            showError("Erreur", "Impossible de retourner à la page précédente", e.getMessage());
            e.printStackTrace();
        }
    }

    private void showError(String title, String header, String content) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(header);
        alert.setContentText(content);
        alert.showAndWait();
    }

    private void showSuccess(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }

    private void showWarning(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }

    private void ouvrirFenetreReponse(Reclamation rec) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/RepondreReclamation.fxml"));
            Parent root = loader.load();
            RepondreReclamationController ctrl = loader.getController();
            ctrl.setReclamation(rec);
            Scene scene = new Scene(root);
            Stage stage = (Stage) reclamationsBox.getScene().getWindow();
            stage.setScene(scene);
            stage.setTitle("Répondre à la réclamation");
            stage.show();
            // Rafraîchir après réponse
            initialize();
        } catch (IOException e) {
            showError("Erreur", "Impossible d'ouvrir la fenêtre de réponse", e.getMessage());
            e.printStackTrace();
        }
    }
}
