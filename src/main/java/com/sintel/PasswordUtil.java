package com.sintel;

import org.mindrot.jbcrypt.BCrypt;

public class PasswordUtil {

    public static String generarHash(String contraseña) {

        return BCrypt.hashpw(
                contraseña,
                BCrypt.gensalt()
        );
    }

    public static boolean verificar(
            String contraseña,
            String hash) {

        return BCrypt.checkpw(
                contraseña,
                hash
        );
    }
}