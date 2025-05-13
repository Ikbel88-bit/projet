package services;

import entities.User;
import utils.MyDatabase;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ServiceUser implements IService<User> {
    private Connection con;

    public ServiceUser() {
        con = MyDatabase.getInstance().getCnx();
    }

    @Override
    public void ajouter(User user) throws SQLException {
        String req = "INSERT INTO user(nom, prenom, email, telephone, role, password) VALUES (?, ?, ?, ?, ?, ?)";
        PreparedStatement ps = con.prepareStatement(req);
        ps.setString(1, user.getNom());
        ps.setString(2, user.getPrenom());
        ps.setString(3, user.getEmail());
        ps.setString(4, user.getTelephone());
        ps.setString(5, user.getRole());
        ps.setString(6, user.getPassword());
        ps.executeUpdate();
    }

    @Override
    public void modifier(User user) throws SQLException {
        String req = "UPDATE user SET nom=?, prenom=?, email=?, telephone=?, role=?, password=? WHERE id=?";
        PreparedStatement ps = con.prepareStatement(req);
        ps.setString(1, user.getNom());
        ps.setString(2, user.getPrenom());
        ps.setString(3, user.getEmail());
        ps.setString(4, user.getTelephone());
        ps.setString(5, user.getRole());
        ps.setString(6, user.getPassword());
        ps.setInt(7, user.getId());
        ps.executeUpdate();
    }

    @Override
    public void supprimer(User user) throws SQLException {
        String req = "DELETE FROM user WHERE id=?";
        PreparedStatement ps = con.prepareStatement(req);
        ps.setInt(1, user.getId());
        ps.executeUpdate();
    }

    @Override
    public List<User> recuperer() throws SQLException {
        List<User> users = new ArrayList<>();
        String req = "SELECT * FROM user";
        Statement st = con.createStatement();
        ResultSet rs = st.executeQuery(req);
        while (rs.next()) {
            int id = rs.getInt("id");
            String nom = rs.getString("nom");
            String prenom = rs.getString("prenom");
            String email = rs.getString("email");
            String telephone = rs.getString("telephone");
            String role = rs.getString("role").trim(); // ✅ Nettoyage important
            String password = rs.getString("password");
            if ("User".equalsIgnoreCase(role)) {
                role = "Employe";
            }
            users.add(new User(id, nom, prenom, email, telephone, role, password));
        }
        return users;
    }

    public User findUserByNameSurnamePassword(String name, String surname, String password) {
        try {
            String req = "SELECT * FROM user WHERE LOWER(nom) = ? AND LOWER(prenom) = ? AND password = ?";
            PreparedStatement ps = con.prepareStatement(req);
            ps.setString(1, name.toLowerCase());
            ps.setString(2, surname.toLowerCase());
            ps.setString(3, password);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                int id = rs.getInt("id");
                String nom = rs.getString("nom");
                String prenom = rs.getString("prenom");
                String email = rs.getString("email");
                String telephone = rs.getString("telephone");
                String role = rs.getString("role").trim(); // ✅ Nettoyage ici aussi
                String userPassword = rs.getString("password");
                return new User(id, nom, prenom, email, telephone, role, userPassword);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public User getUserById(int userId) throws SQLException {
        String req = "SELECT * FROM user WHERE id = ?";
        PreparedStatement ps = con.prepareStatement(req);
        ps.setInt(1, userId);
        ResultSet rs = ps.executeQuery();

        if (rs.next()) {
            int id = rs.getInt("id");
            String nom = rs.getString("nom");
            String prenom = rs.getString("prenom");
            String email = rs.getString("email");
            String telephone = rs.getString("telephone");
            String role = rs.getString("role").trim();
            String password = rs.getString("password");
            if ("User".equalsIgnoreCase(role)) {
                role = "Employe";
            }
            return new User(id, nom, prenom, email, telephone, role, password);
        }
        return null;
    }
}