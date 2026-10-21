package com.sintel.api;

import com.sintel.dao.EstadoProyectoDAO;
import com.sintel.modelo.EstadoProyecto;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

@WebServlet("/api/estados")
public class EstadoApiServlet extends HttpServlet {

    private EstadoProyectoDAO estadoDAO;

    @Override
    public void init() throws ServletException {
        this.estadoDAO = new EstadoProyectoDAO();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        try {
            List<EstadoProyecto> estados = estadoDAO.listarTodos();
            JsonUtil.responder(response, HttpServletResponse.SC_OK, estados);
        } catch (SQLException e) {
            JsonUtil.responderError(response, HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
                    "Error al listar los estados de proyecto: " + e.getMessage());
        }
    }
}
