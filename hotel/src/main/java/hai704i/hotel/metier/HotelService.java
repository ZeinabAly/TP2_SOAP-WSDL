package hai704i.hotel.metier;

import hai704i.hotel.depot.Depots.*;
import hai704i.hotel.metier.Modeles.*;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.*;

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

    public synchronized ConfirmationMetier reserver(CommandeReservation cmd) throws Refus {
        // 1. QUI PARLE ? (avant tout le reste)
        Agence agence = agenceConnue(cmd.idAgence());
        if (cmd.secret() == null
                || !SecretHasher.verifier(cmd.secret().toCharArray(), agence.sel(), agence.empreinte())) {
            throw new Refus(Motif.SECRET_INVALIDE, "Authentification refusée pour l'agence " + agence.id() + ".");
        }

        // 2. EST-CE UN REJEU ? (même agence, même identifiant de demande)
        if (vide(cmd.idDemande())) {
            throw new Refus(Motif.DEMANDE_INVALIDE, "L'identifiant de demande est obligatoire.");
        }
        Optional<Reservation> deja = reservations.parDemande(agence.id(), cmd.idDemande());
        if (deja.isPresent()) {
            if (!deja.get().idOffre().equals(cmd.idOffre())) {
                throw new Refus(Motif.DEMANDE_INCOHERENTE, "Cet identifiant de demande a déjà servi pour une autre offre.");
            }
            return new ConfirmationMetier(deja.get(), true);   // aucun effet supplémentaire
        }

        // 3. LA DEMANDE EST-ELLE VALIDE ?
        if (vide(cmd.idOffre()) || vide(cmd.nom()) || vide(cmd.prenom())) {
            throw new Refus(Motif.DEMANDE_INVALIDE, "Offre, nom et prénom du voyageur sont obligatoires.");
        }
        verifierPaiement(cmd.paiement());

        // 4. L'OFFRE EXISTE, EST À CETTE AGENCE, EST VALABLE, LA CHAMBRE EST LIBRE ?
        Devis d = devis.trouver(cmd.idOffre())
                .filter(x -> x.idAgence().equals(agence.id()))
                .orElseThrow(() -> new Refus(Motif.OFFRE_INCONNUE, "Offre inconnue : " + cmd.idOffre() + "."));
        if (horloge.instant().isAfter(d.expireA())) {
            throw new Refus(Motif.OFFRE_EXPIREE, "L'offre " + d.id() + " n'est plus valable : relancez une recherche.");
        }
        if (reservations.chambreOccupee(d.numeroChambre(), d.arrivee(), d.depart())) {
            throw new Refus(Motif.OFFRE_INDISPONIBLE, "La chambre de l'offre " + d.id() + " n'est plus disponible.");
        }

        // 5. ON ÉCRIT. Le prix est celui du DEVIS, jamais recalculé.
        Reservation r = new Reservation("RES-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase(),
                agence.id(), cmd.idDemande(), d.id(), d.numeroChambre(), d.arrivee(), d.depart(), d.prixTotal(),
                cmd.nom().trim(), cmd.prenom().trim(), Paiements.masquer(cmd.paiement().numeroCarte()),
                horloge.instant());
        reservations.enregistrer(r);
        return new ConfirmationMetier(r, false);
    }

    private Agence agenceConnue(String id) throws Refus {
        if (vide(id)) {
            throw new Refus(Motif.AGENCE_INCONNUE, "Agence non précisée.");
        }
        return agences.trouver(id.trim()).orElseThrow(
                () -> new Refus(Motif.AGENCE_INCONNUE, "L'agence « " + id + " » n'est pas partenaire de cet hôtel."));
    }

    /** Les messages ne répètent JAMAIS la valeur fautive : un numéro de carte ne doit pas finir dans un journal. */
    private void verifierPaiement(DonneesPaiement p) throws Refus {
        if (p == null) {
            throw new Refus(Motif.PAIEMENT_INVALIDE, "Coordonnées de paiement absentes.");
        }
        if (!Paiements.chiffres(p.numeroCarte()).matches("\\d{13,19}")) {
            throw new Refus(Motif.PAIEMENT_INVALIDE, "Numéro de carte invalide (13 à 19 chiffres attendus).");
        }
        if (p.moisExpiration() < 1 || p.moisExpiration() > 12) {
            throw new Refus(Motif.PAIEMENT_INVALIDE, "Mois d'expiration invalide (1 à 12).");
        }
        LocalDate aujourdhui = LocalDate.now(horloge);
        boolean revolue = p.anneeExpiration() < aujourdhui.getYear()
                || (p.anneeExpiration() == aujourdhui.getYear() && p.moisExpiration() < aujourdhui.getMonthValue());
        if (revolue) {
            throw new Refus(Motif.PAIEMENT_INVALIDE, "Carte expirée.");
        }
        if (p.cvc() == null || !p.cvc().matches("\\d{3,4}")) {
            throw new Refus(Motif.PAIEMENT_INVALIDE, "Cryptogramme invalide (3 ou 4 chiffres).");
        }
    }

    private static boolean vide(String s) {
        return s == null || s.isBlank();
    }
}