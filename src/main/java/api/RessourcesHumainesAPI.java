package api;

import entities.Projet;
import entities.Tache;
import entities.User;
import services.ServiceProjet;
import services.ServiceTache;
import services.ServiceUser;

import java.sql.SQLException;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

/**
 * API pour la gestion des ressources humaines
 */
public class RessourcesHumainesAPI {
    private static final Logger LOGGER = Logger.getLogger(RessourcesHumainesAPI.class.getName());
    
    private ServiceUser serviceUser;
    private ServiceTache serviceTache;
    private ServiceProjet serviceProjet;
    
    public RessourcesHumainesAPI() {
        serviceUser = new ServiceUser();
        serviceTache = new ServiceTache();
        serviceProjet = new ServiceProjet();
    }
    
    /**
     * Récupère tous les employés
     * @return Liste des employés
     * @throws SQLException Si erreur de base de données
     */
    public List<User> getAllEmployees() throws SQLException {
        return serviceUser.findByRole("EMPLOYE");
    }
    
    /**
     * Récupère les tâches d'un employé
     * @param idEmploye ID de l'employé
     * @return Liste des tâches de l'employé
     * @throws SQLException Si erreur de base de données
     */
    public List<Tache> getTasksByEmployee(int idEmploye) throws SQLException {
        return serviceTache.findByEmployeId(idEmploye);
    }
    
    /**
     * Récupère les projets auxquels un employé participe
     * @param idEmploye ID de l'employé
     * @return Liste des projets de l'employé
     * @throws SQLException Si erreur de base de données
     */
    public List<Projet> getProjectsByEmployee(int idEmploye) throws SQLException {
        List<Tache> taches = serviceTache.findByEmployeId(idEmploye);
        List<Integer> projetIds = taches.stream()
                .map(Tache::getId_projet)
                .distinct()
                .collect(Collectors.toList());
        
        List<Projet> projets = new ArrayList<>();
        for (Integer projetId : projetIds) {
            Projet projet = serviceProjet.findById(projetId);
            if (projet != null) {
                projets.add(projet);
            }
        }
        
        return projets;
    }
    
    /**
     * Calcule la charge de travail d'un employé (nombre de tâches actives)
     * @param idEmploye ID de l'employé
     * @return Nombre de tâches actives
     * @throws SQLException Si erreur de base de données
     */
    public int getEmployeeWorkload(int idEmploye) throws SQLException {
        List<Tache> taches = serviceTache.findByEmployeId(idEmploye);
        return (int) taches.stream()
                .filter(t -> "EN_COURS".equals(t.getEtat()))
                .count();
    }
    
    /**
     * Calcule la performance d'un employé
     * @param idEmploye ID de l'employé
     * @return Pourcentage de performance
     * @throws SQLException Si erreur de base de données
     */
    public double getEmployeePerformance(int idEmploye) throws SQLException {
        List<Tache> taches = serviceTache.findByEmployeId(idEmploye);
        
        if (taches.isEmpty()) {
            return 0.0;
        }
        
        // Tâches terminées
        long terminees = taches.stream()
                .filter(t -> "TERMINE".equals(t.getEtat()))
                .count();
        
        // Tâches en retard
        long enRetard = taches.stream()
                .filter(t -> "EN_RETARD".equals(t.getEtat()))
                .count();
        
        // Calcul de la performance (tâches terminées / total des tâches) * 100
        // Avec pénalité pour les tâches en retard
        double performance = ((double) terminees / taches.size()) * 100;
        
        // Appliquer une pénalité pour les tâches en retard
        if (enRetard > 0) {
            performance = performance * (1 - (0.1 * enRetard));
        }
        
        return Math.max(0, Math.min(100, performance));
    }
    
    /**
     * Génère un rapport de performance pour tous les employés
     * @return Map avec ID employé -> performance
     * @throws SQLException Si erreur de base de données
     */
    public Map<Integer, Double> generatePerformanceReport() throws SQLException {
        List<User> employes = getAllEmployees();
        Map<Integer, Double> report = new HashMap<>();
        
        for (User employe : employes) {
            double performance = getEmployeePerformance(employe.getId());
            report.put(employe.getId(), performance);
        }
        
        return report;
    }
    
    /**
     * Génère un rapport de charge de travail pour tous les employés
     * @return Map avec ID employé -> charge de travail
     * @throws SQLException Si erreur de base de données
     */
    public Map<Integer, Integer> generateWorkloadReport() throws SQLException {
        List<User> employes = getAllEmployees();
        Map<Integer, Integer> report = new HashMap<>();
        
        for (User employe : employes) {
            int workload = getEmployeeWorkload(employe.getId());
            report.put(employe.getId(), workload);
        }
        
        return report;
    }
    
    /**
     * Assigne une tâche à un employé
     * @param idTache ID de la tâche
     * @param idEmploye ID de l'employé
     * @return true si l'assignation a réussi
     * @throws SQLException Si erreur de base de données
     */
    public boolean assignTaskToEmployee(int idTache, int idEmploye) throws SQLException {
        Tache tache = serviceTache.recupererParId(idTache);
        if (tache == null) {
            return false;
        }
        
        tache.setId_employe(idEmploye);
        serviceTache.modifier(tache);
        return true;
    }
}
