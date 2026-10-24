package hai704i.hotel;

import jakarta.xml.bind.annotation.*;

import javax.xml.datatype.XMLGregorianCalendar;
import java.math.BigDecimal;


@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(propOrder = {"agence", "ville", "dateArrivee", "dateDepart", "personnes", "etoilesMin", "prixMax"})
public class Demande {

    @XmlElement(required = true)
    private String agence;
    @XmlElement(required = true)
    private String ville;
    @XmlElement(required = true)
    @XmlSchemaType(name = "date")
    private XMLGregorianCalendar dateArrivee;
    @XmlElement(required = true)
    @XmlSchemaType(name = "date")
    private XMLGregorianCalendar dateDepart;
    private int personnes;
    private int etoilesMin;
    private BigDecimal prixMax;

    public Demande() {
    }

    public String getAgence() {
        return agence;
    }

    public void setAgence(String agence) {
        this.agence = agence;
    }

    public String getVille() {
        return ville;
    }

    public void setVille(String ville) {
        this.ville = ville;
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

    public int getPersonnes() {
        return personnes;
    }

    public void setPersonnes(int personnes) {
        this.personnes = personnes;
    }

    public int getEtoilesMin() {
        return etoilesMin;
    }

    public void setEtoilesMin(int etoilesMin) {
        this.etoilesMin = etoilesMin;
    }

    public BigDecimal getPrixMax() {
        return prixMax;
    }

    public void setPrixMax(BigDecimal prixMax) {
        this.prixMax = prixMax;
    }
}
