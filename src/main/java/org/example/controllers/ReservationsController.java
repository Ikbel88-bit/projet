package org.example.controllers;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.GridPane;
import org.example.models.Reservation;
import org.example.models.Ressource;
import services.ServiceRessource;

import java.time.LocalDate;
import java.util.List;

public class ReservationsController {
    @FXML private TableView<Reservation> reservationsTable;
    @FXML private TableColumn<Reservation, String> ressourceColumn;
    @FXML private TableColumn<Reservation, String> employeColumn;
    @FXML private TableColumn<Reservation, LocalDate> dateDebutColumn;
    @FXML private TableColumn<Reservation, LocalDate> dateFinColumn;
    @FXML private TableColumn<Reservation, String> motifColumn;
    @FXML private TableColumn<Reservation, String> statutColumn;
    @FXML private GridPane formPane;
    @FXML private ComboBox<String> ressourceCombo;
    @FXML private ComboBox<String> employeCombo;
    @FXML private DatePicker dateDebutPicker;
    @FXML private DatePicker dateFinPicker;
    @FXML private TextField motifField;
    @FXML private ComboBox<String> statutCombo;

    private static final ObservableList<Reservation> reservations = FXCollections.observableArrayList();
    private Reservation selectedReservation;
    private int idCounter = 1;

    @FXML
    public void initialize() {
        ressourceColumn.setCellValueFactory(cellData -> new javafx.beans.property.SimpleStringProperty(getRessourceName(cellData.getValue().getIdRessources())));
        employeColumn.setCellValueFactory(cellData -> new javafx.beans.property.SimpleStringProperty(getEmployeName(cellData.getValue().getIdEmploye())));
        dateDebutColumn.setCellValueFactory(new PropertyValueFactory<>("dateDebut"));
        dateFinColumn.setCellValueFactory(new PropertyValueFactory<>("dateFin"));
        motifColumn.setCellValueFactory(new PropertyValueFactory<>("motif"));
        statutColumn.setCellValueFactory(new PropertyValueFactory<>("statut"));
        reservationsTable.setItems(reservations);
        formPane.setVisible(false);
        reservationsTable.getSelectionModel().selectedItemProperty().addListener((obs, oldSel, newSel) -> {
            selectedReservation = newSel;
        });
        loadRessourcesFromDB();
    }

    private void loadRessourcesFromDB() {
        try {
            ServiceRessource service = new ServiceRessource();
            List<Ressource> ressources = service.recuperer();
            ressourceCombo.getItems().clear();
            for (Ressource r : ressources) {
                ressourceCombo.getItems().add(r.getNom());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Méthodes utilitaires pour afficher le nom à partir du hash (démonstration)
    private String getRessourceName(int id) {
        switch (id) {
            case -1817273702: return "Salle 101";
            case 146634: return "Projecteur";
            case -2015532632: return "Véhicule";
            default: return String.valueOf(id);
        }
    }
    private String getEmployeName(int id) {
        switch (id) {
            case -2023551132: return "Ali Ben";
            case 774099: return "Sara M.";
            case 2019645272: return "Yassine T.";
            default: return String.valueOf(id);
        }
    }

    @FXML
    private void handleAdd() {
        clearForm();
        selectedReservation = null;
        formPane.setVisible(true);
    }

    @FXML
    private void handleEdit() {
        if (selectedReservation != null) {
            ressourceCombo.setValue(String.valueOf(selectedReservation.getIdRessources()));
            employeCombo.setValue(String.valueOf(selectedReservation.getIdEmploye()));
            dateDebutPicker.setValue(selectedReservation.getDateDebut());
            dateFinPicker.setValue(selectedReservation.getDateFin());
            motifField.setText(selectedReservation.getMotif());
            statutCombo.setValue(selectedReservation.getStatut());
            formPane.setVisible(true);
        }
    }

    @FXML
    private void handleDelete() {
        if (selectedReservation != null) {
            reservations.remove(selectedReservation);
            selectedReservation = null;
            formPane.setVisible(false);
        }
    }

    @FXML
    private void handleSave() {
        String ressource = ressourceCombo.getValue();
        String employe = employeCombo.getValue();
        LocalDate dateDebut = dateDebutPicker.getValue();
        LocalDate dateFin = dateFinPicker.getValue();
        String motif = motifField.getText();
        String statut = statutCombo.getValue();
        LocalDate today = LocalDate.now();
        if (ressource == null || employe == null || dateDebut == null || dateFin == null || motif.isEmpty() || statut == null) {
            showAlert("Veuillez remplir tous les champs.");
            return;
        }
        if (dateDebut.isBefore(today)) {
            showAlert("La date de début ne peut pas être antérieure à aujourd'hui.");
            return;
        }
        if (!dateFin.isAfter(dateDebut)) {
            showAlert("La date de fin doit être supérieure à la date de début.");
            return;
        }
        int idRessources = ressource.hashCode(); // Pour la démo, on utilise le hash du nom
        int idEmploye = employe.hashCode(); // Pour la démo, on utilise le hash du nom
        if (selectedReservation == null) {
            Reservation r = new Reservation(idCounter++, idRessources, idEmploye, dateDebut, dateFin, motif, statut);
            reservations.add(r);
        } else {
            selectedReservation.setIdRessources(idRessources);
            selectedReservation.setIdEmploye(idEmploye);
            selectedReservation.setDateDebut(dateDebut);
            selectedReservation.setDateFin(dateFin);
            selectedReservation.setMotif(motif);
            selectedReservation.setStatut(statut);
            reservationsTable.refresh();
        }
        formPane.setVisible(false);
        clearForm();
    }

    @FXML
    private void handleCancel() {
        formPane.setVisible(false);
        clearForm();
    }

    @FXML
    private void handleBack() {
        MainController.navigateTo("accueil");
    }

    private void clearForm() {
        ressourceCombo.setValue(null);
        employeCombo.setValue(null);
        dateDebutPicker.setValue(null);
        dateFinPicker.setValue(null);
        motifField.clear();
        statutCombo.setValue(null);
    }

    private void showAlert(String msg) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle("Attention");
        alert.setHeaderText(null);
        alert.setContentText(msg);
        alert.showAndWait();
    }
} 