package controllers;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ListView;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.stage.Stage;
import java.util.function.Consumer;
import java.net.URI;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.awt.Desktop;

public class MapSelectionController {

    @FXML
    private TextField tfSearchLocation;

    @FXML
    private TextField tfSelectedLocation;

    @FXML
    private Button btnSearch;

    @FXML
    private Button btnMinimize;

    @FXML
    private Button btnMaximize;

    @FXML
    private Button btnClose;

    @FXML
    private Button btnViewOnMap;

    @FXML
    private ComboBox<String> cbPredefinedLocations;

    @FXML
    private ListView<String> lvSearchResults;

    private Consumer<String> locationCallback;
    private String selectedLocation = "";

    // Liste des lieux prédéfinis
    private final String[] predefinedLocations = {
        "Tunis",
        "Sfax",
        "Sousse",
        "Kairouan",
        "Bizerte",
        "Gabès",
        "Ariana",
        "Gafsa",
        "Monastir",
        "Kasserine",
        "Médenine",
        "Nabeul",
        "Tataouine",
        "Ben Arous",
        "La Manouba",
        "Béja",
        "Jendouba",
        "Le Kef",
        "Mahdia",
        "Sidi Bouzid",
        "Tozeur",
        "Siliana",
        "Zaghouan",
        "Kébili"
    };

    // Liste des résultats de recherche
    private ObservableList<String> searchResults = FXCollections.observableArrayList();

    @FXML
    void initialize() {
        // Initialiser la ComboBox avec les lieux prédéfinis
        cbPredefinedLocations.setItems(FXCollections.observableArrayList(predefinedLocations));

        // Initialiser la ListView avec une liste vide
        lvSearchResults.setItems(searchResults);

        // Ajouter un écouteur pour la sélection dans la ComboBox
        cbPredefinedLocations.setOnAction(e -> {
            String selected = cbPredefinedLocations.getValue();
            if (selected != null) {
                tfSelectedLocation.setText(selected);
                selectedLocation = selected;
            }
        });

        // Ajouter un écouteur pour la sélection dans la ListView
        lvSearchResults.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null) {
                tfSelectedLocation.setText(newVal);
                selectedLocation = newVal;
            }
        });
    }

    @FXML
    void handleSearch() {
        String searchTerm = tfSearchLocation.getText().trim().toLowerCase();
        if (!searchTerm.isEmpty()) {
            // Filtrer les lieux prédéfinis en fonction du terme de recherche
            searchResults.clear();
            for (String location : predefinedLocations) {
                if (location.toLowerCase().contains(searchTerm)) {
                    searchResults.add(location);
                }
            }

            // Si des résultats sont trouvés, sélectionner le premier
            if (!searchResults.isEmpty()) {
                lvSearchResults.getSelectionModel().select(0);
                selectedLocation = searchResults.get(0);
                tfSelectedLocation.setText(selectedLocation);
            } else {
                // Si aucun résultat n'est trouvé, utiliser le terme de recherche tel quel
                selectedLocation = tfSearchLocation.getText().trim();
                tfSelectedLocation.setText(selectedLocation);
            }
        }
    }

    @FXML
    void handleConfirm() {
        if (selectedLocation.isEmpty()) {
            showError("Erreur", "Veuillez sélectionner un lieu.");
            return;
        }

        if (locationCallback != null) {
            locationCallback.accept(selectedLocation);
        }

        closeWindow();
    }

    @FXML
    void handleViewOnMap() {
        if (selectedLocation.isEmpty()) {
            showError("Erreur", "Veuillez d'abord sélectionner un lieu.");
            return;
        }

        try {
            // Encoder le lieu pour l'URL
            String encodedLocation = URLEncoder.encode(selectedLocation, StandardCharsets.UTF_8.toString());

            // Construire l'URL Google Maps
            String googleMapsUrl = "https://www.google.com/maps/search/?api=1&query=" + encodedLocation;

            // Ouvrir l'URL dans le navigateur par défaut
            Desktop.getDesktop().browse(new URI(googleMapsUrl));
        } catch (Exception e) {
            showError("Erreur", "Impossible d'ouvrir Google Maps : " + e.getMessage());
        }
    }

    @FXML
    void handleCancel() {
        closeWindow();
    }

    @FXML
    void handleMinimize() {
        Stage stage = (Stage) tfSearchLocation.getScene().getWindow();
        stage.setIconified(true);
    }

    @FXML
    void handleMaximize() {
        Stage stage = (Stage) tfSearchLocation.getScene().getWindow();
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
        closeWindow();
    }

    private void closeWindow() {
        Stage stage = (Stage) tfSearchLocation.getScene().getWindow();
        stage.close();
    }

    private void showError(String title, String message) {
        Alert alert = new Alert(AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    /**
     * Définit le callback qui sera appelé lorsqu'un lieu est sélectionné
     * @param callback La fonction qui recevra le lieu sélectionné
     */
    public void setLocationCallback(Consumer<String> callback) {
        this.locationCallback = callback;
    }

    /**
     * Préremplit le champ de recherche avec un lieu
     * @param location Le lieu à rechercher
     */
    public void setInitialLocation(String location) {
        if (location != null && !location.isEmpty()) {
            tfSearchLocation.setText(location);
            // Déclencher automatiquement la recherche
            handleSearch();

            // Sélectionner le lieu dans la ComboBox si possible
            for (String predefinedLocation : predefinedLocations) {
                if (predefinedLocation.equalsIgnoreCase(location)) {
                    cbPredefinedLocations.setValue(predefinedLocation);
                    break;
                }
            }
        }
    }
}
