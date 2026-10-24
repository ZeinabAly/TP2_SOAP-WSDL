package hai704i.hotel.acces;

import jakarta.xml.ws.WebFault;

@WebFault(name = "RefusMetier", targetNamespace = "http://hotel.hai704i",
        faultBean = "hai704i.hotel.acces.RefusMetier")
public class RefusMetierException extends Exception {

    private final RefusMetier faultInfo;

    public RefusMetierException(String message, RefusMetier faultInfo) {
        super(message);
        this.faultInfo = faultInfo;
    }

    public RefusMetier getFaultInfo() {
        return faultInfo;
    }
}