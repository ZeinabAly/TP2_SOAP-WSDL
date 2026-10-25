package hai704i.hotel.metier;

import java.security.GeneralSecurityException;
import java.security.MessageDigest;
import java.security.SecureRandom;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;

public final class SecretHasher {
    private static final int ITERATIONS = 65_536;
    private static final SecureRandom ALEA = new SecureRandom();

    private SecretHasher() { }

    public static byte[] nouveauSel() {
        byte[] sel = new byte[16];
        ALEA.nextBytes(sel);
        return sel;
    }

    public static byte[] empreinte(char[] secret, byte[] sel) {
        PBEKeySpec spec = new PBEKeySpec(secret, sel, ITERATIONS, 256);
        try {
            return SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256").generateSecret(spec).getEncoded();
        } catch (GeneralSecurityException e) {
            throw new IllegalStateException("PBKDF2 indisponible", e);
        } finally {
            spec.clearPassword();
        }
    }

    /** MessageDigest.isEqual compare en temps constant (un equals() s'arrête au premier octet différent). */
    public static boolean verifier(char[] secretPresente, byte[] sel, byte[] empreinteAttendue) {
        return MessageDigest.isEqual(empreinte(secretPresente, sel), empreinteAttendue);
    }
}