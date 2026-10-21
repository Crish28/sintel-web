package com.sintel.api;

import com.sintel.dao.ClienteDAO;
import com.sintel.dao.ConfiguracionDAO;
import com.sintel.dao.ProyectoDAO;
import com.sintel.modelo.Cliente;
import com.sintel.modelo.Proyecto;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.SQLException;
import java.util.List;

@WebServlet(urlPatterns = {"/api/proyectos", "/api/proyectos/*"})
public class ProyectoApiServlet extends HttpServlet {

    private ProyectoDAO proyectoDAO;
    private ClienteDAO clienteDAO;
    private ConfiguracionDAO configuracionDAO;

    @Override
    public void init() throws ServletException {
        this.proyectoDAO = new ProyectoDAO();
        this.clienteDAO = new ClienteDAO();
        this.configuracionDAO = new ConfiguracionDAO();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        try {
            Integer id = JsonUtil.extraerId(request);

            if (id != null) {
                Proyecto proyecto = proyectoDAO.buscarPorId(id);
                if (proyecto == null) {
                    JsonUtil.responderError(response, HttpServletResponse.SC_NOT_FOUND,
                            "Proyecto con ID " + id + " no encontrado.");
                    return;
                }
                JsonUtil.responder(response, HttpServletResponse.SC_OK, proyecto);
            } else {
                String idEstadoParam = request.getParameter("idestado");
                List<Proyecto> proyectos;

                if (idEstadoParam != null && !idEstadoParam.isBlank()) {
                    int idEstado = Integer.parseInt(idEstadoParam.trim());
                    proyectos = proyectoDAO.listarPorEstado(idEstado);
                } else {
                    proyectos = proyectoDAO.listarTodos();
                }

                JsonUtil.responder(response, HttpServletResponse.SC_OK, proyectos);
            }
        } catch (NumberFormatException e) {
            JsonUtil.responderError(response, HttpServletResponse.SC_BAD_REQUEST,
                    "El parámetro de estado no es un número válido.");
        } catch (SQLException e) {
            JsonUtil.responderError(response, HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
                    "Error al consultar proyectos: " + e.getMessage());
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        try {
            Proyecto nuevo = JsonUtil.leerCuerpo(request, Proyecto.class);

            if (nuevo == null) {
                JsonUtil.responderError(response, HttpServletResponse.SC_BAD_REQUEST,
                        "El cuerpo de la solicitud no contiene un proyecto válido.");
                return;
            }

            // Validaciones
            String errorValidacion = validarProyecto(nuevo);
            if (errorValidacion != null) {
                JsonUtil.responderError(response, HttpServletResponse.SC_BAD_REQUEST, errorValidacion);
                return;
            }

            // Validar que el cliente exista
            Cliente cliente = clienteDAO.buscarPorId(nuevo.getIdclientes());
            if (cliente == null) {
                JsonUtil.responderError(response, HttpServletResponse.SC_BAD_REQUEST,
                        "El cliente con ID " + nuevo.getIdclientes() + " no existe.");
                return;
            }

            // Si el IVA no fue proporcionado, calcularlo automáticamente según la tasa de configuración
            if (nuevo.getIva() == null || nuevo.getIva().compareTo(BigDecimal.ZERO) <= 0) {
                double tasa = configuracionDAO.obtenerTasaIVA();
                BigDecimal tasaDecimal = BigDecimal.valueOf(tasa);
                BigDecimal ivaCalculado = nuevo.getValorTotal().multiply(tasaDecimal)
                        .setScale(2, RoundingMode.HALF_UP);
                nuevo.setIva(ivaCalculado);
            }

            int idGenerado = proyectoDAO.registrarRetornandoId(nuevo);
            if (idGenerado > 0) {
                Proyecto creado = proyectoDAO.buscarPorId(idGenerado);
                JsonUtil.responderExito(response, HttpServletResponse.SC_CREATED,
                        "Proyecto registrado exitosamente.", creado);
            } else {
                JsonUtil.responderError(response, HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
                        "No se pudo registrar el proyecto.");
            }
        } catch (SQLException e) {
            JsonUtil.responderError(response, HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
                    "Error de base de datos al registrar proyecto: " + e.getMessage());
        }
    }

    @Override
    protected void doPut(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        try {
            Proyecto proyecto = JsonUtil.leerCuerpo(request, Proyecto.class);
            Integer idRuta = JsonUtil.extraerId(request);

            if (proyecto == null) {
                JsonUtil.responderError(response, HttpServletResponse.SC_BAD_REQUEST,
                        "El cuerpo de la solicitud no contiene un proyecto válido.");
                return;
            }

            if (idRuta != null) {
                proyecto.setIdproyectos(idRuta);
            }

            if (proyecto.getIdproyectos() <= 0) {
                JsonUtil.responderError(response, HttpServletResponse.SC_BAD_REQUEST,
                        "Debe especificar un ID de proyecto válido.");
                return;
            }

            Proyecto existente = proyectoDAO.buscarPorId(proyecto.getIdproyectos());
            if (existente == null) {
                JsonUtil.responderError(response, HttpServletResponse.SC_NOT_FOUND,
                        "El proyecto con ID " + proyecto.getIdproyectos() + " no existe.");
                return;
            }

            String errorValidacion = validarProyecto(proyecto);
            if (errorValidacion != null) {
                JsonUtil.responderError(response, HttpServletResponse.SC_BAD_REQUEST, errorValidacion);
                return;
            }

            // Verificar cliente
            Cliente cliente = clienteDAO.buscarPorId(proyecto.getIdclientes());
            if (cliente == null) {
                JsonUtil.responderError(response, HttpServletResponse.SC_BAD_REQUEST,
                        "El cliente con ID " + proyecto.getIdclientes() + " no existe.");
                return;
            }

            // Si el IVA es nulo, calcularlo
            if (proyecto.getIva() == null) {
                double tasa = configuracionDAO.obtenerTasaIVA();
                BigDecimal tasaDecimal = BigDecimal.valueOf(tasa);
                proyecto.setIva(proyecto.getValorTotal().multiply(tasaDecimal)
                        .setScale(2, RoundingMode.HALF_UP));
            }

            boolean actualizado = proyectoDAO.actualizar(proyecto);
            if (actualizado) {
                Proyecto actualizadoObj = proyectoDAO.buscarPorId(proyecto.getIdproyectos());
                JsonUtil.responderExito(response, HttpServletResponse.SC_OK,
                        "Proyecto actualizado exitosamente.", actualizadoObj);
            } else {
                JsonUtil.responderError(response, HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
                        "No se pudo actualizar el proyecto.");
            }
        } catch (SQLException e) {
            JsonUtil.responderError(response, HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
                    "Error de base de datos al actualizar proyecto: " + e.getMessage());
        }
    }

    @Override
    protected void doDelete(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        try {
            Integer id = JsonUtil.extraerId(request);

            if (id == null || id <= 0) {
                JsonUtil.responderError(response, HttpServletResponse.SC_BAD_REQUEST,
                        "Debe proporcionar un ID de proyecto válido.");
                return;
            }

            Proyecto existente = proyectoDAO.buscarPorId(id);
            if (existente == null) {
                JsonUtil.responderError(response, HttpServletResponse.SC_NOT_FOUND,
                        "Proyecto con ID " + id + " no encontrado.");
                return;
            }

            boolean eliminado = proyectoDAO.eliminar(id);
            if (eliminado) {
                JsonUtil.responderExito(response, HttpServletResponse.SC_OK,
                        "Proyecto eliminado correctamente.", null);
            } else {
                JsonUtil.responderError(response, HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
                        "No fue posible eliminar el proyecto.");
            }
        } catch (SQLException e) {
            JsonUtil.responderError(response, HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
                    "Error de base de datos al eliminar proyecto: " + e.getMessage());
        }
    }

    private String validarProyecto(Proyecto proyecto) {
        if (proyecto.getNombre() == null || proyecto.getNombre().trim().isEmpty()) {
            return "El nombre del proyecto es obligatorio.";
        }
        if (proyecto.getNombre().trim().length() > 50) {
            return "El nombre del proyecto no puede superar los 50 caracteres.";
        }
        if (proyecto.getIdclientes() <= 0) {
            return "Debe seleccionar un cliente válido para el proyecto.";
        }
        if (proyecto.getIdestados() <= 0) {
            return "Debe seleccionar un estado válido para el proyecto.";
        }
        if (proyecto.getFechaEntrega() == null) {
            return "La fecha de entrega del proyecto es obligatoria.";
        }
        if (proyecto.getValorTotal() == null || proyecto.getValorTotal().compareTo(BigDecimal.ZERO) < 0) {
            return "El valor total del proyecto no puede ser negativo ni nulo.";
        }
        return null;
    }
}
