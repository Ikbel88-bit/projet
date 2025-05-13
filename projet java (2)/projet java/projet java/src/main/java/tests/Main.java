package tests;

import entities.Entretien;
import entities.Evaluation;
import services.ServiceEntretien;
import services.ServiceEvaluation;

import java.sql.SQLException;

public class Main {
    public static void main(String[] args) {
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
