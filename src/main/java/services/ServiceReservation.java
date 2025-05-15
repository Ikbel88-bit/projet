package services;

import entities.Reservation;
import utils.MyDatabase;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ServiceReservation implements IService<Reservation> {
    private Connection con;

    public ServiceReservation() {
        con = MyDatabase.getInstance().getConnection();
    }

    @Override
    public void ajouter(Reservation reservation) throws SQLException {
        String req = "INSERT INTO reservation(id_formation, nom_participant, email_participant, date_reservation) " +
                "VALUES (?, ?, ?, ?)";
        try (PreparedStatement ps = con.prepareStatement(req, Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, reservation.getId_formation());
            ps.setString(2, reservation.getNom_participant());
            ps.setString(3, reservation.getEmail_participant());
            ps.setDate(4, new java.sql.Date(reservation.getDate_reservation().getTime()));
            ps.executeUpdate();

            // Retrieve the generated ID
            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    reservation.setId(rs.getInt(1));
                    System.out.println("Réservation ajoutée avec ID : " + reservation.getId());
                } else {
                    throw new SQLException("Échec de la récupération de l'ID généré pour la réservation.");
                }
            }
        }
    }

    @Override
    public void modifier(Reservation reservation) throws SQLException {
        String req = "UPDATE reservation SET id_formation=?, nom_participant=?, email_participant=?, date_reservation=? WHERE id=?";
        try (PreparedStatement ps = con.prepareStatement(req)) {
            ps.setInt(1, reservation.getId_formation());
            ps.setString(2, reservation.getNom_participant());
            ps.setString(3, reservation.getEmail_participant());
            ps.setDate(4, new java.sql.Date(reservation.getDate_reservation().getTime()));
            ps.setInt(5, reservation.getId());
            int rowsAffected = ps.executeUpdate();
            if (rowsAffected > 0) {
                System.out.println("Réservation modifiée avec ID : " + reservation.getId());
            } else {
                System.out.println("Aucune réservation trouvée avec ID : " + reservation.getId());
            }
        }
    }

    @Override
    public void supprimer(Reservation reservation) throws SQLException {
        String req = "DELETE FROM reservation WHERE id=?";
        try (PreparedStatement ps = con.prepareStatement(req)) {
            ps.setInt(1, reservation.getId());
            int rowsAffected = ps.executeUpdate();
            if (rowsAffected > 0) {
                System.out.println("Réservation supprimée avec ID : " + reservation.getId());
            } else {
                System.out.println("Aucune réservation trouvée avec ID : " + reservation.getId());
            }
        }
    }

    @Override
    public List<Reservation> recuperer() throws SQLException {
        List<Reservation> reservations = new ArrayList<>();
        String req = "SELECT * FROM reservation";
        try (Statement st = con.createStatement();
             ResultSet rs = st.executeQuery(req)) {
            while (rs.next()) {
                int id = rs.getInt("id");
                int id_formation = rs.getInt("id_formation");
                String nom_participant = rs.getString("nom_participant");
                String email_participant = rs.getString("email_participant");
                Date date_reservation = rs.getDate("date_reservation");
                Reservation r = new Reservation(id, id_formation, nom_participant, email_participant, date_reservation);
                reservations.add(r);
            }
        }
        return reservations;
    }
}