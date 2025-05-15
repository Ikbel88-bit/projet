package controllers;

import entities.Session;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.layout.HBox;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonType;
import javafx.scene.layout.Priority;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.application.Platform;

import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Optional;
import java.io.*;
import java.util.stream.Collectors;
import javafx.scene.paint.Color;
import services.ServiceOffre;
import services.ServiceCondidature;
import entities.Offre;
import entities.Condidature;
import java.sql.SQLException;
import java.util.List;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.stage.Window;
import javafx.stage.WindowEvent;

public class GererOffreController {
    @FXML
    private TextField searchField;
    @FXML
    private ComboBox<String> filterContrat;
    @FXML
    private VBox LesOffres;
    @FXML
    private TextField inputTitre;
    @FXML
    private TextArea inputDescription;
    @FXML
    private TextField inputEntreprise;
    @FXML
    private ComboBox<String> comboContrat;
    @FXML
    private Button btnAjouter;
    @FXML
    private Button btnRechercher;
    @FXML
    private Button btnModifierDetail;
    @FXML
    private Button btnSupprimerDetail;
    @FXML
    private Button btnVoirCandidatures;
    @FXML
    private VBox candidaturesBox;
    @FXML
    private Button btnRetour;

    @javafx.fxml.FXML
    private ServiceOffre serviceOffre;
    @javafx.fxml.FXML
    private ServiceCondidature serviceCondidature;
    private ObservableList<Offre> offres = FXCollections.observableArrayList();
    private ObservableList<Offre> offresFiltrees = FXCollections.observableArrayList();
    private Offre offreSelectionnee = null;
    @FXML
    public void initialize() {
        serviceOffre = new ServiceOffre();
        serviceCondidature = new ServiceCondidature();

        // Initialisation des ComboBox
        comboContrat.setItems(FXCollections.observableArrayList("CDI", "CDD", "Stage", "Freelance"));
        filterContrat.setItems(FXCollections.observableArrayList("Tous", "CDI", "CDD", "Stage", "Freelance"));
        filterContrat.setValue("Tous");
        
        // Configuration des boutons
        btnAjouter.setOnAction(e -> ajouterOffre());
        btnRechercher.setOnAction(e -> filtrerOffres());
        btnModifierDetail.setOnAction(e -> modifierOffre());
        btnSupprimerDetail.setOnAction(e -> supprimerOffre());
        btnVoirCandidatures.setOnAction(e -> ouvrirCandidatures());
        
        // Configuration de la recherche
        searchField.setOnAction(e -> filtrerOffres());
        filterContrat.valueProperty().addListener((obs, oldVal, newVal) -> filtrerOffres());
        
        // Désactiver les boutons au démarrage
        btnModifierDetail.setDisable(true);
        btnSupprimerDetail.setDisable(true);
        btnVoirCandidatures.setDisable(true);

        // Configuration du contrôle de saisie
        configurerControleSaisie();

        // Charger les offres depuis la base de données
        chargerOffres();

        // Ajouter un titre personnalisé avec l'ID du responsable
        Label titreLabel = new Label("GESTION DES OFFRES - ID: " + Session.getIdUser());
        titreLabel.setStyle("-fx-font-size: 24px; -fx-font-weight: bold; -fx-text-fill: #2196F3;");
        LesOffres.getChildren().add(0, titreLabel);
    }

    private boolean verifierDescriptionAPI() {
        try {
            String description = inputDescription.getText().trim();
            if (description.isEmpty()) {
                return false;
            }

            URL url = new URL("http://localhost/api/verifier_offre.php");
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setDoOutput(true);

            // Préparer la description à envoyer en JSON
            String jsonInput = "{\"description\":\"" + description + "\"}";

            try (OutputStream os = conn.getOutputStream()) {
                byte[] input = jsonInput.getBytes("utf-8");
                os.write(input, 0, input.length);
            }

            int responseCode = conn.getResponseCode();
            if (responseCode != HttpURLConnection.HTTP_OK) {
                try (BufferedReader br = new BufferedReader(new InputStreamReader(conn.getErrorStream(), "utf-8"))) {
                    StringBuilder errorResponse = new StringBuilder();
                    String responseLine;
                    while ((responseLine = br.readLine()) != null) {
                        errorResponse.append(responseLine.trim());
                    }
                    System.err.println("Erreur API: " + errorResponse.toString());
                }
                return false;
            }

            // Lire la réponse de l'API
            try (BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream(), "utf-8"))) {
                StringBuilder response = new StringBuilder();
                String responseLine;
                while ((responseLine = br.readLine()) != null) {
                    response.append(responseLine.trim());
                }

                String responseString = response.toString();
                if (responseString.contains("\"valid\":true")) {
                    return true;
                } else {
                    // Afficher le message d'erreur de l'API
                    if (responseString.contains("\"message\":")) {
                        String message = responseString.split("\"message\":\"")[1].split("\"")[0];
                        afficherErreur("Description invalide", message);
                    }
                    return false;
                }
            }
        } catch (IOException e) {
            System.err.println("Erreur lors de la vérification de la description: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    private void configurerControleSaisie() {
        // Contrôle du titre
        inputTitre.textProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal.length() > 100) {
                inputTitre.setText(oldVal);
                afficherErreur("Erreur de saisie", "Le titre ne peut pas dépasser 100 caractères");
            }
        });

        // Contrôle de la description
        inputDescription.textProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal.length() > 500) {
                inputDescription.setText(oldVal);
                afficherErreur("Erreur de saisie", "La description ne peut pas dépasser 500 caractères");
            }
        });

        // Contrôle de l'entreprise
        inputEntreprise.textProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal.length() > 50) {
                inputEntreprise.setText(oldVal);
                afficherErreur("Erreur de saisie", "Le nom de l'entreprise ne peut pas dépasser 50 caractères");
            }
        });
    }
    private void filtrerOffres() {
        String recherche = searchField.getText().toLowerCase();
        String filtreContrat = filterContrat.getValue();
        int idResponsable = Session.getIdUser();

        offresFiltrees.setAll(offres.stream()
            .filter(offre -> 
                offre.getIdResponsable() == idResponsable && // Filtrer par ID responsable
                (filtreContrat.equals("Tous") || offre.getTypeContrat().equals(filtreContrat)) &&
                (offre.getTitreOffre().toLowerCase().contains(recherche) ||
                 offre.getDescriptionOffre().toLowerCase().contains(recherche) ||
                 offre.getNomEntreprise().toLowerCase().contains(recherche))
            )
            .collect(Collectors.toList()));

        rafraichirListeOffres();
    }

    private void chargerOffres() {
        try {
            List<Offre> offresList = serviceOffre.recuperer();
            // Filtrer les offres pour n'afficher que celles du responsable connecté
            offresList = offresList.stream()
                .filter(offre -> offre.getIdResponsable() == Session.getIdUser())
                .collect(Collectors.toList());
            offres.setAll(offresList);
            filtrerOffres();
        } catch (SQLException e) {
            afficherErreur("Erreur de chargement", "Impossible de charger les offres");
        }
    }

    private void ouvrirCandidatures() {
        if (offreSelectionnee != null) {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/VoirCandidatures.fxml"));
                Parent root = loader.load();
                
                VoirCandidaturesController controller = loader.getController();
                controller.setIdOffre(offreSelectionnee.getIdOffre());
                
                Stage stage = new Stage();
                stage.setTitle("Candidatures - " + offreSelectionnee.getTitreOffre());
                stage.setScene(new Scene(root));
                stage.show();
            } catch (IOException e) {
                afficherErreur("Erreur", "Impossible d'ouvrir la fenêtre des candidatures");
            }
        }
    }

    private void ajouterOffre() {
        System.out.println("Début de la méthode ajouterOffre");
        try {
            if (validerFormulaire()) {
                System.out.println("Formulaire valide");
                if (verifierDescriptionAPI()) {
                    System.out.println("Description API valide");
                    Offre nouvelleOffre = new Offre(
                            Session.getIdUser(),
                            inputTitre.getText().trim(),
                            inputDescription.getText().trim(),
                            comboContrat.getValue(),
                            inputEntreprise.getText().trim()
                    );
                    
                    try {
                        System.out.println("Tentative d'ajout de l'offre...");
                        serviceOffre.ajouter(nouvelleOffre);
                        System.out.println("Offre ajoutée avec succès");
                        
                        Platform.runLater(() -> {
                            Alert alert = new Alert(AlertType.INFORMATION);
                            alert.setTitle("Succès");
                            alert.setHeaderText(null);
                            alert.setContentText("L'offre a été ajoutée avec succès !");
                            alert.showAndWait();
                            
                            chargerOffres();
                            reinitialiserFormulaire();
                        });
                    } catch (SQLException e) {
                        System.err.println("Erreur SQL lors de l'ajout: " + e.getMessage());
                        e.printStackTrace();
                        Platform.runLater(() -> {
                            afficherErreur("Erreur SQL", "Une erreur est survenue lors de l'ajout de l'offre : " + e.getMessage());
                        });
                    }
                } else {
                    System.out.println("Description API invalide");
                    Platform.runLater(() -> {
                        afficherErreur("Erreur", "La description n'est pas valide selon les critères requis");
                    });
                }
            } else {
                System.out.println("Formulaire invalide");
                Platform.runLater(() -> {
                    afficherErreur("Erreur", "Veuillez remplir tous les champs correctement");
                });
            }
        } catch (Exception e) {
            System.err.println("Erreur inattendue: " + e.getMessage());
            e.printStackTrace();
            Platform.runLater(() -> {
                afficherErreur("Erreur", "Une erreur inattendue est survenue : " + e.getMessage());
            });
        }
    }

    private void modifierOffre() {
        if (offreSelectionnee != null && validerFormulaire()) {
            try {
                offreSelectionnee.setTitreOffre(inputTitre.getText().trim());
                offreSelectionnee.setDescriptionOffre(inputDescription.getText().trim());
                offreSelectionnee.setNomEntreprise(inputEntreprise.getText().trim());
                offreSelectionnee.setTypeContrat(comboContrat.getValue());
                
                serviceOffre.modifier(offreSelectionnee);
                chargerOffres();
                reinitialiserFormulaire();
            } catch (SQLException e) {
                afficherErreur("Erreur", "Impossible de modifier l'offre");
            }
        }
    }

    private void supprimerOffre() {
        if (offreSelectionnee != null) {
            Alert alert = new Alert(AlertType.CONFIRMATION);
            alert.setTitle("Confirmation de suppression");
            alert.setHeaderText("Supprimer l'offre");
            alert.setContentText("Êtes-vous sûr de vouloir supprimer cette offre ?");

            Optional<ButtonType> result = alert.showAndWait();
            if (result.isPresent() && result.get() == ButtonType.OK) {
                try {
                    serviceOffre.supprimer(offreSelectionnee);
                    chargerOffres();
                    reinitialiserFormulaire();
                } catch (SQLException e) {
                    afficherErreur("Erreur", "Impossible de supprimer l'offre");
                }
            }
        }
    }

    private boolean validerFormulaire() {
        // Validation du titre
        if (inputTitre.getText().trim().isEmpty()) {
            afficherErreur("Erreur de validation", "Le titre est obligatoire");
            inputTitre.requestFocus();
            return false;
        }

        // Validation de la description
        if (inputDescription.getText().trim().isEmpty()) {
            afficherErreur("Erreur de validation", "La description est obligatoire");
            inputDescription.requestFocus();
            return false;
        }

        // Validation de l'entreprise
        if (inputEntreprise.getText().trim().isEmpty()) {
            afficherErreur("Erreur de validation", "Le nom de l'entreprise est obligatoire");
            inputEntreprise.requestFocus();
            return false;
        }

        // Validation du type de contrat
        if (comboContrat.getValue() == null) {
            afficherErreur("Erreur de validation", "Le type de contrat est obligatoire");
            comboContrat.requestFocus();
            return false;
        }

        return true;
    }

    private void reinitialiserFormulaire() {
        inputTitre.clear();
        inputDescription.clear();
        inputEntreprise.clear();
        comboContrat.setValue(null);
        offreSelectionnee = null;
        btnModifierDetail.setDisable(true);
        btnSupprimerDetail.setDisable(true);
        btnVoirCandidatures.setDisable(true);
        
        // Vérifier si candidaturesBox n'est pas null avant de le vider
        if (candidaturesBox != null) {
            candidaturesBox.getChildren().clear();
        }
    }

    private void rafraichirListeOffres() {
        LesOffres.getChildren().clear();
        for (Offre offre : offresFiltrees) {
            VBox offreBox = creerOffreBox(offre);
            LesOffres.getChildren().add(offreBox);
        }
    }

    private VBox creerOffreBox(Offre offre) {
        VBox box = new VBox(10);
        box.setStyle("-fx-background-color: white; -fx-padding: 15; -fx-background-radius: 10; -fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.1), 10, 0, 0, 10);");
        
        // Logo de l'entreprise
        ImageView logoView = new ImageView(new Image(getClass().getResourceAsStream("/Image/téléchargement.png")));
        logoView.setFitHeight(69);
        logoView.setFitWidth(57);
        logoView.setPreserveRatio(true);
        
        // Informations de l'offre
        VBox infoBox = new VBox(8);
        Label titreLabel = new Label(offre.getTitreOffre());
        titreLabel.setFont(Font.font("Barlow Condensed ExtraBold Italic", 16));
        titreLabel.setTextFill(Color.web("#2196F3"));
        
        Label entrepriseLabel = new Label("Entreprise : " + offre.getNomEntreprise());
        entrepriseLabel.setFont(Font.font("Arial Narrow", 14));
        entrepriseLabel.setTextFill(Color.web("#666666"));
        
        Label contratLabel = new Label("Type de contrat : " + offre.getTypeContrat());
        contratLabel.setFont(Font.font("Arial Narrow", 14));
        contratLabel.setTextFill(Color.web("#666666"));
        
        HBox buttonBox = new HBox(10);
        Button voirPlusBtn = new Button("Voir plus");
        voirPlusBtn.setStyle("-fx-background-color: #2196F3; -fx-text-fill: white; -fx-background-radius: 5;");
        voirPlusBtn.setFont(Font.font("Barlow Condensed ExtraLight", 12));
        
        Button voirCandidaturesBtn = new Button("Candidatures");
        voirCandidaturesBtn.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white; -fx-background-radius: 5;");
        voirCandidaturesBtn.setFont(Font.font("Barlow Condensed ExtraLight", 12));
        
        buttonBox.getChildren().addAll(voirPlusBtn, voirCandidaturesBtn);
        
        voirPlusBtn.setOnAction(e -> {
            offreSelectionnee = offre;
            inputTitre.setText(offre.getTitreOffre());
            inputDescription.setText(offre.getDescriptionOffre());
            inputEntreprise.setText(offre.getNomEntreprise());
            comboContrat.setValue(offre.getTypeContrat());
            btnModifierDetail.setDisable(false);
            btnSupprimerDetail.setDisable(false);
            btnVoirCandidatures.setDisable(false);
        });
        
        voirCandidaturesBtn.setOnAction(e -> {
            offreSelectionnee = offre;
            ouvrirCandidatures();
        });
        
        infoBox.getChildren().addAll(titreLabel, entrepriseLabel, contratLabel, buttonBox);
        box.getChildren().addAll(logoView, infoBox);
        return box;
    }

    private void afficherErreur(String titre, String message) {
        System.out.println("Affichage erreur - " + titre + ": " + message);
        Alert alert = new Alert(AlertType.ERROR);
        alert.setTitle(titre);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    /**
     * Retourne au menu principal
     */
    @FXML
    private void retourVersMenu() {
        try {
            // Fermer la fenêtre actuelle
            Stage stage = (Stage) btnRetour.getScene().getWindow();
            stage.close();
            
            // Optionnel : ouvrir le menu principal si nécessaire
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/Menu.fxml"));
            Parent root = loader.load();
            Stage menuStage = new Stage();
            menuStage.setTitle("Menu Principal");
            menuStage.setScene(new Scene(root));
            menuStage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
