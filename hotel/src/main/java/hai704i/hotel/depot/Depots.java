package hai704i.hotel.depot;


import hai704i.hotel.metier.Modeles.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public final class Depots {
    private Depots() { }

    public interface ChambreRepository {
        List<Chambre> toutes();
    }

    public interface AgenceRepository {
        Optional<Agence> trouver(String id);
    }

    public interface DevisRepository {
        void enregistrer(Devis devis);
        Optional<Devis> trouver(String id);
    }

    // En A5 on ajoutera enregistrer(...) et parDemande(...).
    public interface ReservationRepository {
        boolean chambreOccupee(String numeroChambre, LocalDate arrivee, LocalDate depart);
        void enregistrer(Reservation r);
        Optional<Reservation> parDemande(String idAgence, String idDemande);
    }
}