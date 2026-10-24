package hai704i.hotel.metier;

import hai704i.hotel.depot.Depots.*;
import hai704i.hotel.metier.Modeles.*;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;

public class HotelService {

    private static final Duration VALIDITE_DEVIS = Duration.ofMinutes(15);

    private final InfoHotel info;
    private final ChambreRepository chambres;
    private final AgenceRepository agences;
    private final DevisRepository devis;
    private final ReservationRepository reservations;
    private final Clock horloge;

    public HotelService(InfoHotel info, ChambreRepository chambres, AgenceRepository agences,
                        DevisRepository devis, ReservationRepository reservations, Clock horloge) {
        this.info = info;
        this.chambres = chambres;
        this.agences = agences;
        this.devis = devis;
        this.reservations = reservations;
        this.horloge = horloge;
    }

    public InfoHotel info() {
        return info;
    }

    public List<Devis> rechercher(Critere c) throws Refus {
        Agence agence = agences.trouver(c.idAgence())
                .orElseThrow(() -> new Refus(Motif.AGENCE_INCONNUE, "Agence inconnue : " + c.idAgence()));
        verifier(c);

        // Pas la bonne ville ou pas assez d'étoiles : liste vide, ce n'est PAS un refus.
        if (!info.ville().equalsIgnoreCase(c.ville().trim()) || info.etoiles() < c.etoilesMin()) {
            return List.of();
        }

        long nuits = ChronoUnit.DAYS.between(c.arrivee(), c.depart());
        Instant expire = horloge.instant().plus(VALIDITE_DEVIS);
        List<Devis> offres = new ArrayList<>();

        for (Chambre ch : chambres.toutes()) {
            if (ch.capacite() < c.personnes()) {
                continue;
            }
            if (reservations.chambreOccupee(ch.numero(), c.arrivee(), c.depart())) {
                continue;
            }
            // prix = prix de base x nombre de nuits x coefficient de CETTE agence
            BigDecimal prix = ch.prixNuitBase()
                    .multiply(BigDecimal.valueOf(nuits))
                    .multiply(agence.coefficient())
                    .setScale(2, RoundingMode.HALF_UP);
            if (c.prixMax() != null && prix.compareTo(c.prixMax()) > 0) {
                continue;
            }
            Devis d = new Devis("OF-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase(),
                    agence.id(), ch.numero(), ch.capacite(), c.arrivee(), c.depart(), prix, expire);
            devis.enregistrer(d);
            offres.add(d);
        }
        offres.sort(Comparator.comparing(Devis::prixTotal));
        return offres;
    }

    private void verifier(Critere c) throws Refus {
        if (c.ville() == null || c.ville().isBlank()) {
            throw new Refus(Motif.DEMANDE_INVALIDE, "La ville est obligatoire.");
        }
        if (c.arrivee() == null || c.depart() == null || !c.depart().isAfter(c.arrivee())) {
            throw new Refus(Motif.DEMANDE_INVALIDE, "Le départ doit être après l'arrivée.");
        }
        if (c.personnes() < 1) {
            throw new Refus(Motif.DEMANDE_INVALIDE, "Au moins une personne.");
        }
    }
}