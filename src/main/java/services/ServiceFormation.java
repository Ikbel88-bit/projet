package services;

import entities.Formation;
import utils.MyDatabase;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ServiceFormation implements IService<Formation> {
    private Connection con;

    public ServiceFormation() {
        con = MyDatabase.getInstance().getConnection();
    }

    @Override
    public void ajouter(Formation formation) throws SQLException {
        String req = "INSERT INTO formation(titre, description, date_debut, date_fin, formateur, capacite) " +
                "VALUES (?, ?, ?, ?, ?, ?)";
        try (PreparedStatement ps = con.prepareStatement(req, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, formation.getTitre());
            ps.setString(2, formation.getDescription());
            ps.setDate(3, new java.sql.Date(formation.getDate_debut().getTime()));
            ps.setDate(4, new java.sql.Date(formation.getDate_fin().getTime()));
            ps.setString(5, formation.getFormateur());
            ps.setInt(6, formation.getCapacite());
            ps.executeUpdate();

            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    formation.setId(rs.getInt(1));
                    System.out.println("Formation ajoutée avec ID : " + formation.getId());
                } else {
                    throw new SQLException("Échec de la récupération de l'ID généré pour la formation.");
                }
            }
        }
    }

    @Override
    public void modifier(Formation formation) throws SQLException {
        String req = "UPDATE formation SET titre=?, description=?, date_debut=?, date_fin=?, formateur=?, capacite=? WHERE id=?";
        try (PreparedStatement ps = con.prepareStatement(req)) {
            ps.setString(1, formation.getTitre());
            ps.setString(2, formation.getDescription());
            ps.setDate(3, new java.sql.Date(formation.getDate_debut().getTime()));
            ps.setDate(4, new java.sql.Date(formation.getDate_fin().getTime()));
            ps.setString(5, formation.getFormateur());
            ps.setInt(6, formation.getCapacite());
            ps.setInt(7, formation.getId());
            int rowsAffected = ps.executeUpdate();
            if (rowsAffected > 0) {
                System.out.println("Formation modifiée avec ID : " + formation.getId());
            } else {
                System.out.println("Aucune formation trouvée avec ID : " + formation.getId());
            }
        }
    }

    @Override
    public void supprimer(Formation formation) throws SQLException {
        String req = "DELETE FROM formation WHERE id=?";
        try (PreparedStatement ps = con.prepareStatement(req)) {
            ps.setInt(1, formation.getId());
            int rowsAffected = ps.executeUpdate();
            if (rowsAffected > 0) {
                System.out.println("Formation supprimée avec ID : " + formation.getId());
            } else {
                System.out.println("Aucune formation trouvée avec ID : " + formation.getId());
            }
        }
    }

    @Override
    public List<Formation> recuperer() throws SQLException {
        List<Formation> formations = new ArrayList<>();
        String req = "SELECT * FROM formation";
        try (Statement st = con.createStatement();
             ResultSet rs = st.executeQuery(req)) {
            while (rs.next()) {
                int id = rs.getInt("id");
                String titre = rs.getString("titre");
                String description = rs.getString("description");
                java.sql.Date sqlDateDebut = rs.getDate("date_debut");
                java.sql.Date sqlDateFin = rs.getDate("date_fin");
                // Convert java.sql.Date to java.util.Date
                java.util.Date date_debut = sqlDateDebut != null ? new java.util.Date(sqlDateDebut.getTime()) : null;
                java.util.Date date_fin = sqlDateFin != null ? new java.util.Date(sqlDateDebut.getTime()) : null;
                String formateur = rs.getString("formateur");
                int capacite = rs.getInt("capacite");
                Formation f = new Formation(id, titre, description, date_debut, date_fin, formateur, capacite);
                formations.add(f);
            }
        }
        return formations;
    }
}