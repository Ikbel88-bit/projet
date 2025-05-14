package services;

import entities.Offre;
import entities.Session;
import utils.MyDatabase;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ServiceOffre implements IService<Offre> {
    private MyDatabase db;

    public ServiceOffre() {
        db = MyDatabase.getInstance();
    }

    @Override
    public void ajouter(Offre offre) throws SQLException {
        try {
            Connection con = db.getCnx();
            if (con == null) {
                throw new SQLException("La connexion à la base de données est null");
            }

            System.out.println("Tentative d'ajout d'une offre...");
            System.out.println("ID Responsable: " + Session.getIdUser());
            System.out.println("Titre: " + offre.getTitreOffre());
            System.out.println("Description: " + offre.getDescriptionOffre());
            System.out.println("Type de contrat: " + offre.getTypeContrat());
            System.out.println("Entreprise: " + offre.getNomEntreprise());

            String req = "INSERT INTO offre (idResponsable, titreOffre, descriptionOffre, typeContrat, nomEntreprise) VALUES (?, ?, ?, ?, ?)";
            try (PreparedStatement ps = con.prepareStatement(req)) {
                ps.setInt(1, Session.getIdUser());
                ps.setString(2, offre.getTitreOffre());
                ps.setString(3, offre.getDescriptionOffre());
                ps.setString(4, offre.getTypeContrat());
                ps.setString(5, offre.getNomEntreprise());
                
                int rowsAffected = ps.executeUpdate();
                if (rowsAffected > 0) {
                    System.out.println("Offre ajoutée avec succès");
                } else {
                    System.out.println("Aucune ligne n'a été affectée lors de l'ajout de l'offre");
                }
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors de l'ajout de l'offre: " + e.getMessage());
            e.printStackTrace();
            throw e;
        }
    }

    @Override
    public void modifier(Offre offre) throws SQLException {
        Connection con = db.getCnx();
        String req = "update offre set idOffre=? , titreOffre=? , descriptionOffre=? , typeContrat=? , idResponsable =?  ,nomEntreprise =? where idOffre=?";
        try (PreparedStatement ps = con.prepareStatement(req)) {
            ps.setInt(1, offre.getIdOffre());
            ps.setString(2, offre.getTitreOffre());
            ps.setString(3, offre.getDescriptionOffre());
            ps.setString(4, offre.getTypeContrat());
            ps.setInt(5, Session.getIdUser());
            ps.setString(6, offre.getNomEntreprise());
            ps.setInt(7, offre.getIdOffre());
            ps.executeUpdate();
            System.out.println("offre modifie");
        }
    }

    @Override
    public void supprimer(Offre offre) throws SQLException {
        Connection con = db.getCnx();
        String sql = "DELETE FROM offre WHERE idOffre=?";
        try (PreparedStatement stmt = con.prepareStatement(sql)) {
            stmt.setInt(1, offre.getIdOffre());
            stmt.executeUpdate();
        }
    }

    @Override
    public List<Offre> recuperer() throws SQLException {
        List<Offre> offres = new ArrayList<>();
        Connection con = db.getCnx();
        String req = "select * from offre";
        try (Statement st = con.createStatement();
             ResultSet rs = st.executeQuery(req)) {
            while (rs.next()) {
                int Offre = rs.getInt("idOffre");
                int Responsable = rs.getInt("idResponsable");
                String titre = rs.getString("titreOffre");
                String Description = rs.getString("descriptionOffre");
                String Contrat = rs.getString("typeContrat");
                String Entremprise = rs.getString("nomEntreprise");

                Offre p = new Offre(Offre, Responsable, titre, Description, Contrat, Entremprise);
                offres.add(p);
            }
        }
        return offres;
    }
}

