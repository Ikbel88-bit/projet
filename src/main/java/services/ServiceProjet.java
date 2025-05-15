package services;

import entities.Projet;
import utils.MyDatabase;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Service pour gérer les projets.
 */
public class ServiceProjet implements IService<Projet> {
    private static final Logger LOGGER = Logger.getLogger(ServiceProjet.class.getName());
    private Connection connection;

    public ServiceProjet() {
        connection = MyDatabase.getInstance().getConnection();
    }

    /**
     * Implémentation de la méthode abstraite afficher() de l'interface IService
     * @return Liste des projets
     */
    @Override
    public List<Projet> afficher() {
        try {
            List<Projet> projets = recuperer();
            for (Projet projet : projets) {
                LOGGER.info("Projet: " + projet.getNom_projet() + 
                           ", Description: " + projet.getDescription() + 
                           ", État: " + projet.getEtat());
            }
            return projets;
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Erreur lors de l'affichage des projets", e);
            return new ArrayList<>();
        }
    }

    /**
     * Récupère tous les projets.
     * @return Liste de tous les projets
     * @throws SQLException Si erreur de base de données
     */
    public List<Projet> findAll() throws SQLException {
        List<Projet> projets = new ArrayList<>();
        String sql = "SELECT id_projet, nom_projet, description, date_debut, date_fin, etat, id, lieu FROM projet";
        
        try (Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery(sql)) {
            
            while (resultSet.next()) {
                Projet projet = new Projet();
                projet.setId_projet(resultSet.getInt("id_projet"));
                projet.setNom_projet(resultSet.getString("nom_projet"));
                projet.setDescription(resultSet.getString("description"));
                projet.setDate_debut(resultSet.getObject("date_debut", LocalDate.class));
                projet.setDate_fin(resultSet.getObject("date_fin", LocalDate.class));
                projet.setEtat(resultSet.getString("etat"));
                projet.setId_admin(resultSet.getInt("id"));
                projet.setLieu(resultSet.getString("lieu"));
                
                projets.add(projet);
            }
        }
        
        return projets;
    }

    /**
     * Alias pour findAll.
     */
    public List<Projet> recuperer() throws SQLException {
        return findAll();
    }

    /**
     * Ajoute un nouveau projet
     * @param projet le projet à ajouter
     * @throws SQLException en cas d'erreur SQL
     */
    public void ajouter(Projet projet) throws SQLException {
        String sql = "INSERT INTO projet (nom_projet, description, date_debut, date_fin, etat, lieu, id) VALUES (?, ?, ?, ?, ?, ?, ?)";
        
        try (PreparedStatement statement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            statement.setString(1, projet.getNom_projet());
            statement.setString(2, projet.getDescription());
            statement.setDate(3, java.sql.Date.valueOf(LocalDate.now()));
            statement.setDate(4, java.sql.Date.valueOf(projet.getDate_fin()));
            statement.setString(5, projet.getEtat());
            statement.setString(6, projet.getLieu());
            statement.setInt(7, projet.getId_admin() > 0 ? projet.getId_admin() : 1); // Utiliser 1 comme valeur par défaut si id_admin n'est pas défini
            
            statement.executeUpdate();
            
            // Récupérer l'ID généré
            try (ResultSet generatedKeys = statement.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    projet.setId_projet(generatedKeys.getInt(1));
                }
            }
        }
    }

    /**
     * Modifie un projet existant
     * @param projet le projet à modifier
     * @throws SQLException en cas d'erreur SQL
     */
    public void modifier(Projet projet) throws SQLException {
        String sql = "UPDATE projet SET nom_projet = ?, description = ?, date_fin = ?, etat = ?, lieu = ? WHERE id_projet = ?";
        
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, projet.getNom_projet());
            statement.setString(2, projet.getDescription());
            statement.setDate(3, java.sql.Date.valueOf(projet.getDate_fin()));
            statement.setString(4, projet.getEtat());
            statement.setString(5, projet.getLieu());
            statement.setInt(6, projet.getId_projet());
            
            statement.executeUpdate();
        }
    }

    /**
     * Supprime un projet par son ID.
     * @param id ID du projet à supprimer
     * @throws SQLException Si erreur de base de données
     */
    public void delete(int id) throws SQLException {
        // D'abord supprimer les tâches associées
        String deleteTaches = "DELETE FROM tache WHERE id_projet = ?";
        try (PreparedStatement statement = connection.prepareStatement(deleteTaches)) {
            statement.setInt(1, id);
            statement.executeUpdate();
        }
        
        // Ensuite supprimer le projet
        String deleteProjet = "DELETE FROM projet WHERE id_projet = ?";
        try (PreparedStatement statement = connection.prepareStatement(deleteProjet)) {
            statement.setInt(1, id);
            int rowsAffected = statement.executeUpdate();
            LOGGER.info("Projet supprimé, ID: " + id + ", lignes affectées: " + rowsAffected);
        }
    }

    /**
     * Alias pour delete.
     */
    public void supprimer(int id) throws SQLException {
        delete(id);
    }

    /**
     * Supprime un projet
     * @param projet Projet à supprimer
     * @throws SQLException Si erreur de base de données
     */
    @Override
    public void supprimer(Projet projet) throws SQLException {
        supprimer(projet.getId_projet());
    }

    /**
     * Récupère les statistiques des projets.
     * @return Liste contenant [terminés, en cours, en retard]
     * @throws SQLException Si erreur de base de données
     */
    public List<Integer> getStatistics() throws SQLException {
        List<Integer> stats = new ArrayList<>();
        String completedSql = "SELECT COUNT(*) FROM projet WHERE etat = 'TERMINE'";
        String ongoingSql = "SELECT COUNT(*) FROM projet WHERE etat != 'TERMINE'";
        String delayedSql = "SELECT COUNT(*) FROM projet WHERE etat != 'TERMINE' AND date_fin < ?";

        // Projets terminés
        try (Statement statement = connection.createStatement();
             ResultSet rs = statement.executeQuery(completedSql)) {
            rs.next();
            stats.add(rs.getInt(1));
        }

        // Projets en cours
        try (Statement statement = connection.createStatement();
             ResultSet rs = statement.executeQuery(ongoingSql)) {
            rs.next();
            stats.add(rs.getInt(1));
        }

        // Projets en retard
        try (PreparedStatement statement = connection.prepareStatement(delayedSql)) {
            statement.setObject(1, LocalDate.now());
            ResultSet rs = statement.executeQuery();
            rs.next();
            stats.add(rs.getInt(1));
        }

        return stats;
    }

    /**
     * Récupère un projet par son ID.
     * @param id ID du projet à récupérer
     * @return Le projet trouvé ou null
     * @throws SQLException Si erreur de base de données
     */
    public Projet findById(int id) throws SQLException {
        String sql = "SELECT id_projet, nom_projet, description, date_debut, date_fin, etat, id, lieu FROM projet WHERE id_projet = ?";
        
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, id);
            
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    Projet projet = new Projet();
                    projet.setId_projet(resultSet.getInt("id_projet"));
                    projet.setNom_projet(resultSet.getString("nom_projet"));
                    projet.setDescription(resultSet.getString("description"));
                    projet.setDate_debut(resultSet.getObject("date_debut", LocalDate.class));
                    projet.setDate_fin(resultSet.getObject("date_fin", LocalDate.class));
                    projet.setEtat(resultSet.getString("etat"));
                    projet.setId_admin(resultSet.getInt("id"));
                    projet.setLieu(resultSet.getString("lieu"));
                    
                    return projet;
                }
            }
        }
        
        return null;
    }

    /**
     * Recherche des projets par critère.
     * @param critere Critère de recherche
     * @return Liste des projets correspondants
     * @throws SQLException Si erreur de base de données
     */
    public List<Projet> search(String critere) throws SQLException {
        List<Projet> projets = new ArrayList<>();
        String sql = "SELECT id_projet, nom_projet, description, date_debut, date_fin, etat, id, lieu FROM projet " +
                     "WHERE nom_projet LIKE ? OR description LIKE ? OR lieu LIKE ?";
        
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            String searchTerm = "%" + critere + "%";
            statement.setString(1, searchTerm);
            statement.setString(2, searchTerm);
            statement.setString(3, searchTerm);
            
            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    Projet projet = new Projet();
                    projet.setId_projet(resultSet.getInt("id_projet"));
                    projet.setNom_projet(resultSet.getString("nom_projet"));
                    projet.setDescription(resultSet.getString("description"));
                    projet.setDate_debut(resultSet.getObject("date_debut", LocalDate.class));
                    projet.setDate_fin(resultSet.getObject("date_fin", LocalDate.class));
                    projet.setEtat(resultSet.getString("etat"));
                    projet.setId_admin(resultSet.getInt("id"));
                    projet.setLieu(resultSet.getString("lieu"));
                    
                    projets.add(projet);
                }
            }
        }
        
        return projets;
    }

    /**
     * Compte le nombre total de projets.
     * @return Nombre de projets
     * @throws SQLException Si erreur de base de données
     */
    public int count() throws SQLException {
        String sql = "SELECT COUNT(*) FROM projet";
        
        try (Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery(sql)) {
            
            if (resultSet.next()) {
                return resultSet.getInt(1);
            }
        }
        
        return 0;
    }

    /**
     * Compte le nombre de projets par état.
     * @param etat État
     * @return Nombre de projets dans cet état
     * @throws SQLException Si erreur de base de données
     */
    public int countByStatus(String etat) throws SQLException {
        String sql = "SELECT COUNT(*) FROM projet WHERE etat = ?";
        
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, etat);
            
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return resultSet.getInt(1);
                }
            }
        }
        
        return 0;
    }

    /**
     * Compte le nombre de projets actifs
     * @return Nombre de projets actifs
     */
    public int countActiveProjects() {
        String sql = "SELECT COUNT(*) FROM projet WHERE etat != 'TERMINE'";
        try (Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery(sql)) {
            if (resultSet.next()) {
                return resultSet.getInt(1);
            }
        } catch (SQLException e) {
            LOGGER.log(java.util.logging.Level.SEVERE, "Erreur lors du comptage des projets actifs", e);
        }
        return 0;
    }

    /**
     * Récupère les projets assignés à un employé spécifique
     * @param idEmploye ID de l'employé
     * @return Liste des projets assignés à l'employé
     * @throws SQLException Si erreur de base de données
     */
    public List<Projet> findByEmploye(int idEmploye) throws SQLException {
        List<Projet> projets = new ArrayList<>();
        String sql = "SELECT DISTINCT p.id_projet, p.nom_projet, p.description, p.date_debut, p.date_fin, p.etat, p.id, p.lieu " +
                     "FROM projet p " +
                     "JOIN tache t ON p.id_projet = t.id_projet " +
                     "WHERE t.id_employe = ?";
        
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, idEmploye);
            
            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    Projet projet = new Projet();
                    projet.setId_projet(resultSet.getInt("id_projet"));
                    projet.setNom_projet(resultSet.getString("nom_projet"));
                    projet.setDescription(resultSet.getString("description"));
                    projet.setDate_debut(resultSet.getObject("date_debut", LocalDate.class));
                    projet.setDate_fin(resultSet.getObject("date_fin", LocalDate.class));
                    projet.setEtat(resultSet.getString("etat"));
                    projet.setId_admin(resultSet.getInt("id"));
                    projet.setLieu(resultSet.getString("lieu"));
                    
                    projets.add(projet);
                }
            }
        }
        
        return projets;
    }

    /**
     * Calcule la charge de travail d'un employé (nombre de tâches assignées)
     * @param idEmploye ID de l'employé
     * @return Nombre de tâches assignées à l'employé
     * @throws SQLException Si erreur de base de données
     */
    public int calculateEmployeeWorkload(int idEmploye) throws SQLException {
        String sql = "SELECT COUNT(*) FROM tache WHERE id_employe = ? AND etat != 'TERMINE'";
        
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, idEmploye);
            
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return resultSet.getInt(1);
                }
            }
        }
        
        return 0;
    }

    /**
     * Calcule le taux de complétion d'un projet (pourcentage de tâches terminées)
     * @param idProjet ID du projet
     * @return Taux de complétion (0-100)
     * @throws SQLException Si erreur de base de données
     */
    public double calculateProjectCompletionRate(int idProjet) throws SQLException {
        String sqlTotal = "SELECT COUNT(*) FROM tache WHERE id_projet = ?";
        String sqlCompleted = "SELECT COUNT(*) FROM tache WHERE id_projet = ? AND etat = 'TERMINE'";
        
        int totalTasks = 0;
        int completedTasks = 0;
        
        try (PreparedStatement statement = connection.prepareStatement(sqlTotal)) {
            statement.setInt(1, idProjet);
            
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    totalTasks = resultSet.getInt(1);
                }
            }
        }
        
        if (totalTasks == 0) {
            return 0;
        }
        
        try (PreparedStatement statement = connection.prepareStatement(sqlCompleted)) {
            statement.setInt(1, idProjet);
            
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    completedTasks = resultSet.getInt(1);
                }
            }
        }
        
        return (double) completedTasks / totalTasks * 100;
    }

    /**
     * Implémentation de la méthode abstraite compter() de l'interface IService
     * @return Nombre total de projets
     */
    @Override
    public int compter() {
        try {
            return count();
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Erreur lors du comptage des projets", e);
            return 0;
        }
    }

    /**
     * Vérifie si un projet existe par son ID
     * @param id ID du projet à vérifier
     * @return true si le projet existe, false sinon
     */
    @Override
    public boolean existe(int id) {
        try {
            Projet projet = findById(id);
            return projet != null;
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Erreur lors de la vérification de l'existence du projet", e);
            return false;
        }
    }

    /**
     * Recherche des projets par critère
     * @param critere Critère de recherche
     * @return Liste des projets correspondants
     */
    @Override
    public List<Projet> rechercher(String critere) {
        try {
            return search(critere);
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Erreur lors de la recherche de projets", e);
            return new ArrayList<>();
        }
    }

    /**
     * Récupère un projet par son ID
     * @param id ID du projet à récupérer
     * @return Le projet trouvé ou null si non trouvé
     */
    @Override
    public Projet recupererParId(int id) {
        try {
            return findById(id);
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Erreur lors de la récupération du projet par ID", e);
            return null;
        }
    }
}
