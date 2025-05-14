package tests;

import entities.Entretien;
import entities.Evaluation;
import entities.Reclamation;
import services.ServiceEntretien;
import services.ServiceEvaluation;
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
    public static void main1(String[] args) {
        ServiceEntretien serviceEntretien = new ServiceEntretien();
        ServiceEvaluation serviceEvaluation = new ServiceEvaluation();

        try {

            serviceEntretien.ajouter(new Entretien("Entretien RH", "2025-04-22", "Tunis", "Ahmed", "prévu"));
            serviceEntretien.modifier(new Entretien(1, "Entretien Technique", "2025-04-25", "Sfax", "Leila", "terminé"));

            System.out.println("Liste des entretiens :");
            System.out.println(serviceEntretien.recuperer());
            serviceEntretien.supprimer(new Entretien(1, "Entretien Technique", "2025-04-25", "Sfax", "Leila", "terminé"));

            serviceEvaluation.ajouter(new Evaluation(1, "Bonne performance", 8, "2025-04-22"));
            serviceEvaluation.modifier(new Evaluation(1, 1, "Excellente performance", 10, "2025-04-23"));
            serviceEvaluation.supprimer(new Evaluation(1, 1, "Excellente performance", 10, "2025-04-23"));
            System.out.println("Liste des évaluations :");
            System.out.println(serviceEvaluation.recuperer());

        } catch (SQLException e) {
            System.out.println("Erreur SQL : " + e.getMessage());
        }
    }
}
