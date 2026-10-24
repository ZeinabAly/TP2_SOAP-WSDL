package hai704i.hotel;

import jakarta.xml.bind.annotation.*;
import javax.xml.datatype.XMLGregorianCalendar;
import java.math.BigDecimal;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(propOrder = {"idOffre", "nomHotel", "ville", "etoiles", "chambre", "capacite",
        "dateArrivee", "dateDepart", "nuits", "prixTotal", "valideJusqua"})
public class Offre {
    @XmlElement(required = true) private String idOffre;
    @XmlElement(required = true) private String nomHotel;
    @XmlElement(required = true) private String ville;
    private int etoiles;
    @XmlElement(required = true) private String chambre;
    private int capacite;
    @XmlElement(required = true) @XmlSchemaType(name = "date")
    private XMLGregorianCalendar dateArrivee;
    @XmlElement(required = true) @XmlSchemaType(name = "date")
    private XMLGregorianCalendar dateDepart;
    private int nuits;
    @XmlElement(required = true) private BigDecimal prixTotal;
    @XmlElement(required = true) @XmlSchemaType(name = "dateTime")
    private XMLGregorianCalendar valideJusqua;

    public Offre() { }

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

    public String getVille() {
        return ville;
    }

    public void setVille(String ville) {
        this.ville = ville;
    }

    public int getEtoiles() {
        return etoiles;
    }

    public void setEtoiles(int etoiles) {
        this.etoiles = etoiles;
    }

    public String getChambre() {
        return chambre;
    }

    public void setChambre(String chambre) {
        this.chambre = chambre;
    }

    public int getCapacite() {
        return capacite;
    }

    public void setCapacite(int capacite) {
        this.capacite = capacite;
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

    public int getNuits() {
        return nuits;
    }

    public void setNuits(int nuits) {
        this.nuits = nuits;
    }

    public BigDecimal getPrixTotal() {
        return prixTotal;
    }

    public void setPrixTotal(BigDecimal prixTotal) {
        this.prixTotal = prixTotal;
    }

    public XMLGregorianCalendar getValideJusqua() {
        return valideJusqua;
    }

    public void setValideJusqua(XMLGregorianCalendar valideJusqua) {
        this.valideJusqua = valideJusqua;
    }
}
