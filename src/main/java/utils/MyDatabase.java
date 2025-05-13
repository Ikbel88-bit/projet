package utils;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class MyDatabase {

    private final String URL = "jdbc:mysql://localhost:3306/groupe";
    private final String USERNAME = "root";
    private final String PASSWORD = "";
    private Connection cnx;
    private static MyDatabase instance;

    private MyDatabase() {
        try {
            // Créer la base de données si elle n'existe pas
            Connection tempCnx = DriverManager.getConnection("jdbc:mysql://localhost:3306", USERNAME, PASSWORD);
            Statement stmt = tempCnx.createStatement();
            stmt.executeUpdate("CREATE DATABASE IF NOT EXISTS groupe");
            tempCnx.close();

            // Se connecter à la base de données groupe
            cnx = DriverManager.getConnection(URL, USERNAME, PASSWORD);
            System.out.println("Connected to database");

            // Créer les tables si elles n'existent pas
            createTables();
        } catch (SQLException e) {
            System.out.println("Erreur de connexion à la base de données: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void createTables() {
        try {
            Statement stmt = cnx.createStatement();
            
            // Créer la table user
            stmt.execute("CREATE TABLE IF NOT EXISTS user (" +
                    "id INT PRIMARY KEY AUTO_INCREMENT, " +
                    "nom VARCHAR(50) NOT NULL, " +
                    "prenom VARCHAR(50) NOT NULL, " +
                    "email VARCHAR(100) NOT NULL, " +
                    "telephone VARCHAR(20) NOT NULL, " +
                    "role VARCHAR(20) NOT NULL DEFAULT 'Employe', " +
                    "password VARCHAR(255) NOT NULL" +
                    ")");

            // Créer la table reclamation
            stmt.execute("CREATE TABLE IF NOT EXISTS reclamation (" +
                    "reclamation_id INT AUTO_INCREMENT PRIMARY KEY, " +
                    "id INT NOT NULL, " +
                    "description VARCHAR(255) NOT NULL, " +
                    "reponse VARCHAR(255) DEFAULT '', " +
                    "FOREIGN KEY (id) REFERENCES user(id) ON DELETE CASCADE ON UPDATE CASCADE" +
                    ")");

            System.out.println("Tables créées avec succès");
        } catch (SQLException e) {
            System.out.println("Erreur lors de la création des tables: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public static MyDatabase getInstance() {
        if (instance == null)
            instance = new MyDatabase();
        return instance;
    }

    public Connection getCnx() {
        return cnx;
    }
}
