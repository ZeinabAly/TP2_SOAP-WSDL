package hai704i.hotel.acces;

import hai704i.hotel.Demande;
import hai704i.hotel.HotelPort;
import hai704i.hotel.Offre;
import jakarta.jws.WebService;
import java.util.List;

import hai704i.hotel.metier.*;

@WebService(serviceName = "HotelService", portName = "HotelSoapPort",
        targetNamespace = "http://hotel.hai704i",
        endpointInterface = "hai704i.hotel.HotelPort")
public class HotelPortImpl implements HotelPort {

    private final HotelService service;

    public HotelPortImpl(HotelService service) {
        this.service = service;
    }

    @Override
    public List<Offre> chercherOffres(Demande demande) {
        try {
            return service.rechercher(Conversions.versCritere(demande)).stream()
                    .map(d -> Conversions.versOffre(d, service.info()))
                    .toList();
        } catch (Refus r) {
            throw new RuntimeException(r.getMessage()); // provisoire, remplacé en A3
        }
    }
}