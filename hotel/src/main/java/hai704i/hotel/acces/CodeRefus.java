package hai704i.hotel.acces;

import jakarta.xml.bind.annotation.XmlEnum;

@XmlEnum
public enum CodeRefus {
    AGENCE_INCONNUE,
    DEMANDE_INVALIDE,
    SECRET_INVALIDE,
    PAIEMENT_INVALIDE,
    OFFRE_INCONNUE,
    OFFRE_EXPIREE,
    OFFRE_INDISPONIBLE,
    DEMANDE_INCOHERENTE
}