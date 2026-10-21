package com.sintel.api;

import com.sintel.dao.EstadisticasDAO;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.sql.SQLException;
import java.util.LinkedHashMap;
import java.util.Map;

@WebServlet("/api/estadisticas")
public class EstadisticasApiServlet extends HttpServlet {

    private EstadisticasDAO estadisticasDAO;

    @Override
    public void init() throws ServletException {
        this.estadisticasDAO = new EstadisticasDAO();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        try {
            int[] valores = estadisticasDAO.obtenerEstadisticas();

            Map<String, Object> stats = new LinkedHashMap<>();
            stats.put("totalProyectos", valores[0]);
            stats.put("pendientes", valores[1]);
            stats.put("enProceso", valores[2]);
            stats.put("finalizados", valores[3]);
            stats.put("atrasados", valores[4]);

            JsonUtil.responder(response, HttpServletResponse.SC_OK, stats);
        } catch (SQLException e) {
            JsonUtil.responderError(response, HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
                    "Error al obtener estadísticas del sistema: " + e.getMessage());
        }
    }
}
