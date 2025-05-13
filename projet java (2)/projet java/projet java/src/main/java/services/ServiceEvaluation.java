package services;

import entities.Evaluation;
import utils.MyDatabase;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ServiceEvaluation implements IService<Evaluation> {
    private Connection con;

    public ServiceEvaluation() {
        try {
            con = MyDatabase.getInstance().getCnx();
            if (con == null) {
                throw new SQLException("Pas de connexion à la base de données");
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors de l'initialisation du service : " + e.getMessage());
        }
    }

    private void checkConnection() throws SQLException {
        if (con == null || con.isClosed()) {
            con = MyDatabase.getInstance().getCnx();
            if (con == null) {
                throw new SQLException("Pas de connexion à la base de données");
            }
        }
    }

    @Override
    public void ajouter(Evaluation e) throws SQLException {
        checkConnection();
        
        String req = "INSERT INTO evaluations(id_entretien, commentaire, note, date_evaluation) VALUES (?, ?, ?, ?)";
        try (PreparedStatement ps = con.prepareStatement(req)) {
            ps.setInt(1, e.getId_entretien());
            ps.setString(2, e.getCommentaire());
            ps.setInt(3, e.getNote());
            ps.setString(4, e.getDate_evaluation());
            ps.executeUpdate();
            System.out.println("Évaluation ajoutée avec succès");
        } catch (SQLException ex) {
            System.err.println("Erreur lors de l'ajout de l'évaluation: " + ex.getMessage());
            throw ex;
        }
    }

    @Override
    public void modifier(Evaluation e) throws SQLException {
        checkConnection();
        
        String req = "UPDATE evaluations SET id_entretien=?, commentaire=?, note=?, date_evaluation=? WHERE id_evaluation=?";
        try (PreparedStatement ps = con.prepareStatement(req)) {
            ps.setInt(1, e.getId_entretien());
            ps.setString(2, e.getCommentaire());
            ps.setInt(3, e.getNote());
            ps.setString(4, e.getDate_evaluation());
            ps.setInt(5, e.getId_evaluation());
            ps.executeUpdate();
            System.out.println("Évaluation modifiée avec succès");
        } catch (SQLException ex) {
            System.err.println("Erreur lors de la modification de l'évaluation: " + ex.getMessage());
            throw ex;
        }
    }

    @Override
    public void supprimer(Evaluation e) throws SQLException {
        checkConnection();
        
        String req = "DELETE FROM evaluations WHERE id_evaluation=?";
        try (PreparedStatement ps = con.prepareStatement(req)) {
            ps.setInt(1, e.getId_evaluation());
            ps.executeUpdate();
            System.out.println("Évaluation supprimée avec succès");
        } catch (SQLException ex) {
            System.err.println("Erreur lors de la suppression de l'évaluation: " + ex.getMessage());
            throw ex;
        }
    }

    @Override
    public List<Evaluation> recuperer() throws SQLException {
        checkConnection();
        
        List<Evaluation> evaluations = new ArrayList<>();
        String req = "SELECT * FROM evaluations";
        try (Statement st = con.createStatement();
             ResultSet rs = st.executeQuery(req)) {
            while (rs.next()) {
                Evaluation e = new Evaluation(
                    rs.getInt("id_evaluation"),
                    rs.getInt("id_entretien"),
                    rs.getString("commentaire"),
                    rs.getInt("note"),
                    rs.getString("date_evaluation")
                );
                evaluations.add(e);
            }
        } catch (SQLException ex) {
            System.err.println("Erreur lors de la récupération des évaluations: " + ex.getMessage());
            throw ex;
        }
        return evaluations;
    }
}
