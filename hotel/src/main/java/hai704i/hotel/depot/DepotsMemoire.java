package hai704i.hotel.depot;



import hai704i.hotel.metier.Modeles.*;
import hai704i.hotel.depot.Depots.*;

import java.time.LocalDate;
import java.util.List;
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
        @Override public void enregistrer(Devis d) {
            devis.put(d.id(), d);
        }
        @Override public Optional<Devis> trouver(String id) {
            return id == null ? Optional.empty() : Optional.ofNullable(devis.get(id));
        }
    }

    public static class ReservationsMemoire implements ReservationRepository {
        @Override public boolean chambreOccupee(String numero, LocalDate arrivee, LocalDate depart) {
            return false; // aucune réservation tant que A5 n'est pas fait
        }
    }
}