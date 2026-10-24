package hai704i.agence.acces;

// La seule classe qui importe les classes générées

import hai704i.agence.acces.client.*;
import hai704i.agence.logique.HotelGateway;
import hai704i.agence.logique.Types.*;
import jakarta.xml.ws.BindingProvider;
import jakarta.xml.ws.WebServiceException;
import java.net.ConnectException;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeConstants;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;

public class HotelSoapGateway implements HotelGateway {

    private final String adresse;
    private final String idAgence;
    private final String secret;     // utilisé à partir de A5
    private final int delaiMs;
    private HotelPort port;          // créé à la première utilisation

    public HotelSoapGateway(String adresse, String idAgence, String secret, int delaiMs) {
        this.adresse = adresse;
        this.idAgence = idAgence;
        this.secret = secret;
        this.delaiMs = delaiMs;
    }

    @Override
    public String nom() {
        return adresse;
    }

    @Override
    public synchronized List<OffreAgence> chercher(Sejour s) throws RefusHotel, HotelInjoignable {
        try {
            Demande d = new Demande();
            d.setAgence(idAgence);
            d.setVille(s.ville());
            d.setDateArrivee(xml(s.arrivee()));
            d.setDateDepart(xml(s.depart()));
            d.setPersonnes(s.personnes());
            d.setEtoilesMin(s.etoilesMin());
            d.setPrixMax(s.prixMax());   // null accepté : élément facultatif

            List<OffreAgence> resultat = new ArrayList<>();
            for (Offre o : port().chercherOffres(d)) {
                resultat.add(new OffreAgence(this, o.getIdOffre(), o.getNomHotel(), o.getEtoiles(),
                        o.getChambre(), o.getCapacite(), date(o.getDateArrivee()), date(o.getDateDepart()),
                        o.getNuits(), o.getPrixTotal(),
                        o.getValideJusqua().toGregorianCalendar().toZonedDateTime()
                                .withZoneSameInstant(ZoneId.systemDefault())));
            }
            return resultat;
        } catch (RefusMetierException e) {
            throw new RefusHotel(e.getFaultInfo().getCode().name(), e.getFaultInfo().getMessage());
        } catch (WebServiceException e) {
            throw new HotelInjoignable(adresse, causeLisible(e));
        }
    }

    private HotelPort port() {
        if (port == null) {
            URL wsdl = HotelSoapGateway.class.getResource("/wsdl/hotel.wsdl");   // ressource du projet
            HotelPort p = new HotelService(wsdl).getHotelSoapPort();             // fabrique le proxy
            Map<String, Object> contexte = ((BindingProvider) p).getRequestContext();
            contexte.put(BindingProvider.ENDPOINT_ADDRESS_PROPERTY, adresse);    // adresse issue de la config
            contexte.put("com.sun.xml.ws.connect.timeout", delaiMs);             // délais d'attente
            contexte.put("com.sun.xml.ws.request.timeout", delaiMs);
            contexte.put("jakarta.xml.ws.client.connectionTimeout", delaiMs);
            contexte.put("jakarta.xml.ws.client.receiveTimeout", delaiMs);
            port = p;
        }
        return port;
    }

    private static String causeLisible(Throwable e) {
        for (Throwable t = e; t != null; t = t.getCause()) {
            if (t instanceof ConnectException) {
                return "connexion refusée : Vérifiez que l'hôtel est lancé et que la bonne adresse est configurée)";
            }
            if (t instanceof SocketTimeoutException) {
                return "délai d'attente dépassé";
            }
        }
        return "service non joignable ou réponse inexploitable";
    }

    private static XMLGregorianCalendar xml(LocalDate d) {
        try {
            return DatatypeFactory.newInstance().newXMLGregorianCalendarDate(
                    d.getYear(), d.getMonthValue(), d.getDayOfMonth(), DatatypeConstants.FIELD_UNDEFINED);
        } catch (DatatypeConfigurationException e) {
            throw new IllegalStateException(e);
        }
    }

    private static LocalDate date(XMLGregorianCalendar c) {
        return LocalDate.of(c.getYear(), c.getMonth(), c.getDay());
    }
}