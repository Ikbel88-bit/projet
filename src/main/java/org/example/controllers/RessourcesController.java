package org.example.controllers;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.GridPane;
import org.example.models.Ressource;
import services.ServiceRessource;

public class RessourcesController {
    @FXML private TableView<Ressource> ressourcesTable;
    @FXML private TableColumn<Ressource, String> nomColumn;
    @FXML private TableColumn<Ressource, String> typeColumn;
    @FXML private TableColumn<Ressource, String> descriptionColumn;
    @FXML private TableColumn<Ressource, String> emplacementColumn;
    @FXML private TableColumn<Ressource, String> etatColumn;
    @FXML private TableColumn<Ressource, Boolean> disponibleColumn;
    @FXML private GridPane formPane;
    @FXML private TextField nomField;
    @FXML private TextField typeField;
    @FXML private TextField descriptionField;
    @FXML private TextField emplacementField;
    @FXML private ComboBox<String> etatCombo;
    @FXML private CheckBox disponibleCheck;

    private static final ObservableList<Ressource> ressources = FXCollections.observableArrayList();
    private Ressource selectedRessource;
    private int idCounter = 1;

    @FXML
    public void initialize() {
        // idColumn.setCellValueFactory(new PropertyValueFactory<>("idRessource"));
        nomColumn.setCellValueFactory(new PropertyValueFactory<>("nom"));
        typeColumn.setCellValueFactory(new PropertyValueFactory<>("type"));
        descriptionColumn.setCellValueFactory(new PropertyValueFactory<>("description"));
        emplacementColumn.setCellValueFactory(new PropertyValueFactory<>("emplacement"));
        etatColumn.setCellValueFactory(new PropertyValueFactory<>("etat"));
        disponibleColumn.setCellValueFactory(new PropertyValueFactory<>("disponible"));
        ressourcesTable.setItems(ressources);
        formPane.setVisible(false);
        ressourcesTable.getSelectionModel().selectedItemProperty().addListener((obs, oldSel, newSel) -> {
            selectedRessource = newSel;
        });
        // Charger les ressources au démarrage
        loadRessourcesFromDB();
    }

    @FXML
    private void handleAdd() {
        clearForm();
        selectedRessource = null;
        formPane.setVisible(true);
    }

    @FXML
    private void handleEdit() {
        if (selectedRessource != null) {
            nomField.setText(selectedRessource.getNom());
            typeField.setText(selectedRessource.getType());
            descriptionField.setText(selectedRessource.getDescription());
            emplacementField.setText(selectedRessource.getEmplacement());
            etatCombo.setValue(selectedRessource.getEtat());
            disponibleCheck.setSelected(selectedRessource.isDisponible());
            formPane.setVisible(true);
        }
    }

    @FXML
    private void handleDelete() {
        if (selectedRessource != null) {
            ServiceRessource service = new ServiceRessource();
            try {
                service.supprimer(selectedRessource);
            } catch (Exception e) {
                e.printStackTrace();
                showAlert("Erreur lors de la suppression.");
            }
            loadRessourcesFromDB();
            selectedRessource = null;
            formPane.setVisible(false);
            MainController.navigateTo("accueil");
        }
    }

    @FXML
    private void handleSave() {
        String nom = nomField.getText();
        String type = typeField.getText();
        String description = descriptionField.getText();
        String emplacement = emplacementField.getText();
        String etat = etatCombo.getValue();
        boolean disponible = disponibleCheck.isSelected();
        if (nom.isEmpty() || type.isEmpty() || description.isEmpty() || emplacement.isEmpty() || etat == null) {
            showAlert("Veuillez remplir tous les champs.");
            return;
        }
        ServiceRessource service = new ServiceRessource();
        try {
            if (selectedRessource == null) {
                Ressource r = new Ressource(0, nom, type, description, disponible, emplacement, etat);
                service.ajouter(r);
                System.out.println("Nouvelle ressource ajoutée avec l'ID: " + r.getIdRessource());
            } else {
                selectedRessource.setNom(nom);
                selectedRessource.setType(type);
                selectedRessource.setDescription(description);
                selectedRessource.setEmplacement(emplacement);
                selectedRessource.setEtat(etat);
                selectedRessource.setDisponible(disponible);
                service.modifier(selectedRessource);
            }
            // Recharger la table immédiatement après l'ajout/modification
            loadRessourcesFromDB();
            formPane.setVisible(false);
            clearForm();
        } catch (Exception e) {
            e.printStackTrace();
            showAlert("Erreur lors de la sauvegarde: " + e.getMessage());
        }
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
        nomField.clear();
        typeField.clear();
        descriptionField.clear();
        emplacementField.clear();
        etatCombo.setValue(null);
        disponibleCheck.setSelected(false);
    }

    private void showAlert(String msg) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle("Attention");
        alert.setHeaderText(null);
        alert.setContentText(msg);
        alert.showAndWait();
    }

    private void loadRessourcesFromDB() {
        ressources.clear();
        try {
            ServiceRessource service = new ServiceRessource();
            ressources.addAll(service.recuperer());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
} 