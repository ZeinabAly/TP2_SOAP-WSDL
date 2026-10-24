package hai704i.hotel;


import jakarta.xml.ws.Endpoint;


import hai704i.hotel.acces.HotelPortImpl;
import hai704i.hotel.depot.DepotsMemoire.*;
import hai704i.hotel.metier.HotelService;
import hai704i.hotel.metier.Modeles.*;

import java.io.IOException;
import java.io.Reader;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Clock;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

public class Main {

    public static void main(String[] args) throws IOException {
        if (args.length != 1) {
            System.err.println("Usage : Main <fichier.properties>");
            System.exit(2);
        }
        Properties p = new Properties();
        try (Reader r = Files.newBufferedReader(Path.of(args[0]), StandardCharsets.UTF_8)) {
            p.load(r);
        }

        InfoHotel info = new InfoHotel(p.getProperty("hotel.nom"), p.getProperty("hotel.ville"),
                Integer.parseInt(p.getProperty("hotel.etoiles")));
        int port = Integer.parseInt(p.getProperty("hotel.port"));

        List<Chambre> chambres = new ArrayList<>();
        AgencesMemoire agences = new AgencesMemoire();
        for (String cle : p.stringPropertyNames()) {
            if (cle.startsWith("chambre.")) {
                String[] t = p.getProperty(cle).split(";");
                chambres.add(new Chambre(t[0].trim(), Integer.parseInt(t[1].trim()), new BigDecimal(t[2].trim())));
            } else if (cle.startsWith("agence.") && cle.endsWith(".coefficient")) {
                String id = cle.substring("agence.".length(), cle.length() - ".coefficient".length());
                agences.ajouter(new Agence(id, new BigDecimal(p.getProperty(cle).trim())));
            }
        }

        HotelService service = new HotelService(info, new ChambresMemoire(chambres), agences,
                new DevisMemoire(), new ReservationsMemoire(), Clock.systemDefaultZone());

        Endpoint.publish("http://localhost:" + port + "/hotel", new HotelPortImpl(service));
        System.out.println(info.nom() + " publié : http://localhost:" + port + "/hotel?wsdl");
    }
}