package entities;

import java.util.Date;

public class Reservation {
    private int id;
    private int id_formation;
    private String nom_participant;
    private String email_participant;
    private Date date_reservation;

    public Reservation(int id, int id_formation, String nom_participant, String email_participant, Date date_reservation) {
        this.id = id;
        this.id_formation = id_formation;
        this.nom_participant = nom_participant;
        this.email_participant = email_participant;
        this.date_reservation = date_reservation;
    }

    public Reservation(int id_formation, String nom_participant, String email_participant, Date date_reservation) {
        this.id_formation = id_formation;
        this.nom_participant = nom_participant;
        this.email_participant = email_participant;
        this.date_reservation = date_reservation;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getId_formation() {
        return id_formation;
    }

    public void setId_formation(int id_formation) {
        this.id_formation = id_formation;
    }

    public String getNom_participant() {
        return nom_participant;
    }

    public void setNom_participant(String nom_participant) {
        this.nom_participant = nom_participant;
    }

    public String getEmail_participant() {
        return email_participant;
    }

    public void setEmail_participant(String email_participant) {
        this.email_participant = email_participant;
    }

    public Date getDate_reservation() {
        return date_reservation;
    }

    public void setDate_reservation(Date date_reservation) {
        this.date_reservation = date_reservation;
    }

    @Override
    public String toString() {
        return "Reservation{" +
                "id=" + id +
                ", id_formation=" + id_formation +
                ", nom_participant='" + nom_participant + '\'' +
                ", email_participant='" + email_participant + '\'' +
                ", date_reservation=" + date_reservation +
                '}';
    }
}
