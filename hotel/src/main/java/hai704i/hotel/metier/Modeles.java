package hai704i.hotel.metier;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

public final class Modeles {
    private Modeles() { }

    public record InfoHotel(String nom, String ville, int etoiles) { }

    public record Chambre(String numero, int capacite, BigDecimal prixNuitBase) { }


    public record Agence(String id, byte[] sel, byte[] empreinte, BigDecimal coefficient) { }

    public record Critere(String idAgence, String ville, LocalDate arrivee, LocalDate depart,
                          int personnes, int etoilesMin, BigDecimal prixMax) { }

    public record Devis(String id, String idAgence, String numeroChambre, int capacite,
                        LocalDate arrivee, LocalDate depart, BigDecimal prixTotal, Instant expireA) {
        public long nuits() {
            return ChronoUnit.DAYS.between(arrivee, depart);
        }
    }

    // A5
    public record DonneesPaiement(String numeroCarte, int moisExpiration, int anneeExpiration, String cvc) {
        @Override public String toString() {
            return "DonneesPaiement[carte=" + Paiements.masquer(numeroCarte) + ", cvc=***]";
        }
    }

    public record CommandeReservation(String idAgence, String secret, String idDemande, String idOffre,
                                      String nom, String prenom, DonneesPaiement paiement) {
        @Override public String toString() {
            return "CommandeReservation[agence=" + idAgence + ", demande=" + idDemande + ", secret=***]";
        }
    }

    public record Reservation(String reference, String idAgence, String idDemande, String idOffre,
                              String numeroChambre, LocalDate arrivee, LocalDate depart, BigDecimal prixTotal,
                              String nom, String prenom, String carteMasquee, Instant creeLe) { }

    public record ConfirmationMetier(Reservation reservation, boolean rejeu) { }

    public static final class Paiements {
        private Paiements() { }
        public static String chiffres(String numero) {
            return numero == null ? "" : numero.replaceAll("[ -]", "");
        }
        public static String masquer(String numero) {
            String c = chiffres(numero);
            return c.length() < 4 ? "****" : "**** **** **** " + c.substring(c.length() - 4);
        }
    }
}