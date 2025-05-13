package services;

import entities.Reclamation;
import utils.MyDatabase;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class ServiceReclamation {
    private Connection con;

    public ServiceReclamation() {
        con = MyDatabase.getInstance().getCnx();
    }

    public void ajouter(Reclamation reclamation) throws SQLException {
        String req = "INSERT INTO reclamation(id, description) VALUES (?, ?)";
        try (PreparedStatement ps = con.prepareStatement(req)) {
            ps.setInt(1, reclamation.getUserId());
            ps.setString(2, reclamation.getDescription());
            ps.executeUpdate();
            System.out.println("Réclamation ajoutée avec succès");
        } catch (SQLException e) {
            System.err.println("Erreur lors de l'ajout de la réclamation: " + e.getMessage());
            throw e;
        }
    }

    public List<Reclamation> recupererToutes() throws SQLException {
        List<Reclamation> reclamations = new ArrayList<>();
        String req = "SELECT r.*, u.nom, u.prenom FROM reclamation r JOIN user u ON r.id = u.id";
        try (Statement st = con.createStatement();
             ResultSet rs = st.executeQuery(req)) {
            while (rs.next()) {
                int reclamationId = rs.getInt("reclamation_id");
                int userId = rs.getInt("id");
                String description = rs.getString("description");
                String reponse = rs.getString("reponse");
                Reclamation reclamation = new Reclamation(reclamationId, userId, description, reponse);
                reclamations.add(reclamation);
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors de la récupération des réclamations: " + e.getMessage());
            throw e;
        }
        return reclamations;
    }

    public List<Reclamation> recupererParUtilisateur(int userId) throws SQLException {
        List<Reclamation> reclamations = new ArrayList<>();
        String req = "SELECT * FROM reclamation WHERE id = ?";
        try (PreparedStatement ps = con.prepareStatement(req)) {
            ps.setInt(1, userId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                int reclamationId = rs.getInt("reclamation_id");
                String description = rs.getString("description");
                String reponse = rs.getString("reponse");
                Reclamation reclamation = new Reclamation(reclamationId, userId, description, reponse);
                reclamations.add(reclamation);
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors de la récupération des réclamations de l'utilisateur: " + e.getMessage());
            throw e;
        }
        return reclamations;
    }

    public void supprimer(int reclamationId) throws SQLException {
        String req = "DELETE FROM reclamation WHERE reclamation_id = ?";
        try (PreparedStatement ps = con.prepareStatement(req)) {
            ps.setInt(1, reclamationId);
            int rowsAffected = ps.executeUpdate();
            if (rowsAffected > 0) {
                System.out.println("Réclamation supprimée avec succès");
            } else {
                System.out.println("Aucune réclamation trouvée avec l'ID: " + reclamationId);
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors de la suppression de la réclamation: " + e.getMessage());
            throw e;
        }
    }

    // Méthode pour que l'admin réponde à une réclamation
    public void repondreAReclamation(int reclamationId, String reponse) throws SQLException {
        String req = "UPDATE reclamation SET reponse = ? WHERE reclamation_id = ?";
        try (PreparedStatement ps = con.prepareStatement(req)) {
            ps.setString(1, reponse);
            ps.setInt(2, reclamationId);
            ps.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Erreur lors de la réponse à la réclamation: " + e.getMessage());
            throw e;
        }
    }
}
