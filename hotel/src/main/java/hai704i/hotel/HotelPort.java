package hai704i.hotel;

import jakarta.jws.WebMethod;
import jakarta.jws.WebParam;
import jakarta.jws.WebResult;
import jakarta.jws.WebService;
import java.util.List;

@WebService(name = "HotelPort", targetNamespace = "http://hotel.hai704i")
public interface HotelPort {

    @WebMethod
    @WebResult(name = "offre")
    List<Offre> chercherOffres(@WebParam(name = "demande") Demande demande);
}