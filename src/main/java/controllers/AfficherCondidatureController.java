package controllers;
import javafx.stage.FileChooser;
import java.io.File;
import java.io.OutputStream;
import java.io.FileInputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.scene.layout.HBox;
import javafx.geometry.Insets;

import java.io.IOException;
import java.util.List;

import entities.Offre;
import entities.Condidature;
import entities.Utilisateur;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import services.ServiceCondidature;
import java.sql.SQLException;
import javafx.scene.control.Alert;

import javax.swing.*;

public class AfficherCondidatureController {
    public JEditorPane urlCvField;
    @FXML
    private Button btnChoisirCv;
    @FXML
    private Label labelNomFichierCv;

    private File fichierCv;

    @FXML
    private Label labelTitre;
    @FXML
    private Label labelEntreprise;
    @FXML
    public TextArea lettreMotivationField;

    private Offre offre;
    int idUser = 1;

    public void setOffre(Offre offre) {
        this.offre = offre;
        labelTitre.setText(String.valueOf(offre.getIdOffre()));
        labelEntreprise.setText(offre.getNomEntreprise());
    }

    @FXML
    private void envoyerCandidature() {
        String lettre = lettreMotivationField.getText();
        if (fichierCv == null) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Erreur");
            alert.setHeaderText(null);
            alert.setContentText("Veuillez sélectionner un CV.");
            alert.showAndWait();
            return;
        }

        String urlCv = uploadCv(fichierCv);
        if (urlCv == null) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Erreur");
            alert.setHeaderText(null);
            alert.setContentText("Erreur lors de l'upload du CV.");
            alert.showAndWait();
            return;
        }

        String statut = "en attente";
        if (lettre.isEmpty() || urlCv.isEmpty()) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Erreur");
            alert.setHeaderText(null);
            alert.setContentText("Veuillez remplir tous les champs.");
            alert.showAndWait();
            return;
        }
        Condidature condidature = new Condidature(offre.getIdOffre(), idUser, urlCv, lettre, statut);
        try {
            ServiceCondidature service = new ServiceCondidature();
            service.ajouter(condidature);
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Succès");
            alert.setHeaderText(null);
            alert.setContentText("Votre candidature a été envoyée avec succès.");
            alert.showAndWait();
        } catch (SQLException e) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Erreur SQL");
            alert.setHeaderText(null);
            alert.setContentText("Erreur lors de l'enregistrement : " + e.getMessage());
            alert.showAndWait();
        }
        Stage stage = (Stage) lettreMotivationField.getScene().getWindow();
        stage.close();
    }

    public void initialize() {

    }

    @FXML
    public void choisirFichierCv(ActionEvent actionEvent) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Sélectionner un CV");
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Fichiers PDF", "*.pdf"),
                new FileChooser.ExtensionFilter("Tous les fichiers", "*.*")
        );
        File selectedFile = fileChooser.showOpenDialog(btnChoisirCv.getScene().getWindow());
        if (selectedFile != null) {
            fichierCv = selectedFile;
            labelNomFichierCv.setText(fichierCv.getName());
        }
    }

    private String uploadCv(File file) {
        String boundary = "===" + System.currentTimeMillis() + "===";
        String LINE_FEED = "\r\n";
        String requestURL = "http://localhost/api/upload_cv.php";

        try {
            URL url = new URL(requestURL);
            HttpURLConnection httpConn = (HttpURLConnection) url.openConnection();
            httpConn.setUseCaches(false);
            httpConn.setDoOutput(true);
            httpConn.setDoInput(true);
            httpConn.setRequestMethod("POST");
            httpConn.setRequestProperty("Content-Type", "multipart/form-data; boundary=" + boundary);

            OutputStream outputStream = httpConn.getOutputStream();

            StringBuilder headerBuilder = new StringBuilder();
            headerBuilder.append("--").append(boundary).append(LINE_FEED);
            headerBuilder.append("Content-Disposition: form-data; name=\"cv\"; filename=\"")
                    .append(file.getName()).append("\"").append(LINE_FEED);
            headerBuilder.append("Content-Type: application/octet-stream").append(LINE_FEED);
            headerBuilder.append(LINE_FEED);
            outputStream.write(headerBuilder.toString().getBytes());

            // Écrire le fichier
            FileInputStream inputStream = new FileInputStream(file);
            byte[] buffer = new byte[4096];
            int bytesRead;
            while ((bytesRead = inputStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, bytesRead);
            }
            outputStream.write(LINE_FEED.getBytes());
            outputStream.flush();
            inputStream.close();

            // Fin du multipart
            String footer = "--" + boundary + "--" + LINE_FEED;
            outputStream.write(footer.getBytes());
            outputStream.flush();
            outputStream.close();

            // Lire la réponse
            int responseCode = httpConn.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) {
                java.util.Scanner s = new java.util.Scanner(httpConn.getInputStream()).useDelimiter("\\A");
                String response = s.hasNext() ? s.next() : "";
                s.close();
                System.out.println("Réponse serveur : " + response);
                // Ici on peut parser le JSON pour récupérer le chemin
                if (response.contains("\"success\":true")) {
                    // Extraire path (ici simple parsing à adapter si besoin)
                    int pathIndex = response.indexOf("\"path\":\"") + 8;
                    int endIndex = response.indexOf("\"", pathIndex);
                    String path = response.substring(pathIndex, endIndex);
                    return path;
                }
            }
            httpConn.disconnect();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}


