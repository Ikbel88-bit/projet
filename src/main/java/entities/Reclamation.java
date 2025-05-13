package entities;

public class Reclamation {
    private int reclamationId;
    private int userId;
    private String description;
    private String reponse;

    public Reclamation() {
    }

    public Reclamation(int userId, String description) {
        this.userId = userId;
        this.description = description;
    }

    public Reclamation(int reclamationId, int userId, String description, String reponse) {
        this.reclamationId = reclamationId;
        this.userId = userId;
        this.description = description;
        this.reponse = reponse;
    }

    public int getReclamationId() {
        return reclamationId;
    }

    public void setReclamationId(int reclamationId) {
        this.reclamationId = reclamationId;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getReponse() {
        return reponse;
    }

    public void setReponse(String reponse) {
        this.reponse = reponse;
    }

    @Override
    public String toString() {
        return "Reclamation{" +
                "reclamationId=" + reclamationId +
                ", userId=" + userId +
                ", description='" + description + '\'' +
                ", reponse='" + reponse + '\'' +
                '}';
    }
}
