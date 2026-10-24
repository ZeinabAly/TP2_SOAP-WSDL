package hai704i.agence.logique;

// Frontière entre la logique et Soap
import hai704i.agence.logique.Types.*;
import java.util.List;

public interface HotelGateway {
    String nom();
    List<OffreAgence> chercher(Sejour sejour) throws RefusHotel, HotelInjoignable;
}