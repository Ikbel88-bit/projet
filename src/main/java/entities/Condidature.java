package entities;

public class Condidature {
    private int idOffre ,idUser ,idCondidature ;
    private String urlCv,lettreDeMotivation,statut;

    public Condidature( int idCondidature,int idOffre ,int idUser, String urlCv, String lettreDeMotivation ,String statut) {
        this.idCondidature = idCondidature;
        this.idOffre = idOffre;
        this.idUser = idUser;
        this.urlCv = urlCv;
        this.lettreDeMotivation = lettreDeMotivation;
        this.statut = statut;
    }
    public Condidature(){}
    public Condidature (int idCondidature){}
    public Condidature(int idOffre ,int idUser, String urlCv, String lettreDeMotivation, String statut ) {
        this.idOffre = idOffre;
        this.idUser = idUser;
        this.urlCv = urlCv;
        this.lettreDeMotivation = lettreDeMotivation;
        this.statut = statut;
    }
    public Condidature(int idOffre ,int idUser, String urlCv, String lettreDeMotivation ) {
        this.idOffre = idOffre;
        this.idUser = idUser;
        this.urlCv = urlCv;
        this.lettreDeMotivation = lettreDeMotivation;
    }

    public int getIdOffre() {
        return idOffre;
    }
    public void setIdOffre(int id) {
        this.idOffre = id;
    }

    public int getIdCondidature() {
        return idCondidature;
    }
    public void setIdCondidature(int idCondidature) {
        this.idCondidature = idCondidature;
    }

    public int  getIdUser() {return idUser;}
    public void setIdUser(int idUser) {this.idUser = idUser;}
    public String getUrlCv() {return urlCv;}
    public void setUrlCv(String urlCv) {this.urlCv = urlCv;}

    public String getLettreDeMotivation() {return lettreDeMotivation;}
    public void setLettreDeMotivation(String lettreDeMotivation) {
        this.lettreDeMotivation = lettreDeMotivation;
    }
    public String getStatut() {
        // Si le statut est null, retourner une valeur par défaut
        return statut != null ? statut : "en attente";
    }
    public void setStatut(String statut) {
        this.statut = statut;
    }
    @Override
    public String toString() {
        return "condidature{ " +
                " numero: " + idCondidature+
                " concernant l'offre: " +  idOffre+
                " mise par: "+idUser+
                " lien de cv :" + urlCv +
                " lettre de motivation : " +lettreDeMotivation +
                " statut: " + statut +
                '}';
    }
}
