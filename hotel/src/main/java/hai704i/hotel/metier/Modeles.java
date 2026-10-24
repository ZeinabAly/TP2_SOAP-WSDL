package hai704i.hotel.metier;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

public final class Modeles {
    private Modeles() { }

    public record InfoHotel(String nom, String ville, int etoiles) { }

    public record Chambre(String numero, int capacite, BigDecimal prixNuitBase) { }

    // En A5 on ajoutera ici le sel et l'empreinte du secret.
    public record Agence(String id, BigDecimal coefficient) { }

    public record Critere(String idAgence, String ville, LocalDate arrivee, LocalDate depart,
                          int personnes, int etoilesMin, BigDecimal prixMax) { }

    public record Devis(String id, String idAgence, String numeroChambre, int capacite,
                        LocalDate arrivee, LocalDate depart, BigDecimal prixTotal, Instant expireA) {
        public long nuits() {
            return ChronoUnit.DAYS.between(arrivee, depart);
        }
    }
}