package org.example;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.controlsfx.control.Notifications;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception {
        try {
            // Charger la page principale
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(getClass().getResource("/org/example/views/menu.fxml"));
            Parent root = loader.load();
            
            // Configurer la scène
            Scene scene = new Scene(root);
            scene.getStylesheets().add(getClass().getResource("/org/example/styles.css").toExternalForm());
            
            // Configurer la fenêtre principale
            primaryStage.setTitle("SmartRH");
            primaryStage.setScene(scene);
            primaryStage.setMinWidth(800);
            primaryStage.setMinHeight(600);
            primaryStage.show();
            
        } catch (Exception e) {
            e.printStackTrace();
            System.exit(1);
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
} 