package hai704i.hotel.acces;


import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlType;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(propOrder = {"code", "message"})
public class RefusMetier {

    @XmlElement(required = true)
    private CodeRefus code;
    @XmlElement(required = true)
    private String message;

    public RefusMetier() { }   // obligatoire pour JAXB

    public RefusMetier(CodeRefus code, String message) {
        this.code = code;
        this.message = message;
    }

    public CodeRefus getCode() { return code; }
    public String getMessage() { return message; }
}