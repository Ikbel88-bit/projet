package controllers;

import entities.Reclamation;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import services.ServiceReclamation;
import java.sql.SQLException;
import java.util.List;

public class MesReclamationsController {
    @FXML
    private TableView<Reclamation> tableView;
    @FXML
    private TableColumn<Reclamation, String> colReclamation;
    @FXML
    private TableColumn<Reclamation, String> colReponse;
    @FXML
    private Button btnFermer;

    private int userId;

    public void setUserId(int userId) {
        this.userId = userId;
        chargerReclamations();
    }

    private void chargerReclamations() {
        try {
            ServiceReclamation service = new ServiceReclamation();
            List<Reclamation> list = service.recupererParUtilisateur(userId);
            ObservableList<Reclamation> data = FXCollections.observableArrayList(list);
            colReclamation.setCellValueFactory(new PropertyValueFactory<>("description"));
            colReponse.setCellValueFactory(new PropertyValueFactory<>("reponse"));
            tableView.setItems(data);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @FXML
    void fermer() {
        Stage stage = (Stage) btnFermer.getScene().getWindow();
        stage.close();
    }
} 