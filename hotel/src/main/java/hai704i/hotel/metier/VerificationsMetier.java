package hai704i.hotel.metier;

import hai704i.hotel.Offre;
import hai704i.hotel.depot.DepotsMemoire.*;
import hai704i.hotel.metier.Modeles.*;
import java.math.BigDecimal;
import java.time.Clock;
import java.time.LocalDate;
import java.util.List;

public class VerificationsMetier {
    public static void main(String[] args) throws Exception {

        byte[] sel = SecretHasher.nouveauSel();

        AgencesMemoire agences = new AgencesMemoire();
        agences.ajouter(new Agence("AG-01", sel, SecretHasher.empreinte("demo-secret-01".toCharArray(), sel), new BigDecimal("0.90")));

        // Offre
        Offre offre = new Offre();


        HotelService s = new HotelService(new InfoHotel("Soleil", "Montpellier", 4),
                new ChambresMemoire(List.of(new Chambre("101", 2, new BigDecimal("80.00")))),
                agences, new DevisMemoire(), new ReservationsMemoire(), Clock.systemUTC());

        Critere c = new Critere("AG-01", "Montpellier", LocalDate.of(2026, 11, 10),
                LocalDate.of(2026, 11, 13), 2, 0, null);
        System.out.println("Offres : " + s.rechercher(c));      // un devis à 216.00

        try {
            s.rechercher(new Critere("INCONNUE", "Montpellier", LocalDate.of(2026, 11, 10),
                    LocalDate.of(2026, 11, 13), 2, 0, null));


            // remplace idOffre par d.id()
            CommandeReservation cmd = new CommandeReservation("AG-01", "demo-secret-01", "D-1", "10", "Camara", "Zéinab",
                    new DonneesPaiement("4111 1111 1111 1111", 12, 2030, "123"));
            ConfirmationMetier a = s.reserver(cmd);
            ConfirmationMetier b = s.reserver(cmd);   // même demande rejouée
            System.out.println(a.reservation().reference().equals(b.reservation().reference()) && b.rejeu());

        } catch (Refus r) {
            System.out.println("Refus attendu : " + r.motif());  // AGENCE_INCONNUE
        }
    }
}