package hai704i.hotel.acces;

import jakarta.xml.bind.annotation.*;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(propOrder = {
        "idDemande",
        "authentification",
        "idOffre",
        "voyageur",
        "paiement"
})
public class DemandeReservation {

    @XmlElement(required = true)
    private String idDemande;

    @XmlElement(required = true)
    private Authentification authentification;

    @XmlElement(required = true)
    private String idOffre;

    @XmlElement(required = true)
    private Voyageur voyageur;

    @XmlElement(required = true)
    private Paiement paiement;

    public DemandeReservation() {
    }

    public String getIdDemande() {
        return idDemande;
    }

    public void setIdDemande(String idDemande) {
        this.idDemande = idDemande;
    }

    public Authentification getAuthentification() {
        return authentification;
    }

    public void setAuthentication(Authentification authentification) {
        this.authentification = authentification;
    }

    public String getIdOffre() {
        return idOffre;
    }

    public void setIdOffre(String idOffre) {
        this.idOffre = idOffre;
    }

    public Voyageur getVoyageur() {
        return voyageur;
    }

    public void setVoyageur(Voyageur voyageur) {
        this.voyageur = voyageur;
    }

    public Paiement getPaiement() {
        return paiement;
    }

    public void setPaiement(Paiement paiement) {
        this.paiement = paiement;
    }

}