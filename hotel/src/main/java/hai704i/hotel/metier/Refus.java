package hai704i.hotel.metier;

public class Refus extends Exception {
    private final Motif motif;

    public Refus(Motif motif, String message) {
        super(message);
        this.motif = motif;
    }

    public Motif motif() {
        return motif;
    }
}