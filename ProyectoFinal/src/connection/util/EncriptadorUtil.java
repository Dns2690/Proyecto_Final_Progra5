package connection.util;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Clase de utilidades para encriptación y hashing.
 */
public final class EncriptadorUtil {

    private EncriptadorUtil() {
    }

    /**
     * Retorna el hash MD5 de un texto dado.
     * @param texto texto a hashear
     * @return hash MD5 como cadena hexadecimal de 32 caracteres en minúscula
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
            throw new IllegalStateException("MD5 no disponible en esta JVM", e);
        }
    }
}
