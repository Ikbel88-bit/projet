package entities;

import java.sql.Timestamp;
import java.time.LocalDateTime;

public class Notification {
    private int id;
    private String message;
    private int userId;
    private String type;
    private Timestamp dateCreation;
    private boolean lue;

    public Notification() {
        this.dateCreation = Timestamp.valueOf(LocalDateTime.now());
        this.lue = false;
    }

    public Notification(String message, int userId, String type) {
        this.message = message;
        this.userId = userId;
        this.type = type;
        this.dateCreation = Timestamp.valueOf(LocalDateTime.now());
        this.lue = false;
    }

    public Notification(int id, String message, int userId, String type, Timestamp dateCreation, boolean lue) {
        this.id = id;
        this.message = message;
        this.userId = userId;
        this.type = type;
        this.dateCreation = dateCreation;
        this.lue = lue;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Timestamp getDateCreation() {
        return dateCreation;
    }

    public void setDateCreation(Timestamp dateCreation) {
        this.dateCreation = dateCreation;
    }

    public boolean isLue() {
        return lue;
    }

    public void setLue(boolean lue) {
        this.lue = lue;
    }

    @Override
    public String toString() {
        return "Notification{" +
                "id=" + id +
                ", message='" + message + '\'' +
                ", userId=" + userId +
                ", type='" + type + '\'' +
                ", dateCreation=" + dateCreation +
                ", lue=" + lue +
                '}';
    }
}