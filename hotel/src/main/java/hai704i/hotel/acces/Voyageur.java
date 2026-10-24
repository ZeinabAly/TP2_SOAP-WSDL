package hai704i.hotel.acces;

import jakarta.xml.bind.annotation.*;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(propOrder = {"nom", "prenom"})
public class Voyageur {

    @XmlElement(required = true)
    private String nom;

    @XmlElement(required = true)
    private String prenom;

    public Voyageur() {
    }

    public String getNom() {
        return nom;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public String getPrenom() {
        return prenom;
    }

    public void setPrenom(String prenom) {
        this.prenom = prenom;
    }
}