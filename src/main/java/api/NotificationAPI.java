package api;

import entities.Notification;
import services.ServiceNotification;

import java.time.LocalDateTime;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.sql.Timestamp;

/**
 * API pour la gestion des notifications
 */
public class NotificationAPI {
    private static final Logger LOGGER = Logger.getLogger(NotificationAPI.class.getName());
    private final ServiceNotification serviceNotification = new ServiceNotification();

    /**
     * Récupère toutes les notifications
     * @return Liste des notifications
     */
    public List<Notification> getAllNotifications() {
        try {
            return serviceNotification.recuperer();
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Erreur lors de la récupération des notifications", e);
            throw new RuntimeException("Erreur lors de la récupération des notifications: " + e.getMessage(), e);
        }
    }

    /**
     * Récupère une notification par son ID
     * @param id ID de la notification
     * @return Notification ou null si non trouvée
     */
    public Notification getNotificationById(int id) {
        try {
            return serviceNotification.recupererParId(id);
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Erreur lors de la récupération de la notification", e);
            throw new RuntimeException("Erreur lors de la récupération de la notification: " + e.getMessage(), e);
        }
    }

    /**
     * Récupère toutes les notifications d'un utilisateur
     * @param idUtilisateur ID de l'utilisateur
     * @return Liste des notifications
     */
    public List<Notification> getNotificationsByUtilisateur(int idUtilisateur) {
        try {
            return serviceNotification.getNotificationsByUser(idUtilisateur);
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Erreur lors de la récupération des notifications", e);
            throw new RuntimeException("Erreur lors de la récupération des notifications: " + e.getMessage(), e);
        }
    }

    /**
     * Récupère le nombre de notifications non lues d'un utilisateur
     * @param idUtilisateur ID de l'utilisateur
     * @return Nombre de notifications non lues
     */
    public int getNombreNotificationsNonLues(int idUtilisateur) {
        try {
            return serviceNotification.countUnreadByUser(idUtilisateur);
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Erreur lors du comptage des notifications non lues", e);
            throw new RuntimeException("Erreur lors du comptage des notifications non lues: " + e.getMessage(), e);
        }
    }

    /**
     * Crée une nouvelle notification
     * @param notification Notification à créer
     * @return Notification créée avec son ID
     */
    public Notification createNotification(Notification notification) {
        try {
            if (notification.getDateCreation() == null) {
                // Convertir LocalDateTime en Timestamp
                notification.setDateCreation(Timestamp.valueOf(LocalDateTime.now()));
            }
            
            boolean success = serviceNotification.ajouter(notification);
            if (!success) {
                throw new RuntimeException("Échec de la création de la notification");
            }
            return notification;
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Erreur lors de la création de la notification", e);
            throw new RuntimeException("Erreur lors de la création de la notification: " + e.getMessage(), e);
        }
    }

    /**
     * Met à jour une notification existante
     * @param id ID de la notification à mettre à jour
     * @param notification Nouvelles données de la notification
     * @return Notification mise à jour
     */
    public Notification updateNotification(int id, Notification notification) {
        try {
            Notification existingNotification = serviceNotification.recupererParId(id);
            if (existingNotification == null) {
                throw new RuntimeException("Notification non trouvée avec l'ID: " + id);
            }
            
            notification.setId(id);
            boolean success = serviceNotification.modifier(notification);
            if (!success) {
                throw new RuntimeException("Échec de la mise à jour de la notification");
            }
            return notification;
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Erreur lors de la mise à jour de la notification", e);
            throw new RuntimeException("Erreur lors de la mise à jour de la notification: " + e.getMessage(), e);
        }
    }

    /**
     * Marque une notification comme lue
     * @param id ID de la notification
     * @return true si réussi, false sinon
     */
    public boolean marquerCommeLue(int id) {
        try {
            return serviceNotification.marquerCommeLue(id);
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Erreur lors du marquage de la notification comme lue", e);
            throw new RuntimeException("Erreur lors du marquage de la notification comme lue: " + e.getMessage(), e);
        }
    }

    /**
     * Supprime une notification
     * @param id ID de la notification à supprimer
     * @return true si réussi, false sinon
     */
    public boolean deleteNotification(int id) {
        try {
            return serviceNotification.supprimer(id);
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Erreur lors de la suppression de la notification", e);
            throw new RuntimeException("Erreur lors de la suppression de la notification: " + e.getMessage(), e);
        }
    }
}
