package services;

import entities.Rapport;
import utils.MyDatabase;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Service pour gérer les rapports
 */
public class ServiceRapport {
    private static final Logger LOGGER = Logger.getLogger(ServiceRapport.class.getName());
    private Connection connection;

    public ServiceRapport() {
        connection = MyDatabase.getInstance().getConnection();
        createTableIfNotExists();
    }

    public ServiceRapport(Connection connection) {
        this.connection = connection;
        createTableIfNotExists();
    }

    private void createTableIfNotExists() {
        try (Statement stmt = connection.createStatement()) {
            String sql = "CREATE TABLE IF NOT EXISTS rapport (" +
                    "id INT PRIMARY KEY AUTO_INCREMENT, " +
                    "titre VARCHAR(255) NOT NULL, " +
                    "contenu TEXT, " +
                    "date_creation DATE DEFAULT (CURRENT_DATE), " +
                    "id_projet INT, " +
                    "id_tache INT, " +
                    "id_auteur INT NOT NULL, " +
                    "statut VARCHAR(50) DEFAULT 'BROUILLON', " +
                    "FOREIGN KEY (id_projet) REFERENCES projet(id_projet), " +
                    "FOREIGN KEY (id_tache) REFERENCES tache(id_tache), " +
                    "FOREIGN KEY (id_auteur) REFERENCES user(id)" +
                    ")";
            stmt.execute(sql);
            LOGGER.info("Table rapport vérifiée/créée");
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Erreur lors de la création de la table rapport", e);
        }
    }

    /**
     * Ajoute un nouveau rapport
     * @param rapport le rapport à ajouter
     * @return true si l'ajout a réussi, false sinon
     */
    public boolean ajouter(Rapport rapport) {
        String sql = "INSERT INTO rapport (titre, contenu, date_creation, id_projet, id_tache, id_auteur, statut) " +
                     "VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (PreparedStatement pstmt = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            pstmt.setString(1, rapport.getTitre());
            pstmt.setString(2, rapport.getContenu());
            pstmt.setDate(3, Date.valueOf(rapport.getDateCreation()));
            pstmt.setInt(4, rapport.getIdProjet());
            
            if (rapport.getIdTache() > 0) {
                pstmt.setInt(5, rapport.getIdTache());
            } else {
                pstmt.setNull(5, Types.INTEGER);
            }
            
            pstmt.setInt(6, rapport.getIdAuteur());
            pstmt.setString(7, rapport.getStatut());
            
            int affectedRows = pstmt.executeUpdate();
            if (affectedRows > 0) {
                try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        rapport.setId(generatedKeys.getInt(1));
                        return true;
                    }
                }
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Erreur lors de l'ajout d'un rapport", e);
        }
        return false;
    }

    /**
     * Met à jour un rapport existant
     * @param rapport le rapport à mettre à jour
     * @return true si la mise à jour a réussi, false sinon
     */
    public boolean modifier(Rapport rapport) {
        String sql = "UPDATE rapport SET titre = ?, contenu = ?, date_creation = ?, " +
                     "id_projet = ?, id_tache = ?, id_auteur = ?, statut = ? WHERE id = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, rapport.getTitre());
            pstmt.setString(2, rapport.getContenu());
            pstmt.setDate(3, Date.valueOf(rapport.getDateCreation()));
            pstmt.setInt(4, rapport.getIdProjet());
            
            if (rapport.getIdTache() > 0) {
                pstmt.setInt(5, rapport.getIdTache());
            } else {
                pstmt.setNull(5, Types.INTEGER);
            }
            
            pstmt.setInt(6, rapport.getIdAuteur());
            pstmt.setString(7, rapport.getStatut());
            pstmt.setInt(8, rapport.getId());
            
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Erreur lors de la modification d'un rapport", e);
            return false;
        }
    }

    /**
     * Supprime un rapport
     * @param id l'identifiant du rapport
     * @return true si la suppression a réussi, false sinon
     */
    public boolean supprimer(int id) {
        String sql = "DELETE FROM rapport WHERE id = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Erreur lors de la suppression d'un rapport", e);
            return false;
        }
    }

    /**
     * Récupère un rapport par son identifiant
     * @param id l'identifiant du rapport
     * @return le rapport ou null s'il n'existe pas
     */
    public Rapport getById(int id) {
        String sql = "SELECT * FROM rapport WHERE id = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToRapport(rs);
                }
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Erreur lors de la récupération d'un rapport", e);
        }
        return null;
    }

    /**
     * Récupère tous les rapports
     * @return la liste des rapports
     */
    public List<Rapport> getAll() {
        List<Rapport> rapports = new ArrayList<>();
        String sql = "SELECT * FROM rapport ORDER BY date_creation DESC";
        
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                rapports.add(mapResultSetToRapport(rs));
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Erreur lors de la récupération des rapports", e);
        }
        
        return rapports;
    }

    /**
     * Récupère tous les rapports d'un projet
     * @param idProjet l'identifiant du projet
     * @return la liste des rapports du projet
     */
    public List<Rapport> getByProjet(int idProjet) {
        List<Rapport> rapports = new ArrayList<>();
        String sql = "SELECT * FROM rapport WHERE id_projet = ? ORDER BY date_creation DESC";
        
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, idProjet);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    rapports.add(mapResultSetToRapport(rs));
                }
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Erreur lors de la récupération des rapports par projet", e);
        }
        
        return rapports;
    }
    
    /**
     * Récupère tous les rapports d'une tâche
     * @param idTache l'identifiant de la tâche
     * @return la liste des rapports de la tâche
     */
    public List<Rapport> getByTache(int idTache) {
        List<Rapport> rapports = new ArrayList<>();
        String sql = "SELECT * FROM rapport WHERE id_tache = ? ORDER BY date_creation DESC";
        
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, idTache);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    rapports.add(mapResultSetToRapport(rs));
                }
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Erreur lors de la récupération des rapports par tâche", e);
        }
        
        return rapports;
    }
    
    /**
     * Récupère tous les rapports d'un auteur
     * @param idAuteur l'identifiant de l'auteur
     * @return la liste des rapports de l'auteur
     */
    public List<Rapport> getByAuteur(int idAuteur) {
        List<Rapport> rapports = new ArrayList<>();
        String sql = "SELECT * FROM rapport WHERE id_auteur = ? ORDER BY date_creation DESC";
        
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, idAuteur);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    rapports.add(mapResultSetToRapport(rs));
                }
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Erreur lors de la récupération des rapports par auteur", e);
        }
        
        return rapports;
    }
    
    /**
     * Récupère tous les rapports avec un statut spécifique
     * @param statut le statut recherché
     * @return la liste des rapports avec ce statut
     */
    public List<Rapport> getByStatut(String statut) {
        List<Rapport> rapports = new ArrayList<>();
        String sql = "SELECT * FROM rapport WHERE statut = ? ORDER BY date_creation DESC";
        
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, statut);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    rapports.add(mapResultSetToRapport(rs));
                }
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Erreur lors de la récupération des rapports par statut", e);
        }
        
        return rapports;
    }
    
    /**
     * Convertit un ResultSet en objet Rapport
     * @param rs le ResultSet à convertir
     * @return l'objet Rapport créé
     * @throws SQLException si une erreur SQL survient
     */
    private Rapport mapResultSetToRapport(ResultSet rs) throws SQLException {
        Rapport rapport = new Rapport();
        rapport.setId(rs.getInt("id"));
        rapport.setTitre(rs.getString("titre"));
        rapport.setContenu(rs.getString("contenu"));
        
        Date dateCreation = rs.getDate("date_creation");
        rapport.setDateCreation(dateCreation != null ? dateCreation.toLocalDate() : LocalDate.now());
        
        rapport.setIdProjet(rs.getInt("id_projet"));
        rapport.setIdTache(rs.getInt("id_tache"));
        rapport.setIdAuteur(rs.getInt("id_auteur"));
        rapport.setStatut(rs.getString("statut"));
        
        return rapport;
    }
    
    /**
     * Récupère les statistiques des rapports
     * @return Liste contenant [brouillons, publiés, total]
     */
    public List<Integer> getStatistics() {
        List<Integer> stats = new ArrayList<>();
        String brouillonsSql = "SELECT COUNT(*) FROM rapport WHERE statut = 'BROUILLON'";
        String publiesSql = "SELECT COUNT(*) FROM rapport WHERE statut = 'PUBLIE'";
        String totalSql = "SELECT COUNT(*) FROM rapport";

        // Rapports en brouillon
        try (Statement statement = connection.createStatement();
             ResultSet rs = statement.executeQuery(brouillonsSql)) {
            rs.next();
            stats.add(rs.getInt(1));
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Erreur lors du comptage des rapports en brouillon", e);
            stats.add(0);
        }

        // Rapports publiés
        try (Statement statement = connection.createStatement();
             ResultSet rs = statement.executeQuery(publiesSql)) {
            rs.next();
            stats.add(rs.getInt(1));
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Erreur lors du comptage des rapports publiés", e);
            stats.add(0);
        }

        // Total des rapports
        try (Statement statement = connection.createStatement();
             ResultSet rs = statement.executeQuery(totalSql)) {
            rs.next();
            stats.add(rs.getInt(1));
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Erreur lors du comptage total des rapports", e);
            stats.add(0);
        }

        return stats;
    }
}
