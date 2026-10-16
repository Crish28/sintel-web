package com.sintel;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

@WebServlet("/recuperar-contrasena")
public class RecuperarPasswordServlet extends HttpServlet {

    private static final Logger LOGGER =
            Logger.getLogger(RecuperarPasswordServlet.class.getName());

    private final ServicioRecuperacion servicio = new ServicioRecuperacion();

    @Override
    protected void doGet(
            HttpServletRequest request,
            HttpServletResponse response)
            throws ServletException, IOException {

        response.setHeader("Cache-Control", "no-store, no-cache, must-revalidate");
        response.setHeader("Pragma", "no-cache");

        request.setAttribute("csrfToken", CsrfUtil.obtenerToken(request));

        request.getRequestDispatcher("/recuperar-contrasena.jsp")
                .forward(request, response);
    }

    @Override
    protected void doPost(
            HttpServletRequest request,
            HttpServletResponse response)
            throws ServletException, IOException {

        request.setCharacterEncoding("UTF-8");
        response.setHeader("Cache-Control", "no-store");

        String tokenCSRF = request.getParameter("csrfToken");

        if (!CsrfUtil.validarToken(request, tokenCSRF)) {
            response.sendError(HttpServletResponse.SC_FORBIDDEN,
                    "La solicitud no es válida. Recarga la página e inténtalo nuevamente.");
            return;
        }

        String correo = request.getParameter("correo");

        try {
            String urlBase = System.getenv("SINTEL_BASE_URL");

            if (urlBase == null || urlBase.isBlank()) {
                throw new IllegalStateException(
                        "No está configurada la variable SINTEL_BASE_URL.");
            }

            servicio.solicitarRecuperacion(correo, urlBase.trim());

            request.setAttribute("mensaje",
                    "Si el correo corresponde a una cuenta activa, recibirás "
                    + "un enlace para restablecer tu contraseña. "
                    + "Revisa también la carpeta de correo no deseado.");

        } catch (Exception e) {
            LOGGER.log(Level.SEVERE,
                    "Error al procesar una solicitud de recuperación.", e);

            request.setAttribute("error",
                    "No fue posible procesar la solicitud en este momento. "
                    + "Inténtalo nuevamente más tarde.");
        }

        request.setAttribute("csrfToken", CsrfUtil.obtenerToken(request));

        request.getRequestDispatcher("/recuperar-contrasena.jsp")
                .forward(request, response);
    }
}
