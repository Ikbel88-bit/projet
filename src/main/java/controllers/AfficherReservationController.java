package controllers;

import entities.Reservation;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import services.ServiceReservation;

public class AfficherReservationController {

    @FXML
    private TableView<Reservation> tableReservations;

    @FXML
    private TableColumn<Reservation, Integer> colId;
    @FXML
    private TableColumn<Reservation, String> colResource;
    @FXML
    private TableColumn<Reservation, String> colDateDebut;
    @FXML
    private TableColumn<Reservation, String> colDateFin;
    @FXML
    private TableColumn<Reservation, String> colStatus;

    private final ServiceReservation service = new ServiceReservation();

    @FXML
    public void initialize() {
        colId.setCellValueFactory(new PropertyValueFactory<>("id"));
        colResource.setCellValueFactory(new PropertyValueFactory<>("resource"));
        colDateDebut.setCellValueFactory(new PropertyValueFactory<>("dateDebut"));
        colDateFin.setCellValueFactory(new PropertyValueFactory<>("dateFin"));
        colStatus.setCellValueFactory(new PropertyValueFactory<>("status"));

        loadReservations();
    }

    private void loadReservations() {
        ObservableList<Reservation> list = FXCollections.observableArrayList(service.recuperer());
        tableReservations.setItems(list);
    }

    @FXML
    public void handleRetour(ActionEvent actionEvent) {
        try {
            javafx.fxml.FXMLLoader loader = new javafx.fxml.FXMLLoader(getClass().getResource("/path/to/menu.fxml"));
            javafx.scene.Parent root = loader.load();
            javafx.scene.Scene scene = new javafx.scene.Scene(root);
            javafx.stage.Stage stage = (javafx.stage.Stage) ((javafx.scene.Node) actionEvent.getSource()).getScene().getWindow();
            stage.setScene(scene);
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
