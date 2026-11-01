package hai704i.agence;

import hai704i.agence.console.GuiPartieD;
import hai704i.agence.logique.AgenceService;
import java.util.List;

public class VerificationGuiPartieD {
    public static void main(String[] args) {
        // aucune passerelle : la fenêtre s'ouvre, la recherche renverra simplement 0 offre
        new GuiPartieD(new AgenceService(List.of(), 1000), "AG-TEST").afficher();
    }
}