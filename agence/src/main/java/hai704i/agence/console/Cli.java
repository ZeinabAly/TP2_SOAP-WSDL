package hai704i.agence.console;


import hai704i.agence.logique.AgenceService;
import hai704i.agence.logique.Types.*;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;

public class Cli {

    private static final class FinDeSaisie extends RuntimeException { }

    private final AgenceService agence;
    private final String idAgence;
    private final BufferedReader entree = new BufferedReader(new InputStreamReader(System.in, StandardCharsets.UTF_8));
    private final PrintStream sortie = System.out;
    private List<OffreAgence> dernieresOffres = List.of();

    public Cli(AgenceService agence, String idAgence) {
        this.agence = agence;
        this.idAgence = idAgence;
    }

    public void lancer() {
        sortie.println("=== Agence " + idAgence + " ===");
        try {
            while (true) {
                sortie.println();
                sortie.println("1. Rechercher un séjour");
                sortie.println("2. Revoir les dernières offres");
                sortie.println("3. Réserver une offre");
                sortie.println("0. Quitter");
                switch (lireLigne("Votre choix")) {
                    case "1" -> rechercher();
                    case "2" -> afficherOffres();
                    case "3" -> reserver();
                    case "0" -> { sortie.println("Au revoir."); return; }
                    default -> sortie.println("Choix inconnu : tapez 0, 1, 2 ou 3");
                }
            }
        } catch (FinDeSaisie e) {
            sortie.println();
            sortie.println("Fin de la saisie : au revoir.");
        }
    }

    // ---------------------------------------------------------------- 1. recherche
    private void rechercher() {
        String ville = lireNonVide("Ville");
        LocalDate arrivee = lireDate("Date d'arrivée");
        LocalDate depart = lireDate("Date de départ");
        while (!depart.isAfter(arrivee)) {
            sortie.println("La date de départ doit suivre la date d'arrivée.");
            depart = lireDate("Date de départ");
        }
        int personnes = lireEntier("Nombre de personnes", 1, 20);
        int etoiles = lireEntier("Étoiles minimum (0 = peu importe)", 0, 5);
        BigDecimal prixMax = lirePrixMaxFacultatif();

        sortie.println("Interrogation des hôtels…");
        ResultatRecherche res = agence.rechercher(new Sejour(ville, arrivee, depart, personnes, etoiles, prixMax));
        dernieresOffres = res.offres();
        afficherOffres();
        for (String incident : res.incidents()) {
            sortie.println("  Alerte ! " + incident);
        }
    }

    private void afficherOffres() {
        if (dernieresOffres.isEmpty()) {
            sortie.println("Aucune offre à afficher - Faites d'abord une recherche (option 1)");
            return;
        }
        DateTimeFormatter heure = DateTimeFormatter.ofPattern("HH:mm");
        sortie.println("Offres (du moins cher au plus cher) :");
        for (int i = 0; i < dernieresOffres.size(); i++) {
            OffreAgence o = dernieresOffres.get(i);
            sortie.printf("  %d. %s (%d★) - chambre %s, %d pers. - %d nuit(s) - %s € - valable jusqu'à %s%n",
                    i + 1, o.nomHotel(), o.etoiles(), o.chambre(), o.capacite(), o.nuits(),
                    o.prixTotal().toPlainString(), o.valideJusqua().format(heure));
        }
    }

    // ---------------------------------------------------------------- 3. réservation
    private void reserver() {
        if (dernieresOffres.isEmpty()) {
            sortie.println("Faites d'abord une recherche (option 1).");
            return;
        }
        afficherOffres();
        OffreAgence offre = dernieresOffres.get(lireEntier("Numéro de l'offre", 1, dernieresOffres.size()) - 1);

        sortie.println("Voyageur principal :");
        Voyageur voyageur = new Voyageur(lireNonVide("  Nom"), lireNonVide("  Prénom"));
        sortie.println("Paiement - N'UTILISEZ QUE DES NUMÉROS FICTIFS (ex. 4111 1111 1111 1111) :");
        Carte carte = new Carte(lireNonVide("  Numéro de carte"),
                lireEntier("  Mois d'expiration (1-12)", 1, 12),
                lireEntier("  Année d'expiration (ex. 2030)", 2000, 2100),
                lireSecret("  Cryptogramme (CVC)"));

        // L'identifiant de demande est fixé ICI, une fois pour toutes.
        ReservationDemandee demande = agence.nouvelleDemande(offre, voyageur, carte);
        boolean encore = true;
        while (encore) {
            encore = false;
            try {
                afficherConfirmation(agence.reserver(demande));
            } catch (RefusHotel r) {
                // L'hôtel a répondu : refus clair, définitif. On n'insiste pas.
                sortie.println("Réservation REFUSÉE : " + r.getMessage());
            } catch (HotelInjoignable h) {
                // Pas de réponse : l'hôtel a peut-être traité. Rejouer est SÛR (même identifiant).
                sortie.println(h.getMessage());
                sortie.println("Attention : la réservation a peut-être été enregistrée malgré tout.");
                encore = oui("Réessayer avec la même demande (sans risque de doublon) ?");
            }
        }
    }

    private void afficherConfirmation(ConfirmationAgence c) {
        sortie.println();
        sortie.println(c.dejaEnregistree()
                ? "Cette demande avait déjà été enregistrée : voici la réservation existante."
                : "Réservation CONFIRMÉE.");
        sortie.println("  Référence : " + c.reference());
        sortie.println("  Hôtel     : " + c.nomHotel() + ", chambre " + c.chambre());
        sortie.println("  Séjour    : du " + c.arrivee() + " au " + c.depart());
        sortie.println("  Prix      : " + c.prixTotal().toPlainString() + " €");
        sortie.println("  Carte     : " + c.carteMasquee());
    }

    // ---------------------------------------------------------------- saisie robuste
    private String lireLigne(String invite) {
        sortie.print(invite + " : ");
        sortie.flush();
        try {
            String ligne = entree.readLine();
            if (ligne == null) throw new FinDeSaisie();
            return ligne.trim();
        } catch (IOException e) {
            throw new FinDeSaisie();
        }
    }

    private String lireNonVide(String invite) {
        while (true) {
            String s = lireLigne(invite);
            if (!s.isEmpty()) return s;
            sortie.println("Ce champ est obligatoire.");
        }
    }

    private LocalDate lireDate(String invite) {
        while (true) {
            try {
                return LocalDate.parse(lireLigne(invite + " (AAAA-MM-JJ)"));
            } catch (DateTimeParseException e) {
                sortie.println("Date invalide : écrivez par exemple 2026-11-10.");
            }
        }
    }

    private int lireEntier(String invite, int min, int max) {
        while (true) {
            try {
                int v = Integer.parseInt(lireLigne(invite));
                if (v >= min && v <= max) return v;
            } catch (NumberFormatException ignore) { /* on redemande */ }
            sortie.println("Entrez un nombre entre " + min + " et " + max + ".");
        }
    }

    private BigDecimal lirePrixMaxFacultatif() {
        while (true) {
            String s = lireLigne("Prix maximum en € (vide = sans limite)");
            if (s.isEmpty()) return null;
            try {
                BigDecimal v = new BigDecimal(s.replace(',', '.'));
                if (v.signum() > 0) return v;
            } catch (NumberFormatException ignore) { /* on redemande */ }
            sortie.println("Entrez un montant positif, ou laissez vide.");
        }
    }

    /** CVC : saisie masquée si la console le permet ; jamais réaffiché, jamais écrit. */
    private String lireSecret(String invite) {
        while (true) {
            String s;
            if (System.console() != null) {
                char[] c = System.console().readPassword("%s : ", invite);
                if (c == null) throw new FinDeSaisie();
                s = new String(c).trim();
            } else {
                s = lireLigne(invite + " (non masqué dans cet environnement)");
            }
            if (!s.isEmpty()) return s;
            sortie.println("Ce champ est obligatoire.");
        }
    }

    private boolean oui(String question) {
        return lireLigne(question + " (o/n)").toLowerCase().startsWith("o");
    }
}