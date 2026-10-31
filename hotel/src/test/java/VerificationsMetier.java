import hai704i.hotel.depot.DepotsMemoire.*;
import hai704i.hotel.metier.HotelService;
import hai704i.hotel.metier.Modeles.*;
import hai704i.hotel.metier.Motif;
import hai704i.hotel.metier.Refus;
import hai704i.hotel.metier.SecretHasher;

import java.math.BigDecimal;
import java.time.Clock;
import java.time.LocalDate;
import java.util.List;

public class VerificationsMetier {

    interface Action { void run() throws Refus; }

    static int echecs = 0;

    static void verifie(String libelle, boolean ok) {
        System.out.println((ok ? "OK    " : "ECHEC ") + libelle);
        if (!ok) echecs++;
    }

    static void verifieRefus(String libelle, Motif attendu, Action action) {
        try {
            action.run();
            verifie(libelle + " (aucun refus levé)", false);
        } catch (Refus refus) {
            verifie(libelle + " -> " + refus.motif(), refus.motif() == attendu);
        }
    }

    public static void main(String[] args) throws Exception {
        byte[] sel = SecretHasher.nouveauSel();
        AgencesMemoire agences = new AgencesMemoire();
        agences.ajouter(new Agence("AG-01", sel,
                SecretHasher.empreinte("demo-secret-01".toCharArray(), sel), new BigDecimal("0.90")));

        HotelService service = new HotelService(new InfoHotel("Soleil", "Montpellier", 4),
                new ChambresMemoire(List.of(new Chambre("101", 2, new BigDecimal("80.00")))),
                agences, new DevisMemoire(), new ReservationsMemoire(), Clock.systemUTC());

        LocalDate arrivee = LocalDate.of(2026, 11, 10);
        LocalDate depart = LocalDate.of(2026, 11, 13);

        // 1. Recherche : on récupère le devis que le service a lui-même enregistré
        List<Devis> devis = service.rechercher(new Critere("AG-01", "Montpellier", arrivee, depart, 2, 0, null));
        verifie("un seul devis, à 216.00 (80 x 3 x 0.90)",
                devis.size() == 1 && devis.get(0).prixTotal().equals(new BigDecimal("216.00")));
        Devis d = devis.get(0);

        verifieRefus("agence inconnue", Motif.AGENCE_INCONNUE,
                () -> service.rechercher(new Critere("INCONNUE", "Montpellier", arrivee, depart, 2, 0, null)));

        // 2. Réservation, puis rejeu de la même demande
        CommandeReservation cmd = new CommandeReservation("AG-01", "demo-secret-01", "D-1", d.id(),
                "Camara", "Zéinab", new DonneesPaiement("4111 1111 1111 1111", 12, 2030, "123"));
        try {
            ConfirmationMetier confirmA = service.reserver(cmd);
            verifie("réservation créée, au prix du devis",
                    !confirmA.rejeu() && confirmA.reservation().prixTotal().equals(d.prixTotal()));
            ConfirmationMetier confirmB = service.reserver(cmd);
            verifie("rejeu : même référence, marqué rejeu",
                    confirmB.rejeu() && confirmA.reservation().reference().equals(confirmB.reservation().reference()));
        } catch (Refus r) {
            verifie("réservation inattendue refusée : " + r.motif(), false);
        }

        // 3. Les refus
        verifieRefus("mauvais secret", Motif.SECRET_INVALIDE, () -> service.reserver(
                new CommandeReservation("AG-01", "faux", "D-2", d.id(), "Camara", "Zéinab",
                        new DonneesPaiement("4111 1111 1111 1111", 12, 2030, "123"))));
        verifieRefus("carte expirée", Motif.PAIEMENT_INVALIDE, () -> service.reserver(
                new CommandeReservation("AG-01", "demo-secret-01", "D-3", d.id(), "Camara", "Zéinab",
                        new DonneesPaiement("4111 1111 1111 1111", 12, 2020, "123"))));
        verifieRefus("même chambre, mêmes dates, nouvelle demande", Motif.OFFRE_INDISPONIBLE, () -> service.reserver(
                new CommandeReservation("AG-01", "demo-secret-01", "D-4", d.id(), "Camara", "Zéinab",
                        new DonneesPaiement("4111 1111 1111 1111", 12, 2030, "123"))));
        verifieRefus("même demande, autre offre", Motif.DEMANDE_INCOHERENTE, () -> service.reserver(
                new CommandeReservation("AG-01", "demo-secret-01", "D-1", "OF-AUTRE", "Camara", "Zéinab",
                        new DonneesPaiement("4111 1111 1111 1111", 12, 2030, "123"))));

        System.out.println(echecs == 0 ? "TOUT EST OK" : echecs + " ECHEC(S)");
    }
}