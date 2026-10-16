
package com.sintel;

import com.sintel.dao.EstadisticasDAO;

import java.io.IOException;
import java.sql.SQLException;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@WebServlet("/dashboard")
public class DashboardServlet extends HttpServlet {

    @Override
    protected void doGet(
            HttpServletRequest request,
            HttpServletResponse response)
            throws ServletException, IOException {

        try {

            EstadisticasDAO estadisticasDAO =
                    new EstadisticasDAO();

            int[] estadisticas =
                    estadisticasDAO.obtenerEstadisticas();

            request.setAttribute(
                    "totalProyectos", estadisticas[0]);

            request.setAttribute(
                    "pendientes", estadisticas[1]);

            request.setAttribute(
                    "enProceso", estadisticas[2]);

            request.setAttribute(
                    "finalizados", estadisticas[3]);

            request.setAttribute(
                    "atrasados", estadisticas[4]);

            request.getRequestDispatcher("/index.jsp")
                    .forward(request, response);

        } catch (SQLException e) {

            throw new ServletException(
                    "Error al cargar las estadísticas del dashboard.",
                    e
            );
        }
    }
}
