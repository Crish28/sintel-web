package com.sintel;

import com.sintel.dao.ClienteDAO;
import com.sintel.modelo.Cliente;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;
import java.util.regex.Pattern;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@WebServlet("/clientes")
public class ClienteServlet extends HttpServlet {

    private static final Pattern PATRON_CORREO =
            Pattern.compile("^[^\\s@]+@[^\\s@]+\\.[^\\s@]+$");

    private static final int MAX_NOMBRE = 50;
    private static final int MAX_CORREO = 50;
    private static final int MAX_TELEFONO = 50;
    private static final int MAX_DIRECCION = 150;

    @Override
    protected void doGet(HttpServletRequest request,
                         HttpServletResponse response)
            throws ServletException, IOException {

        String accion = request.getParameter("accion");

        try {
            ClienteDAO clienteDAO = new ClienteDAO();

            // EDITAR CLIENTE
            if ("editar".equals(accion)) {
                Integer id = obtenerId(request.getParameter("id"));

                if (id == null) {
                    response.sendError(
                            HttpServletResponse.SC_BAD_REQUEST,
                            "El ID del cliente no es válido."
                    );
                    return;
                }

                Cliente cliente = clienteDAO.buscarPorId(id);

                if (cliente == null) {
                    response.sendError(
                            HttpServletResponse.SC_NOT_FOUND,
                            "Cliente no encontrado."
                    );
                    return;
                }

                request.setAttribute("cliente", cliente);

                request.getRequestDispatcher("/editar-cliente.jsp")
                        .forward(request, response);
                return;
            }

            // MOSTRAR TODOS LOS CLIENTES
            List<Cliente> clientes = clienteDAO.listarTodos();
            request.setAttribute("clientes", clientes);

            request.getRequestDispatcher("/clientes.jsp")
                    .forward(request, response);

        } catch (SQLException e) {
            throw new ServletException(
                    "Error al consultar los clientes.", e
            );
        }
    }

    @Override
    protected void doPost(HttpServletRequest request,
                          HttpServletResponse response)
            throws ServletException, IOException {

        request.setCharacterEncoding("UTF-8");

        // Validar CSRF antes de procesar cualquier operación.
        if (!CsrfUtil.validarToken(
                request, request.getParameter("csrfToken"))) {

            response.sendError(
                    HttpServletResponse.SC_FORBIDDEN,
                    "La solicitud no es válida o el token de seguridad "
                    + "ha caducado. Recarga la página e inténtalo nuevamente."
            );
            return;
        }

        String accion = request.getParameter("accion");
        String idParametro = request.getParameter("id");

        try {
            ClienteDAO clienteDAO = new ClienteDAO();

            // ELIMINAR CLIENTE
            if ("eliminar".equals(accion)) {
                Integer id = obtenerId(idParametro);

                if (id == null) {
                    response.sendError(
                            HttpServletResponse.SC_BAD_REQUEST,
                            "El ID del cliente no es válido."
                    );
                    return;
                }

                if (clienteDAO.tieneProyectosAsociados(id)) {
                    mostrarListaConError(
                            request,
                            response,
                            clienteDAO,
                            "No se puede eliminar este cliente porque "
                            + "tiene proyectos asociados. "
                            + "El cliente debe permanecer registrado."
                    );
                    return;
                }

                boolean eliminado = clienteDAO.eliminar(id);

                if (!eliminado) {
                    response.sendError(
                            HttpServletResponse.SC_NOT_FOUND,
                            "El cliente no existe o ya fue eliminado."
                    );
                    return;
                }

                response.sendRedirect(
                        request.getContextPath() + "/clientes"
                );
                return;
            }

            // Solo se permiten las acciones conocidas.
            if (!"guardar".equals(accion)
                    && !"editar".equals(accion)) {

                response.sendError(
                        HttpServletResponse.SC_BAD_REQUEST,
                        "La operación solicitada no es válida."
                );
                return;
            }

            String nombre = limpiar(request.getParameter("nombre"));
            String correo = limpiar(request.getParameter("correo"));
            String telefono = limpiar(request.getParameter("telefono"));
            String direccion = limpiar(request.getParameter("direccion"));

            // VALIDAR CAMPOS
            String error = validarDatos(
                    nombre, correo, telefono, direccion
            );

            if (error != null) {
                mostrarFormularioConError(
                        request, response, idParametro,
                        nombre, correo, telefono, direccion, error
                );
                return;
            }

            boolean esEdicion =
                    idParametro != null && !idParametro.isBlank();

            Cliente cliente = new Cliente(
                    nombre, correo, telefono, direccion
            );

            if (esEdicion) {
                Integer id = obtenerId(idParametro);

                if (id == null) {
                    response.sendError(
                            HttpServletResponse.SC_BAD_REQUEST,
                            "El ID del cliente no es válido."
                    );
                    return;
                }

                Cliente existente = clienteDAO.buscarPorId(id);

                if (existente == null) {
                    response.sendError(
                            HttpServletResponse.SC_NOT_FOUND,
                            "El cliente que intentas editar no existe."
                    );
                    return;
                }

                // Evitar correos duplicados en otros clientes.
                if (clienteDAO.correoExisteEnOtroCliente(correo, id)) {
                    mostrarFormularioConError(
                            request, response, idParametro,
                            nombre, correo, telefono, direccion,
                            "Ese correo ya pertenece a otro cliente."
                    );
                    return;
                }

                cliente.setIdclientes(id);

                boolean actualizado = clienteDAO.actualizar(cliente);

                if (!actualizado) {
                    response.sendError(
                            HttpServletResponse.SC_CONFLICT,
                            "No se pudo actualizar el cliente."
                    );
                    return;
                }

            } else {
                // REGISTRAR CLIENTE
                if (clienteDAO.correoExiste(correo)) {
                    mostrarFormularioConError(
                            request, response, null,
                            nombre, correo, telefono, direccion,
                            "Ya existe un cliente registrado con ese correo."
                    );
                    return;
                }

                boolean registrado = clienteDAO.registrar(cliente);

                if (!registrado) {
                    response.sendError(
                            HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
                            "No se pudo registrar el cliente."
                    );
                    return;
                }
            }

            response.sendRedirect(
                    request.getContextPath() + "/clientes"
            );

        } catch (SQLException e) {
            // El índice UNIQUE de correo en MySQL sigue siendo
            // la protección definitiva contra duplicados.
            throw new ServletException(
                    "Error al guardar o eliminar el cliente.", e
            );
        }
    }

    private String validarDatos(
            String nombre,
            String correo,
            String telefono,
            String direccion) {

        if (nombre.isEmpty()
                || correo.isEmpty()
                || telefono.isEmpty()) {
            return "Nombre, correo y teléfono son obligatorios.";
        }

        if (nombre.length() > MAX_NOMBRE) {
            return "El nombre no puede superar los 50 caracteres.";
        }

        if (correo.length() > MAX_CORREO) {
            return "El correo no puede superar los 50 caracteres.";
        }

        if (!PATRON_CORREO.matcher(correo).matches()) {
            return "Ingresa un correo electrónico válido.";
        }

        if (telefono.length() > MAX_TELEFONO) {
            return "El teléfono no puede superar los 50 caracteres.";
        }

        if (direccion.length() > MAX_DIRECCION) {
            return "La dirección no puede superar los 150 caracteres.";
        }

        return null;
    }

    private void mostrarFormularioConError(
            HttpServletRequest request,
            HttpServletResponse response,
            String id,
            String nombre,
            String correo,
            String telefono,
            String direccion,
            String error)
            throws ServletException, IOException, SQLException {

        request.setAttribute("nombreIngresado", nombre);
        request.setAttribute("correoIngresado", correo);
        request.setAttribute("telefonoIngresado", telefono);
        request.setAttribute("direccionIngresada", direccion);
        request.setAttribute("error", error);

        if (id != null && !id.isBlank()) {
            try {
                Cliente cliente = new Cliente(
                        nombre, correo, telefono, direccion
                );
                cliente.setIdclientes(Integer.parseInt(id));
                request.setAttribute("cliente", cliente);
                request.getRequestDispatcher("/editar-cliente.jsp")
                        .forward(request, response);
            } catch (NumberFormatException e) {
                response.sendError(
                        HttpServletResponse.SC_BAD_REQUEST,
                        "El ID del cliente no es válido."
                );
            }
        } else {
            ClienteDAO clienteDAO = new ClienteDAO();
            request.setAttribute(
                    "clientes", clienteDAO.listarTodos()
            );
            request.getRequestDispatcher("/clientes.jsp")
                    .forward(request, response);
        }
    }

    private void mostrarListaConError(
            HttpServletRequest request,
            HttpServletResponse response,
            ClienteDAO clienteDAO,
            String error)
            throws ServletException, IOException, SQLException {

        request.setAttribute("error", error);
        request.setAttribute("clientes", clienteDAO.listarTodos());

        request.getRequestDispatcher("/clientes.jsp")
                .forward(request, response);
    }

    private Integer obtenerId(String idParametro) {
        if (idParametro == null || idParametro.isBlank()) {
            return null;
        }

        try {
            int id = Integer.parseInt(idParametro);
            return id > 0 ? id : null;
        } catch (NumberFormatException e) {
            return null;
        }
    }

    private String limpiar(String valor) {
        return valor == null ? "" : valor.trim();
    }
}

