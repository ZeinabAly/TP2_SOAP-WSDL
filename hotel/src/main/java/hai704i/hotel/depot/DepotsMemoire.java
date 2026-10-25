package hai704i.hotel.depot;



import hai704i.hotel.metier.Modeles.*;
import hai704i.hotel.depot.Depots.*;

import java.time.Clock;
import java.time.Instant;
import java.time.LocalDate;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

public final class DepotsMemoire {
    private DepotsMemoire() { }

    public static class ChambresMemoire implements ChambreRepository {
        private final List<Chambre> chambres;
        public ChambresMemoire(List<Chambre> chambres) {
            this.chambres = List.copyOf(chambres);
        }
        @Override public List<Chambre> toutes() {
            return chambres;
        }
    }

    public static class AgencesMemoire implements AgenceRepository {
        private final Map<String, Agence> agences = new ConcurrentHashMap<>();
        public void ajouter(Agence a) {
            agences.put(a.id(), a);
        }
        @Override public Optional<Agence> trouver(String id) {
            return id == null ? Optional.empty() : Optional.ofNullable(agences.get(id));
        }
    }

    public static class DevisMemoire implements DevisRepository {
        private final Map<String, Devis> devis = new ConcurrentHashMap<>();
        private final Clock horloge;

        // Se debarasser des devis qui ont expiré
        public DevisMemoire() { this(Clock.systemUTC()); }
        public DevisMemoire(Clock horloge) { this.horloge = horloge; }

        @Override public void enregistrer(Devis d) {
            Instant maintenant = horloge.instant();
            devis.values().removeIf(x -> x.expireA().isBefore(maintenant));
            devis.put(d.id(), d);
        }
        @Override public Optional<Devis> trouver(String id) {
            return id == null ? Optional.empty() : Optional.ofNullable(devis.get(id));
        }
    }

    public static class ReservationsMemoire implements ReservationRepository {
        private final List<Reservation> reservations = new CopyOnWriteArrayList<>();

        @Override public void enregistrer(Reservation r) { reservations.add(r); }

        @Override public Optional<Reservation> parDemande(String idAgence, String idDemande) {
            return reservations.stream()
                    .filter(r -> r.idAgence().equals(idAgence) && r.idDemande().equals(idDemande))
                    .findFirst();
        }

        @Override public boolean chambreOccupee(String numero, LocalDate arrivee, LocalDate depart) {
            // Deux séjours se chevauchent ssi arrivée1 < départ2 ET arrivée2 < départ1.
            return reservations.stream().anyMatch(r -> r.numeroChambre().equals(numero)
                    && r.arrivee().isBefore(depart) && arrivee.isBefore(r.depart()));
        }
    }
}