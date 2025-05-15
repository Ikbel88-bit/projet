package entities;
public class Offre  {

    private int idOffre ,idResponsable  ;
    private String titreOffre,descriptionOffre,typeContrat;
    private String nomEntreprise;
    private String image;

    public Offre(int idOffre , int idResponsable, String titreOffre, String descriptionOffre, String typeContrat ,String nomEntreprise) {
        this.idOffre = idOffre;
        this.idResponsable = idResponsable;
        this.titreOffre = titreOffre;
        this.descriptionOffre = descriptionOffre;
        this.typeContrat = typeContrat;
        this.nomEntreprise = nomEntreprise;
    }
    public Offre(){}
    public Offre (int idOffre){}
    public Offre(int idResponsable, String titreOffre, String descriptionOffre, String typeContrat,String nomEntreprise) {
        this.idResponsable = idResponsable;
        this.titreOffre = titreOffre;
        this.descriptionOffre = descriptionOffre;
        this.typeContrat = typeContrat;
        this.nomEntreprise = nomEntreprise;
    }
    public Offre(int idResponsable, String titreOffre, String descriptionOffre, String typeContrat,String nomEntreprise,String imag) {
        this.idResponsable = idResponsable;
        this.titreOffre = titreOffre;
        this.descriptionOffre = descriptionOffre;
        this.typeContrat = typeContrat;
        this.nomEntreprise = nomEntreprise;
    }

    public int getIdOffre() {
        return idOffre;
    }
    public void setIdOffre(int id) {
        this.idOffre = id;
    }

    public int getIdResponsable() {
        return idResponsable;
    }
    public void setIdResponsable(int idResponsable) {
        this.idResponsable = idResponsable;
    }

    public String getTitreOffre() {
        return titreOffre;
    }
    public void setTitreOffre(String titreOffre) {
        this.titreOffre = titreOffre;
    }

    public String getDescriptionOffre() {
        return descriptionOffre;
    }
    public void setDescriptionOffre(String descriptionOffre) {
        this.descriptionOffre = descriptionOffre;
    }

    public String getTypeContrat() {return typeContrat;}
    public void setTypeContrat(String typeContrat) {}

    public String getNomEntreprise() {return nomEntreprise;}
    public void setNomEntreprise(String nomEntreprise) {}

    public String getImage() {
        return image;
    }
    public void setImageurl(String image) {
        this.image = image;
    }

    @Override
    public String toString() {
        return "Offre{" +
                " numero : " + idOffre +
                " mise par :" + idResponsable +
                " l'entreprise: "+nomEntreprise+
                " sous tittre: " + titreOffre +
                " voici description: " + descriptionOffre +
                " type de contrat:  " + typeContrat +
                '}';

    }
}