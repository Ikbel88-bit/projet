package controllers;

import entities.Offre;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.control.ScrollPane;
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

    public VBox getDetailOffre() {
        return detailOffre;
    }
    public void initialize(URL location, ResourceBundle resources) {
        ServiceOffre servicesoffre = new ServiceOffre();

        List<Offre> offres = null;
        try {
            offres = Collections.unmodifiableList(servicesoffre.recuperer());
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        for(int i = 0; i<offres.toArray().length; i++){
            FXMLLoader fxmlLoader = new FXMLLoader();
            fxmlLoader.setLocation(getClass().getResource("/OffreItem.fxml"));
            try {
                Parent item = fxmlLoader.load();
                if (item instanceof VBox) {
                    VBox vBox = (VBox) item;
                    controllers.OffreItemController cir = fxmlLoader.getController();
                    cir.setData(offres.get(i));
                    cir.setParentController(this);
                    LesOffres.getChildren().add(vBox);
                } else {
                    System.err.println("Le fichier OffreItem.fxml doit contenir un VBox comme élément racine");
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
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

}


