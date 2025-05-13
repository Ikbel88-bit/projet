package controllers;

import entities.Entretien;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.layout.HBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import services.ServiceEntretien;

import java.io.IOException;
import java.sql.SQLException;

public class ListeEntretiensController {

    @FXML
    private TableView<Entretien> tableEntretiens;
    @FXML
    private TableColumn<Entretien, String> colTitre;
    @FXML
    private TableColumn<Entretien, String> colDate;
    @FXML
    private TableColumn<Entretien, String> colLieu;
    @FXML
    private TableColumn<Entretien, String> colParticipant;
    @FXML
    private TableColumn<Entretien, String> colStatut;
    @FXML
    private TableColumn<Entretien, Void> colActions;

    private ServiceEntretien serviceEntretien;

    @FXML
    void initialize() {
        serviceEntretien = new ServiceEntretien();
        
        // Configurer les colonnes
        colTitre.setCellValueFactory(cellData -> cellData.getValue().titreProperty());
        colDate.setCellValueFactory(cellData -> cellData.getValue().date_entretienProperty());
        colLieu.setCellValueFactory(cellData -> cellData.getValue().lieuProperty());
        colParticipant.setCellValueFactory(cellData -> cellData.getValue().participantProperty());
        colStatut.setCellValueFactory(cellData -> cellData.getValue().statutProperty());

        // Ajouter la colonne d'actions
        colActions.setCellFactory(param -> new TableCell<Entretien, Void>() {
            private final Button btnModifier = new Button("Modifier");
            private final Button btnSupprimer = new Button("Supprimer");
            {
                btnModifier.setStyle("-fx-background-color: #3498db; -fx-text-fill: white; -fx-background-radius: 5; -fx-padding: 4 10;");
                btnSupprimer.setStyle("-fx-background-color: #e74c3c; -fx-text-fill: white; -fx-background-radius: 5; -fx-padding: 4 10;");
                btnModifier.setOnAction(e -> {
                    Entretien entretien = getTableView().getItems().get(getIndex());
                    handleModifier(entretien);
                });
                btnSupprimer.setOnAction(e -> {
                    Entretien entretien = getTableView().getItems().get(getIndex());
                    handleSupprimer(entretien);
                });
            }
            private final HBox pane = new HBox(8, btnModifier, btnSupprimer);
            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    setGraphic(pane);
                }
            }
        });

        // Charger les données
        refreshTable();
    }

    private void refreshTable() {
        try {
            tableEntretiens.setItems(FXCollections.observableList(serviceEntretien.recuperer()));
        } catch (SQLException e) {
            showError("Erreur", "Impossible de charger la liste des entretiens");
        }
    }

    @FXML
    void handleFermer() {
        Stage stage = (Stage) tableEntretiens.getScene().getWindow();
        stage.close();
    }

    private void handleModifier(Entretien entretien) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/EntretienView.fxml"));
            Parent root = loader.load();
            
            // Récupérer le contrôleur et initialiser les champs avec l'entretien sélectionné
            EntretienViewController controller = loader.getController();
            controller.initData(entretien);
            
            Stage stage = new Stage();
            stage.setTitle("Modifier l'entretien");
            stage.setScene(new Scene(root));
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setOnHidden(e -> refreshTable()); // Rafraîchir la liste après modification
            stage.show();
        } catch (IOException e) {
            showError("Erreur", "Impossible d'ouvrir le formulaire de modification : " + e.getMessage());
        }
    }

    private void handleSupprimer(Entretien entretien) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirmation de suppression");
        alert.setHeaderText("Supprimer l'entretien");
        alert.setContentText("Êtes-vous sûr de vouloir supprimer cet entretien ?");
        if (alert.showAndWait().get() == ButtonType.OK) {
            try {
                serviceEntretien.supprimer(entretien);
                refreshTable();
            } catch (Exception e) {
                showError("Erreur", "Impossible de supprimer l'entretien.");
            }
        }
    }

    private void showError(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
} 
