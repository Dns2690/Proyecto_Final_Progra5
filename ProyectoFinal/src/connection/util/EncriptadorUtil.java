package connection.util;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Utility class for hashing passwords with MD5 before they are sent to the
 * database. Per project rules, plaintext passwords are never stored or
 * compared: every insert/login hashes the password in Java first.
 */
public final class EncriptadorUtil {

    private EncriptadorUtil() {
    }

    /**
     * Returns the MD5 hash of the given text as a 32-character lowercase
     * hexadecimal string.
     *
     * @param texto plaintext to hash (e.g. a password)
     * @return MD5 hex digest, or {@code null} if {@code texto} is null
     */
    public static String md5(String texto) {
        if (texto == null) {
            return null;
        }
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            byte[] hash = md.digest(texto.getBytes(StandardCharsets.UTF_8));
            StringBuilder sb = new StringBuilder(32);
            for (byte b : hash) {
                sb.append(String.format("%02x", b));
            }
            return sb.toString();
        } catch (NoSuchAlgorithmException e) {
            // MD5 is guaranteed to exist on every JVM
            throw new IllegalStateException("MD5 no disponible en esta JVM", e);
        }
    }
}
