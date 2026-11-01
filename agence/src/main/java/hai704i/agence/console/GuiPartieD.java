package hai704i.agence.console;


import hai704i.agence.logique.AgenceService;
import hai704i.agence.logique.Types.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.concurrent.ExecutionException;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;

public class GuiPartieD {

    private final AgenceService agence;
    private final String idAgence;

    private JFrame fenetre;
    private JTextField ville;
    private JTextField arrivee;
    private JTextField depart;
    private JSpinner personnes;
    private JSpinner etoiles;
    private JTextField prixMax;
    private JButton boutonChercher;
    private JButton boutonReserver;
    private DefaultTableModel modele;
    private JTable table;
    private JTextArea incidents;
    private JLabel statut;
    private List<OffreAgence> offres = List.of();

    public GuiPartieD(AgenceService agence, String idAgence) {
        this.agence = agence;
        this.idAgence = idAgence;
    }

    /** Point d'entrée : la fenêtre se construit sur l'EDT */
    public void afficher() {
        SwingUtilities.invokeLater(this::construire);
    }

    private void construire() {
        fenetre = new JFrame("Agence " + idAgence);
        fenetre.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        fenetre.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosed(WindowEvent e) {
                //Arrêter proprement le pool de threads de la logique
                agence.close();
            }
        });

        ville = new JTextField("Montpellier", 10);
        arrivee = new JTextField("2026-11-10", 8);
        depart = new JTextField("2026-11-13", 8);
        personnes = new JSpinner(new SpinnerNumberModel(2, 1, 20, 1));
        etoiles = new JSpinner(new SpinnerNumberModel(0, 0, 5, 1));
        prixMax = new JTextField(6);
        boutonChercher = new JButton("Rechercher");

        boutonChercher.addActionListener(e -> rechercher());

        JPanel ligne1 = new JPanel(new FlowLayout(FlowLayout.LEFT));
        ligne1.add(new JLabel("Ville"));
        ligne1.add(ville);
        ligne1.add(new JLabel("Arrivée (AAAA-MM-JJ)"));
        ligne1.add(arrivee);
        ligne1.add(new JLabel("Départ"));
        ligne1.add(depart);

        JPanel ligne2 = new JPanel(new FlowLayout(FlowLayout.LEFT));
        ligne2.add(new JLabel("Personnes"));
        ligne2.add(personnes);
        ligne2.add(new JLabel("Étoiles min."));
        ligne2.add(etoiles);
        ligne2.add(new JLabel("Prix max. (€)"));
        ligne2.add(prixMax);
        ligne2.add(boutonChercher);

        JPanel formulaire = new JPanel(new GridLayout(2, 1));
        formulaire.add(ligne1);
        formulaire.add(ligne2);
        fenetre.add(formulaire, BorderLayout.NORTH);

        // Le tableau, les avertissements et le bouton réserver
        modele = new DefaultTableModel(
                new Object[] {"Hôtel", "Étoiles", "Chambre", "Pers.", "Nuits", "Prix total (€)", "Valable jusqu'à"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;           // l'utilisateur ne modifie pas les offres
            }
        };
        table = new JTable(modele);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table.setAutoCreateRowSorter(false);   // l'ordre = celui de la logique (prix croissant)

        incidents = new JTextArea(3, 40);
        incidents.setEditable(false);
        incidents.setBorder(BorderFactory.createTitledBorder("Avertissements"));

        boutonReserver = new JButton("Réserver l'offre sélectionnée");
        statut = new JLabel("Prêt.");

        boutonReserver.addActionListener(e -> reserver());

        JPanel bas = new JPanel(new BorderLayout());
        bas.add(new JScrollPane(incidents), BorderLayout.CENTER);
        JPanel actions = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        actions.add(statut);
        actions.add(boutonReserver);
        bas.add(actions, BorderLayout.SOUTH);

        fenetre.add(new JScrollPane(table), BorderLayout.CENTER);
        fenetre.add(bas, BorderLayout.SOUTH);

        fenetre.setSize(980, 460);
        // Centrer la fenêtre
        fenetre.setLocationRelativeTo(null);
        fenetre.setVisible(true);
    }

    // La recherche : lire le formulaire, appeler la logique sans geler la fenêtre
    private Sejour lireSejour() {
        try {
            LocalDate a = LocalDate.parse(arrivee.getText().trim());
            LocalDate d = LocalDate.parse(depart.getText().trim());
            if (ville.getText().isBlank()) {
                throw new IllegalArgumentException("La ville est obligatoire.");
            }
            if (!d.isAfter(a)) {
                throw new IllegalArgumentException("Le départ doit suivre l'arrivée.");
            }
            BigDecimal max = prixMax.getText().isBlank()
                    ? null
                    : new BigDecimal(prixMax.getText().trim().replace(',', '.'));
            if (max != null && max.signum() <= 0) {
                throw new IllegalArgumentException("Le prix maximum doit être positif.");
            }
            return new Sejour(ville.getText().trim(), a, d,
                    (Integer) personnes.getValue(), (Integer) etoiles.getValue(), max);
        } catch (DateTimeParseException e) {
            erreur("Date invalide : écrivez par exemple 2026-11-10.");
        } catch (IllegalArgumentException e) {      // inclut NumberFormatException (prix non numérique)
            erreur(e instanceof NumberFormatException ? "Prix maximum invalide." : e.getMessage());
        }
        return null;
    }

    private void erreur(String message) {
        JOptionPane.showMessageDialog(fenetre, message, "Attention", JOptionPane.WARNING_MESSAGE);
    }

    // Remplir le tableau
    private void remplir(ResultatRecherche r) {
        offres = r.offres();
        modele.setRowCount(0);                    // on vide le modèle
        DateTimeFormatter heure = DateTimeFormatter.ofPattern("HH:mm");
        for (OffreAgence o : offres) {
            modele.addRow(new Object[] {o.nomHotel(), o.etoiles(), o.chambre(), o.capacite(), o.nuits(),
                    o.prixTotal().toPlainString(), o.valideJusqua().format(heure)});
        }
        incidents.setText(String.join("\n", r.incidents()));
    }


    private void rechercher() {
        Sejour s = lireSejour();
        if (s == null) {
            return;                                  // saisie invalide : déjà signalée
        }
        boutonChercher.setEnabled(false);            // empêche un double clic pendant la recherche
        statut.setText("Interrogation des hôtels…");

        new SwingWorker<ResultatRecherche, Void>() {
            @Override
            protected ResultatRecherche doInBackground() {
                return agence.rechercher(s);         // hors de l'EDT : la fenêtre reste vivante
            }

            @Override
            protected void done() {                  // de retour sur l'EDT
                boutonChercher.setEnabled(true);
                try {
                    ResultatRecherche r = get();
                    remplir(r);
                    statut.setText(r.offres().size() + " offre(s).");
                } catch (InterruptedException | ExecutionException e) {
                    statut.setText("Recherche impossible.");
                    erreur("La recherche a échoué : " + e.getCause());
                }
            }
        }.execute();
    }

    // La réservation : dialogue, CVC masqué, réessai sans doublon
    private void reserver() {
        int ligne = table.getSelectedRow();
        if (ligne < 0) {
            erreur("Sélectionnez d'abord une offre dans la liste.");
            return;
        }
        OffreAgence offre = offres.get(ligne);

        JTextField nom = new JTextField();
        JTextField prenom = new JTextField();
        JTextField numero = new JTextField();
        JSpinner mois = new JSpinner(new SpinnerNumberModel(12, 1, 12, 1));
        JSpinner annee = new JSpinner(new SpinnerNumberModel(2030, 2000, 2100, 1));
        JPasswordField cvc = new JPasswordField();      // saisie masquée (des points)

        JPanel champs = new JPanel(new GridLayout(0, 2, 6, 6));
        champs.add(new JLabel("Nom"));                   champs.add(nom);
        champs.add(new JLabel("Prénom"));                champs.add(prenom);
        champs.add(new JLabel("Carte (NUMÉRO FICTIF)")); champs.add(numero);
        champs.add(new JLabel("Mois d'expiration"));     champs.add(mois);
        champs.add(new JLabel("Année d'expiration"));   champs.add(annee);
        champs.add(new JLabel("CVC"));                   champs.add(cvc);

        int reponse = JOptionPane.showConfirmDialog(fenetre, champs,
                "Réserver : " + offre.nomHotel() + ", chambre " + offre.chambre() + ", " + offre.prixTotal() + " €",
                JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
        if (reponse != JOptionPane.OK_OPTION) {
            return;                                       // l'utilisateur a annulé
        }
        if (nom.getText().isBlank() || prenom.getText().isBlank()
                || numero.getText().isBlank() || cvc.getPassword().length == 0) {
            erreur("Tous les champs sont obligatoires.");
            return;
        }

        // L'identifiant de demande est fixé ICI, une seule fois : un réessai réutilise CET objet.
        ReservationDemandee demande = agence.nouvelleDemande(offre,
                new Voyageur(nom.getText().trim(), prenom.getText().trim()),
                new Carte(numero.getText().trim(), (Integer) mois.getValue(), (Integer) annee.getValue(),
                        new String(cvc.getPassword())));
        cvc.setText("");                                  // on ne garde pas le CVC dans le composant
        lancerReservation(demande);
    }

    // L'envoi non bloquant
    /** Appelée une fois, puis à nouveau avec LA MÊME demande si l'utilisateur choisit de réessayer. */
    private void lancerReservation(ReservationDemandee demande) {
        boutonReserver.setEnabled(false);
        statut.setText("Réservation en cours…");

        new SwingWorker<ConfirmationAgence, Void>() {
            @Override
            protected ConfirmationAgence doInBackground() throws Exception {
                return agence.reserver(demande);          // peut lancer RefusHotel ou HotelInjoignable
            }

            @Override
            protected void done() {
                boutonReserver.setEnabled(true);
                try {
                    ConfirmationAgence c = get();
                    statut.setText("Réservation " + c.reference());
                    JOptionPane.showMessageDialog(fenetre,
                            (c.dejaEnregistree()
                                    ? "Cette demande avait déjà été enregistrée.\n\n"
                                    : "Réservation CONFIRMÉE.\n\n")
                                    + "Référence : " + c.reference()
                                    + "\nHôtel : " + c.nomHotel() + ", chambre " + c.chambre()
                                    + "\nSéjour : du " + c.arrivee() + " au " + c.depart()
                                    + "\nPrix : " + c.prixTotal().toPlainString() + " €"
                                    + "\nCarte : " + c.carteMasquee(),
                            "Réservation", JOptionPane.INFORMATION_MESSAGE);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                } catch (ExecutionException e) {
                    traiterEchec(e.getCause(), demande);   // la VRAIE cause : RefusHotel, HotelInjoignable...
                }
            }
        }.execute();
    }

    // Traiter Echec
    private void traiterEchec(Throwable cause, ReservationDemandee demande) {
        if (cause instanceof RefusHotel r) {
            // L'hôtel a RÉPONDU : refus définitif pour cette demande. On n'insiste pas.
            statut.setText("Réservation refusée.");
            JOptionPane.showMessageDialog(fenetre, r.getMessage(), "Réservation refusée",
                    JOptionPane.WARNING_MESSAGE);
        } else if (cause instanceof HotelInjoignable h) {
            // PAS de réponse : l'hôtel a peut-être traité. Rejouer est SÛR : même identifiant de demande.
            statut.setText("Hôtel injoignable.");
            int choix = JOptionPane.showConfirmDialog(fenetre,
                    h.getMessage() + "\n\nLa réservation a peut-être été enregistrée malgré tout.\n"
                            + "Réessayer avec la même demande (sans risque de doublon) ?",
                    "Hôtel injoignable", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
            if (choix == JOptionPane.YES_OPTION) {
                lancerReservation(demande);              // MÊME objet => même idDemande
            }
        } else {
            statut.setText("Erreur.");
            erreur("Erreur inattendue : " + cause);
        }
    }
}