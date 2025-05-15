package api;

import entities.Rapport;
import services.ServiceRapport;

import java.time.LocalDate;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * API pour la gestion des rapports
 */
public class RapportAPI {
    private static final Logger LOGGER = Logger.getLogger(RapportAPI.class.getName());
    private final ServiceRapport serviceRapport = new ServiceRapport();

    /**
     * Récupère tous les rapports
     * @return Liste des rapports
     */
    public List<Rapport> getAllRapports() {
        try {
            return serviceRapport.getAll();
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Erreur lors de la récupération des rapports", e);
            throw new RuntimeException("Erreur lors de la récupération des rapports: " + e.getMessage(), e);
        }
    }

    /**
     * Récupère un rapport par son ID
     * @param id ID du rapport
     * @return Rapport ou null si non trouvé
     */
    public Rapport getRapportById(int id) {
        try {
            return serviceRapport.getById(id);
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Erreur lors de la récupération du rapport", e);
            throw new RuntimeException("Erreur lors de la récupération du rapport: " + e.getMessage(), e);
        }
    }

    /**
     * Crée un nouveau rapport
     * @param rapport Rapport à créer
     * @return Rapport créé
     */
    public Rapport createRapport(Rapport rapport) {
        try {
            if (rapport.getDateCreation() == null) {
                rapport.setDateCreation(LocalDate.now());
            }
            
            boolean success = serviceRapport.ajouter(rapport);
            if (!success) {
                throw new RuntimeException("Échec de la création du rapport");
            }
            return rapport;
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Erreur lors de la création du rapport", e);
            throw new RuntimeException("Erreur lors de la création du rapport: " + e.getMessage(), e);
        }
    }

    /**
     * Met à jour un rapport existant
     * @param id ID du rapport à mettre à jour
     * @param rapport Nouvelles données du rapport
     * @return Rapport mis à jour
     */
    public Rapport updateRapport(int id, Rapport rapport) {
        try {
            Rapport existingRapport = serviceRapport.getById(id);
            if (existingRapport == null) {
                throw new RuntimeException("Rapport non trouvé avec l'ID: " + id);
            }
            
            rapport.setId(id);
            boolean success = serviceRapport.modifier(rapport);
            if (!success) {
                throw new RuntimeException("Échec de la mise à jour du rapport");
            }
            return rapport;
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Erreur lors de la mise à jour du rapport", e);
            throw new RuntimeException("Erreur lors de la mise à jour du rapport: " + e.getMessage(), e);
        }
    }

    /**
     * Supprime un rapport
     * @param id ID du rapport à supprimer
     * @return true si réussi, false sinon
     */
    public boolean deleteRapport(int id) {
        try {
            Rapport existingRapport = serviceRapport.getById(id);
            if (existingRapport == null) {
                throw new RuntimeException("Rapport non trouvé avec l'ID: " + id);
            }
            
            return serviceRapport.supprimer(id);
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Erreur lors de la suppression du rapport", e);
            throw new RuntimeException("Erreur lors de la suppression du rapport: " + e.getMessage(), e);
        }
    }

    /**
     * Récupère les rapports par projet
     * @param idProjet ID du projet
     * @return Liste des rapports du projet
     */
    public List<Rapport> getRapportsByProjet(int idProjet) {
        try {
            return serviceRapport.getByProjet(idProjet);
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Erreur lors de la récupération des rapports par projet", e);
            throw new RuntimeException("Erreur lors de la récupération des rapports par projet: " + e.getMessage(), e);
        }
    }
    
    /**
     * Récupère les rapports par tâche
     * @param idTache ID de la tâche
     * @return Liste des rapports de la tâche
     */
    public List<Rapport> getRapportsByTache(int idTache) {
        try {
            return serviceRapport.getByTache(idTache);
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Erreur lors de la récupération des rapports par tâche", e);
            throw new RuntimeException("Erreur lors de la récupération des rapports par tâche: " + e.getMessage(), e);
        }
    }
    
    /**
     * Récupère les rapports par auteur
     * @param idAuteur ID de l'auteur
     * @return Liste des rapports de l'auteur
     */
    public List<Rapport> getRapportsByAuteur(int idAuteur) {
        try {
            return serviceRapport.getByAuteur(idAuteur);
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Erreur lors de la récupération des rapports par auteur", e);
            throw new RuntimeException("Erreur lors de la récupération des rapports par auteur: " + e.getMessage(), e);
        }
    }
    
    /**
     * Récupère les rapports par statut
     * @param statut Statut des rapports
     * @return Liste des rapports avec ce statut
     */
    public List<Rapport> getRapportsByStatut(String statut) {
        try {
            return serviceRapport.getByStatut(statut);
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Erreur lors de la récupération des rapports par statut", e);
            throw new RuntimeException("Erreur lors de la récupération des rapports par statut: " + e.getMessage(), e);
        }
    }
    
    /**
     * Récupère les statistiques des rapports
     * @return Statistiques des rapports
     */
    public List<Integer> getRapportStatistics() {
        try {
            return serviceRapport.getStatistics();
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Erreur lors de la récupération des statistiques des rapports", e);
            throw new RuntimeException("Erreur lors de la récupération des statistiques: " + e.getMessage(), e);
        }
    }
}