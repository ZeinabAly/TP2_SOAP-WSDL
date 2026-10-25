package hai704i.agence.logique;


import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.ZonedDateTime;
import java.util.List;

public final class Types {
    private Types() { }

    public record Sejour(String ville, LocalDate arrivee, LocalDate depart, int personnes,
                         int etoilesMin, BigDecimal prixMax) { }

    public record OffreAgence(HotelGateway hotel, String idOffre, String nomHotel, int etoiles,
                              String chambre, int capacite, LocalDate arrivee, LocalDate depart,
                              int nuits, BigDecimal prixTotal, ZonedDateTime valideJusqua) { }


    public record ResultatRecherche(List<OffreAgence> offres, List<String> incidents) { }

    /** L'hôtel a RÉPONDU « non » : décision définitive. */
    public static class RefusHotel extends Exception {
        private final String code;
        public RefusHotel(String code, String message) { super(message); this.code = code; }
        public String code() { return code; }
    }

    /** L'hôtel n'a pas répondu : l'issue est INCERTAINE. */
    public static class HotelInjoignable extends Exception {
        public HotelInjoignable(String hotel, String cause) {
            super("Hôtel « " + hotel + " » injoignable : " + cause);
        }
    }

    public record Voyageur(String nom, String prenom) { }

    /** toString masqué : un affichage accidentel ne révèle rien. */
    public record Carte(String numero, int mois, int annee, String cvc) {
        @Override public String toString() { return "Carte[****]"; }
    }

    /** L'identifiant est créé UNE FOIS, avant le premier envoi : après une panne on renvoie le MÊME objet. */
    public record ReservationDemandee(String idDemande, OffreAgence offre, Voyageur voyageur, Carte carte) { }

    public record ConfirmationAgence(String reference, String nomHotel, String chambre, LocalDate arrivee,
                                     LocalDate depart, BigDecimal prixTotal, String carteMasquee,
                                     boolean dejaEnregistree) { }


}