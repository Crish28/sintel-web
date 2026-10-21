package com.sintel.api;

import com.sintel.dao.ClienteDAO;
import com.sintel.modelo.Cliente;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;
import java.util.regex.Pattern;

@WebServlet(urlPatterns = {"/api/clientes", "/api/clientes/*"})
public class ClienteApiServlet extends HttpServlet {

    private static final Pattern PATRON_CORREO =
            Pattern.compile("^[^\\s@]+@[^\\s@]+\\.[^\\s@]+$");

    private ClienteDAO clienteDAO;

    @Override
    public void init() throws ServletException {
        this.clienteDAO = new ClienteDAO();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        try {
            Integer id = JsonUtil.extraerId(request);

            if (id != null) {
                Cliente cliente = clienteDAO.buscarPorId(id);
                if (cliente == null) {
                    JsonUtil.responderError(response, HttpServletResponse.SC_NOT_FOUND,
                            "Cliente con ID " + id + " no encontrado.");
                    return;
                }
                JsonUtil.responder(response, HttpServletResponse.SC_OK, cliente);
            } else {
                List<Cliente> clientes = clienteDAO.listarTodos();
                JsonUtil.responder(response, HttpServletResponse.SC_OK, clientes);
            }
        } catch (SQLException e) {
            JsonUtil.responderError(response, HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
                    "Error al consultar clientes: " + e.getMessage());
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        try {
            Cliente nuevoCliente = JsonUtil.leerCuerpo(request, Cliente.class);

            if (nuevoCliente == null) {
                JsonUtil.responderError(response, HttpServletResponse.SC_BAD_REQUEST,
                        "El cuerpo de la solicitud no contiene un cliente válido.");
                return;
            }

            // Validaciones de negocio
            String errorValidacion = validarCliente(nuevoCliente);
            if (errorValidacion != null) {
                JsonUtil.responderError(response, HttpServletResponse.SC_BAD_REQUEST, errorValidacion);
                return;
            }

            // Verificar duplicidad de correo
            if (clienteDAO.correoExiste(nuevoCliente.getCorreo().trim())) {
                JsonUtil.responderError(response, HttpServletResponse.SC_CONFLICT,
                        "El correo " + nuevoCliente.getCorreo() + " ya se encuentra registrado.");
                return;
            }

            int idGenerado = clienteDAO.registrarRetornandoId(nuevoCliente);
            if (idGenerado > 0) {
                Cliente creado = clienteDAO.buscarPorId(idGenerado);
                JsonUtil.responderExito(response, HttpServletResponse.SC_CREATED,
                        "Cliente registrado exitosamente.", creado);
            } else {
                JsonUtil.responderError(response, HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
                        "No se pudo registrar el cliente.");
            }
        } catch (SQLException e) {
            JsonUtil.responderError(response, HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
                    "Error de base de datos al registrar cliente: " + e.getMessage());
        }
    }

    @Override
    protected void doPut(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        try {
            Cliente clienteActualizar = JsonUtil.leerCuerpo(request, Cliente.class);
            Integer idRuta = JsonUtil.extraerId(request);

            if (clienteActualizar == null) {
                JsonUtil.responderError(response, HttpServletResponse.SC_BAD_REQUEST,
                        "El cuerpo de la solicitud no contiene un cliente válido.");
                return;
            }

            if (idRuta != null) {
                clienteActualizar.setIdclientes(idRuta);
            }

            if (clienteActualizar.getIdclientes() <= 0) {
                JsonUtil.responderError(response, HttpServletResponse.SC_BAD_REQUEST,
                        "Debe especificar un ID de cliente válido a actualizar.");
                return;
            }

            // Verificar si el cliente existe
            Cliente clienteExistente = clienteDAO.buscarPorId(clienteActualizar.getIdclientes());
            if (clienteExistente == null) {
                JsonUtil.responderError(response, HttpServletResponse.SC_NOT_FOUND,
                        "El cliente con ID " + clienteActualizar.getIdclientes() + " no existe.");
                return;
            }

            // Validaciones
            String errorValidacion = validarCliente(clienteActualizar);
            if (errorValidacion != null) {
                JsonUtil.responderError(response, HttpServletResponse.SC_BAD_REQUEST, errorValidacion);
                return;
            }

            // Validar correo en otro cliente
            if (clienteDAO.correoExisteEnOtroCliente(clienteActualizar.getCorreo().trim(), clienteActualizar.getIdclientes())) {
                JsonUtil.responderError(response, HttpServletResponse.SC_CONFLICT,
                        "El correo " + clienteActualizar.getCorreo() + " ya está asignado a otro cliente.");
                return;
            }

            boolean actualizado = clienteDAO.actualizar(clienteActualizar);
            if (actualizado) {
                Cliente actualizadoObj = clienteDAO.buscarPorId(clienteActualizar.getIdclientes());
                JsonUtil.responderExito(response, HttpServletResponse.SC_OK,
                        "Cliente actualizado exitosamente.", actualizadoObj);
            } else {
                JsonUtil.responderError(response, HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
                        "No se pudo actualizar la información del cliente.");
            }
        } catch (SQLException e) {
            JsonUtil.responderError(response, HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
                    "Error al actualizar cliente: " + e.getMessage());
        }
    }

    @Override
    protected void doDelete(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        try {
            Integer id = JsonUtil.extraerId(request);

            if (id == null || id <= 0) {
                JsonUtil.responderError(response, HttpServletResponse.SC_BAD_REQUEST,
                        "Debe proporcionar un ID de cliente válido para eliminar.");
                return;
            }

            Cliente existente = clienteDAO.buscarPorId(id);
            if (existente == null) {
                JsonUtil.responderError(response, HttpServletResponse.SC_NOT_FOUND,
                        "Cliente con ID " + id + " no encontrado.");
                return;
            }

            // Comprobar proyectos asociados antes de eliminar
            if (clienteDAO.tieneProyectosAsociados(id)) {
                JsonUtil.responderError(response, HttpServletResponse.SC_CONFLICT,
                        "No es posible eliminar el cliente porque tiene proyectos vinculados.");
                return;
            }

            boolean eliminado = clienteDAO.eliminar(id);
            if (eliminado) {
                JsonUtil.responderExito(response, HttpServletResponse.SC_OK,
                        "Cliente eliminado correctamente.", null);
            } else {
                JsonUtil.responderError(response, HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
                        "No fue posible eliminar el cliente.");
            }
        } catch (SQLException e) {
            JsonUtil.responderError(response, HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
                    "Error de base de datos al eliminar cliente: " + e.getMessage());
        }
    }

    private String validarCliente(Cliente cliente) {
        if (cliente.getNombre() == null || cliente.getNombre().trim().isEmpty()) {
            return "El nombre del cliente es obligatorio.";
        }
        if (cliente.getNombre().trim().length() > 50) {
            return "El nombre no puede superar los 50 caracteres.";
        }
        if (cliente.getCorreo() == null || cliente.getCorreo().trim().isEmpty()) {
            return "El correo electrónico es obligatorio.";
        }
        if (!PATRON_CORREO.matcher(cliente.getCorreo().trim()).matches()) {
            return "El formato del correo electrónico no es válido.";
        }
        if (cliente.getCorreo().trim().length() > 50) {
            return "El correo no puede superar los 50 caracteres.";
        }
        if (cliente.getTelefono() != null && cliente.getTelefono().trim().length() > 50) {
            return "El teléfono no puede superar los 50 caracteres.";
        }
        if (cliente.getDireccion() != null && cliente.getDireccion().trim().length() > 150) {
            return "La dirección no puede superar los 150 caracteres.";
        }
        return null;
    }
}
