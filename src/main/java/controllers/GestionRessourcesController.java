package controllers;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.ListView;

public class GestionRessourcesController {

    @FXML
    private ListView<String> listViewRessources;

    @FXML
    public void initialize() {
        // Exemple de données simulées
        ObservableList<String> ressources = FXCollections.observableArrayList(
                "Salle de réunion A", "Projecteur B", "Ordinateur C"
        );

        listViewRessources.setItems(ressources);
    }
}
