package hai704i.hotel;

import jakarta.jws.WebService;
import java.util.List;

@WebService(serviceName = "HotelService", portName = "HotelSoapPort",
        targetNamespace = "http://hotel.hai704i",
        endpointInterface = "hai704i.hotel.HotelPort")
public class HotelPortImpl implements HotelPort {

    @Override
    public List<Offre> chercherOffres(Demande demande) {
        Offre o = new Offre();
        o.setIdOffre("OF-TEST");
        o.setNomHotel("Hôtel de test");
        o.setVille(demande.getVille());
        o.setChambre("101");
        return List.of(o);   // offre, remplacée en A2
    }
}