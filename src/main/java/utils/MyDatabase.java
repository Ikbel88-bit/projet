package utils;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class MyDatabase {

    private static final String URL = "jdbc:mysql://localhost:3306/groupe";
    private static final String USER = "root";
    private static final String PASSWORD = "";
    private static MyDatabase instance;
    private Connection cnx;

    private MyDatabase() {
        try {
            // Charger le driver MySQL
            Class.forName("com.mysql.cj.jdbc.Driver");

            // Créer la base de données si elle n'existe pas
            Connection tempCnx = DriverManager.getConnection("jdbc:mysql://localhost:3306", USER, PASSWORD);
            Statement stmt = tempCnx.createStatement();
            stmt.executeUpdate("CREATE DATABASE IF NOT EXISTS groupe");
            tempCnx.close();

            // Connexion à la base groupe
            cnx = DriverManager.getConnection(URL, USER, PASSWORD);
            System.out.println("Connexion établie à la base de données.");

            // Création des tables
            createTables();

        } catch (ClassNotFoundException e) {
            System.err.println("Driver MySQL non trouvé : " + e.getMessage());
        } catch (SQLException e) {
            System.err.println("Erreur SQL : " + e.getMessage());
        }
    }

    private void createTables() {
        try (Statement stmt = cnx.createStatement()) {
            // Table user
            stmt.execute("CREATE TABLE IF NOT EXISTS user (" +
                    "id INT PRIMARY KEY AUTO_INCREMENT, " +
                    "nom VARCHAR(50) NOT NULL, " +
                    "prenom VARCHAR(50) NOT NULL, " +
                    "email VARCHAR(100) NOT NULL, " +
                    "telephone VARCHAR(20) NOT NULL, " +
                    "role VARCHAR(20) NOT NULL DEFAULT 'Employe', " +
                    "password VARCHAR(255) NOT NULL" +
                    ")");

            // Table reclamation
            stmt.execute("CREATE TABLE IF NOT EXISTS reclamation (" +
                    "reclamation_id INT AUTO_INCREMENT PRIMARY KEY, " +
                    "id INT NOT NULL, " +
                    "description VARCHAR(255) NOT NULL, " +
                    "reponse VARCHAR(255) DEFAULT '', " +
                    "FOREIGN KEY (id) REFERENCES user(id) ON DELETE CASCADE ON UPDATE CASCADE" +
                    ")");

            System.out.println("Tables créées ou déjà existantes.");
        } catch (SQLException e) {
            System.err.println("Erreur lors de la création des tables : " + e.getMessage());
        }
    }

    public static MyDatabase getInstance() {
        if (instance == null) {
            instance = new MyDatabase();
        }
        return instance;
    }

    public Connection getCnx() {
        try {
            if (cnx == null || cnx.isClosed()) {
                cnx = DriverManager.getConnection(URL, USER, PASSWORD);
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors de la récupération de la connexion : " + e.getMessage());
        }
        return cnx;
    }

    public boolean isConnected() {
        try {
            return cnx != null && !cnx.isClosed();
        } catch (SQLException e) {
            return false;
        }
    }

    public void closeConnection() {
        try {
            if (cnx != null && !cnx.isClosed()) {
                cnx.close();
                System.out.println("Connexion fermée avec succès !");
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors de la fermeture de la connexion : " + e.getMessage());
        }
    }
}
