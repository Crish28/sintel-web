package com.sintel;

import com.sintel.dao.ConfiguracionDAO;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.math.BigDecimal;
import java.sql.SQLException;

@WebServlet("/configuracion")
public class ConfiguracionServlet extends HttpServlet {

    private final ConfiguracionDAO configuracionDAO =
            new ConfiguracionDAO();

    private boolean esAdministrador(HttpServletRequest request) {
        HttpSession sesion = request.getSession(false);

        return sesion != null
                && "Administrador".equals(
                        sesion.getAttribute("rolUsuario"));
    }

    @Override
    protected void doGet(HttpServletRequest request,
                         HttpServletResponse response)
            throws ServletException, IOException {

        if (!esAdministrador(request)) {
            response.sendError(
                    HttpServletResponse.SC_FORBIDDEN,
                    "Solo un administrador puede acceder a esta configuración."
            );
            return;
        }

        try {
            BigDecimal tasaIVA = BigDecimal.valueOf(
                    configuracionDAO.obtenerTasaIVA()
            );

            request.setAttribute("tasaIVA", tasaIVA);
            request.getRequestDispatcher("/configuracion.jsp")
                    .forward(request, response);

        } catch (SQLException e) {
            throw new ServletException(
                    "Error al consultar la configuración financiera.", e
            );
        }
    }

    @Override
    protected void doPost(HttpServletRequest request,
                          HttpServletResponse response)
            throws ServletException, IOException {

        request.setCharacterEncoding("UTF-8");

        if (!esAdministrador(request)) {
            response.sendError(
                    HttpServletResponse.SC_FORBIDDEN,
                    "Solo un administrador puede modificar esta configuración."
            );
            return;
        }

        try {
            String valor = request.getParameter("tasaIVA");

            if (valor == null || valor.isBlank()) {
                response.sendRedirect(
                        request.getContextPath()
                                + "/configuracion?error=valor"
                );
                return;
            }

            BigDecimal nuevaTasa = new BigDecimal(
                    valor.trim().replace(",", ".")
            );

            if (nuevaTasa.compareTo(BigDecimal.ZERO) < 0
                    || nuevaTasa.compareTo(new BigDecimal("100")) > 0) {
                response.sendRedirect(
                        request.getContextPath()
                                + "/configuracion?error=rango"
                );
                return;
            }

            configuracionDAO.actualizarTasaIVA(nuevaTasa);

            response.sendRedirect(
                    request.getContextPath()
                            + "/configuracion?actualizado=true"
            );

        } catch (NumberFormatException e) {
            response.sendRedirect(
                    request.getContextPath()
                            + "/configuracion?error=valor"
            );

        } catch (SQLException e) {
            throw new ServletException(
                    "Error al actualizar la tasa del IVA.", e
            );
        }
    }
}
