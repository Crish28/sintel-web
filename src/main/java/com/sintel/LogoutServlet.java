package com.sintel;

import java.io.IOException;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

@WebServlet("/logout")
public class LogoutServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request,
                         HttpServletResponse response)
            throws ServletException, IOException {

        // El cierre de sesión solamente se permite mediante POST.
        response.setHeader("Allow", "POST");
        response.setHeader("Cache-Control", "no-store");

        response.sendError(
                HttpServletResponse.SC_METHOD_NOT_ALLOWED,
                "Utiliza el formulario para cerrar sesión."
        );
    }

    @Override
    protected void doPost(HttpServletRequest request,
                          HttpServletResponse response)
            throws ServletException, IOException {

        // Evitar que el navegador almacene la respuesta.
        response.setHeader(
                "Cache-Control",
                "no-store, no-cache, must-revalidate"
        );
        response.setHeader("Pragma", "no-cache");
        response.setDateHeader("Expires", 0);

        // Obtener la sesión existente sin crear otra.
        HttpSession sesion = request.getSession(false);

        // Si no hay sesión, no hay nada que cerrar.
        if (sesion == null) {
            response.sendRedirect(
                    request.getContextPath() + "/login"
            );
            return;
        }

        // Validar el token CSRF antes de cerrar la sesión.
        String tokenRecibido =
                request.getParameter("csrfToken");

        if (!CsrfUtil.validarToken(request, tokenRecibido)) {
            response.sendError(
                    HttpServletResponse.SC_FORBIDDEN,
                    "La solicitud no es válida. Recarga la página e inténtalo nuevamente."
            );
            return;
        }

        // Invalidar la sesión después de validar el token.
        sesion.invalidate();

        // Regresar al inicio de sesión.
        response.sendRedirect(
                request.getContextPath() + "/login"
        );
    }
}

