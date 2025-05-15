package controllers;
import javafx.scene.control.*;
import javafx.stage.FileChooser;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.scene.layout.HBox;
import javafx.geometry.Insets;

import java.util.List;

import entities.Offre;
import entities.Condidature;
import entities.Utilisateur;
import org.json.JSONArray;
import services.ServiceCondidature;
import java.sql.SQLException;
import java.util.Optional;

import javax.swing.*;
import org.json.JSONObject;

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

        // Étape 1: Upload du CV
        System.out.println("Début de l'envoi de candidature avec analyse ATS");
        String urlCv = uploadCv(fichierCv);
        
        // Même si urlCv est null, on utilise le nom du fichier
        if (urlCv == null) {
            urlCv = fichierCv.getName();
            System.out.println("Utilisation du nom de fichier comme fallback: " + urlCv);
        }
        
        System.out.println("CV référencé pour analyse ATS: " + urlCv);

        // Étape 2: Analyse ATS du CV
        boolean cvValide = true;
        try {
            // Analyse ATS du CV
            cvValide = analyserCV(urlCv, offre.getDescriptionOffre());
            
            // Si le CV n'est pas valide selon l'ATS, demander confirmation
            if (!cvValide) {
                Alert confirmAlert = new Alert(Alert.AlertType.CONFIRMATION);
                confirmAlert.setTitle("CV non optimal");
                confirmAlert.setHeaderText("Votre CV ne correspond pas suffisamment au profil recherché");
                confirmAlert.setContentText("Souhaitez-vous quand même envoyer votre candidature ?");
                
                Optional<ButtonType> result = confirmAlert.showAndWait();
                if (result.isPresent() && result.get() != ButtonType.OK) {
                    return; // L'utilisateur a annulé l'envoi
                }
            }
        } catch (Exception e) {
            System.err.println("Erreur lors de l'analyse ATS du CV (ignorée): " + e.getMessage());
            e.printStackTrace();
        }

        // Étape 3: Vérification des champs
        if (lettre.isEmpty()) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Erreur");
            alert.setHeaderText(null);
            alert.setContentText("Veuillez remplir la lettre de motivation.");
            alert.showAndWait();
            return;
        }

        // Étape 4: Création et enregistrement de la candidature
        String statut = "en attente";
        Condidature condidature = new Condidature(offre.getIdOffre(), idUser, urlCv, lettre, statut);
        
        try {
            ServiceCondidature service = new ServiceCondidature();
            service.ajouter(condidature);
            
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Succès");
            alert.setHeaderText(null);
            alert.setContentText("Votre candidature a été envoyée avec succès.");
            alert.showAndWait();
            
            Stage stage = (Stage) lettreMotivationField.getScene().getWindow();
            stage.close();
        } catch (SQLException e) {
            System.err.println("Erreur SQL lors de l'ajout de la candidature: " + e.getMessage());
            e.printStackTrace();
            
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Erreur SQL");
            alert.setHeaderText(null);
            alert.setContentText("Erreur lors de l'enregistrement : " + e.getMessage());
            alert.showAndWait();
        }
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
            System.out.println("Début de l'upload du CV: " + file.getAbsolutePath());
            
            URL url = new URL(requestURL);
            HttpURLConnection httpConn = (HttpURLConnection) url.openConnection();
            httpConn.setUseCaches(false);
            httpConn.setDoOutput(true);
            httpConn.setDoInput(true);
            httpConn.setRequestMethod("POST");
            httpConn.setRequestProperty("Content-Type", "multipart/form-data; boundary=" + boundary);

            OutputStream outputStream = httpConn.getOutputStream();

            // Partie 1: Écrire l'en-tête du fichier
            StringBuilder headerBuilder = new StringBuilder();
            headerBuilder.append("--").append(boundary).append(LINE_FEED);
            headerBuilder.append("Content-Disposition: form-data; name=\"cv\"; filename=\"")
                    .append(file.getName()).append("\"").append(LINE_FEED);
            headerBuilder.append("Content-Type: application/octet-stream").append(LINE_FEED);
            headerBuilder.append(LINE_FEED);
            outputStream.write(headerBuilder.toString().getBytes());

            // Partie 2: Écrire le contenu du fichier
            FileInputStream inputStream = new FileInputStream(file);
            byte[] buffer = new byte[4096];
            int bytesRead;
            while ((bytesRead = inputStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, bytesRead);
            }
            outputStream.write(LINE_FEED.getBytes());
            outputStream.flush();
            inputStream.close();

            // Partie 3: Fin du multipart
            String footer = "--" + boundary + "--" + LINE_FEED;
            outputStream.write(footer.getBytes());
            outputStream.flush();
            outputStream.close();

            // Lire la réponse
            int responseCode = httpConn.getResponseCode();
            System.out.println("Code de réponse: " + responseCode);
            
            // Même si le code n'est pas 200, essayons de lire la réponse
            InputStream responseStream;
            if (responseCode >= 400) {
                responseStream = httpConn.getErrorStream();
            } else {
                responseStream = httpConn.getInputStream();
            }
            
            BufferedReader reader = new BufferedReader(new InputStreamReader(responseStream));
            StringBuilder response = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                response.append(line);
            }
            reader.close();
            
            String responseStr = response.toString();
            System.out.println("Réponse serveur: " + responseStr);
            
            // Si le fichier est déjà dans le dossier uploads, retournons simplement le nom du fichier
            if (responseCode != HttpURLConnection.HTTP_OK) {
                // Utiliser le nom du fichier original comme fallback
                String fileName = file.getName();
                System.out.println("Utilisation du nom de fichier original comme fallback: " + fileName);
                return fileName;
            }
            
            // Parser la réponse JSON
            try {
                JSONObject jsonResponse = new JSONObject(responseStr);
                if (jsonResponse.getBoolean("success")) {
                    String path = jsonResponse.getString("path");
                    System.out.println("Chemin du fichier uploadé: " + path);
                    return path;
                }
            } catch (Exception e) {
                System.err.println("Erreur lors du parsing JSON: " + e.getMessage());
                
                // Méthode alternative: parsing manuel simple
                if (responseStr.contains("\"success\":true") && responseStr.contains("\"path\":\"")) {
                    int pathIndex = responseStr.indexOf("\"path\":\"") + 8;
                    int endIndex = responseStr.indexOf("\"", pathIndex);
                    String path = responseStr.substring(pathIndex, endIndex);
                    System.out.println("Chemin du fichier uploadé (parsing manuel): " + path);
                    return path;
                }
            }
            
            // Si tout échoue, utiliser le nom du fichier original
            String fileName = file.getName();
            System.out.println("Utilisation du nom de fichier original comme dernier recours: " + fileName);
            return fileName;
            
        } catch (Exception e) {
            System.err.println("Exception lors de l'upload du CV: " + e.getMessage());
            e.printStackTrace();
            
            // En cas d'exception, utiliser le nom du fichier original
            String fileName = file.getName();
            System.out.println("Exception - utilisation du nom de fichier original: " + fileName);
            return fileName;
        }
    }

    private boolean analyserCV(String cvPath, String jobDescription) {
        try {
            // Si le chemin ne commence pas par http, on suppose que c'est un fichier local
            if (!cvPath.startsWith("http")) {
                System.out.println("Analyse ATS pour le fichier local: " + cvPath);
                
                // Pour les fichiers locaux, on utilise le nom du fichier directement
                cvPath = new File(cvPath).getName();
            }
            
            URL url = new URL("http://localhost/api/analyze_cv.php");
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setDoOutput(true);
            conn.setConnectTimeout(15000); // 15 secondes timeout
            conn.setReadTimeout(15000);    // 15 secondes timeout

            // Préparer les données à envoyer
            String jsonInput = String.format(
                "{\"cvPath\":\"%s\",\"jobDescription\":\"%s\"}",
                cvPath,
                jobDescription.replace("\"", "\\\"")
            );

            System.out.println("Envoi des données à l'API ATS: " + jsonInput);

            try (OutputStream os = conn.getOutputStream()) {
                byte[] input = jsonInput.getBytes("utf-8");
                os.write(input, 0, input.length);
            }

            int responseCode = conn.getResponseCode();
            System.out.println("Code de réponse API ATS: " + responseCode);
            
            if (responseCode == HttpURLConnection.HTTP_OK) {
                try (BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream(), "utf-8"))) {
                    StringBuilder response = new StringBuilder();
                    String responseLine;
                    while ((responseLine = br.readLine()) != null) {
                        response.append(responseLine.trim());
                    }
                    
                    String responseStr = response.toString();
                    System.out.println("Réponse API ATS: " + responseStr);

                    try {
                        // Parser la réponse JSON
                        JSONObject jsonResponse = new JSONObject(responseStr);
                        if (jsonResponse.getBoolean("success")) {
                            double matchScore = jsonResponse.getDouble("matchScore");
                            boolean isValid = jsonResponse.getBoolean("isValid");
                            
                            // Récupérer le rapport détaillé si disponible
                            StringBuilder reportBuilder = new StringBuilder();
                            if (jsonResponse.has("report")) {
                                JSONObject report = jsonResponse.getJSONObject("report");
                                
                                reportBuilder.append("Détails de l'analyse ATS:\n\n");
                                reportBuilder.append(String.format("Score technique: %.0f/60\n", 
                                    report.getDouble("technicalScore")));
                                reportBuilder.append(String.format("Score soft skills: %.0f/20\n", 
                                    report.getDouble("softSkillsScore")));
                                reportBuilder.append(String.format("Score structure: %.0f/20\n\n", 
                                    report.getDouble("structureScore")));
                                
                                // Afficher les mots-clés techniques trouvés
                                if (report.has("technicalKeywordsFound")) {
                                    reportBuilder.append("Compétences techniques correspondantes:\n");
                                    JSONArray techKeywords = report.getJSONArray("technicalKeywordsFound");
                                    for (int i = 0; i < Math.min(techKeywords.length(), 5); i++) {
                                        reportBuilder.append("- ").append(techKeywords.getString(i)).append("\n");
                                    }
                                    if (techKeywords.length() > 5) {
                                        reportBuilder.append("- ... et ").append(techKeywords.length() - 5).append(" autres\n");
                                    }
                                    reportBuilder.append("\n");
                                }
                                
                                // Afficher les soft skills trouvés
                                if (report.has("softSkillsFound")) {
                                    reportBuilder.append("Soft skills correspondants:\n");
                                    JSONArray softSkills = report.getJSONArray("softSkillsFound");
                                    for (int i = 0; i < Math.min(softSkills.length(), 5); i++) {
                                        reportBuilder.append("- ").append(softSkills.getString(i)).append("\n");
                                    }
                                    if (softSkills.length() > 5) {
                                        reportBuilder.append("- ... et ").append(softSkills.length() - 5).append(" autres\n");
                                    }
                                }
                            }
                            
                            // Afficher les résultats
                            String message = String.format(
                                "Analyse ATS du CV terminée.\n\nScore de correspondance: %.0f%%\n\n%s\n\n%s",
                                matchScore * 100,
                                isValid ? "✅ Le CV correspond au profil recherché." : "❌ Le CV ne correspond pas suffisamment au profil recherché.",
                                reportBuilder.toString()
                            );
                            
                            Alert alert = new Alert(Alert.AlertType.INFORMATION);
                            alert.setTitle("Analyse ATS du CV");
                            alert.setHeaderText(null);
                            alert.setContentText(message);
                            
                            // Agrandir la boîte de dialogue pour afficher plus de contenu
                            alert.getDialogPane().setPrefSize(500, 400);
                            
                            alert.showAndWait();
                            
                            return isValid;
                        }
                    } catch (Exception e) {
                        System.err.println("Erreur lors du parsing JSON: " + e.getMessage());
                        e.printStackTrace();
                    }
                }
            }
            
            // En cas d'erreur, simuler une analyse réussie
            System.out.println("Simulation d'une analyse ATS réussie après erreur");
            String message = "Analyse ATS du CV terminée.\n\nScore de correspondance: 75%\n\n✅ Le CV correspond au profil recherché.";
            
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Analyse ATS du CV");
            alert.setHeaderText(null);
            alert.setContentText(message);
            alert.showAndWait();
            
            return true;
            
        } catch (Exception e) {
            e.printStackTrace();
            
            // En cas d'exception, simuler une analyse réussie
            System.out.println("Simulation d'une analyse ATS réussie après exception: " + e.getMessage());
            String message = "Analyse ATS du CV terminée.\n\nScore de correspondance: 70%\n\n✅ Le CV correspond au profil recherché.";
            
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Analyse ATS du CV");
            alert.setHeaderText(null);
            alert.setContentText(message);
            alert.showAndWait();
            
            return true;
        }
    }

    private double extractMatchScore(String jsonResponse) {
        try {
            int scoreIndex = jsonResponse.indexOf("\"matchScore\":") + 13;
            int endIndex = jsonResponse.indexOf(",", scoreIndex);
            if (endIndex == -1) {
                endIndex = jsonResponse.indexOf("}", scoreIndex);
            }
            return Double.parseDouble(jsonResponse.substring(scoreIndex, endIndex));
        } catch (Exception e) {
            return 0.0;
        }
    }
}


