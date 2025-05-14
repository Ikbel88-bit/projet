package test;

import entities.Condidature;
import entities.Offre;
import services.IService;
import services.ServiceCondidature;
import services.ServiceOffre;
import utils.MyDatabase;

import java.sql.SQLException;

public class Main {
    public static void main(String[] args) {
        ServiceOffre serviceOffre = new ServiceOffre();
        try {
            IService<Object> serviceoffre = null;
           serviceOffre.ajouter(new Offre(888,"Offre d'emploi de genie logiciel","description","CDD","horizon"));
            serviceOffre.modifier(new Offre(1,7568584,"Offre d'emploi de genie logiciel","description","CDI","SPRITE"));
            serviceOffre.supprimer(new Offre(1));
            System.out.println(serviceOffre.recuperer());
        } catch (SQLException e) {
           System.out.println(e.getMessage());
        }
        ServiceCondidature serviceCondidature = new ServiceCondidature();
        try {
            IService<Object> serviceCONDIDATURE = null;
            serviceCondidature.ajouter(new Condidature(888,412,"https//:hizcbs","CDD"));
            serviceCondidature.modifier(new Condidature(1,7568584,12345,"ihcjbanqcbi","Cycgv","accepte"));
            serviceCondidature.supprimer(new Condidature(2));
            System.out.println(serviceCondidature.recuperer());
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }


    }
}
