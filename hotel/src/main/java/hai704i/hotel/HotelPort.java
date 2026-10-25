package hai704i.hotel;

import hai704i.hotel.acces.Confirmation;
import hai704i.hotel.acces.DemandeReservation;
import hai704i.hotel.acces.RefusMetierException;
import jakarta.jws.WebMethod;
import jakarta.jws.WebParam;
import jakarta.jws.WebResult;
import jakarta.jws.WebService;
import java.util.List;

@WebService(name = "HotelPort", targetNamespace = "http://hotel.hai704i")
public interface HotelPort {

    @WebMethod(operationName = "chercherOffres")
    @WebResult(name = "offre")
    List<Offre> chercherOffres(@WebParam(name = "demande") Demande demande) throws RefusMetierException;


    @WebMethod
    @WebResult(name = "confirmation")
    Confirmation reserver(@WebParam(name = "reservation") DemandeReservation reservation)
            throws RefusMetierException;
}