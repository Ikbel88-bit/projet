package utils;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Classe utilitaire pour le cryptage des mots de passe.
 */
public class PasswordUtil {
    private static final Logger LOGGER = Logger.getLogger(PasswordUtil.class.getName());
    
    /**
     * Crypte un mot de passe en utilisant l'algorithme SHA-256.
     * @param password Le mot de passe à crypter
     * @return Le mot de passe crypté ou null en cas d'erreur
     */
    public static String hashPassword(String password) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(password.getBytes(StandardCharsets.UTF_8));
            
            // Convertir le hash en représentation hexadécimale
            StringBuilder hexString = new StringBuilder();
            for (byte b : hash) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) {
                    hexString.append('0');
                }
                hexString.append(hex);
            }
            return hexString.toString();
        } catch (NoSuchAlgorithmException e) {
            LOGGER.log(Level.SEVERE, "Erreur lors du cryptage du mot de passe", e);
            return null;
        }
    }
    
    /**
     * Vérifie si un mot de passe correspond à un hash.
     * @param password Le mot de passe en clair
     * @param hash Le hash à vérifier
     * @return true si le mot de passe correspond au hash, false sinon
     */
    public static boolean verifyPassword(String password, String hash) {
        String passwordHash = hashPassword(password);
        return passwordHash != null && passwordHash.equals(hash);
    }
}