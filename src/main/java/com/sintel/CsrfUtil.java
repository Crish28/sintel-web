package com.sintel;

import java.security.SecureRandom;
import java.util.Base64;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;

public class CsrfUtil {

    private static final String ATRIBUTO_TOKEN = "csrfToken";

    private static final SecureRandom RANDOM = new SecureRandom();

    private CsrfUtil() {
        // Evita crear instancias de esta clase.
    }

    public static String obtenerToken(HttpServletRequest request) {
        HttpSession sesion = request.getSession();

        String token = (String) sesion.getAttribute(ATRIBUTO_TOKEN);

        if (token == null) {
            byte[] bytes = new byte[32];
            RANDOM.nextBytes(bytes);

            token = Base64.getUrlEncoder()
                    .withoutPadding()
                    .encodeToString(bytes);

            sesion.setAttribute(ATRIBUTO_TOKEN, token);
        }

        return token;
    }

    public static boolean validarToken(
            HttpServletRequest request,
            String tokenRecibido) {

        HttpSession sesion = request.getSession(false);

        if (sesion == null || tokenRecibido == null) {
            return false;
        }

        String tokenGuardado =
                (String) sesion.getAttribute(ATRIBUTO_TOKEN);

        return tokenGuardado != null
                && java.security.MessageDigest.isEqual(
                        tokenGuardado.getBytes(
                                java.nio.charset.StandardCharsets.UTF_8),
                        tokenRecibido.getBytes(
                                java.nio.charset.StandardCharsets.UTF_8));
    }
}
