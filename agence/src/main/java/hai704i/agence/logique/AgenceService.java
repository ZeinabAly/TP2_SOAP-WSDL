package hai704i.agence.logique;


import hai704i.agence.logique.Types.*;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.*;

public class AgenceService implements AutoCloseable {

    private final List<HotelGateway> hotels;
    private final long delaiMs;
    private final ExecutorService pool;

    public AgenceService(List<HotelGateway> hotels, long delaiMs) {
        this.hotels = List.copyOf(hotels);
        this.delaiMs = delaiMs;
        this.pool = Executors.newFixedThreadPool(Math.max(1, hotels.size()), r -> {
            Thread t = new Thread(r, "agence-recherche");
            t.setDaemon(true);          // ne retient pas la JVM à la sortie
            return t;
        });
    }

    /** Interroge TOUS les hôtels en parallèle : le temps total ≈ le plus lent, pas la somme. */
    public ResultatRecherche rechercher(Sejour sejour) {
        List<Future<List<OffreAgence>>> futurs = new ArrayList<>();
        for (HotelGateway h : hotels) {
            futurs.add(pool.submit(() -> h.chercher(sejour)));   // tous partent AVANT qu'on attende
        }

        List<OffreAgence> offres = new ArrayList<>();
        List<String> incidents = new ArrayList<>();
        for (int i = 0; i < hotels.size(); i++) {
            String nom = hotels.get(i).nom();
            try {
                offres.addAll(futurs.get(i).get(delaiMs + 1000, TimeUnit.MILLISECONDS));
            } catch (TimeoutException e) {
                futurs.get(i).cancel(true);
                incidents.add(nom + " : pas de réponse dans le délai (" + delaiMs / 1000.0 + " s).");
            } catch (ExecutionException e) {
                Throwable cause = e.getCause();
                if (cause instanceof RefusHotel r) {
                    incidents.add(nom + " : refus de l'hôtel — " + r.getMessage());
                } else if (cause instanceof HotelInjoignable h) {
                    incidents.add(h.getMessage());
                } else {
                    incidents.add(nom + " : erreur inattendue (" + cause.getClass().getSimpleName() + ").");
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                incidents.add("Recherche interrompue.");
                break;
            }
        }
        offres.sort(Comparator.comparing(OffreAgence::prixTotal));
        return new ResultatRecherche(List.copyOf(offres), List.copyOf(incidents));
    }

    /** L'identifiant est créé UNE fois ici ; pour réessayer après une panne, on repasse CET objet. */
    public ReservationDemandee nouvelleDemande(OffreAgence offre, Voyageur voyageur, Carte carte) {
        return new ReservationDemandee(UUID.randomUUID().toString(), offre, voyageur, carte);
    }

    /** Réserve chez l'hôtel qui a émis l'offre. */
    public ConfirmationAgence reserver(ReservationDemandee demande) throws RefusHotel, HotelInjoignable {
        return demande.offre().hotel().reserver(demande);
    }

    @Override
    public void close() {
        pool.shutdownNow();
    }
}