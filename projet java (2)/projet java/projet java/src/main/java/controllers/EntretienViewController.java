package controllers;

import entities.Entretien;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import services.ServiceEntretien;
import java.time.LocalDate;
import java.io.IOException;
import javafx.scene.control.Alert;
import java.util.function.Consumer;
import java.net.URI;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.awt.Desktop;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import utils.DatabaseUpdater;

import java.sql.SQLException;

public class EntretienViewController {

    @FXML
    private TextField tfTitre;
    @FXML
    private DatePicker dpDate;
    @FXML
    private TextField tfLieu;
    @FXML
    private TextField tfParticipant;
    @FXML
    private ComboBox<String> cbStatut;
    @FXML
    private Button btnVoirCarte;
    @FXML
    private TextField tfCV;
    @FXML
    private TextField tfLM;
    @FXML
    private Button btnSelectCV;
    @FXML
    private Button btnSelectLM;
    @FXML
    private Button btnViewCV;
    @FXML
    private Button btnViewLM;

    private ServiceEntretien serviceEntretien;
    private Entretien selectedEntretien;
    private Stage listeStage = null;
    private Stage mapStage = null;
    private String documentsDir = System.getProperty("user.dir") + File.separator + "documents";
    private File selectedCVFile = null;
    private File selectedLMFile = null;

    @FXML
    void initialize() {
        serviceEntretien = new ServiceEntretien();

        // Initialiser la ComboBox des statuts
        cbStatut.setItems(FXCollections.observableArrayList(
                "Prévu",
                "En cours",
                "Terminé",
                "Annulé"
        ));

        // Configurer le DatePicker pour empêcher la sélection de dates passées
        dpDate.setDayCellFactory(picker -> new DateCell() {
            @Override
            public void updateItem(LocalDate date, boolean empty) {
                super.updateItem(date, empty);
                setDisable(empty || date.isBefore(LocalDate.now()));
            }
        });

        // Configurer le bouton pour ouvrir la sélection de lieu avec Google Maps
        btnVoirCarte.setDisable(false);
        btnVoirCarte.setText("Sélectionner sur la carte");
        btnVoirCarte.setOnAction(e -> {
            openMapSelection();
        });

        // Ajouter un écouteur de double-clic sur le champ de lieu pour ouvrir Google Maps
        tfLieu.setOnMouseClicked(e -> {
            if (e.getClickCount() == 2 && !tfLieu.getText().trim().isEmpty()) {
                openGoogleMaps(tfLieu.getText().trim());
            }
        });

        // Rendre le champ de lieu obligatoire
        tfLieu.setPromptText("Cliquez sur 'Sélectionner sur la carte' pour choisir un lieu");
        tfLieu.setEditable(false); // Empêcher l'édition directe

        // Créer le répertoire des documents s'il n'existe pas
        createDocumentsDirectory();

        // Désactiver les boutons "Voir" par défaut
        btnViewCV.setDisable(true);
        btnViewLM.setDisable(true);

        // Vérifier si les colonnes existent dans la base de données
        try {
            boolean columnsExist = DatabaseUpdater.columnExists(serviceEntretien.getConnection(), "entretiens", "cv_path") &&
                                  DatabaseUpdater.columnExists(serviceEntretien.getConnection(), "entretiens", "lettre_motivation_path");

            if (!columnsExist) {
                System.out.println("Les colonnes cv_path et lettre_motivation_path n'existent pas encore dans la base de données.");
                System.out.println("Tentative de mise à jour de la structure de la base de données...");
                boolean updated = DatabaseUpdater.updateEntretiensTable(serviceEntretien.getConnection());
                if (updated) {
                    System.out.println("Structure de la base de données mise à jour avec succès.");
                } else {
                    System.err.println("Impossible de mettre à jour la structure de la base de données.");
                    showError("Attention", "La fonctionnalité d'upload de documents pourrait ne pas fonctionner correctement.");
                }
            }
        } catch (Exception e) {
            System.err.println("Erreur lors de la vérification de la structure de la base de données : " + e.getMessage());
            e.printStackTrace();
        }

        refreshCards();
    }

    @FXML
    void handleVoirListe() {
        if (listeStage != null && listeStage.isShowing()) {
            listeStage.toFront();
            listeStage.requestFocus();
            return;
        }
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/ListeEntretiensView.fxml"));
            Parent root = loader.load();

            listeStage = new Stage();
            listeStage.setTitle("Liste des Entretiens");
            listeStage.setScene(new Scene(root));
            listeStage.initModality(Modality.APPLICATION_MODAL);
            listeStage.setOnHidden(e -> listeStage = null); // Libère la référence à la fermeture
            listeStage.show();
        } catch (IOException e) {
            showError("Erreur", "Impossible d'ouvrir la liste des entretiens");
        }
    }

    private void refreshCards() {
        // IMPLEMENTATION DE LA REFRESH CARDS
    }

    private void populateFields(Entretien entretien) {
        // IMPLEMENTATION DE LA POPULATION DES CHAMPS
    }

    private void clearFields() {
        tfTitre.clear();
        dpDate.setValue(null);
        tfLieu.clear();
        tfParticipant.clear();
        cbStatut.setValue(null);
        tfCV.clear();
        tfLM.clear();
        selectedCVFile = null;
        selectedLMFile = null;
        btnViewCV.setDisable(true);
        btnViewLM.setDisable(true);
        selectedEntretien = null;
    }

    @FXML
    void handleAjouter() {
        if (!validateFields()) return;

        try {
            // Copier les fichiers sélectionnés dans le répertoire des documents
            String cvPath = "";
            String lmPath = "";

            try {
                if (selectedCVFile != null) {
                    cvPath = saveDocument(selectedCVFile, "cv");
                    System.out.println("CV enregistré : " + cvPath);
                }
            } catch (IOException e) {
                System.err.println("Erreur lors de l'enregistrement du CV : " + e.getMessage());
                e.printStackTrace();
                showError("Erreur", "Impossible d'enregistrer le CV : " + e.getMessage() +
                         "\nL'entretien sera ajouté sans le CV.");
            }

            try {
                if (selectedLMFile != null) {
                    lmPath = saveDocument(selectedLMFile, "lm");
                    System.out.println("Lettre de motivation enregistrée : " + lmPath);
                }
            } catch (IOException e) {
                System.err.println("Erreur lors de l'enregistrement de la lettre de motivation : " + e.getMessage());
                e.printStackTrace();
                showError("Erreur", "Impossible d'enregistrer la lettre de motivation : " + e.getMessage() +
                         "\nL'entretien sera ajouté sans la lettre de motivation.");
            }

            Entretien entretien = new Entretien(
                tfTitre.getText(),
                dpDate.getValue().toString(),
                tfLieu.getText(),
                tfParticipant.getText(),
                cbStatut.getValue(),
                cvPath,
                lmPath
            );

            serviceEntretien.ajouter(entretien);
            showSuccess("Entretien ajouté avec succès");
            clearFields();
            refreshCards();
        } catch (SQLException e) {
            System.err.println("Erreur SQL lors de l'ajout : " + e.getMessage());
            e.printStackTrace();
            showError("Erreur lors de l'ajout", e.getMessage());
        } catch (Exception e) {
            System.err.println("Erreur inattendue lors de l'ajout : " + e.getMessage());
            e.printStackTrace();
            showError("Erreur inattendue", "Une erreur inattendue s'est produite : " + e.getMessage());
        }
    }

    @FXML
    void handleModifier() {
        if (selectedEntretien == null) {
            showError("Erreur", "Veuillez sélectionner un entretien à modifier");
            return;
        }

        if (!validateFields()) return;

        try {
            // Copier les fichiers sélectionnés dans le répertoire des documents
            String cvPath = selectedEntretien.getCv_path();
            String lmPath = selectedEntretien.getLettre_motivation_path();

            try {
                if (selectedCVFile != null) {
                    cvPath = saveDocument(selectedCVFile, "cv");
                    System.out.println("CV enregistré : " + cvPath);
                }
            } catch (IOException e) {
                System.err.println("Erreur lors de l'enregistrement du CV : " + e.getMessage());
                e.printStackTrace();
                showError("Erreur", "Impossible d'enregistrer le CV : " + e.getMessage() +
                         "\nL'entretien sera modifié sans changer le CV.");
            }

            try {
                if (selectedLMFile != null) {
                    lmPath = saveDocument(selectedLMFile, "lm");
                    System.out.println("Lettre de motivation enregistrée : " + lmPath);
                }
            } catch (IOException e) {
                System.err.println("Erreur lors de l'enregistrement de la lettre de motivation : " + e.getMessage());
                e.printStackTrace();
                showError("Erreur", "Impossible d'enregistrer la lettre de motivation : " + e.getMessage() +
                         "\nL'entretien sera modifié sans changer la lettre de motivation.");
            }

            selectedEntretien.setTitre(tfTitre.getText());
            selectedEntretien.setDate_entretien(dpDate.getValue().toString());
            selectedEntretien.setLieu(tfLieu.getText());
            selectedEntretien.setParticipant(tfParticipant.getText());
            selectedEntretien.setStatut(cbStatut.getValue());
            selectedEntretien.setCv_path(cvPath);
            selectedEntretien.setLettre_motivation_path(lmPath);

            serviceEntretien.modifier(selectedEntretien);
            showSuccess("Entretien modifié avec succès");
            clearFields();
            refreshCards();
        } catch (SQLException e) {
            System.err.println("Erreur SQL lors de la modification : " + e.getMessage());
            e.printStackTrace();
            showError("Erreur lors de la modification", e.getMessage());
        } catch (Exception e) {
            System.err.println("Erreur inattendue lors de la modification : " + e.getMessage());
            e.printStackTrace();
            showError("Erreur inattendue", "Une erreur inattendue s'est produite : " + e.getMessage());
        }
    }

    @FXML
    void handleSupprimer() {
        if (selectedEntretien == null) {
            showError("Erreur", "Veuillez sélectionner un entretien à supprimer");
            return;
        }

        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirmation de suppression");
        alert.setHeaderText("Supprimer l'entretien");
        alert.setContentText("Êtes-vous sûr de vouloir supprimer cet entretien ?");

        if (alert.showAndWait().get() == ButtonType.OK) {
            try {
                serviceEntretien.supprimer(selectedEntretien);
                showSuccess("Entretien supprimé avec succès");
                clearFields();
                refreshCards();
            } catch (SQLException e) {
                showError("Erreur lors de la suppression", e.getMessage());
            }
        }
    }

    @FXML
    void handleRetour() {
        Stage stage = (Stage) tfTitre.getScene().getWindow();
        stage.close();
    }

    @FXML
    void handleMinimize() {
        Stage stage = (Stage) tfTitre.getScene().getWindow();
        stage.setIconified(true);
    }

    @FXML
    void handleQuitter() {
        Stage stage = (Stage) tfTitre.getScene().getWindow();
        stage.close();
    }

    private boolean validateFields() {
        if (tfTitre.getText().trim().isEmpty()) {
            showError("Erreur", "Veuillez saisir un titre pour l'entretien");
            return false;
        }
        if (dpDate.getValue() == null) {
            showError("Erreur", "Veuillez sélectionner une date pour l'entretien");
            return false;
        }
        if (tfLieu.getText().trim().isEmpty()) {
            showError("Erreur", "Veuillez sélectionner un lieu pour l'entretien en utilisant la carte");
            return false;
        }
        if (tfParticipant.getText().trim().isEmpty()) {
            showError("Erreur", "Veuillez saisir le nom du participant");
            return false;
        }
        if (cbStatut.getValue() == null) {
            showError("Erreur", "Veuillez sélectionner un statut pour l'entretien");
            return false;
        }
        return true;
    }

    private void showError(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }

    private void showSuccess(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Succès");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    /**
     * Ouvre la fenêtre de sélection de lieu avec Google Maps
     */
    private void openMapSelection() {
        if (mapStage != null && mapStage.isShowing()) {
            mapStage.toFront();
            mapStage.requestFocus();
            return;
        }

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/MapSelectionView.fxml"));
            Parent root = loader.load();

            MapSelectionController controller = loader.getController();

            // Définir le callback pour récupérer le lieu sélectionné
            controller.setLocationCallback(location -> {
                tfLieu.setText(location);
                // Ouvrir automatiquement Google Maps après la sélection
                openGoogleMaps(location);
            });

            // Si un lieu est déjà sélectionné, l'utiliser comme point de départ
            String currentLocation = tfLieu.getText().trim();
            if (!currentLocation.isEmpty()) {
                controller.setInitialLocation(currentLocation);
            } else {
                // Utiliser Tunis comme lieu par défaut
                controller.setInitialLocation("Tunis");
            }

            Scene scene = new Scene(root);
            mapStage = new Stage();
            mapStage.setScene(scene);
            mapStage.setTitle("Sélection du lieu");
            mapStage.initModality(Modality.APPLICATION_MODAL);
            mapStage.setWidth(800);
            mapStage.setHeight(600);
            mapStage.setResizable(true);
            mapStage.setOnHidden(e -> mapStage = null);
            mapStage.show();
            mapStage.centerOnScreen();
        } catch (IOException e) {
            showError("Erreur", "Impossible d'ouvrir la carte : " + e.getMessage());
        }
    }

    /**
     * Ouvre Google Maps dans le navigateur par défaut avec le lieu spécifié
     * @param location Le lieu à afficher sur Google Maps
     */
    private void openGoogleMaps(String location) {
        try {
            // Encoder le lieu pour l'URL
            String encodedLocation = URLEncoder.encode(location, StandardCharsets.UTF_8.toString());

            // Construire l'URL Google Maps
            String googleMapsUrl = "https://www.google.com/maps/search/?api=1&query=" + encodedLocation;

            // Ouvrir l'URL dans le navigateur par défaut
            Desktop.getDesktop().browse(new URI(googleMapsUrl));
        } catch (Exception e) {
            showError("Erreur", "Impossible d'ouvrir Google Maps : " + e.getMessage());
        }
    }

    /**
     * Crée le répertoire des documents s'il n'existe pas
     */
    private void createDocumentsDirectory() {
        File directory = new File(documentsDir);
        if (!directory.exists()) {
            boolean created = directory.mkdirs();
            if (!created) {
                System.err.println("Impossible de créer le répertoire : " + documentsDir);
                // Essayer de créer dans le répertoire temporaire du système
                documentsDir = System.getProperty("java.io.tmpdir") + File.separator + "entretiens_documents";
                directory = new File(documentsDir);
                if (!directory.exists()) {
                    created = directory.mkdirs();
                    if (!created) {
                        System.err.println("Impossible de créer le répertoire temporaire : " + documentsDir);
                    } else {
                        System.out.println("Répertoire temporaire créé : " + documentsDir);
                    }
                }
            } else {
                System.out.println("Répertoire créé : " + documentsDir);
            }
        }
    }

    /**
     * Enregistre un document dans le répertoire des documents
     * @param file Le fichier à enregistrer
     * @param prefix Le préfixe à ajouter au nom du fichier (cv ou lm)
     * @return Le chemin du fichier enregistré
     * @throws IOException En cas d'erreur lors de l'enregistrement
     */
    private String saveDocument(File file, String prefix) throws IOException {
        try {
            // Vérifier que le répertoire existe
            createDocumentsDirectory();

            // Générer un nom de fichier unique
            String timestamp = String.valueOf(System.currentTimeMillis());
            String fileName = prefix + "_" + timestamp + "_" + file.getName();
            Path destination = Paths.get(documentsDir, fileName);

            // Créer les répertoires parents si nécessaire
            Files.createDirectories(destination.getParent());

            // Copier le fichier
            Files.copy(file.toPath(), destination, StandardCopyOption.REPLACE_EXISTING);

            System.out.println("Document enregistré : " + destination);

            return destination.toString();
        } catch (IOException e) {
            System.err.println("Erreur lors de l'enregistrement du document : " + e.getMessage());
            e.printStackTrace();

            // Essayer d'enregistrer dans le répertoire temporaire
            String tempDir = System.getProperty("java.io.tmpdir");
            String timestamp = String.valueOf(System.currentTimeMillis());
            String fileName = prefix + "_" + timestamp + "_" + file.getName();
            Path destination = Paths.get(tempDir, fileName);

            // Copier le fichier
            Files.copy(file.toPath(), destination, StandardCopyOption.REPLACE_EXISTING);

            System.out.println("Document enregistré dans le répertoire temporaire : " + destination);

            return destination.toString();
        }
    }

    /**
     * Ouvre un document avec l'application par défaut
     * @param filePath Le chemin du fichier à ouvrir
     */
    private void openDocument(String filePath) {
        try {
            File file = new File(filePath);
            if (file.exists()) {
                try {
                    Desktop.getDesktop().open(file);
                } catch (Exception e) {
                    System.err.println("Erreur lors de l'ouverture du document avec l'application par défaut : " + e.getMessage());
                    e.printStackTrace();

                    // Essayer d'ouvrir le répertoire contenant le fichier
                    try {
                        Desktop.getDesktop().open(file.getParentFile());
                        showError("Erreur", "Impossible d'ouvrir le document directement. Le répertoire contenant le fichier a été ouvert.");
                    } catch (Exception ex) {
                        System.err.println("Erreur lors de l'ouverture du répertoire : " + ex.getMessage());
                        ex.printStackTrace();
                        showError("Erreur", "Impossible d'ouvrir le document ou son répertoire : " + e.getMessage());
                    }
                }
            } else {
                System.err.println("Le fichier n'existe pas : " + filePath);

                // Vérifier si le répertoire existe
                File directory = new File(documentsDir);
                if (directory.exists()) {
                    try {
                        Desktop.getDesktop().open(directory);
                        showError("Erreur", "Le fichier n'existe pas : " + filePath + "\nLe répertoire des documents a été ouvert.");
                    } catch (Exception ex) {
                        System.err.println("Erreur lors de l'ouverture du répertoire des documents : " + ex.getMessage());
                        ex.printStackTrace();
                        showError("Erreur", "Le fichier n'existe pas et impossible d'ouvrir le répertoire des documents.");
                    }
                } else {
                    showError("Erreur", "Le fichier n'existe pas et le répertoire des documents n'existe pas non plus.");
                }
            }
        } catch (Exception e) {
            System.err.println("Erreur inattendue lors de l'ouverture du document : " + e.getMessage());
            e.printStackTrace();
            showError("Erreur", "Impossible d'ouvrir le document : " + e.getMessage());
        }
    }

    @FXML
    void handleSelectCV() {
        try {
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Sélectionner un CV");
            fileChooser.getExtensionFilters().addAll(
                new ExtensionFilter("Documents PDF", "*.pdf"),
                new ExtensionFilter("Documents Word", "*.doc", "*.docx"),
                new ExtensionFilter("Tous les fichiers", "*.*")
            );

            // Définir le répertoire initial
            File initialDirectory = new File(System.getProperty("user.home"));
            if (initialDirectory.exists()) {
                fileChooser.setInitialDirectory(initialDirectory);
            }

            File file = fileChooser.showOpenDialog(tfCV.getScene().getWindow());
            if (file != null) {
                selectedCVFile = file;
                tfCV.setText(file.getName());
                btnViewCV.setDisable(false);

                // Essayer d'enregistrer immédiatement pour tester
                try {
                    String path = saveDocument(file, "cv_test");
                    System.out.println("Test d'enregistrement réussi : " + path);
                } catch (IOException e) {
                    System.err.println("Test d'enregistrement échoué : " + e.getMessage());
                    e.printStackTrace();
                    showError("Erreur de test", "Impossible d'enregistrer le fichier : " + e.getMessage());
                }
            }
        } catch (Exception e) {
            System.err.println("Erreur lors de la sélection du CV : " + e.getMessage());
            e.printStackTrace();
            showError("Erreur", "Impossible de sélectionner le CV : " + e.getMessage());
        }
    }

    @FXML
    void handleSelectLM() {
        try {
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Sélectionner une lettre de motivation");
            fileChooser.getExtensionFilters().addAll(
                new ExtensionFilter("Documents PDF", "*.pdf"),
                new ExtensionFilter("Documents Word", "*.doc", "*.docx"),
                new ExtensionFilter("Tous les fichiers", "*.*")
            );

            // Définir le répertoire initial
            File initialDirectory = new File(System.getProperty("user.home"));
            if (initialDirectory.exists()) {
                fileChooser.setInitialDirectory(initialDirectory);
            }

            File file = fileChooser.showOpenDialog(tfLM.getScene().getWindow());
            if (file != null) {
                selectedLMFile = file;
                tfLM.setText(file.getName());
                btnViewLM.setDisable(false);
            }
        } catch (Exception e) {
            System.err.println("Erreur lors de la sélection de la lettre de motivation : " + e.getMessage());
            e.printStackTrace();
            showError("Erreur", "Impossible de sélectionner la lettre de motivation : " + e.getMessage());
        }
    }

    @FXML
    void handleViewCV() {
        if (selectedEntretien != null && selectedEntretien.getCv_path() != null && !selectedEntretien.getCv_path().isEmpty()) {
            openDocument(selectedEntretien.getCv_path());
        } else if (selectedCVFile != null) {
            try {
                Desktop.getDesktop().open(selectedCVFile);
            } catch (Exception e) {
                showError("Erreur", "Impossible d'ouvrir le CV : " + e.getMessage());
            }
        }
    }

    @FXML
    void handleViewLM() {
        if (selectedEntretien != null && selectedEntretien.getLettre_motivation_path() != null && !selectedEntretien.getLettre_motivation_path().isEmpty()) {
            openDocument(selectedEntretien.getLettre_motivation_path());
        } else if (selectedLMFile != null) {
            try {
                Desktop.getDesktop().open(selectedLMFile);
            } catch (Exception e) {
                showError("Erreur", "Impossible d'ouvrir la lettre de motivation : " + e.getMessage());
            }
        }
    }

    /**
     * Initialise les champs avec les données de l'entretien sélectionné
     * @param entretien L'entretien à modifier
     */
    public void initData(Entretien entretien) {
        this.selectedEntretien = entretien;

        // Remplir les champs avec les données de l'entretien
        tfTitre.setText(entretien.getTitre());

        // Convertir la date String en LocalDate pour le DatePicker
        try {
            LocalDate date = LocalDate.parse(entretien.getDate_entretien());
            dpDate.setValue(date);
        } catch (Exception e) {
            // En cas d'erreur de format de date, ne pas définir de date
            System.err.println("Erreur de format de date : " + e.getMessage());
        }

        tfLieu.setText(entretien.getLieu());
        tfParticipant.setText(entretien.getParticipant());
        cbStatut.setValue(entretien.getStatut());

        // Afficher les chemins des documents s'ils existent
        if (entretien.getCv_path() != null && !entretien.getCv_path().isEmpty()) {
            tfCV.setText(entretien.getCv_path());
            btnViewCV.setDisable(false);
        } else {
            tfCV.clear();
            btnViewCV.setDisable(true);
        }

        if (entretien.getLettre_motivation_path() != null && !entretien.getLettre_motivation_path().isEmpty()) {
            tfLM.setText(entretien.getLettre_motivation_path());
            btnViewLM.setDisable(false);
        } else {
            tfLM.clear();
            btnViewLM.setDisable(true);
        }
    }
}
