package controllers;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import entities.User;
import entities.Reclamation;
import services.ServiceUser;
import services.ServiceReclamation;
import java.sql.SQLException;
import java.io.IOException;
import javafx.stage.Stage;

public class AfficherUserController {

    @FXML
    private TableView<User> tableView;

    @FXML
    private TableColumn<User, Integer> id;

    @FXML
    private TableColumn<User, String> nom;

    @FXML
    private TableColumn<User, String> prenom;

    @FXML
    private TableColumn<User, String> email;

    @FXML
    private TableColumn<User, String> telephone;

    @FXML
    private TableColumn<User, String> role;

    @FXML
    private Button btnRetour;

    @FXML
    private Button btnRetourPrecedent;

    private ObservableList<User> users;
    private ServiceUser serviceUser;
    private ServiceReclamation serviceReclamation;

    @FXML
    public void initialize() {
        serviceUser = new ServiceUser();
        serviceReclamation = new ServiceReclamation();
        try {
            users = FXCollections.observableArrayList(serviceUser.recuperer());

            // Configurer les colonnes
            id.setCellValueFactory(new PropertyValueFactory<>("id"));
            nom.setCellValueFactory(new PropertyValueFactory<>("nom"));
            prenom.setCellValueFactory(new PropertyValueFactory<>("prenom"));
            email.setCellValueFactory(new PropertyValueFactory<>("email"));
            telephone.setCellValueFactory(new PropertyValueFactory<>("telephone"));
            role.setCellValueFactory(new PropertyValueFactory<>("role"));

            // Rendre la colonne ID invisible
            id.setVisible(false);

            // Ajouter la liste d'utilisateurs à la table
            tableView.setItems(users);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @FXML
    public void ajouter() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/AjouterEmploye.fxml"));
            Parent root = loader.load();
            Scene scene = new Scene(root);
            Stage stage = (Stage) tableView.getScene().getWindow();
            stage.setScene(scene);
            stage.setTitle("Ajouter un Employé");
            stage.show();
        } catch (IOException e) {
            System.err.println("Erreur lors du chargement de AjouterEmploye.fxml: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML
    public void voirReclamations() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/AfficherReclamation.fxml"));
            Parent root = loader.load();
            Scene scene = new Scene(root);
            Stage stage = (Stage) tableView.getScene().getWindow();
            stage.setScene(scene);
            stage.setTitle("Gestion des Réclamations");
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    public void retourLogin() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/Login.fxml"));
            Parent root = loader.load();
            Scene scene = new Scene(root);
            Stage stage = (Stage) btnRetour.getScene().getWindow();
            stage.setScene(scene);
            stage.setTitle("Login");
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    public void retourPrecedent() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/Login.fxml"));
            Parent root = loader.load();
            Scene scene = new Scene(root);
            Stage stage = (Stage) btnRetourPrecedent.getScene().getWindow();
            stage.setScene(scene);
            stage.setTitle("Login");
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
