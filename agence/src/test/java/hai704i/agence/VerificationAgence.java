package hai704i.agence;


import hai704i.agence.logique.*;
import hai704i.agence.logique.Types.*;
import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.List;

public class VerificationAgence {

    /** Faux hôtel : répond avec une offre, ou lance une exception, ou dort trop longtemps. */
    static class Faux implements HotelGateway {
        final String nom; final String prix; final String panne; final long dormirMs;
        Faux(String nom, String prix, String panne, long dormirMs) {
            this.nom = nom; this.prix = prix; this.panne = panne; this.dormirMs = dormirMs;
        }
        @Override public String nom() { return nom; }
        @Override public List<OffreAgence> chercher(Sejour s) throws RefusHotel, HotelInjoignable {
            try { Thread.sleep(dormirMs); } catch (InterruptedException e) { Thread.currentThread().interrupt(); }
            if ("injoignable".equals(panne)) throw new HotelInjoignable(nom, "connexion refusée");
            if ("refus".equals(panne)) throw new RefusHotel("AGENCE_INCONNUE", "pas partenaire");
            return List.of(new OffreAgence(this, nom + "-1", nom, 3, "101", 2, s.arrivee(), s.depart(), 3, new BigDecimal(prix),
                    java.time.ZonedDateTime.now().plusMinutes(15)));
        }
        @Override public ConfirmationAgence reserver(ReservationDemandee d) { return null; }
    }

    static int echecs = 0;
    static void verifie(String libelle, boolean ok) {
        System.out.println((ok ? "OK   " : "ECHEC") + " " + libelle);
        if (!ok) echecs++;
    }

     static void main(String[] args) {
        Sejour s = new Sejour("Montpellier", LocalDate.of(2026, 11, 10), LocalDate.of(2026, 11, 13), 2, 0, null);

        try (AgenceService a = new AgenceService(List.of(
                new Faux("Cher", "300.00", null, 0), new Faux("Pas cher", "100.00", null, 0)), 500)) {
            ResultatRecherche r = a.rechercher(s);
            verifie("deux offres, triées par prix croissant",
                    r.offres().size() == 2 && r.offres().get(0).nomHotel().equals("Pas cher"));
            verifie("aucun incident", r.incidents().isEmpty());
        }

        try (AgenceService a = new AgenceService(List.of(
                new Faux("Ok", "100.00", null, 0), new Faux("Eteint", "0", "injoignable", 0)), 500)) {
            ResultatRecherche r = a.rechercher(s);
            verifie("un hôtel en panne : l'autre répond quand même", r.offres().size() == 1);
            verifie("la panne est signalée", r.incidents().size() == 1);
        }

        try (AgenceService a = new AgenceService(List.of(
                new Faux("Ok", "100.00", null, 0), new Faux("Lent", "50.00", null, 5000)), 500)) {
            long debut = System.currentTimeMillis();
            ResultatRecherche r = a.rechercher(s);
            long ms = System.currentTimeMillis() - debut;
            verifie("un hôtel trop lent est abandonné (" + ms + " ms < 3000)", ms < 3000 && r.offres().size() == 1);
            verifie("le délai dépassé est signalé", r.incidents().size() == 1);
        }

        try (AgenceService a = new AgenceService(List.of(
                new Faux("A", "100.00", null, 800), new Faux("B", "100.00", null, 800)), 2000)) {
            long debut = System.currentTimeMillis();
            a.rechercher(s);
            long ms = System.currentTimeMillis() - debut;
            verifie("recherche parallèle : " + ms + " ms (< 1500, pas 1600)", ms < 1500);
        }

        System.out.println(echecs == 0 ? "TOUT EST OK" : echecs + " ÉCHEC(S)");
    }
}