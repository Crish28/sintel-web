package com.sintel;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Base64;

public final class TokenUtil {

    private static final SecureRandom RANDOM = new SecureRandom();

    private TokenUtil() {
    }

    /**
     * Genera un token aleatorio seguro para enviarlo por correo.
     */
    public static String generarToken() {
        byte[] bytes = new byte[32];
        RANDOM.nextBytes(bytes);

        return Base64.getUrlEncoder()
                .withoutPadding()
                .encodeToString(bytes);
    }

    /**
     * Calcula el hash SHA-256 del token.
     * En la base de datos se guarda este hash,
     * no el token original.
     */
    public static String calcularHash(String token) {
        try {
            MessageDigest digest =
                    MessageDigest.getInstance("SHA-256");

            byte[] hash = digest.digest(
                    token.getBytes(StandardCharsets.UTF_8)
            );

            StringBuilder hexadecimal = new StringBuilder();

            for (byte b : hash) {
                hexadecimal.append(
                        String.format("%02x", b & 0xff)
                );
            }

            return hexadecimal.toString();

        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException(
                    "No se pudo calcular el hash del token.", e
            );
        }
    }
}
