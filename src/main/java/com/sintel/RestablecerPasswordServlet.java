package com.sintel;

import com.sintel.dao.TokenRecuperacionDAO;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

@WebServlet("/restablecer-contrasena")
public class RestablecerPasswordServlet extends HttpServlet {

    private static final Logger LOGGER =
            Logger.getLogger(RestablecerPasswordServlet.class.getName());

    private final TokenRecuperacionDAO tokenDAO =
            new TokenRecuperacionDAO();

    @Override
    protected void doGet(HttpServletRequest request,
                         HttpServletResponse response)
            throws ServletException, IOException {

        response.setHeader("Cache-Control", "no-store, no-cache, must-revalidate");
        response.setHeader("Pragma", "no-cache");

        String token = request.getParameter("token");

        if (token == null || !token.matches("[A-Za-z0-9_-]{43}")) {
            mostrarError(request, response,
                    "El enlace no es válido o ha expirado.");
            return;
        }

        try {
            String tokenHash = TokenUtil.calcularHash(token);

            if (!tokenDAO.tokenValido(tokenHash)) {
                mostrarError(request, response,
                        "El enlace no es válido, ya fue utilizado o ha expirado.");
                return;
            }

            request.setAttribute("token", token);
            request.setAttribute("csrfToken", CsrfUtil.obtenerToken(request));

            request.getRequestDispatcher("/restablecer-contrasena.jsp")
                    .forward(request, response);

        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE,
                    "Error al validar el token de recuperación.", e);

            response.sendError(
                    HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
                    "No fue posible verificar el enlace.");
        }
    }

    @Override
    protected void doPost(HttpServletRequest request,
                          HttpServletResponse response)
            throws ServletException, IOException {

        request.setCharacterEncoding("UTF-8");
        response.setHeader("Cache-Control", "no-store");

        if (!CsrfUtil.validarToken(
                request, request.getParameter("csrfToken"))) {

            response.sendError(
                    HttpServletResponse.SC_FORBIDDEN,
                    "La solicitud no es válida. Recarga la página.");
            return;
        }

        String token = request.getParameter("token");
        String password = request.getParameter("password");
        String confirmarPassword = request.getParameter("confirmarPassword");

        if (token == null || !token.matches("[A-Za-z0-9_-]{43}")) {
            mostrarError(request, response,
                    "El enlace no es válido o ha expirado.");
            return;
        }

        if (password == null || confirmarPassword == null
                || password.length() < 12
                || password.length() > 72) {

            mostrarFormularioConError(request, response, token,
                    "La contraseña debe tener entre 12 y 72 caracteres.");
            return;
        }

        if (!password.equals(confirmarPassword)) {
            mostrarFormularioConError(request, response, token,
                    "Las contraseñas no coinciden.");
            return;
        }

        try {
            String tokenHash = TokenUtil.calcularHash(token);
            String passwordHash = PasswordUtil.generarHash(password);

            boolean actualizado =
                    tokenDAO.restablecerPassword(tokenHash, passwordHash);

            if (!actualizado) {
                mostrarError(request, response,
                        "El enlace no es válido, ya fue utilizado o ha expirado.");
                return;
            }

            request.setAttribute("mensaje",
                    "Tu contraseña se actualizó correctamente. "
                    + "Ya puedes iniciar sesión.");

            request.getRequestDispatcher("/login.jsp")
                    .forward(request, response);

        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE,
                    "Error al restablecer la contraseña.", e);

            response.sendError(
                    HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
                    "No fue posible actualizar la contraseña. Inténtalo más tarde.");
        }
    }

    private void mostrarError(HttpServletRequest request,
                              HttpServletResponse response,
                              String mensaje)
            throws ServletException, IOException {

        request.setAttribute("error", mensaje);
        request.getRequestDispatcher("/restablecer-contrasena.jsp")
                .forward(request, response);
    }

    private void mostrarFormularioConError(
            HttpServletRequest request,
            HttpServletResponse response,
            String token,
            String mensaje)
            throws ServletException, IOException {

        request.setAttribute("token", token);
        request.setAttribute("error", mensaje);
        request.setAttribute("csrfToken", CsrfUtil.obtenerToken(request));

        request.getRequestDispatcher("/restablecer-contrasena.jsp")
                .forward(request, response);
    }
}
