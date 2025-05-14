package services;

import entities.Entretien;
import utils.MyDatabase;
import utils.DatabaseUpdater;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ServiceEntretien implements IService<Entretien> {
    private Connection con;

    /**
     * Retourne la connexion à la base de données
     * @return La connexion à la base de données
     */
    public Connection getConnection() {
        return con;
    }

    public ServiceEntretien() {
        con = MyDatabase.getInstance().getCnx();
        // Mettre à jour la structure de la base de données si nécessaire
        DatabaseUpdater.updateEntretiensTable(con);
    }

    @Override
    public void ajouter(Entretien e) throws SQLException {
        try {
            // Vérifier si les colonnes existent
            boolean columnsExist = DatabaseUpdater.columnExists(con, "entretiens", "cv_path") &&
                                  DatabaseUpdater.columnExists(con, "entretiens", "lettre_motivation_path");

            String req;
            if (columnsExist) {
                req = "INSERT INTO entretiens(titre, date_entretien, lieu, participant, statut, cv_path, lettre_motivation_path) VALUES (?, ?, ?, ?, ?, ?, ?)";
            } else {
                req = "INSERT INTO entretiens(titre, date_entretien, lieu, participant, statut) VALUES (?, ?, ?, ?, ?)";
            }

            try (PreparedStatement ps = con.prepareStatement(req)) {
                ps.setString(1, e.getTitre());
                ps.setString(2, e.getDate_entretien());
                ps.setString(3, e.getLieu());
                ps.setString(4, e.getParticipant());
                ps.setString(5, e.getStatut());

                if (columnsExist) {
                    ps.setString(6, e.getCv_path());
                    ps.setString(7, e.getLettre_motivation_path());
                }

                ps.executeUpdate();
                System.out.println("Entretien ajouté avec succès");
            }
        } catch (SQLException ex) {
            System.err.println("Erreur lors de l'ajout de l'entretien: " + ex.getMessage());
            throw ex;
        }
    }

    @Override
    public void modifier(Entretien e) throws SQLException {
        try {
            // Vérifier si les colonnes existent
            boolean columnsExist = DatabaseUpdater.columnExists(con, "entretiens", "cv_path") &&
                                  DatabaseUpdater.columnExists(con, "entretiens", "lettre_motivation_path");

            String req;
            if (columnsExist) {
                req = "UPDATE entretiens SET titre=?, date_entretien=?, lieu=?, participant=?, statut=?, cv_path=?, lettre_motivation_path=? WHERE id_entretien=?";
            } else {
                req = "UPDATE entretiens SET titre=?, date_entretien=?, lieu=?, participant=?, statut=? WHERE id_entretien=?";
            }

            try (PreparedStatement ps = con.prepareStatement(req)) {
                ps.setString(1, e.getTitre());
                ps.setString(2, e.getDate_entretien());
                ps.setString(3, e.getLieu());
                ps.setString(4, e.getParticipant());
                ps.setString(5, e.getStatut());

                if (columnsExist) {
                    ps.setString(6, e.getCv_path());
                    ps.setString(7, e.getLettre_motivation_path());
                    ps.setInt(8, e.getId_entretien());
                } else {
                    ps.setInt(6, e.getId_entretien());
                }

                ps.executeUpdate();
                System.out.println("Entretien modifié avec succès");
            }
        } catch (SQLException ex) {
            System.err.println("Erreur lors de la modification de l'entretien: " + ex.getMessage());
            throw ex;
        }
    }

    @Override
    public void supprimer(Entretien e) throws SQLException {
        String req = "DELETE FROM entretiens WHERE id_entretien=?";
        try (PreparedStatement ps = con.prepareStatement(req)) {
            ps.setInt(1, e.getId_entretien());
            ps.executeUpdate();
            System.out.println("Entretien supprimé avec succès");
        } catch (SQLException ex) {
            System.err.println("Erreur lors de la suppression de l'entretien: " + ex.getMessage());
            throw ex;
        }
    }

    @Override
    public List<Entretien> recuperer() throws SQLException {
        List<Entretien> entretiens = new ArrayList<>();
        String req = "SELECT * FROM entretiens";
        try (Statement st = con.createStatement();
             ResultSet rs = st.executeQuery(req)) {
            while (rs.next()) {
                // Vérifier si les colonnes existent
                boolean columnsExist = DatabaseUpdater.columnExists(con, "entretiens", "cv_path") &&
                                      DatabaseUpdater.columnExists(con, "entretiens", "lettre_motivation_path");

                Entretien e;
                if (columnsExist) {
                    e = new Entretien(
                            rs.getInt("id_entretien"),
                            rs.getString("titre"),
                            rs.getString("date_entretien"),
                            rs.getString("lieu"),
                            rs.getString("participant"),
                            rs.getString("statut"),
                            rs.getString("cv_path"),
                            rs.getString("lettre_motivation_path")
                    );
                } else {
                    e = new Entretien(
                            rs.getInt("id_entretien"),
                            rs.getString("titre"),
                            rs.getString("date_entretien"),
                            rs.getString("lieu"),
                            rs.getString("participant"),
                            rs.getString("statut")
                    );
                }
                entretiens.add(e);
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors de la récupération des entretiens: " + e.getMessage());
            throw e;
        }
        return entretiens;
    }
}
