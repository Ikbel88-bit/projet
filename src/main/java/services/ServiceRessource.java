package services;

import org.example.models.Ressource;
import utils.MyDatabase;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ServiceRessource implements IService<Ressource> {
    private Connection con;

    public ServiceRessource() {
        con = MyDatabase.getInstance().getConnection();
    }

    @Override
    public void ajouter(Ressource ressource) throws SQLException {
        String req = "INSERT INTO ressources(nom, type, description, disponible, emplacement, etat) VALUES (?, ?, ?, ?, ?, ?)";
        try (PreparedStatement ps = con.prepareStatement(req, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, ressource.getNom());
            ps.setString(2, ressource.getType());
            ps.setString(3, ressource.getDescription());
            ps.setBoolean(4, ressource.isDisponible());
            ps.setString(5, ressource.getEmplacement());
            ps.setString(6, ressource.getEtat());
            ps.executeUpdate();
            
            // Récupérer l'ID généré
            try (ResultSet generatedKeys = ps.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    ressource.setIdRessource(generatedKeys.getInt(1));
                }
            }
            
            System.out.println("Ressource ajoutée avec l'ID: " + ressource.getIdRessource());
        } catch (SQLException e) {
            System.err.println("Erreur SQL lors de l'ajout : " + e.getMessage());
            e.printStackTrace();
            throw new SQLException("Erreur lors de l'ajout de la ressource", e);
        }
    }

    @Override
    public void modifier(Ressource ressource) throws SQLException {
        String req = "UPDATE ressources SET nom=?, type=?, description=?, disponible=?, emplacement=?, etat=? WHERE id_ressource=?";
        try (PreparedStatement ps = con.prepareStatement(req)) {
            ps.setString(1, ressource.getNom());
            ps.setString(2, ressource.getType());
            ps.setString(3, ressource.getDescription());
            ps.setBoolean(4, ressource.isDisponible());
            ps.setString(5, ressource.getEmplacement());
            ps.setString(6, ressource.getEtat());
            ps.setInt(7, ressource.getIdRessource());
            ps.executeUpdate();
            System.out.println("Ressource modifiée");
        } catch (SQLException e) {
            e.printStackTrace();
            throw new SQLException("Erreur lors de la modification de la ressource", e);
        }
    }

    @Override
    public void supprimer(Ressource ressource) throws SQLException {
        String req = "DELETE FROM ressources WHERE id_ressource=?";
        try (PreparedStatement ps = con.prepareStatement(req)) {
            ps.setInt(1, ressource.getIdRessource());
            ps.executeUpdate();
            System.out.println("Ressource supprimée");
        } catch (SQLException e) {
            e.printStackTrace();
            throw new SQLException("Erreur lors de la suppression de la ressource", e);
        }
    }

    @Override
    public List<Ressource> recuperer() throws SQLException {
        List<Ressource> ressources = new ArrayList<>();
        String req = "SELECT * FROM ressources";
        try (Statement st = con.createStatement(); ResultSet rs = st.executeQuery(req)) {
            ResultSetMetaData metaData = rs.getMetaData();
            String idCol = metaData.getColumnName(1); // nom réel de la première colonne
            System.out.println("Nom réel de la colonne ID : " + idCol);
            while (rs.next()) {
                int id_ressource = rs.getInt(idCol);
                String nom = rs.getString("nom");
                String type = rs.getString("type");
                String description = rs.getString("description");
                boolean disponible = rs.getBoolean("disponible");
                String emplacement = rs.getString("emplacement");
                String etat = rs.getString("etat");
                Ressource r = new Ressource(id_ressource, nom, type, description, disponible, emplacement, etat);
                System.out.println("DEBUG: " + r);
                ressources.add(r);
            }
        } catch (SQLException e) {
            System.err.println("Erreur SQL lors de la récupération : " + e.getMessage());
            e.printStackTrace();
            throw new SQLException("Erreur lors de la récupération des ressources", e);
        }
        System.out.println("Ressources récupérées : " + ressources.size());
        return ressources;
    }
}
