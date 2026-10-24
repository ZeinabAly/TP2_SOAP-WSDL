package hai704i.hotel.metier;

import hai704i.hotel.depot.DepotsMemoire.*;
import hai704i.hotel.metier.Modeles.*;
import java.math.BigDecimal;
import java.time.Clock;
import java.time.LocalDate;
import java.util.List;

public class VerificationsMetier {
    public static void main(String[] args) throws Exception {

        AgencesMemoire agences = new AgencesMemoire();
        agences.ajouter(new Agence("AG-01", new BigDecimal("0.90")));
        HotelService s = new HotelService(new InfoHotel("Soleil", "Montpellier", 4),
                new ChambresMemoire(List.of(new Chambre("101", 2, new BigDecimal("80.00")))),
                agences, new DevisMemoire(), new ReservationsMemoire(), Clock.systemUTC());

        Critere c = new Critere("AG-01", "Montpellier", LocalDate.of(2026, 11, 10),
                LocalDate.of(2026, 11, 13), 2, 0, null);
        System.out.println("Offres : " + s.rechercher(c));      // un devis à 216.00

        try {
            s.rechercher(new Critere("INCONNUE", "Montpellier", LocalDate.of(2026, 11, 10),
                    LocalDate.of(2026, 11, 13), 2, 0, null));
        } catch (Refus r) {
            System.out.println("Refus attendu : " + r.motif());  // AGENCE_INCONNUE
        }
    }
}