package tests;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class MainFxback extends Application {

    @Override
    public void start(Stage primaryStage) {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/FormationList.fxml"));
            Scene scene = new Scene(root);
            primaryStage.setScene(scene);
            primaryStage.setTitle("Ajouter");
            primaryStage.setMaximized(true); // ✅ Make the window full screen
            primaryStage.show();

        } catch (IOException ex) {
            System.out.println("Erreur de chargement FXML: " + ex.getMessage());
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}
