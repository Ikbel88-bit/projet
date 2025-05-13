package utils;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import org.example.models.Ressource;
import services.ServiceRessource;

public class MyDatabase {

    private static MyDatabase instance;
    private Connection connection;
    private final String URL = "jdbc:mysql://localhost:3306/esprit3b2";
    private final String USER = "root";
    private final String PASSWORD = ""; // Mets ici ton mot de passe MySQL si tu en as un

    // Constructeur privé pour empêcher la création d'instances multiples
    private MyDatabase() {
        try {
            connection = DriverManager.getConnection(URL, USER, PASSWORD);
            System.out.println("Connected to MySQL database.");
        } catch (SQLException e) {
            System.err.println("Connection failed: " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("Impossible de se connecter à la base de données", e);
        }
    }

    // Méthode pour obtenir l'instance unique de la classe
    public static MyDatabase getInstance() {
        if (instance == null) {
            instance = new MyDatabase();
        }
        return instance;
    }

    // Méthode pour obtenir la connexion à la base de données
    public Connection getConnection() {
        return connection;
    }

    // Méthode pour fermer la connexion à la base de données
    public void closeConnection() {
        if (connection != null) {
            try {
                connection.close();
                System.out.println("Connection closed.");
            } catch (SQLException e) {
                System.err.println("Error closing connection: " + e.getMessage());
            }
        }
    }

    public void ajouter(Ressource ressource) throws SQLException {
        String sql = "INSERT INTO ressources (nom, type, description, emplacement, etat, disponible) VALUES (?, ?, ?, ?, ?, ?)";
        PreparedStatement ps = connection.prepareStatement(sql);
        ps.setString(1, ressource.getNom());
        ps.setString(2, ressource.getType());
        ps.setString(3, ressource.getDescription());
        ps.setString(4, ressource.getEmplacement());
        ps.setString(5, ressource.getEtat());
        ps.setBoolean(6, ressource.isDisponible());
        ps.executeUpdate();
    }
}
