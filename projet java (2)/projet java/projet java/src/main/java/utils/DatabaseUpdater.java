package utils;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.Statement;

/**
 * Classe utilitaire pour mettre à jour la structure de la base de données
 */
public class DatabaseUpdater {
    
    /**
     * Vérifie si une colonne existe dans une table
     * @param connection La connexion à la base de données
     * @param tableName Le nom de la table
     * @param columnName Le nom de la colonne
     * @return true si la colonne existe, false sinon
     */
    public static boolean columnExists(Connection connection, String tableName, String columnName) {
        try {
            DatabaseMetaData meta = connection.getMetaData();
            ResultSet rs = meta.getColumns(null, null, tableName, columnName);
            boolean exists = rs.next();
            rs.close();
            return exists;
        } catch (Exception e) {
            System.err.println("Erreur lors de la vérification de l'existence de la colonne : " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
    
    /**
     * Ajoute une colonne à une table si elle n'existe pas
     * @param connection La connexion à la base de données
     * @param tableName Le nom de la table
     * @param columnName Le nom de la colonne
     * @param columnType Le type de la colonne
     * @return true si la colonne a été ajoutée ou existe déjà, false sinon
     */
    public static boolean addColumnIfNotExists(Connection connection, String tableName, String columnName, String columnType) {
        try {
            if (!columnExists(connection, tableName, columnName)) {
                Statement stmt = connection.createStatement();
                String sql = "ALTER TABLE " + tableName + " ADD COLUMN " + columnName + " " + columnType;
                stmt.executeUpdate(sql);
                stmt.close();
                System.out.println("Colonne " + columnName + " ajoutée à la table " + tableName);
                return true;
            } else {
                System.out.println("Colonne " + columnName + " existe déjà dans la table " + tableName);
                return true;
            }
        } catch (Exception e) {
            System.err.println("Erreur lors de l'ajout de la colonne : " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
    
    /**
     * Met à jour la structure de la base de données pour les entretiens
     * @param connection La connexion à la base de données
     * @return true si la mise à jour a réussi, false sinon
     */
    public static boolean updateEntretiensTable(Connection connection) {
        try {
            boolean cvPathAdded = addColumnIfNotExists(connection, "entretiens", "cv_path", "VARCHAR(255)");
            boolean lmPathAdded = addColumnIfNotExists(connection, "entretiens", "lettre_motivation_path", "VARCHAR(255)");
            return cvPathAdded && lmPathAdded;
        } catch (Exception e) {
            System.err.println("Erreur lors de la mise à jour de la table entretiens : " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
}
