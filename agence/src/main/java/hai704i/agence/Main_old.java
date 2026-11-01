package hai704i.agence;

import hai704i.agence.acces.HotelSoapGateway;
import hai704i.agence.logique.AgenceService;
import hai704i.agence.logique.HotelGateway;
import hai704i.agence.logique.Types.*;

import java.time.LocalDate;
import java.util.List;


public class Main_old {
    static void main() {
        List<HotelGateway> hotels = List.of(
                new HotelSoapGateway("http://localhost:8081/hotel", "AG-01", "demo-secret-01", 3000),
                new HotelSoapGateway("http://localhost:8082/hotel", "AG-01", "demo-secret-01", 3000));
        try (AgenceService a = new AgenceService(hotels, 3000)) {
            ResultatRecherche r = a.rechercher(new Sejour("Montpellier", LocalDate.of(2026, 11, 10), LocalDate.of(2026, 11, 13), 2, 0, null));
            r.offres().forEach(o -> System.out.println(o.nomHotel() + " " + o.prixTotal()));
            r.incidents().forEach(i -> System.out.println("INCIDENT : " + i));
        }
    }
}
