package hai704i.agence.console;


import hai704i.agence.acces.HotelSoapGateway;
import hai704i.agence.logique.Types;
import hai704i.agence.logique.Types.*;
import java.time.LocalDate;

public class Main {
    public static void main(String[] args) throws Exception {
        HotelSoapGateway hotel = new HotelSoapGateway("http://localhost:8081/hotel", "AG-01", "demo-secret-01", 3000);
        Sejour s = new Sejour("Montpellier", LocalDate.of(2026, 11, 10), LocalDate.of(2026, 11, 13), 2, 0, null);
        OffreAgence offre = hotel.chercher(s).getFirst();
        ReservationDemandee demande = new ReservationDemandee(java.util.UUID.randomUUID().toString(), offre,
                new Types.Voyageur("Camara", "Zéinab"), new Types.Carte("4111 1111 1111 1111", 12, 2030, "123"));
        System.out.println(hotel.reserver(demande));
        System.out.println(hotel.reserver(demande));   // même demande : dejaEnregistree = true
    }
}