package com.sintel;

import java.util.Properties;

import jakarta.mail.Authenticator;
import jakarta.mail.Message;
import jakarta.mail.PasswordAuthentication;
import jakarta.mail.Session;
import jakarta.mail.Transport;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;

public final class CorreoUtil {

    private CorreoUtil() {
    }

    public static void enviar(
            String destinatario,
            String asunto,
            String contenido) throws Exception {

        String correo = System.getenv("SINTEL_MAIL_USER");
        String clave = System.getenv("SINTEL_MAIL_PASSWORD");

        if (correo == null || correo.isBlank()
                || clave == null || clave.isBlank()) {
            throw new IllegalStateException(
                "Faltan las variables de entorno del correo de Sintel."
            );
        }

        Properties propiedades = new Properties();
        propiedades.put("mail.smtp.host", "smtp.gmail.com");
        propiedades.put("mail.smtp.port", "587");
        propiedades.put("mail.smtp.auth", "true");
        propiedades.put("mail.smtp.starttls.enable", "true");
        propiedades.put("mail.smtp.starttls.required", "true");
        propiedades.put("mail.smtp.connectiontimeout", "10000");
        propiedades.put("mail.smtp.timeout", "10000");
        propiedades.put("mail.smtp.writetimeout", "10000");

        Session sesion = Session.getInstance(
            propiedades,
            new Authenticator() {
                @Override
                protected PasswordAuthentication
                        getPasswordAuthentication() {
                    return new PasswordAuthentication(correo, clave);
                }
            }
        );

        MimeMessage mensaje = new MimeMessage(sesion);

        mensaje.setFrom(new InternetAddress(correo));
        mensaje.setRecipient(
            Message.RecipientType.TO,
            new InternetAddress(destinatario)
        );
        mensaje.setSubject(asunto, "UTF-8");
        mensaje.setText(contenido, "UTF-8");

        Transport.send(mensaje);
    }
}
