package controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import javafx.scene.control.Alert;
import javafx.scene.control.TextField;
import utils.MyDatabase;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.io.IOException;
import entities.Session;

public class MenuController {
    @FXML
    private TextField fxloginemail;
    @FXML
    private TextField fxpasswordlogin;

    /*
    @Deprecated
    public void ToOffres(javafx.event.ActionEvent actionEvent) {try {
            Parent root = FXMLLoader.load(getClass().getResource("/AfficherOffre.fxml"));
        Stage stage = new Stage();
        stage.setScene(new Scene(root));
        stage.setTitle("Gestion des Offres");
        stage.show();

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        }
    @Deprecated
    public void ToCondidature(javafx.event.ActionEvent actionEvent) {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/AfficherCondidature.fxml"));
            Stage stage = new Stage();
            stage.setScene(new Scene(root));
            stage.setTitle("Gestion des Condidatures");
            stage.show();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Deprecated
    public void retour(javafx.event.ActionEvent actionEvent) {
        Stage oldStage = (Stage) ((Node) actionEvent.getSource()).getScene().getWindow();
        oldStage.close();
    }


     */

    @FXML
    public void login(ActionEvent actionEvent) {
                String email = fxloginemail.getText();
                String password = fxpasswordlogin.getText();

                if (email.isEmpty() || password.isEmpty()) {
                    showAlert(Alert.AlertType.ERROR, "Veuillez remplir tous les champs !");
                    return;
                }

                String sql = "SELECT id,role FROM user WHERE email = ? AND password = ?";

                try (Connection conn = MyDatabase.getInstance().getCnx();
                     PreparedStatement pstmt = conn.prepareStatement(sql)) {

                    pstmt.setString(1, email);
                    pstmt.setString(2, password);

                    ResultSet rs = pstmt.executeQuery();

                    if (rs.next()) {
                        showAlert(Alert.AlertType.INFORMATION, "Connexion réussie !");
                        int idUser = rs.getInt("id");
                        Session.setIdUser(idUser);
                        String role = rs.getString("role"); // 🔥 Ici on récupère bien la valeur du rôle
                        if (role.equals("Candidat")) {
                            Parent root = FXMLLoader.load(getClass().getResource("/AfficherOffre.fxml"));
                            Stage stage = new Stage();
                            stage.setScene(new Scene(root));
                            stage.setTitle("Gestion des Offres");
                            stage.show();
                        } else if (role.equals("Employe")) {
                            Parent root = FXMLLoader.load(getClass().getResource("/GererOffre.fxml"));
                            Stage stage = new Stage();
                            stage.setScene(new Scene(root));
                            stage.setTitle("Gestion des Offres");
                            stage.show();
                        }
                    } else {
                        showAlert(Alert.AlertType.ERROR, "Email ou mot de passe incorrect !");
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                    showAlert(Alert.AlertType.ERROR, "Erreur lors de la connexion : " + e.getMessage());
                }
            }

            private void showAlert(Alert.AlertType alertType, String message) {
                Alert alert = new Alert(alertType);
                alert.setTitle("Information");
                alert.setHeaderText(null);
                alert.setContentText(message);
                alert.showAndWait();
            }
        }

