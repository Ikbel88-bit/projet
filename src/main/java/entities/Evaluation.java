package entities;

public class Evaluation {
    private int id_evaluation;
    private int id_entretien;
    private String commentaire;
    private int note;
    private String date_evaluation;

    public Evaluation(int id_evaluation, int id_entretien, String commentaire, int note, String date_evaluation) {
        this.id_evaluation = id_evaluation;
        this.id_entretien = id_entretien;
        this.commentaire = commentaire;
        this.note = note;
        this.date_evaluation = date_evaluation;
    }


    public Evaluation(int id_entretien, String commentaire, int note, String date_evaluation) {
        this.id_entretien = id_entretien;
        this.commentaire = commentaire;
        this.note = note;
        this.date_evaluation = date_evaluation;
    }

    public int getId_evaluation() {
        return id_evaluation;
    }

    public void setId_evaluation(int id_evaluation) {
        this.id_evaluation = id_evaluation;
    }

    public int getId_entretien() {
        return id_entretien;
    }

    public void setId_entretien(int id_entretien) {
        this.id_entretien = id_entretien;
    }

    public String getCommentaire() {
        return commentaire;
    }

    public void setCommentaire(String commentaire) {
        this.commentaire = commentaire;
    }

    public int getNote() {
        return note;
    }

    public void setNote(int note) {
        this.note = note;
    }

    public String getDate_evaluation() {
        return date_evaluation;
    }

    public void setDate_evaluation(String date_evaluation) {
        this.date_evaluation = date_evaluation;
    }

    @Override
    public String toString() {
        return "Evaluation{" +
                "id_evaluation=" + id_evaluation +
                ", id_entretien=" + id_entretien +
                ", commentaire='" + commentaire + '\'' +
                ", note=" + note +
                ", date_evaluation='" + date_evaluation + '\'' +
                '}';
    }
}
