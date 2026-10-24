package hai704i.hotel.acces;

import jakarta.xml.bind.annotation.*;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(propOrder = {
        "numeroCarte",
        "moisExpiration",
        "anneeExpiration",
        "cvc"
})
public class Paiement {

    @XmlElement(required = true)
    private String numeroCarte;

    private int moisExpiration;

    private int anneeExpiration;

    @XmlElement(required = true)
    private String cvc;

    public Paiement() {
    }

    public String getNumeroCarte() {
        return numeroCarte;
    }

    public void setNumeroCarte(String numeroCarte) {
        this.numeroCarte = numeroCarte;
    }

    public int getMoisExpiration() {
        return moisExpiration;
    }

    public void setMoisExpiration(int moisExpiration) {
        this.moisExpiration = moisExpiration;
    }

    public int getAnneeExpiration() {
        return anneeExpiration;
    }

    public void setAnneeExpiration(int anneeExpiration) {
        this.anneeExpiration = anneeExpiration;
    }

    public String getCvc() {
        return cvc;
    }

    public void setCvc(String cvc) {
        this.cvc = cvc;
    }
}