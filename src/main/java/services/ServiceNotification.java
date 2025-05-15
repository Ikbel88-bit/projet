package services;

import entities.Notification;
import entities.User;
import utils.MyDatabase;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Service pour gérer les notifications
 */
public class ServiceNotification {
    private static final Logger LOGGER = Logger.getLogger(ServiceNotification.class.getName());
    private Connection connection;

    public ServiceNotification() {
        connection = MyDatabase.getInstance().getConnection();
    }

    /**
     * Ajoute une notification dans la base de données
     * @param notification La notification à ajouter
     * @return true si l'ajout a réussi, false sinon
     */
    public boolean ajouter(Notification notification) {
        String req = "INSERT INTO notification (message, user_id, type, lue) VALUES (?, ?, ?, ?)";
        try (PreparedStatement ps = connection.prepareStatement(req)) {
            ps.setString(1, notification.getMessage());
            ps.setInt(2, notification.getUserId());
            ps.setString(3, notification.getType());
            ps.setBoolean(4, notification.isLue());
            
            int rowsAffected = ps.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Erreur lors de l'ajout d'une notification", e);
            return false;
        }
    }

    /**
     * Récupère toutes les notifications
     * @return La liste de toutes les notifications
     */
    public List<Notification> recuperer() throws SQLException {
        List<Notification> notifications = new ArrayList<>();
        String req = "SELECT * FROM notification ORDER BY date_creation DESC";
        
        try (Statement st = connection.createStatement();
             ResultSet rs = st.executeQuery(req)) {
            
            while (rs.next()) {
                Notification notification = new Notification(
                    rs.getInt("id"),
                    rs.getString("message"),
                    rs.getInt("user_id"),
                    rs.getString("type"),
                    rs.getTimestamp("date_creation"),
                    rs.getBoolean("lue")
                );
                notifications.add(notification);
            }
        }
        
        return notifications;
    }

    /**
     * Récupère une notification par son ID
     * @param id L'ID de la notification
     * @return La notification ou null si non trouvée
     */
    public Notification recupererParId(int id) throws SQLException {
        String req = "SELECT * FROM notification WHERE id = ?";
        
        try (PreparedStatement ps = connection.prepareStatement(req)) {
            ps.setInt(1, id);
            
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return new Notification(
                        rs.getInt("id"),
                        rs.getString("message"),
                        rs.getInt("user_id"),
                        rs.getString("type"),
                        rs.getTimestamp("date_creation"),
                        rs.getBoolean("lue")
                    );
                }
            }
        }
        
        return null;
    }

    /**
     * Modifie une notification
     * @param notification La notification à modifier
     * @return true si la modification a réussi, false sinon
     */
    public boolean modifier(Notification notification) {
        String req = "UPDATE notification SET message = ?, user_id = ?, type = ?, lue = ? WHERE id = ?";
        try (PreparedStatement ps = connection.prepareStatement(req)) {
            ps.setString(1, notification.getMessage());
            ps.setInt(2, notification.getUserId());
            ps.setString(3, notification.getType());
            ps.setBoolean(4, notification.isLue());
            ps.setInt(5, notification.getId());
            
            int rowsAffected = ps.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Erreur lors de la modification d'une notification", e);
            return false;
        }
    }

    /**
     * Récupère toutes les notifications d'un utilisateur
     * @param userId L'ID de l'utilisateur
     * @return La liste des notifications de l'utilisateur
     */
    public List<Notification> getNotificationsByUser(int userId) {
        List<Notification> notifications = new ArrayList<>();
        
        try {
            // Vérifier si la table notification existe
            DatabaseMetaData metaData = connection.getMetaData();
            ResultSet tables = metaData.getTables(null, null, "notification", null);
            
            if (!tables.next()) {
                LOGGER.warning("La table notification n'existe pas encore");
                return notifications;
            }
            
            String req = "SELECT * FROM notification WHERE user_id = ? ORDER BY date_creation DESC";
            try (PreparedStatement ps = connection.prepareStatement(req)) {
                ps.setInt(1, userId);
                
                try (ResultSet rs = ps.executeQuery()) {
                    while (rs.next()) {
                        Notification notification = new Notification(
                            rs.getInt("id"),
                            rs.getString("message"),
                            rs.getInt("user_id"),
                            rs.getString("type"),
                            rs.getTimestamp("date_creation"),
                            rs.getBoolean("lue")
                        );
                        notifications.add(notification);
                    }
                }
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Erreur lors de la récupération des notifications", e);
        }
        
        return notifications;
    }

    /**
     * Marque une notification comme lue
     * @param notificationId L'ID de la notification
     * @return true si la mise à jour a réussi, false sinon
     */
    public boolean marquerCommeLue(int notificationId) {
        String req = "UPDATE notification SET lue = true WHERE id = ?";
        try (PreparedStatement ps = connection.prepareStatement(req)) {
            ps.setInt(1, notificationId);
            
            int rowsAffected = ps.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Erreur lors de la mise à jour d'une notification", e);
            return false;
        }
    }

    /**
     * Supprime une notification
     * @param notificationId L'ID de la notification à supprimer
     * @return true si la suppression a réussi, false sinon
     */
    public boolean supprimer(int notificationId) {
        String req = "DELETE FROM notification WHERE id = ?";
        try (PreparedStatement ps = connection.prepareStatement(req)) {
            ps.setInt(1, notificationId);
            
            int rowsAffected = ps.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Erreur lors de la suppression d'une notification", e);
            return false;
        }
    }

    /**
     * Compte le nombre de notifications non lues pour un utilisateur
     * @param userId L'ID de l'utilisateur
     * @return Le nombre de notifications non lues
     */
    public int countUnreadNotifications(int userId) {
        try {
            // Vérifier si la table notification existe
            DatabaseMetaData metaData = connection.getMetaData();
            ResultSet tables = metaData.getTables(null, null, "notification", null);
            
            if (!tables.next()) {
                LOGGER.warning("La table notification n'existe pas encore");
                return 0;
            }
            
            String req = "SELECT COUNT(*) FROM notification WHERE user_id = ? AND lue = false";
            try (PreparedStatement ps = connection.prepareStatement(req)) {
                ps.setInt(1, userId);
                
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        return rs.getInt(1);
                    }
                }
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Erreur lors du comptage des notifications non lues", e);
        }
        
        return 0;
    }
    
    /**
     * Compte le nombre de notifications non lues par utilisateur
     * @param userId L'ID de l'utilisateur
     * @return Le nombre de notifications non lues
     */
    public int countUnreadByUser(int userId) {
        return countUnreadNotifications(userId);
    }
}
