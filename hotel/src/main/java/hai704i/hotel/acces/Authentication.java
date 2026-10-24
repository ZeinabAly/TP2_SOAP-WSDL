package hai704i.hotel.acces;

import jakarta.xml.bind.annotation.*;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(propOrder = {"idAgence", "secret"})
public class Authentication {

    @XmlElement(required = true)
    private String idAgence;

    @XmlElement(required = true)
    private String secret;

    public Authentication() {
    }

    public String getIdAgence() {
        return idAgence;
    }

    public void setIdAgence(String idAgence) {
        this.idAgence = idAgence;
    }

    public String getSecret() {
        return secret;
    }

    public void setSecret(String secret) {
        this.secret = secret;
    }
}
