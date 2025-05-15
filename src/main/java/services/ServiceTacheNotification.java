package services;

import entities.Notification;
import entities.Tache;
import entities.User;
import utils.MyDatabase;

import java.sql.Connection;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Service pour gérer les notifications liées aux tâches
 */
public class ServiceTacheNotification {
    private static final Logger LOGGER = Logger.getLogger(ServiceTacheNotification.class.getName());
    private Connection connection;
    private ServiceTache serviceTache;
    private ServiceUser serviceUser;
    private ServiceNotification serviceNotification;

    public ServiceTacheNotification() {
        connection = MyDatabase.getInstance().getConnection();
        serviceTache = new ServiceTache();
        serviceUser = new ServiceUser();
        serviceNotification = new ServiceNotification();
    }

    /**
     * Vérifie les tâches en retard et envoie des notifications aux administrateurs
     * @return Le nombre de notifications envoyées
     */
    public int verifierTachesEnRetard() {
        int notificationsEnvoyees = 0;
        try {
            // Récupérer toutes les tâches non terminées dont la date de fin est passée
            List<Tache> tachesEnRetard = serviceTache.findTasksInLate();
            
            if (tachesEnRetard.isEmpty()) {
                return 0;
            }
            
            // Récupérer tous les administrateurs
            List<User> admins = serviceUser.findByRole("admin");
            
            if (admins.isEmpty()) {
                LOGGER.warning("Aucun administrateur trouvé pour envoyer les notifications de retard");
                return 0;
            }
            
            // Pour chaque tâche en retard, créer une notification pour chaque admin
            for (Tache tache : tachesEnRetard) {
                // Vérifier si l'employé existe
                User employe = null;
                if (tache.getId_employe() > 0) {
                    try {
                        employe = serviceUser.recupererParId(tache.getId_employe());
                    } catch (SQLException e) {
                        LOGGER.log(Level.WARNING, "Impossible de récupérer l'employé pour la tâche " + tache.getId_tache(), e);
                    }
                }
                
                String employeInfo = (employe != null) ? 
                    " assignée à " + employe.getPrenom() + " " + employe.getNom() : 
                    " (non assignée)";
                
                String message = "La tâche \"" + tache.getNom_tache() + "\"" + employeInfo + 
                                " est en retard. Date d'échéance: " + tache.getDate_fin();
                
                for (User admin : admins) {
                    Notification notification = new Notification(
                        message,
                        admin.getId(),
                        "TACHE_RETARD"
                    );
                    
                    serviceNotification.ajouter(notification);
                    notificationsEnvoyees++;
                }
                
                // Marquer la tâche comme notifiée pour éviter les doublons
                tache.setNotified(true);
                serviceTache.update(tache, true);
            }
            
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Erreur lors de la vérification des tâches en retard", e);
        }
        
        return notificationsEnvoyees;
    }
    
    /**
     * Envoie une notification pour une tâche terminée
     * @param tache La tâche terminée
     * @param employe L'employé qui a terminé la tâche
     * @return Nombre de notifications envoyées
     */
    public int notifierTacheTerminee(Tache tache, User employe) {
        int notificationsEnvoyees = 0;
        try {
            // Récupérer tous les administrateurs
            List<User> admins = serviceUser.findByRole("ADMIN");
            if (admins.isEmpty()) {
                LOGGER.warning("Aucun administrateur trouvé pour envoyer les notifications");
                return 0;
            }
            
            String message = "L'employé " + employe.getPrenom() + " " + employe.getNom() + 
                            " a terminé la tâche: " + tache.getNom_tache();
            
            // Envoyer une notification à chaque admin
            for (User admin : admins) {
                Notification notification = new Notification(
                    message,
                    admin.getId(),
                    "TACHE_TERMINEE"
                );
                if (serviceNotification.ajouter(notification)) {
                    notificationsEnvoyees++;
                }
            }
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Erreur lors de l'envoi des notifications de tâche terminée", e);
        }
        
        return notificationsEnvoyees;
    }
}