package services;

import entities.Condidature;
import entities.Offre;
import utils.MyDatabase;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ServiceCondidature implements IService<Condidature> {
    private Connection con;

    public ServiceCondidature() {
        con = MyDatabase.getInstance().getCnx();

    }
    @Override
    public void ajouter(Condidature condidature) throws SQLException {
        String req = "INSERT INTO condidature (idOffre ,idUser ,urlCv,lettreDeMotivation,statut) VALUES  (?, ?, ?, ?,?)";
        PreparedStatement ps = con.prepareStatement(req);
        ps.setInt(1, condidature.getIdOffre());
        ps.setInt(2, condidature.getIdUser());
        ps.setString(3, condidature.getUrlCv());
        ps.setString(4, condidature.getLettreDeMotivation());
        ps.setString(5, condidature.getStatut());
        ps.executeUpdate();

        ps.close();
        System.out.println("Condidature ajoutee");
    }
    @Override
    public void modifier(Condidature condidature) throws SQLException {
        String req = "update  condidature set idCondidature =?, idOffre=? , idUser=? , urlCv=? , lettreDeMotivation=? , statut=?  where idCondidature=?";
        PreparedStatement ps = con.prepareStatement(req);
        ps.setInt(2,condidature.getIdOffre() );
        ps.setInt(3, condidature.getIdUser());
        ps.setString(4, condidature.getUrlCv());
        ps.setString(5, condidature.getLettreDeMotivation());
        ps.setString(6, condidature.getStatut());
        ps.setInt(1, condidature.getIdCondidature());
        ps.setInt(7, condidature.getIdCondidature());
        ps.executeUpdate();
        System.out.println("condidature modifie");
    }
    @Override
    public void supprimer(Condidature condidature) throws SQLException {
        String req = "delete from condidature where idCondidature=?";
        PreparedStatement ps = con.prepareStatement(req);
        ps.setInt(1, condidature.getIdCondidature());
        ps.executeUpdate();
        System.out.println("condidature supprimé");
    }

    @Override
    public  List<Condidature> recuperer() throws SQLException {
        List<Condidature> condidatures = new ArrayList<>();
        String req = "select * from condidature";
        Statement st = con.createStatement();
        ResultSet rs = st.executeQuery(req);
        while (rs.next()) {
            int condidature = rs.getInt("idCondidature");
            int offre = rs.getInt("idOffre");
            int user = rs.getInt("idUser");
            String url= rs.getString("urlCv");
            String lettre = rs.getString("lettreDeMotivation");
            String statut = rs.getString("statut");

            Condidature c= new Condidature(condidature,offre,user,url,lettre,statut);
            condidatures.add(c);

        }
        return condidatures;
    }
}

