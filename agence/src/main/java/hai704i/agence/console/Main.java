package hai704i.agence.console;

import hai704i.agence.acces.HotelSoapGateway;
import hai704i.agence.logique.AgenceService;
import hai704i.agence.logique.HotelGateway;
import java.io.IOException;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

public final class Main {

    private Main() { }

    public static void main(String[] args) throws IOException {
        // Ajout de 2e argument pour l'interface graphique
        if (args.length < 1 || args.length > 2) {
            System.err.println("Usage : Main <fichier-de-configuration.properties> [--gui]");
            System.exit(2);
        }
        Properties p = new Properties();
        try (Reader r = Files.newBufferedReader(Path.of(args[0]), StandardCharsets.UTF_8)) {
            p.load(r);
        }

        String idAgence = exiger(p, "agence.id");
        String secret = System.getenv("AGENCE_SECRET");
        if (secret == null || secret.isBlank()) {
            System.err.println("Secret introuvable : définissez la variable d'environnement AGENCE_SECRET.");
            System.exit(2);
        }
        int delaiMs = Integer.parseInt(p.getProperty("delai.ms", "3000").trim());

        List<HotelGateway> hotels = new ArrayList<>();
        for (int i = 1; p.getProperty("hotel." + i) != null; i++) {
            hotels.add(new HotelSoapGateway(p.getProperty("hotel." + i).trim(), idAgence, secret, delaiMs));
        }
        if (hotels.isEmpty()) {
            System.err.println("Aucun hôtel configuré (clés hotel.1, hotel.2, ...).");
            System.exit(2);
        }

        // Lancer l'interface
        if (args.length == 2 && args[1].equals("--gui")) {
            // Même logique d'agence, autre interface. La fenêtre ferme le service à sa fermeture.
            new GuiPartieD(new AgenceService(hotels, delaiMs), idAgence).afficher();
            return;
        }

        try (AgenceService agence = new AgenceService(hotels, delaiMs)) {
            new Cli(agence, idAgence).lancer();
        }
    }

    private static String exiger(Properties p, String cle) {
        String v = p.getProperty(cle);
        if (v == null || v.isBlank()) {
            System.err.println("Paramètre manquant : " + cle);
            System.exit(2);
        }
        return v.trim();
    }
}