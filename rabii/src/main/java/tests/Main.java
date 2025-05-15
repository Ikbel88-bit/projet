package tests;

import entities.Formation;
import entities.Reservation;
import services.ServiceFormation;
import services.ServiceReservation;

import java.sql.SQLException;
import java.util.Date;

public class  Main {
    public static void main(String[] args) throws SQLException {
        ServiceReservation serviceReservation = new ServiceReservation();
        ServiceFormation serviceFormation = new ServiceFormation();


        Date dateDebut = new Date(2025 - 1900, 5, 1); // 01/06/2025
        Date dateFin = new Date(2025 - 1900, 5, 5); // 05/06/2025
        Formation formation = new Formation("Java Avancé", "Formation sur Java avancé", dateDebut, dateFin, "Rabii Ben", 20);
        serviceFormation.ajouter(formation);
        System.out.println("Formation ajoutée : " + formation);
    }
}
