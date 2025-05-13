package controllers;

import entities.User;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.PasswordField;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.fxml.FXMLLoader;
import services.ServiceUser;
import services.EmailService;
import java.io.IOException;
import java.sql.SQLException;

public class RegisterController {
    @FXML
    private TextField nomField;
    @FXML
    private TextField prenomField;
    @FXML
    private TextField emailField;
    @FXML
    private TextField telephoneField;
    @FXML
    private PasswordField passwordField;
    @FXML
    private TextField verificationCodeField;
    @FXML
    private Label errorLabel;

    private String generatedCode = "";
    private EmailService emailService = new EmailService();

    @FXML
    void sendVerificationCode(ActionEvent event) {
        errorLabel.setText("");
        String email = emailField.getText();
        
        if (email.isEmpty()) {
            errorLabel.setText("Veuillez entrer une adresse email");
            return;
        }
        
        if (!isValidEmail(email)) {
            errorLabel.setText("Format d'email invalide. Exemple: nom.prenom@gmail.com");
            return;
        }
        
        try {
            generatedCode = emailService.generateVerificationCode();
            emailService.sendVerificationCode(email, generatedCode);
            errorLabel.setText("Code de vérification envoyé à " + email);
            errorLabel.setStyle("-fx-text-fill: green;");
        } catch (Exception e) {
            errorLabel.setText("Erreur lors de l'envoi du code: " + e.getMessage());
            errorLabel.setStyle("-fx-text-fill: red;");
            e.printStackTrace();
        }
    }

    @FXML
    void registerCandidat(ActionEvent event) {
        errorLabel.setText("");
        String nom = nomField.getText();
        String prenom = prenomField.getText();
        String email = emailField.getText();
        String telephone = telephoneField.getText();
        String password = passwordField.getText();
        String verificationCode = verificationCodeField.getText();
        
        if (nom.isEmpty() || prenom.isEmpty() || email.isEmpty() || telephone.isEmpty() || password.isEmpty() || verificationCode.isEmpty()) {
            errorLabel.setText("Veuillez remplir tous les champs");
            errorLabel.setStyle("-fx-text-fill: red;");
            return;
        }
        
        if (!isValidPhoneNumber(telephone)) {
            errorLabel.setText(getPhoneNumberErrorMessage(telephone));
            errorLabel.setStyle("-fx-text-fill: red;");
            return;
        }
        
        if (!isValidEmail(email)) {
            errorLabel.setText("Format d'email invalide. Exemple: nom.prenom@gmail.com");
            errorLabel.setStyle("-fx-text-fill: red;");
            return;
        }
        
        if (generatedCode.isEmpty()) {
            errorLabel.setText("Veuillez demander un code de vérification");
            errorLabel.setStyle("-fx-text-fill: red;");
            return;
        }
        
        if (!verificationCode.equals(generatedCode)) {
            errorLabel.setText("Code de vérification incorrect");
            errorLabel.setStyle("-fx-text-fill: red;");
            return;
        }
        
        try {
            // Vérifier si l'email ou le téléphone existe déjà
            ServiceUser serviceUser = new ServiceUser();
            for (User existingUser : serviceUser.recuperer()) {
                if (existingUser.getEmail().equals(email) || existingUser.getTelephone().equals(telephone)) {
                    errorLabel.setText("Un compte avec cet email ou ce numéro de téléphone existe déjà");
                    errorLabel.setStyle("-fx-text-fill: red;");
                    return;
                }
            }
            
            // Créer le nouvel utilisateur
            User user = new User();
            user.setNom(nom);
            user.setPrenom(prenom);
            user.setEmail(email);
            user.setTelephone(telephone);
            user.setPassword(password);
            user.setRole("Candidat");
            
            // Ajouter l'utilisateur à la base de données
            serviceUser.ajouter(user);
            
            // Récupérer l'utilisateur créé avec son ID
            User createdUser = null;
            for (User u : serviceUser.recuperer()) {
                if (u.getEmail().equals(email) && u.getTelephone().equals(telephone)) {
                    createdUser = u;
                    break;
                }
            }
            
            if (createdUser != null) {
                try {
                    // Charger l'interface candidat
                    FXMLLoader loader = new FXMLLoader(getClass().getResource("/Candidat.fxml"));
                    Parent root = loader.load();
                    
                    // Configurer le contrôleur
                    CandidatController controller = loader.getController();
                    controller.setUser(createdUser);
                    
                    // Afficher la nouvelle scène
                    Stage stage = (Stage) nomField.getScene().getWindow();
                    Scene scene = new Scene(root);
                    stage.setScene(scene);
                    stage.setTitle("Espace Candidat");
                    stage.show();
                } catch (IOException e) {
                    errorLabel.setText("Erreur lors du chargement de l'interface candidat");
                    errorLabel.setStyle("-fx-text-fill: red;");
                    e.printStackTrace();
                }
            } else {
                errorLabel.setText("Erreur lors de la création du compte");
                errorLabel.setStyle("-fx-text-fill: red;");
            }
        } catch (SQLException e) {
            errorLabel.setText("Erreur lors de la création du compte : " + e.getMessage());
            errorLabel.setStyle("-fx-text-fill: red;");
            e.printStackTrace();
        }
    }

    private boolean isValidPhoneNumber(String phone) {
        // Vérifie si le numéro est un numéro tunisien valide
        // Format: 8 chiffres commençant par 2, 3, 4, 5, 7, 8 ou 9
        return phone.matches("^[2-59][0-9]{7}$");
    }

    private String getPhoneNumberErrorMessage(String phone) {
        if (phone.length() != 8) {
            return "Le numéro de téléphone doit contenir exactement 8 chiffres";
        }
        if (!phone.matches("^[2-59][0-9]{7}$")) {
            return "Le numéro de téléphone doit commencer par 2, 3, 4, 5, 7, 8 ou 9";
        }
        return "Format de numéro de téléphone invalide";
    }

    private boolean isValidEmail(String email) {
        // Expression régulière pour valider le format d'email
        String emailRegex = "^[A-Za-z0-9+_.-]+@(.+)$";
        return email.matches(emailRegex);
    }

    @FXML
    void retourLogin(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/Login.fxml"));
            Parent root = loader.load();
            Scene scene = new Scene(root);
            Stage stage = (Stage) nomField.getScene().getWindow();
            stage.setScene(scene);
            stage.setTitle("Login");
            stage.show();
        } catch (IOException e) {
            errorLabel.setText("Erreur lors du retour au login");
            e.printStackTrace();
        }
    }
}
