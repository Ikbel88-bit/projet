package services;

import entities.Utilisateur;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import utils.MyDatabase;

public class ServiceUtilisateur {
    private Connection connection;

    public ServiceUtilisateur() {
        connection = MyDatabase.getInstance().getCnx();
    }

    public Utilisateur recupererParId(int id) throws SQLException {
        String query = "SELECT * FROM user WHERE id = ?";
        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setInt(1, id);
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                return new Utilisateur(
                    resultSet.getInt("id"),
                    resultSet.getString("nom"),
                    resultSet.getString("prenom"),
                    resultSet.getString("email"),
                    resultSet.getString("telephone"),
                    resultSet.getString("role")
                );
            }
        }
        return null;
    }

    public List<Utilisateur> recupererTous() throws SQLException {
        List<Utilisateur> utilisateurs = new ArrayList<>();
        String query = "SELECT * FROM user";
        try (Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery(query)) {
            while (resultSet.next()) {
                utilisateurs.add(new Utilisateur(
                    resultSet.getInt("id"),
                    resultSet.getString("nom"),
                    resultSet.getString("prenom"),
                    resultSet.getString("email"),
                    resultSet.getString("telephone"),
                    resultSet.getString("role")
                ));
            }
        }
        return utilisateurs;
    }
} 