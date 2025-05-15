package services;

import entities.User;
import utils.MyDatabase;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Service pour gérer les utilisateurs.
 */
public class ServiceUser implements IService<User> {
    private static final Logger LOGGER = Logger.getLogger(ServiceUser.class.getName());
    private Connection connection;

    public ServiceUser() {
        connection = MyDatabase.getInstance().getCnx();
    }

    /**
     * Vérifie si un email existe déjà dans la base de données
     * @param email L'email à vérifier
     * @paramemail L'email à vérifier
     * @return true si l'email existe, false sinon
     * @throws SQLException Si erreur de base de données
     */
    public boolean emailExists(String email) throws SQLException {
        String sql = "SELECT COUNT(*) FROM user WHERE email = ?";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, email);
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                return resultSet.getInt(1) > 0;
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Erreur lors de la vérification de l'existence de l'email", e);
            throw e;
        }
        return false;
    }

    /**
     * Compte le nombre d'employés (utilisateurs avec rôle 'EMPLOYE')
     * @return Le nombre d'employés
     * @throws SQLException Si erreur de base de données
     */
    public int countEmployes() throws SQLException {
        String sql = "SELECT COUNT(*) FROM user WHERE role = 'EMPLOYE'";
        try (Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery(sql)) {
            if (resultSet.next()) {
                return resultSet.getInt(1);
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Erreur lors du comptage des employés", e);
            throw e;
        }
        return 0;
    }

    /**
     * Récupère les utilisateurs par rôle
     * @param role Le rôle à rechercher
     * @return Liste des utilisateurs ayant ce rôle
     * @throws SQLException Si erreur de base de données
     */
    public List<User> findByRole(String role) throws SQLException {
        List<User> users = new ArrayList<>();
        String sql = "SELECT * FROM user WHERE role = ?";

        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, role);

            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    User user = new User();
                    user.setId(resultSet.getInt("id"));
                    user.setNom(resultSet.getString("nom"));
                    user.setPrenom(resultSet.getString("prenom"));
                    user.setEmail(resultSet.getString("email"));
                    user.setRole(resultSet.getString("role"));
                    user.setTelephone(resultSet.getString("telephone"));
                    users.add(user);
                }
            }
        }

        return users;
    }

    @Override
    public void ajouter(User user) throws SQLException {
        String sql = "INSERT INTO user (nom, prenom, email, mot_de_passe, role, telephone) VALUES (?, ?, ?, SHA2(?, 256), ?, ?)";

        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, user.getNom());
            statement.setString(2, user.getPrenom());
            statement.setString(3, user.getEmail());
            statement.setString(4, user.getPassword());
            statement.setString(5, user.getRole());
            statement.setString(6, user.getTelephone());

            statement.executeUpdate();
            LOGGER.info("Utilisateur ajouté avec succès : " + user.getEmail());
        }
    }

    @Override
    public void modifier(User user) throws SQLException {
        String sql;
        PreparedStatement statement;

        // Si le mot de passe est fourni, on le met à jour également
        if (user.getPassword() != null && !user.getPassword().isEmpty()) {
            sql = "UPDATE user SET nom = ?, prenom = ?, email = ?, mot_de_passe = SHA2(?, 256), role = ?, telephone = ? WHERE id = ?";
            statement = connection.prepareStatement(sql);
            statement.setString(1, user.getNom());
            statement.setString(2, user.getPrenom());
            statement.setString(3, user.getEmail());
            statement.setString(4, user.getPassword());
            statement.setString(5, user.getRole());
            statement.setString(6, user.getTelephone());
            statement.setInt(7, user.getId());
        } else {
            // Sinon, on ne modifie pas le mot de passe
            sql = "UPDATE user SET nom = ?, prenom = ?, email = ?, role = ?, telephone = ? WHERE id = ?";
            statement = connection.prepareStatement(sql);
            statement.setString(1, user.getNom());
            statement.setString(2, user.getPrenom());
            statement.setString(3, user.getEmail());
            statement.setString(4, user.getRole());
            statement.setString(5, user.getTelephone());
            statement.setInt(6, user.getId());
        }

        try (statement) {
            statement.executeUpdate();
            LOGGER.info("Utilisateur modifié avec succès : " + user.getEmail());
        }
    }

    @Override
    public void supprimer(User user) throws SQLException {
        supprimer(user.getId());
    }

    public void supprimer(int id) throws SQLException {
        String sql = "DELETE FROM user WHERE id = ?";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, id);
            statement.executeUpdate();
        }
    }

    @Override
    public List<User> recuperer() throws SQLException {
        List<User> users = new ArrayList<>();
        String sql = "SELECT * FROM user";
        try (Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery(sql)) {
            while (resultSet.next()) {
                User user = new User();
                user.setId(resultSet.getInt("id"));
                user.setNom(resultSet.getString("nom"));
                user.setPrenom(resultSet.getString("prenom"));
                user.setEmail(resultSet.getString("email"));
                // Ne pas essayer de récupérer le mot de passe
                user.setRole(resultSet.getString("role"));
                user.setTelephone(resultSet.getString("telephone"));
                users.add(user);
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Erreur lors de la récupération des utilisateurs", e);
            throw e;
        }
        return users;
    }


    public User recupererParId(int id) throws SQLException {
        String sql = "SELECT * FROM user WHERE id = ?";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, id);
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    User user = new User();
                    user.setId(resultSet.getInt("id"));
                    user.setNom(resultSet.getString("nom"));
                    user.setPrenom(resultSet.getString("prenom"));
                    user.setEmail(resultSet.getString("email"));
                    user.setRole(resultSet.getString("role"));
                    user.setTelephone(resultSet.getString("telephone"));
                    return user;
                }
            }
        }
        return null;
    }


    public List<User> rechercher(String critere) throws SQLException {
        List<User> users = new ArrayList<>();
        String sql = "SELECT * FROM user WHERE nom LIKE ? OR prenom LIKE ? OR email LIKE ?";

        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            String searchPattern = "%" + critere + "%";
            statement.setString(1, searchPattern);
            statement.setString(2, searchPattern);
            statement.setString(3, searchPattern);

            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    User user = new User();
                    user.setId(resultSet.getInt("id"));
                    user.setNom(resultSet.getString("nom"));
                    user.setPrenom(resultSet.getString("prenom"));
                    user.setEmail(resultSet.getString("email"));
                    user.setRole(resultSet.getString("role"));
                    user.setTelephone(resultSet.getString("telephone"));
                    users.add(user);
                }
            }
        }

        return users;
    }


    public boolean existe(int id) {
        try {
            return recupererParId(id) != null;
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Erreur lors de la vérification de l'existence de l'utilisateur", e);
            return false;
        }
    }


    public int compter() {
        String sql = "SELECT COUNT(*) FROM user";
        try (Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery(sql)) {
            if (resultSet.next()) {
                return resultSet.getInt(1);
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Erreur lors du comptage des utilisateurs", e);
        }
        return 0;
    }


    public List<User> afficher() {
        try {
            return recuperer();
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Erreur lors de l'affichage des utilisateurs", e);
            return new ArrayList<>();
        }
    }

    /**
     * Authentifie un utilisateur
     * @param email Email de l'utilisateur
     * @param password Mot de passe de l'utilisateur
     * @return L'utilisateur authentifié ou null si échec
     */
    public User authenticate(String email, String password) {
        String sql = "SELECT * FROM user WHERE email = ? AND mot_de_passe = SHA2(?, 256)";

        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, email);
            statement.setString(2, password);

            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    User user = new User();
                    user.setId(resultSet.getInt("id"));
                    user.setNom(resultSet.getString("nom"));
                    user.setPrenom(resultSet.getString("prenom"));
                    user.setEmail(resultSet.getString("email"));
                    user.setRole(resultSet.getString("role"));
                    user.setTelephone(resultSet.getString("telephone"));
                    return user;
                }
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Erreur lors de l'authentification", e);
        }

        return null;
    }
}
