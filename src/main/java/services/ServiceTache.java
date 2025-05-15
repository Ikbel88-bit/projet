package services;

import entities.Tache;
import utils.MyDatabase;
import services.IService;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ServiceTache{
    private static final Logger LOGGER = Logger.getLogger(ServiceTache.class.getName());
    private Connection connection;

    public ServiceTache() {
        connection = MyDatabase.getInstance().getConnection();
    }

    // Méthode pour trouver les tâches en retard
    public List<Tache> findTasksInLate() {
        List<Tache> tachesEnRetard = new ArrayList<>();
        
        try {
            // Utiliser une requête qui ne dépend pas de la colonne notified
            String sql = "SELECT * FROM tache WHERE etat != 'TERMINE' AND date_fin < ?";
            
            try (PreparedStatement statement = connection.prepareStatement(sql)) {
                statement.setDate(1, java.sql.Date.valueOf(LocalDate.now()));
                
                try (ResultSet resultSet = statement.executeQuery()) {
                    while (resultSet.next()) {
                        Tache tache = mapResultSetToTache(resultSet);
                        tachesEnRetard.add(tache);
                    }
                }
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Erreur lors de la recherche des tâches en retard", e);
        }
        
        return tachesEnRetard;
    }

    /**
     * Trouve les tâches assignées à un employé
     * @param idEmploye ID de l'employé
     * @return Liste des tâches de l'employé
     * @throws SQLException Si erreur de base de données
     */
    public List<Tache> findByEmployeId(int idEmploye) throws SQLException {
        List<Tache> taches = new ArrayList<>();
        String sql = "SELECT t.*, p.nom_projet FROM tache t LEFT JOIN projet p ON t.id_projet = p.id_projet WHERE t.id_employe = ?";
        
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, idEmploye);
            
            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    Tache tache = new Tache();
                    tache.setId_tache(resultSet.getInt("id_tache"));
                    tache.setNom_tache(resultSet.getString("nom_tache"));
                    tache.setDescription(resultSet.getString("description"));
                    
                    // Conversion des dates
                    Date dateDebut = resultSet.getDate("date_debut");
                    if (dateDebut != null) {
                        tache.setDate_debut(dateDebut.toLocalDate());
                    }
                    
                    Date dateFin = resultSet.getDate("date_fin");
                    if (dateFin != null) {
                        tache.setDate_fin(dateFin.toLocalDate());
                    }
                    
                    tache.setEtat(resultSet.getString("etat"));
                    tache.setId_projet(resultSet.getInt("id_projet"));
                    tache.setId_employe(resultSet.getInt("id_employe"));
                    
                    // Récupérer le nom du projet
                    String nomProjet = resultSet.getString("nom_projet");
                    tache.setNomProjet(nomProjet);
                    
                    taches.add(tache);
                }
            }
        }
        
        return taches;
    }

    // Méthode pour mettre à jour une tâche
    public boolean update(Tache tache, boolean updateNotified) {
        String sql;
        if (updateNotified) {
            sql = "UPDATE tache SET nom_tache = ?, description = ?, date_debut = ?, date_fin = ?, etat = ?, id_projet = ?, id_employe = ?, notified = ? WHERE id_tache = ?";
        } else {
            sql = "UPDATE tache SET nom_tache = ?, description = ?, date_debut = ?, date_fin = ?, etat = ?, id_projet = ?, id_employe = ? WHERE id_tache = ?";
        }
        
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, tache.getNom_tache());
            statement.setString(2, tache.getDescription());
            statement.setDate(3, java.sql.Date.valueOf(tache.getDate_debut()));
            statement.setDate(4, java.sql.Date.valueOf(tache.getDate_fin()));
            statement.setString(5, tache.getEtat());
            statement.setInt(6, tache.getId_projet());
            statement.setInt(7, tache.getId_employe());
            
            if (updateNotified) {
                statement.setBoolean(8, tache.isNotified());
                statement.setInt(9, tache.getId_tache());
            } else {
                statement.setInt(8, tache.getId_tache());
            }
            
            int rowsUpdated = statement.executeUpdate();
            return rowsUpdated > 0;
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Erreur lors de la mise à jour de la tâche", e);
            return false;
        }
    }

    // Méthode utilitaire pour mapper un ResultSet à un objet Tache
    private Tache mapResultSetToTache(ResultSet resultSet) throws SQLException {
        Tache tache = new Tache();
        tache.setId_tache(resultSet.getInt("id_tache"));
        tache.setNom_tache(resultSet.getString("nom_tache"));
        tache.setDescription(resultSet.getString("description"));
        
        // Conversion des dates SQL en LocalDate
        Date dateDebut = resultSet.getDate("date_debut");
        Date dateFin = resultSet.getDate("date_fin");
        tache.setDate_debut(dateDebut != null ? dateDebut.toLocalDate() : LocalDate.now());
        tache.setDate_fin(dateFin != null ? dateFin.toLocalDate() : LocalDate.now().plusDays(7));
        
        tache.setEtat(resultSet.getString("etat"));
        tache.setId_projet(resultSet.getInt("id_projet"));
        tache.setId_employe(resultSet.getInt("id_employe"));
        
        return tache;
    }
    
    // Implémentation des méthodes de l'interface IService
    /**
     * Ajoute une nouvelle tâche à la base de données
     * @param t La tâche à ajouter
     * @throws SQLException En cas d'erreur SQL
     */

    public void ajouter(Tache t) throws SQLException {
        try {
            // Obtenir l'ID de l'utilisateur connecté ou un ID valide de la table user
            int userId = 1; // Valeur par défaut (admin)
            try (Statement stmt = connection.createStatement();
                 ResultSet rs = stmt.executeQuery("SELECT id FROM user LIMIT 1")) {
                if (rs.next()) {
                    userId = rs.getInt("id");
                }
            } catch (SQLException e) {
                LOGGER.warning("Impossible de récupérer un ID utilisateur valide: " + e.getMessage());
            }
            
            // Utiliser une requête qui inclut explicitement la colonne 'id'
            String req = "INSERT INTO tache (id, nom_tache, description, date_debut, date_fin, etat, id_projet, id_employe) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
            
            try (PreparedStatement ps = connection.prepareStatement(req, Statement.RETURN_GENERATED_KEYS)) {
                // Utiliser l'ID utilisateur comme valeur pour 'id'
                ps.setInt(1, userId);
                ps.setString(2, t.getNom_tache());
                ps.setString(3, t.getDescription());
                ps.setDate(4, java.sql.Date.valueOf(t.getDate_debut()));
                
                if (t.getDate_fin() != null) {
                    ps.setDate(5, java.sql.Date.valueOf(t.getDate_fin()));
                } else {
                    ps.setNull(5, java.sql.Types.DATE);
                }
                
                ps.setString(6, t.getEtat());
                ps.setInt(7, t.getId_projet());
                
                if (t.getId_employe() > 0) {
                    ps.setInt(8, t.getId_employe());
                } else {
                    ps.setNull(8, java.sql.Types.INTEGER);
                }
                
                int affectedRows = ps.executeUpdate();
                
                if (affectedRows > 0) {
                    // Récupérer l'ID généré pour id_tache si disponible
                    try (ResultSet generatedKeys = ps.getGeneratedKeys()) {
                        if (generatedKeys.next()) {
                            int generatedId = generatedKeys.getInt(1);
                            t.setId_tache(generatedId);
                            
                            // Essayer également de définir l'ID si la classe a cette méthode
                            try {
                                java.lang.reflect.Method setIdMethod = t.getClass().getMethod("setId", int.class);
                                setIdMethod.invoke(t, userId);
                            } catch (Exception e) {
                                // Ignorer si la méthode n'existe pas
                            }
                        }
                    }
                    
                    LOGGER.info("Tâche ajoutée avec succès, ID: " + t.getId_tache());
                }
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Erreur lors de l'ajout de la tâche", e);
            throw e;
        }
    }
    

    public void modifier(Tache tache) throws SQLException {
        String req = "UPDATE tache SET nom_tache=?, description=?, date_debut=?, date_fin=?, etat=?, id_projet=?, id_employe=?, commentaire=? WHERE id=?";
        PreparedStatement ps = connection.prepareStatement(req);
        ps.setString(1, tache.getNom_tache());
        ps.setString(2, tache.getDescription());
        ps.setDate(3, tache.getDate_debut() != null ? java.sql.Date.valueOf(tache.getDate_debut()) : null);
        ps.setDate(4, tache.getDate_fin() != null ? java.sql.Date.valueOf(tache.getDate_fin()) : null);
        ps.setString(5, tache.getEtat());
        ps.setInt(6, tache.getId_projet());
        ps.setInt(7, tache.getId_employe());
        ps.setString(8, tache.getCommentaire());
        ps.setInt(9, tache.getId());
        ps.executeUpdate();
    }
    

    public void supprimer(Tache tache) throws SQLException {
        supprimer(tache.getId_tache());
    }

    public void supprimer(int id) throws SQLException {
        String sql = "DELETE FROM tache WHERE id_tache = ?";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, id);
            int rowsDeleted = statement.executeUpdate();
            
            if (rowsDeleted > 0) {
                LOGGER.info("Tâche supprimée avec succès : " + id);
            } else {
                LOGGER.warning("Aucune tâche trouvée avec l'ID : " + id);
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Erreur lors de la suppression de la tâche", e);
            throw e;
        }
    }

    public List<Tache> recuperer() throws SQLException {
        List<Tache> taches = new ArrayList<>();
        String sql = "SELECT t.*, p.nom_projet FROM tache t LEFT JOIN projet p ON t.id_projet = p.id_projet";
        try (Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery(sql)) {
            while (resultSet.next()) {
                Tache tache = new Tache();
                tache.setId_tache(resultSet.getInt("id_tache"));
                tache.setNom_tache(resultSet.getString("nom_tache"));
                tache.setDescription(resultSet.getString("description"));
                
                // Conversion des dates
                Date dateDebut = resultSet.getDate("date_debut");
                if (dateDebut != null) {
                    tache.setDate_debut(dateDebut.toLocalDate());
                }
                
                Date dateFin = resultSet.getDate("date_fin");
                if (dateFin != null) {
                    tache.setDate_fin(dateFin.toLocalDate());
                }
                
                tache.setEtat(resultSet.getString("etat"));
                tache.setId_projet(resultSet.getInt("id_projet"));
                
                // Récupérer l'ID de l'employé s'il existe
                int idEmploye = resultSet.getInt("id_employe");
                if (!resultSet.wasNull()) {
                    tache.setId_employe(idEmploye);
                }
                
                // Récupérer le nom du projet
                String nomProjet = resultSet.getString("nom_projet");
                tache.setNomProjet(nomProjet);
                
                taches.add(tache);
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Erreur lors de la récupération des tâches", e);
            throw e;
        }
        return taches;
    }

    public Tache recupererParId(int id) throws SQLException {
        String sql = "SELECT t.*, p.nom_projet FROM tache t LEFT JOIN projet p ON t.id_projet = p.id_projet WHERE t.id_tache = ?";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, id);
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                Tache tache = new Tache();
                tache.setId_tache(resultSet.getInt("id_tache"));
                tache.setNom_tache(resultSet.getString("nom_tache"));
                tache.setDescription(resultSet.getString("description"));
                
                // Conversion des dates
                Date dateDebut = resultSet.getDate("date_debut");
                if (dateDebut != null) {
                    tache.setDate_debut(dateDebut.toLocalDate());
                }
                
                Date dateFin = resultSet.getDate("date_fin");
                if (dateFin != null) {
                    tache.setDate_fin(dateFin.toLocalDate());
                }
                
                tache.setEtat(resultSet.getString("etat"));
                tache.setId_projet(resultSet.getInt("id_projet"));
                
                // Récupérer l'ID de l'employé s'il existe
                int idEmploye = resultSet.getInt("id_employe");
                if (!resultSet.wasNull()) {
                    tache.setId_employe(idEmploye);
                }
                
                // Récupérer le nom du projet
                String nomProjet = resultSet.getString("nom_projet");
                tache.setNomProjet(nomProjet);
                
                return tache;
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Erreur lors de la récupération de la tâche par ID", e);
            throw e;
        }
        return null;
    }
    
    public List<Tache> rechercher(String critere) throws SQLException {
        List<Tache> taches = new ArrayList<>();
        String sql = "SELECT t.*, p.nom_projet FROM tache t LEFT JOIN projet p ON t.id_projet = p.id_projet WHERE t.nom_tache LIKE ? OR t.description LIKE ? OR t.etat LIKE ?";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            String searchPattern = "%" + critere + "%";
            statement.setString(1, searchPattern);
            statement.setString(2, searchPattern);
            statement.setString(3, searchPattern);
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                Tache tache = mapResultSetToTache(resultSet);
                taches.add(tache);
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Erreur lors de la recherche de tâches", e);
            throw e;
        }
        return taches;
    }
    

    public boolean existe(int id) throws SQLException {
        String sql = "SELECT COUNT(*) FROM tache WHERE id_tache = ?";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, id);
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                return resultSet.getInt(1) > 0;
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Erreur lors de la vérification d'existence de la tâche", e);
            throw e;
        }
        return false;
    }
    

    public int compter() throws SQLException {
        String sql = "SELECT COUNT(*) FROM tache";
        try (PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet resultSet = statement.executeQuery()) {
            if (resultSet.next()) {
                return resultSet.getInt(1);
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Erreur lors du comptage des tâches", e);
            throw e;
        }
        return 0;
    }
    

    public List<Tache> afficher() throws SQLException {
        return recuperer();
    }
    
    /**
     * Récupère toutes les tâches.
     * Alias pour recuperer().
     * @return Liste de toutes les tâches
     * @throws SQLException Si erreur de base de données
     */
    public List<Tache> findAll() throws SQLException {
        return recuperer();
    }
    
    /**
     * Sauvegarde une tâche (alias pour ajouter).
     * @param tache Tâche à sauvegarder
     * @throws SQLException Si erreur de base de données
     */
    public void save(Tache tache) throws SQLException {
        ajouter(tache);
    }

    /**
     * Récupère les statistiques des tâches.
     * @return Liste contenant [terminées, en cours, en retard]
     * @throws SQLException Si erreur de base de données
     */
    public List<Integer> getStatistics() throws SQLException {
        List<Integer> stats = new ArrayList<>();
        String completedSql = "SELECT COUNT(*) FROM tache WHERE etat = 'TERMINE'";
        String ongoingSql = "SELECT COUNT(*) FROM tache WHERE etat != 'TERMINE'";
        String delayedSql = "SELECT COUNT(*) FROM tache WHERE etat != 'TERMINE' AND date_fin < ?";

        // Tâches terminées
        try (Statement statement = connection.createStatement();
             ResultSet rs = statement.executeQuery(completedSql)) {
            rs.next();
            stats.add(rs.getInt(1));
        }

        // Tâches en cours
        try (Statement statement = connection.createStatement();
             ResultSet rs = statement.executeQuery(ongoingSql)) {
            rs.next();
            stats.add(rs.getInt(1));
        }

        // Tâches en retard
        try (PreparedStatement statement = connection.prepareStatement(delayedSql)) {
            statement.setObject(1, LocalDate.now());
            ResultSet rs = statement.executeQuery();
            rs.next();
            stats.add(rs.getInt(1));
        }

        return stats;
    }

    /**
     * Récupère les tâches récentes
     * @param limit Nombre maximum de tâches à récupérer
     * @return Liste des tâches récentes
     */
    public List<Tache> getRecentTasks(int limit) {
        List<Tache> taches = new ArrayList<>();
        String sql = "SELECT * FROM tache ORDER BY date_debut DESC LIMIT ?";
        
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, limit);
            
            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    Tache tache = new Tache();
                    tache.setId_tache(resultSet.getInt("id_tache"));
                    tache.setNom_tache(resultSet.getString("nom_tache"));
                    tache.setDescription(resultSet.getString("description"));
                    tache.setDate_debut(resultSet.getDate("date_debut").toLocalDate());
                    tache.setDate_fin(resultSet.getDate("date_fin").toLocalDate());
                    tache.setEtat(resultSet.getString("etat"));
                    tache.setId_projet(resultSet.getInt("id_projet"));
                    tache.setId_employe(resultSet.getInt("id_employe"));
                    taches.add(tache);
                }
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Erreur lors de la récupération des tâches récentes", e);
        }
        
        return taches;
    }

    /**
     * Assigne une tâche à un employé
     * @param idTache ID de la tâche
     * @param idEmploye ID de l'employé
     * @return true si l'assignation a réussi
     * @throws SQLException Si erreur de base de données
     */
    public boolean assignTaskToEmployee(int idTache, int idEmploye) throws SQLException {
        String sql = "UPDATE tache SET id_employe = ? WHERE id_tache = ?";
        
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, idEmploye);
            statement.setInt(2, idTache);
            
            int rowsAffected = statement.executeUpdate();
            return rowsAffected > 0;
        }
    }

    /**
     * Récupère les tâches assignées à un employé
     * @param idEmploye ID de l'employé
     * @return Liste des tâches assignées à l'employé
     * @throws SQLException Si erreur de base de données
     */
    public List<Tache> findTasksByEmployee(int idEmploye) throws SQLException {
        List<Tache> taches = new ArrayList<>();
        String sql = "SELECT t.*, p.nom_projet FROM tache t JOIN projet p ON t.id_projet = p.id_projet WHERE t.id_employe = ?";
        
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, idEmploye);
            
            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    Tache tache = new Tache();
                    tache.setId_tache(resultSet.getInt("id_tache"));
                    tache.setId_projet(resultSet.getInt("id_projet"));
                    tache.setNom_tache(resultSet.getString("nom_tache"));
                    tache.setDescription(resultSet.getString("description"));
                    tache.setDate_debut(resultSet.getObject("date_debut", LocalDate.class));
                    tache.setDate_fin(resultSet.getObject("date_fin", LocalDate.class));
                    tache.setEtat(resultSet.getString("etat"));
                    tache.setId_employe(resultSet.getInt("id_employe"));
                    // Ajouter le nom du projet comme information supplémentaire
                    tache.setNomProjet(resultSet.getString("nom_projet"));
                    
                    taches.add(tache);
                }
            }
        }
        
        return taches;
    }

    /**
     * Calcule la performance d'un employé basée sur ses tâches
     * @param idEmploye ID de l'employé
     * @return Pourcentage de performance (0-100)
     * @throws SQLException Si erreur de base de données
     */
    public double calculateEmployeePerformance(int idEmploye) throws SQLException {
        List<Tache> taches = findByEmployeId(idEmploye);
        
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
}
