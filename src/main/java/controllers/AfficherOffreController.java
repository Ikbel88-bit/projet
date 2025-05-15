package controllers;

import entities.Offre;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.geometry.Insets;
import javafx.scene.layout.Priority;
import services.ServiceOffre;

import java.awt.event.ActionEvent;
import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.ResourceBundle;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.layout.HBox;
import java.util.stream.Collectors;

public class AfficherOffreController implements Initializable {
    @FXML
    private VBox LesOffres;
    @FXML
    private VBox detailOffre; // c'est celui dans ta vue principale
    @FXML
    private Label CONTRAT;
    @FXML
    private Label DECRIPTION;
    @FXML
    private Label TITTRE;
    @FXML
    private Label TYPE;
    
    // Nouveaux éléments pour le filtrage
    @FXML
    private ComboBox<String> filtreContratComboBox;
    @FXML
    private TextField rechercheTextField;
    @FXML
    private Button btnRechercher;
    
    private List<Offre> toutesLesOffres;
    private ServiceOffre serviceOffre;

    public VBox getDetailOffre() {
        return detailOffre;
    }
    
    public void initialize(URL location, ResourceBundle resources) {
        serviceOffre = new ServiceOffre();
        
        // Initialiser les contrôles de filtrage
        initFiltres();
        
        // Charger toutes les offres
        chargerOffres();
    }
    
    private void initFiltres() {
        // Créer les éléments de filtrage s'ils n'existent pas dans le FXML
        HBox filtresBox = new HBox(10);
        filtresBox.setAlignment(javafx.geometry.Pos.CENTER_LEFT);
        filtresBox.setPadding(new Insets(10, 10, 10, 10));
        filtresBox.setStyle("-fx-background-color: #f5f5f5; -fx-border-color: #e0e0e0; -fx-border-width: 0 0 1 0;");
        
        // Filtre par type de contrat
        Label filtreLabel = new Label("Type de contrat:");
        filtreLabel.setStyle("-fx-font-weight: bold;");
        
        filtreContratComboBox = new ComboBox<>();
        filtreContratComboBox.getItems().addAll("Tous", "CDI", "CDD", "Stage", "Freelance", "Alternance");
        filtreContratComboBox.setValue("Tous");
        filtreContratComboBox.setPrefWidth(150);
        filtreContratComboBox.valueProperty().addListener((obs, oldVal, newVal) -> filtrerOffres());
        
        // Recherche par titre
        Label rechercheLabel = new Label("Rechercher:");
        rechercheLabel.setStyle("-fx-font-weight: bold;");
        
        rechercheTextField = new TextField();
        rechercheTextField.setPromptText("Titre de l'offre...");
        rechercheTextField.setPrefWidth(200);
        
        btnRechercher = new Button("Rechercher");
        btnRechercher.getStyleClass().add("rh-btn");
        btnRechercher.setOnAction(e -> filtrerOffres());
        
        // Ajouter les éléments à la boîte de filtres
        filtresBox.getChildren().addAll(
            filtreLabel, filtreContratComboBox, 
            rechercheLabel, rechercheTextField, 
            btnRechercher
        );
        
        // Ajouter la boîte de filtres au début du VBox principal
        if (LesOffres.getChildren().size() > 0) {
            LesOffres.getChildren().add(0, filtresBox);
        } else {
            LesOffres.getChildren().add(filtresBox);
        }
    }
    
    private void chargerOffres() {
        try {
            toutesLesOffres = Collections.unmodifiableList(serviceOffre.recuperer());
            filtrerOffres();
        } catch (SQLException e) {
            afficherErreur("Erreur", "Impossible de charger les offres: " + e.getMessage());
            throw new RuntimeException(e);
        }
    }
    
    private void filtrerOffres() {
        // Vider la liste des offres affichées (sauf les filtres)
        if (LesOffres.getChildren().size() > 1) {
            LesOffres.getChildren().remove(1, LesOffres.getChildren().size());
        }
        
        // Récupérer les valeurs des filtres
        String typeContrat = filtreContratComboBox.getValue();
        String recherche = rechercheTextField.getText().toLowerCase().trim();
        
        // Filtrer les offres
        List<Offre> offresFiltrees = toutesLesOffres.stream()
            .filter(offre -> typeContrat.equals("Tous") || offre.getTypeContrat().equals(typeContrat))
            .filter(offre -> recherche.isEmpty() || 
                   offre.getTitreOffre().toLowerCase().contains(recherche))
            .collect(Collectors.toList());
        
        // Afficher les offres filtrées
        for (Offre offre : offresFiltrees) {
            FXMLLoader fxmlLoader = new FXMLLoader();
            fxmlLoader.setLocation(getClass().getResource("/OffreItem.fxml"));
            try {
                Parent item = fxmlLoader.load();
                if (item instanceof VBox) {
                    VBox vBox = (VBox) item;
                    controllers.OffreItemController cir = fxmlLoader.getController();
                    cir.setData(offre);
                    cir.setParentController(this);
                    LesOffres.getChildren().add(vBox);
                } else {
                    System.err.println("Le fichier OffreItem.fxml doit contenir un VBox comme élément racine");
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        
        // Afficher un message si aucune offre ne correspond aux critères
        if (offresFiltrees.isEmpty()) {
            Label aucuneOffreLabel = new Label("Aucune offre ne correspond à vos critères de recherche");
            aucuneOffreLabel.setStyle("-fx-font-size: 14px; -fx-text-fill: #757575; -fx-padding: 20;");
            LesOffres.getChildren().add(aucuneOffreLabel);
        }
    }
    
    private void afficherErreur(String titre, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(titre);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
    /*
    @FXML private VBox offresContainer;
    @FXML private Button btnAjouter;
    @FXML private Button btnModifier;
    @FXML private Button btnSupprimer;
    @FXML private HBox adminControls;


        Text titre = new Text(offre.getTitre());
        titre.getStyleClass().add("subtitle");

        Text entreprise = new Text(offre.getEntreprise());
        Text contrat = new Text("Type de contrat: " + offre.getContrat());
        Text description = new Text(offre.getDescription());

        HBox actions = new HBox(10);
        Button postulerBtn = new Button("Postuler");
        postulerBtn.getStyleClass().add("button");
        postulerBtn.setOnAction(e -> handlePostuler(offre));

        actions.getChildren().add(postulerBtn);

        if (isAdmin) {
            Button modifierBtn = new Button("Modifier");
            modifierBtn.getStyleClass().add("button");
            modifierBtn.setOnAction(e -> handleModifierOffre(offre));

            Button supprimerBtn = new Button("Supprimer");
            supprimerBtn.getStyleClass().add("button");
            supprimerBtn.getStyleClass().add("button-danger");
            supprimerBtn.setOnAction(e -> handleSupprimerOffre(offre));

            actions.getChildren().addAll(modifierBtn, supprimerBtn);
        }

        card.getChildren().addAll(titre, entreprise, contrat, description, actions);
        return card;
    }

    @FXML
    private void handleAjouterOffre() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/AjouterOffre.fxml"));
            Parent root = loader.load();
            Stage stage = new Stage();
            stage.setTitle("Ajouter une offre");
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void handleModifierOffre(Offre offre) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/ModifierOffre.fxml"));
            Parent root = loader.load();
            ModifierOffreController controller = loader.getController();
            controller.setOffre(offre);
            
            Stage stage = new Stage();
            stage.setTitle("Modifier l'offre");
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void handleSupprimerOffre(Offre offre) {
        offreService.deleteOffre(offre.getId());
        loadOffres();
    }

    private void handlePostuler(Offre offre) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/AjouterCondidature.fxml"));
            Parent root = loader.load();
            AjouterCondidatureController controller = loader.getController();
            controller.setOffre(offre);
            
            Stage stage = new Stage();
            stage.setTitle("Postuler à l'offre");
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

     */

    public void afficherDetail(Parent detailPane) {
        detailOffre.getChildren().setAll(detailPane);
        // Rendre le panneau parent (HBox) visible si jamais il a été caché
        if (detailOffre.getParent() != null) {
            detailOffre.getParent().setVisible(true);
        }
    }
    public void tocondidature(javafx.event.ActionEvent actionEvent) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/CondidatureUser.fxml"));
            Parent root = loader.load();
            Stage stage = new Stage();
            stage.setTitle("Candidater à l'offre");
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            System.out.println("Exception : " + e.getCause());
            e.printStackTrace();
        }
    }

    @FXML
    private Button btnRetour;

    /**
     * Retourne au menu principal
     */
    @FXML
    public void retourVersMenu() {
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
