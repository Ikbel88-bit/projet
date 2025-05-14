package entities;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class Entretien {
    private int id_entretien;
    private StringProperty titre;
    private StringProperty date_entretien;
    private StringProperty lieu;
    private StringProperty participant;
    private StringProperty statut;
    private StringProperty cv_path;
    private StringProperty lettre_motivation_path;

    public Entretien(int id_entretien, String titre, String date_entretien, String lieu, String participant, String statut) {
        this.id_entretien = id_entretien;
        this.titre = new SimpleStringProperty(titre);
        this.date_entretien = new SimpleStringProperty(date_entretien);
        this.lieu = new SimpleStringProperty(lieu);
        this.participant = new SimpleStringProperty(participant);
        this.statut = new SimpleStringProperty(statut);
        this.cv_path = new SimpleStringProperty("");
        this.lettre_motivation_path = new SimpleStringProperty("");
    }

    public Entretien(int id_entretien, String titre, String date_entretien, String lieu, String participant, String statut, String cv_path, String lettre_motivation_path) {
        this.id_entretien = id_entretien;
        this.titre = new SimpleStringProperty(titre);
        this.date_entretien = new SimpleStringProperty(date_entretien);
        this.lieu = new SimpleStringProperty(lieu);
        this.participant = new SimpleStringProperty(participant);
        this.statut = new SimpleStringProperty(statut);
        this.cv_path = new SimpleStringProperty(cv_path != null ? cv_path : "");
        this.lettre_motivation_path = new SimpleStringProperty(lettre_motivation_path != null ? lettre_motivation_path : "");
    }

    public Entretien(String titre, String date_entretien, String lieu, String participant, String statut) {
        this.titre = new SimpleStringProperty(titre);
        this.date_entretien = new SimpleStringProperty(date_entretien);
        this.lieu = new SimpleStringProperty(lieu);
        this.participant = new SimpleStringProperty(participant);
        this.statut = new SimpleStringProperty(statut);
        this.cv_path = new SimpleStringProperty("");
        this.lettre_motivation_path = new SimpleStringProperty("");
    }

    public Entretien(String titre, String date_entretien, String lieu, String participant, String statut, String cv_path, String lettre_motivation_path) {
        this.titre = new SimpleStringProperty(titre);
        this.date_entretien = new SimpleStringProperty(date_entretien);
        this.lieu = new SimpleStringProperty(lieu);
        this.participant = new SimpleStringProperty(participant);
        this.statut = new SimpleStringProperty(statut);
        this.cv_path = new SimpleStringProperty(cv_path != null ? cv_path : "");
        this.lettre_motivation_path = new SimpleStringProperty(lettre_motivation_path != null ? lettre_motivation_path : "");
    }

    public int getId_entretien() {
        return id_entretien;
    }

    public void setId_entretien(int id_entretien) {
        this.id_entretien = id_entretien;
    }

    public String getTitre() {
        return titre.get();
    }

    public StringProperty titreProperty() {
        return titre;
    }

    public void setTitre(String titre) {
        this.titre.set(titre);
    }

    public String getDate_entretien() {
        return date_entretien.get();
    }

    public StringProperty date_entretienProperty() {
        return date_entretien;
    }

    public void setDate_entretien(String date_entretien) {
        this.date_entretien.set(date_entretien);
    }

    public String getLieu() {
        return lieu.get();
    }

    public StringProperty lieuProperty() {
        return lieu;
    }

    public void setLieu(String lieu) {
        this.lieu.set(lieu);
    }

    public String getParticipant() {
        return participant.get();
    }

    public StringProperty participantProperty() {
        return participant;
    }

    public void setParticipant(String participant) {
        this.participant.set(participant);
    }

    public String getStatut() {
        return statut.get();
    }

    public StringProperty statutProperty() {
        return statut;
    }

    public void setStatut(String statut) {
        this.statut.set(statut);
    }

    public String getCv_path() {
        return cv_path.get();
    }

    public StringProperty cv_pathProperty() {
        return cv_path;
    }

    public void setCv_path(String cv_path) {
        this.cv_path.set(cv_path);
    }

    public String getLettre_motivation_path() {
        return lettre_motivation_path.get();
    }

    public StringProperty lettre_motivation_pathProperty() {
        return lettre_motivation_path;
    }

    public void setLettre_motivation_path(String lettre_motivation_path) {
        this.lettre_motivation_path.set(lettre_motivation_path);
    }

    @Override
    public String toString() {
        return "Entretien{" +
                "id_entretien=" + id_entretien +
                ", titre='" + getTitre() + '\'' +
                ", date_entretien='" + getDate_entretien() + '\'' +
                ", lieu='" + getLieu() + '\'' +
                ", participant='" + getParticipant() + '\'' +
                ", statut='" + getStatut() + '\'' +
                ", cv_path='" + getCv_path() + '\'' +
                ", lettre_motivation_path='" + getLettre_motivation_path() + '\'' +
                '}';
    }
}
