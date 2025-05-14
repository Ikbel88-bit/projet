package controllers;

import entities.Entretien;
import entities.Evaluation;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import services.ServiceEntretien;
import services.ServiceEvaluation;

import java.io.IOException;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class EvaluationViewController {

    @FXML
    private ComboBox<Entretien> cbEntretien;
    @FXML
    private TextArea taCommentaire;
    @FXML
    private Spinner<Integer> spNote;
    @FXML
    private DatePicker dpDate;
    @FXML
    private FlowPane flowPaneEvaluations;

    @FXML
    private Button btnMinimize;

    @FXML
    private Button btnMaximize;

    @FXML
    private Button btnClose;

    private ServiceEvaluation serviceEvaluation;
    private ServiceEntretien serviceEntretien;
    private Evaluation selectedEvaluation;
    private double xOffset = 0;
    private double yOffset = 0;

    @FXML
    void initialize() {
        serviceEvaluation = new ServiceEvaluation();
        serviceEntretien = new ServiceEntretien();



        // Configurer le Spinner pour les notes (0-10)
        SpinnerValueFactory.IntegerSpinnerValueFactory valueFactory =
                new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 10, 5);
        spNote.setValueFactory(valueFactory);

        // Charger les entretiens dans la ComboBox
        try {
            ObservableList<Entretien> entretiens = FXCollections.observableList(serviceEntretien.recuperer());
            cbEntretien.setItems(entretiens);
            cbEntretien.setCellFactory(param -> new ListCell<Entretien>() {
                @Override
                protected void updateItem(Entretien item, boolean empty) {
                    super.updateItem(item, empty);
                    if (empty || item == null) {
                        setText(null);
                    } else {
                        setText(item.getTitre());
                    }
                }
            });
        } catch (SQLException e) {
            showError("Erreur", "Impossible de charger les entretiens");
        }

        // Charger les données
        refreshCards();
    }

    private void refreshCards() {
        flowPaneEvaluations.getChildren().clear();
        try {
            for (Evaluation evaluation : serviceEvaluation.recuperer()) {
                VBox card = createEvaluationCard(evaluation);
                flowPaneEvaluations.getChildren().add(card);
            }
        } catch (SQLException e) {
            showError("Erreur lors du chargement des évaluations", e.getMessage());
        }
    }

    private VBox createEvaluationCard(Evaluation evaluation) {
        VBox card = new VBox(10);
        card.setStyle("-fx-background-color: white; -fx-padding: 12; -fx-background-radius: 8; " +
                "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.1), 10, 0, 0, 2); " +
                "-fx-min-width: 220; -fx-max-width: 280; -fx-font-size: 13;");

        try {
            Entretien entretien = serviceEntretien.recuperer().stream()
                    .filter(e -> e.getId_entretien() == evaluation.getId_entretien())
                    .findFirst()
                    .orElse(null);

            Text title = new Text(entretien != null ? entretien.getTitre() : "Entretien inconnu");
            title.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-fill: #2c3e50;");

            Text commentaire = new Text(evaluation.getCommentaire());
            commentaire.setStyle("-fx-font-size: 14px; -fx-fill: #34495e;");
            commentaire.setWrappingWidth(280);

            HBox noteBox = new HBox(5);
            Text noteLabel = new Text("Note : ");
            noteLabel.setStyle("-fx-font-size: 14px; -fx-font-weight: bold; -fx-fill: #2c3e50;");
            Text noteValue = new Text(String.valueOf(evaluation.getNote()) + "/10");
            noteValue.setStyle("-fx-font-size: 14px; -fx-fill: #27ae60;");
            noteBox.getChildren().addAll(noteLabel, noteValue);

            Text date = new Text("Date : " + evaluation.getDate_evaluation());
            date.setStyle("-fx-font-size: 14px; -fx-fill: #7f8c8d;");

            HBox buttonsBox = new HBox(10);
            Button editButton = new Button("Modifier");
            editButton.setStyle("-fx-background-color: #3498db; -fx-text-fill: white; -fx-background-radius: 5;");
            Button deleteButton = new Button("Supprimer");
            deleteButton.setStyle("-fx-background-color: #e74c3c; -fx-text-fill: white; -fx-background-radius: 5;");

            editButton.setOnAction(e -> {
                selectedEvaluation = evaluation;
                populateFields(evaluation);
            });

            deleteButton.setOnAction(e -> {
                selectedEvaluation = evaluation;
                handleSupprimer();
            });

            buttonsBox.getChildren().addAll(editButton, deleteButton);

            card.getChildren().addAll(title, commentaire, noteBox, date, buttonsBox);
            card.setOnMouseClicked(e -> {
                selectedEvaluation = evaluation;
                populateFields(evaluation);
            });

        } catch (SQLException e) {
            showError("Erreur", "Impossible de charger les détails de l'évaluation");
        }

        return card;
    }

    private void populateFields(Evaluation evaluation) {
        try {
            Entretien entretien = serviceEntretien.recuperer().stream()
                    .filter(e -> e.getId_entretien() == evaluation.getId_entretien())
                    .findFirst()
                    .orElse(null);
            if (entretien != null) {
                cbEntretien.setValue(entretien);
            }
            taCommentaire.setText(evaluation.getCommentaire());
            spNote.getValueFactory().setValue(evaluation.getNote());
            dpDate.setValue(LocalDate.parse(evaluation.getDate_evaluation()));
        } catch (SQLException e) {
            showError("Erreur", "Impossible de charger les détails de l'évaluation");
        }
    }

    private void clearFields() {
        cbEntretien.setValue(null);
        taCommentaire.clear();
        spNote.getValueFactory().setValue(5);
        dpDate.setValue(null);
        selectedEvaluation = null;
    }

    @FXML
    void handleAjouter() {
        if (!validateFields()) return;

        try {
            Evaluation evaluation = new Evaluation(
                    cbEntretien.getValue().getId_entretien(),
                    taCommentaire.getText(),
                    spNote.getValue(),
                    dpDate.getValue().format(DateTimeFormatter.ISO_DATE)
            );

            serviceEvaluation.ajouter(evaluation);
            showSuccess("Évaluation ajoutée avec succès");
            clearFields();
            refreshCards();
        } catch (SQLException e) {
            showError("Erreur lors de l'ajout", e.getMessage());
        }
    }

    @FXML
    void handleModifier() {
        if (selectedEvaluation == null) {
            showError("Erreur", "Veuillez sélectionner une évaluation à modifier");
            return;
        }

        if (!validateFields()) return;

        try {
            selectedEvaluation.setId_entretien(cbEntretien.getValue().getId_entretien());
            selectedEvaluation.setCommentaire(taCommentaire.getText());
            selectedEvaluation.setNote(spNote.getValue());
            selectedEvaluation.setDate_evaluation(dpDate.getValue().format(DateTimeFormatter.ISO_DATE));

            serviceEvaluation.modifier(selectedEvaluation);
            showSuccess("Évaluation modifiée avec succès");
            clearFields();
            refreshCards();
        } catch (SQLException e) {
            showError("Erreur lors de la modification", e.getMessage());
        }
    }

    @FXML
    void handleSupprimer() {
        if (selectedEvaluation == null) {
            showError("Erreur", "Veuillez sélectionner une évaluation à supprimer");
            return;
        }

        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirmation de suppression");
        alert.setHeaderText("Supprimer l'évaluation");
        alert.setContentText("Êtes-vous sûr de vouloir supprimer cette évaluation ?");

        if (alert.showAndWait().get() == ButtonType.OK) {
            try {
                serviceEvaluation.supprimer(selectedEvaluation);
                showSuccess("Évaluation supprimée avec succès");
                clearFields();
                refreshCards();
            } catch (SQLException e) {
                showError("Erreur lors de la suppression", e.getMessage());
            }
        }
    }

    @FXML
    void handleRetour() {
        Stage stage = (Stage) flowPaneEvaluations.getScene().getWindow();
        stage.close();
    }

    @FXML
    void handleMinimize() {
        Stage stage = (Stage) flowPaneEvaluations.getScene().getWindow();
        stage.setIconified(true);
    }

    @FXML
    void handleMaximize() {
        Stage stage = (Stage) flowPaneEvaluations.getScene().getWindow();
        if (stage.isMaximized()) {
            stage.setMaximized(false);
            btnMaximize.setText("□"); // Square symbol for maximize
        } else {
            stage.setMaximized(true);
            btnMaximize.setText("❐"); // Different symbol for restore
        }
    }

    @FXML
    void handleClose() {
        Stage stage = (Stage) flowPaneEvaluations.getScene().getWindow();
        stage.close();
    }

    private boolean validateFields() {
        if (cbEntretien.getValue() == null) {
            showError("Erreur", "Veuillez sélectionner un entretien");
            return false;
        }
        if (taCommentaire.getText().trim().isEmpty()) {
            showError("Erreur", "Veuillez saisir un commentaire");
            return false;
        }
        if (dpDate.getValue() == null) {
            showError("Erreur", "Veuillez sélectionner une date");
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

    private void showSuccess(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Succès");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}