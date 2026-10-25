package hai704i.hotel.acces;


import hai704i.hotel.Demande;
import hai704i.hotel.Offre;

import hai704i.hotel.metier.Modeles.*;
import hai704i.hotel.metier.Motif;
import hai704i.hotel.metier.Refus;

import java.time.DateTimeException;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.util.GregorianCalendar;
import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeConstants;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;

public final class Conversions {

    private static final DatatypeFactory FABRIQUE;

    static {
        try {
            FABRIQUE = DatatypeFactory.newInstance();
        } catch (DatatypeConfigurationException e) {
            throw new ExceptionInInitializerError(e);
        }
    }

    static RefusMetierException versFaute(Refus r) {
        CodeRefus code = CodeRefus.valueOf(r.motif().name());
        return new RefusMetierException(r.getMessage(), new RefusMetier(code, r.getMessage()));
    }

    private Conversions() { }

    public static Critere versCritere(Demande d) throws Refus {
        return new Critere(d.getAgence(), d.getVille(), date(d.getDateArrivee()), date(d.getDateDepart()),
                d.getPersonnes(), d.getEtoilesMin(), d.getPrixMax());
    }

    public static Offre versOffre(Devis d, InfoHotel hotel) {
        Offre o = new Offre();
        o.setIdOffre(d.id());
        o.setNomHotel(hotel.nom());
        o.setVille(hotel.ville());
        o.setEtoiles(hotel.etoiles());
        o.setChambre(d.numeroChambre());
        o.setCapacite(d.capacite());
        o.setDateArrivee(xml(d.arrivee()));
        o.setDateDepart(xml(d.depart()));
        o.setNuits((int) d.nuits());
        o.setPrixTotal(d.prixTotal());
        o.setValideJusqua(xml(d.expireA()));
        return o;
    }

    private static LocalDate date(XMLGregorianCalendar c) throws Refus {
        if (c == null) {
            throw new Refus(Motif.DEMANDE_INVALIDE, "Date manquante");
        }
        try {
            return LocalDate.of(c.getYear(), c.getMonth(), c.getDay());
        } catch (DateTimeException e) {
            throw new Refus(Motif.DEMANDE_INVALIDE, "Date invalide " );
        }
    }

    private static XMLGregorianCalendar xml(LocalDate d) {
        return FABRIQUE.newXMLGregorianCalendarDate(d.getYear(), d.getMonthValue(), d.getDayOfMonth(),
                DatatypeConstants.FIELD_UNDEFINED);
    }

    private static XMLGregorianCalendar xml(Instant i) {
        return FABRIQUE.newXMLGregorianCalendar(GregorianCalendar.from(i.atZone(ZoneOffset.UTC)));
    }

    static CommandeReservation versCommande(DemandeReservation r) throws Refus {
        if (r == null || r.getAuthentification() == null || r.getVoyageur() == null) {
            throw new Refus(Motif.DEMANDE_INVALIDE, "Demande de réservation incomplète.");
        }
        Paiement p = r.getPaiement();
        DonneesPaiement paiement = p == null ? null
                : new DonneesPaiement(p.getNumeroCarte(), p.getMoisExpiration(), p.getAnneeExpiration(), p.getCvc());
        return new CommandeReservation(r.getAuthentification().getIdAgence(), r.getAuthentification().getSecret(),
                r.getIdDemande(), r.getIdOffre(), r.getVoyageur().getNom(), r.getVoyageur().getPrenom(), paiement);
    }

    static Confirmation versConfirmation(ConfirmationMetier c, InfoHotel hotel) {
        Reservation r = c.reservation();
        Confirmation x = new Confirmation();
        x.setReference(r.reference());
        x.setIdDemande(r.idDemande());
        x.setIdOffre(r.idOffre());
        x.setNomHotel(hotel.nom());
        x.setChambre(r.numeroChambre());
        x.setDateArrivee(xml(r.arrivee()));
        x.setDateDepart(xml(r.depart()));
        x.setPrixTotal(r.prixTotal());
        x.setCarteMasquee(r.carteMasquee());
        x.setDejaEnregistree(c.rejeu());
        return x;
    }
}