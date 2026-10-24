package hai704i.agence.console;


import hai704i.agence.acces.HotelSoapGateway;
import hai704i.agence.logique.Types.*;
import java.time.LocalDate;

public class Main {
    public static void main(String[] args) throws Exception {
        HotelSoapGateway hotel = new HotelSoapGateway("http://localhost:8081/hotel", "AG-1", "", 3000);
        Sejour s = new Sejour("Montpellier", LocalDate.of(2026, 11, 10), LocalDate.of(2026, 11, 13), 2, 0, null);
        for (OffreAgence o : hotel.chercher(s)) {
            System.out.println(o.nomHotel() + " chambre " + o.chambre() + " : " + o.prixTotal() + " EUR");
        }
    }
}