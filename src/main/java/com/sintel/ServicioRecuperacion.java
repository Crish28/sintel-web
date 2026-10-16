package com.sintel;

import com.sintel.dao.TokenRecuperacionDAO;
import com.sintel.dao.UsuarioDAO;
import com.sintel.modelo.Usuario;

import java.sql.SQLException;

public class ServicioRecuperacion {

    private final UsuarioDAO usuarioDAO = new UsuarioDAO();
    private final TokenRecuperacionDAO tokenDAO =
            new TokenRecuperacionDAO();

    /**
     * Solicita una recuperación de contraseña.
     *
     * La respuesta externa debe ser genérica, exista o no
     * una cuenta asociada al correo.
     */
    public void solicitarRecuperacion(
            String correo,
            String urlBase) throws SQLException {

        if (correo == null || correo.isBlank()) {
            return;
        }

        String correoNormalizado = correo.trim();

        Usuario usuario =
                usuarioDAO.buscarPorCorreo(correoNormalizado);

        // No se generan enlaces para cuentas inexistentes
        // o desactivadas.
        if (usuario == null || !usuario.isActivo()) {
            return;
        }

        String token = TokenUtil.generarToken();
        String tokenHash = TokenUtil.calcularHash(token);

        // Registra el token y anula los anteriores.
        tokenDAO.registrarNuevoToken(
                usuario.getIdusuario(),
                tokenHash
        );

        String enlace = urlBase
                + "/restablecer-contrasena?token=" + token;

        String asunto = "Recuperación de contraseña - Sintel";

        String contenido = """
                Hola %s:

                Hemos recibido una solicitud para restablecer
                la contraseña de tu cuenta de Sintel.

                Para crear una contraseña nueva, abre este enlace:

                %s

                El enlace caduca en 15 minutos y solo puede
                utilizarse una vez.

                Si no solicitaste este cambio, puedes ignorar
                este mensaje. Tu contraseña actual no cambiará.

                Saludos,
                Sistema Sintel
                """.formatted(usuario.getNombre(), enlace);

        try {
            CorreoUtil.enviar(
                    usuario.getCorreo(),
                    asunto,
                    contenido
            );
        } catch (Exception e) {
            // No ocultamos el fallo internamente.
            // El servlet deberá responder de forma genérica
            // al usuario y registrar el error sin incluir
            // tokens ni credenciales.
            throw new IllegalStateException(
                    "No se pudo enviar el correo de recuperación.", e
            );
        }
    }
}
