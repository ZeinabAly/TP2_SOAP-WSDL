package hai704i.hotel.acces;

import jakarta.xml.bind.annotation.*;

import javax.xml.datatype.XMLGregorianCalendar;
import java.math.BigDecimal;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(propOrder = {
        "reference",
        "idDemande",
        "idOffre",
        "nomHotel",
        "chambre",
        "dateArrivee",
        "dateDepart",
        "prixTotal",
        "carteMasquee",
        "dejaEnregistree"
})
public class Confirmation {

    @XmlElement(required = true)
    private String reference;

    @XmlElement(required = true)
    private String idDemande;

    @XmlElement(required = true)
    private String idOffre;

    @XmlElement(required = true)
    private String nomHotel;

    @XmlElement(required = true)
    private String chambre;

    @XmlElement(required = true)
    @XmlSchemaType(name = "date")
    private XMLGregorianCalendar dateArrivee;

    @XmlElement(required = true)
    @XmlSchemaType(name = "date")
    private XMLGregorianCalendar dateDepart;

    private BigDecimal prixTotal;

    @XmlElement(required = true)
    private String carteMasquee;

    private boolean dejaEnregistree;

    public Confirmation() {
    }

    public String getReference() {
        return reference;
    }

    public void setReference(String reference) {
        this.reference = reference;
    }

    public String getIdDemande() {
        return idDemande;
    }

    public void setIdDemande(String idDemande) {
        this.idDemande = idDemande;
    }

    public String getIdOffre() {
        return idOffre;
    }

    public void setIdOffre(String idOffre) {
        this.idOffre = idOffre;
    }

    public String getNomHotel() {
        return nomHotel;
    }

    public void setNomHotel(String nomHotel) {
        this.nomHotel = nomHotel;
    }

    public String getChambre() {
        return chambre;
    }

    public void setChambre(String chambre) {
        this.chambre = chambre;
    }

    public XMLGregorianCalendar getDateArrivee() {
        return dateArrivee;
    }

    public void setDateArrivee(XMLGregorianCalendar dateArrivee) {
        this.dateArrivee = dateArrivee;
    }

    public XMLGregorianCalendar getDateDepart() {
        return dateDepart;
    }

    public void setDateDepart(XMLGregorianCalendar dateDepart) {
        this.dateDepart = dateDepart;
    }

    public BigDecimal getPrixTotal() {
        return prixTotal;
    }

    public void setPrixTotal(BigDecimal prixTotal) {
        this.prixTotal = prixTotal;
    }

    public String getCarteMasquee() {
        return carteMasquee;
    }

    public void setCarteMasquee(String carteMasquee) {
        this.carteMasquee = carteMasquee;
    }

    public boolean isDejaEnregistree() {
        return dejaEnregistree;
    }

    public void setDejaEnregistree(boolean dejaEnregistree) {
        this.dejaEnregistree = dejaEnregistree;
    }
}