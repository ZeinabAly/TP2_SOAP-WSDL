package hai704i.agence.logique;


import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.ZonedDateTime;

public final class Types {
    private Types() { }

    public record Sejour(String ville, LocalDate arrivee, LocalDate depart, int personnes,
                         int etoilesMin, BigDecimal prixMax) { }

    public record OffreAgence(HotelGateway hotel, String idOffre, String nomHotel, int etoiles,
                              String chambre, int capacite, LocalDate arrivee, LocalDate depart,
                              int nuits, BigDecimal prixTotal, ZonedDateTime valideJusqua) { }

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
}