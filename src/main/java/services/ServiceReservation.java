package services;

import entities.Reservation;
import utils.MyDatabase;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ServiceReservation implements IService<Reservation> {

    private final Connection conn;

    public ServiceReservation() {
        conn = MyDatabase.getInstance().getConnection();
    }

    @Override
    public void ajouter(Reservation reservation) {
        String sql = "INSERT INTO reservations(resource, dateDebut, dateFin, status) VALUES(?, ?, ?, ?)";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, reservation.getResource());
            stmt.setString(2, reservation.getDateDebut());
            stmt.setString(3, reservation.getDateFin());
            stmt.setString(4, reservation.getStatus());
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace(); // À remplacer par un logger en production
        }
    }

    @Override
    public void modifier(Reservation reservation) {
        String sql = "UPDATE reservations SET resource = ?, dateDebut = ?, dateFin = ?, status = ? WHERE id = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, reservation.getResource());
            stmt.setString(2, reservation.getDateDebut());
            stmt.setString(3, reservation.getDateFin());
            stmt.setString(4, reservation.getStatus());
            stmt.setInt(5, reservation.getId());
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void supprimer(Reservation reservation) {
        String sql = "DELETE FROM reservations WHERE id = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, reservation.getId());
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }




    @Override
    public List<Reservation> recuperer() {
        List<Reservation> list = new ArrayList<>();
        String sql = "SELECT * FROM reservations";
        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                Reservation r = new Reservation(
                        rs.getInt("id"),
                        rs.getString("resource"),
                        rs.getString("dateDebut"),
                        rs.getString("dateFin"),
                        rs.getString("status")
                );
                list.add(r);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public Reservation recupererParId(int id) {
        String sql = "SELECT * FROM reservations WHERE id = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return new Reservation(
                            rs.getInt("id"),
                            rs.getString("resource"),
                            rs.getString("dateDebut"),
                            rs.getString("dateFin"),
                            rs.getString("status")
                    );
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
}
