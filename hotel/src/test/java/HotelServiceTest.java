//package hai704i.hotel.metier;
//
//
//import hai704i.hotel.depot.DepotsMemoire.*;
//import hai704i.hotel.metier.Modeles.*;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//
//import java.math.BigDecimal;
//import java.time.*;
//import java.util.List;
//
//import static org.junit.jupiter.api.Assertions.*;
//
//class HotelServiceTest {
//
//    private static final Clock HORLOGE =
//            Clock.fixed(Instant.parse("2026-11-01T10:00:00Z"), ZoneOffset.UTC);
//    private HotelService service;
//
//    @BeforeEach
//    void init() {
//        AgencesMemoire agences = new AgencesMemoire();
//        agences.ajouter(new Agence("AG-01", new BigDecimal("0.90")));
//        agences.ajouter(new Agence("AG-02", new BigDecimal("1.10")));
//        service = new HotelService(new InfoHotel("Soleil", "Montpellier", 4),
//                new ChambresMemoire(List.of(
//                        new Chambre("101", 2, new BigDecimal("80.00")),
//                        new Chambre("201", 4, new BigDecimal("140.00")))),
//                agences, new DevisMemoire(), new ReservationsMemoire(), HORLOGE);
//    }
//
//    private Critere critere(String agence, String ville, LocalDate arrivee, LocalDate depart,
//                            int personnes, BigDecimal prixMax) {
//        return new Critere(agence, ville, arrivee, depart, personnes, 0, prixMax);
//    }
//
//    private static final LocalDate A = LocalDate.of(2026, 11, 10);
//    private static final LocalDate D = LocalDate.of(2026, 11, 13);
//
//    @Test
//    void prixEgalBaseXNuitsXCoefficient() throws Refus {
//        List<Devis> r = service.rechercher(critere("AG-01", "Montpellier", A, D, 2, null));
//        assertEquals(2, r.size());
//        assertEquals(new BigDecimal("216.00"), r.get(0).prixTotal());   // 80 x 3 x 0.90
//    }
//
//    @Test
//    void deuxAgencesNeVoientPasLeMemePrix() throws Refus {
//        BigDecimal p1 = service.rechercher(critere("AG-01", "Montpellier", A, D, 2, null)).get(0).prixTotal();
//        BigDecimal p2 = service.rechercher(critere("AG-02", "Montpellier", A, D, 2, null)).get(0).prixTotal();
//        assertNotEquals(p1, p2);
//    }
//
//    @Test
//    void agenceInconnueEstRefusee() {
//        Refus r = assertThrows(Refus.class,
//                () -> service.rechercher(critere("???", "Montpellier", A, D, 2, null)));
//        assertEquals(Motif.AGENCE_INCONNUE, r.motif());
//    }
//
//    @Test
//    void departAvantArriveeEstInvalide() {
//        Refus r = assertThrows(Refus.class,
//                () -> service.rechercher(critere("AG-01", "Montpellier", D, A, 2, null)));
//        assertEquals(Motif.DEMANDE_INVALIDE, r.motif());
//    }
//
//    @Test
//    void autreVilleDonneUneListeVideSansRefus() throws Refus {
//        assertTrue(service.rechercher(critere("AG-01", "Paris", A, D, 2, null)).isEmpty());
//    }
//
//    @Test
//    void capaciteEtPrixMaxFiltrent() throws Refus {
//        assertEquals(1, service.rechercher(critere("AG-01", "Montpellier", A, D, 3, null)).size()); // 201 seule
//        assertEquals(1, service.rechercher(critere("AG-01", "Montpellier", A, D, 2,
//                new BigDecimal("300"))).size());                                                     // 101 seule
//    }
//}