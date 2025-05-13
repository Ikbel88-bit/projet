package tests;

import entities.Reclamation;
import services.ServiceReclamation;

import java.sql.SQLException;
import java.time.LocalDateTime;

public class Main {
    public static void main(String[] args) {
        ServiceReclamation sr = new ServiceReclamation();

        try {
            // Ajouter une réclamation (ex: l'utilisateur avec id=2 existe)
            Reclamation r = new Reclamation(2, "Contenu de la réclamation");
            sr.ajouter(r);

            // Afficher toutes les réclamations
            System.out.println("Toutes les réclamations :");
            for (Reclamation reclamation : sr.recupererToutes()) {
                System.out.println("ID: " + reclamation.getReclamationId() + 
                                 ", User ID: " + reclamation.getUserId() + 
                                 ", Description: " + reclamation.getDescription());
            }

            // Afficher les réclamations d'un utilisateur spécifique
            System.out.println("\nRéclamations de l'utilisateur 2 :");
            for (Reclamation reclamation : sr.recupererParUtilisateur(2)) {
                System.out.println("ID: " + reclamation.getReclamationId() + 
                                 ", Description: " + reclamation.getDescription());
            }

        } catch (SQLException e) {
            System.out.println("Erreur SQL : " + e.getMessage());
        }
    }
}
