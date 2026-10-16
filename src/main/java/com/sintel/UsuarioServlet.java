package com.sintel;

import com.sintel.dao.UsuarioDAO;
import com.sintel.modelo.Usuario;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.sql.SQLException;
import java.util.Set;

@WebServlet("/usuarios")
public class UsuarioServlet extends HttpServlet {

    private final UsuarioDAO usuarioDAO = new UsuarioDAO();

    private boolean esAdministrador(HttpServletRequest request) {
        HttpSession sesion = request.getSession(false);

        return sesion != null
                && sesion.getAttribute("usuario") instanceof Usuario
                && "Administrador".equals(
                        sesion.getAttribute("rolUsuario"));
    }

    private boolean rolValido(String rol) {
        return "Administrador".equals(rol)
                || "Usuario".equals(rol);
    }

    private void mostrarError(
            HttpServletRequest request,
            HttpServletResponse response,
            String mensaje)
            throws ServletException, IOException {

        request.setAttribute("error", mensaje);

        try {
            request.setAttribute("usuarios", usuarioDAO.listar());

            request.getRequestDispatcher("/usuarios.jsp")
                    .forward(request, response);

        } catch (SQLException e) {
            throw new ServletException(
                    "Error al cargar la lista de usuarios.", e);
        }
    }

    @Override
    protected void doGet(HttpServletRequest request,
                         HttpServletResponse response)
            throws ServletException, IOException {

        if (!esAdministrador(request)) {
            response.sendError(
                    HttpServletResponse.SC_FORBIDDEN,
                    "No tienes permiso para administrar usuarios."
            );
            return;
        }

        request.setCharacterEncoding("UTF-8");

        String accion = request.getParameter("accion");

        try {
            if ("nuevo".equals(accion)) {
                request.setAttribute("accionFormulario", "crear");

                request.getRequestDispatcher("/formulario-usuario.jsp")
                        .forward(request, response);
                return;
            }

            if ("editar".equals(accion)) {
                int id = Integer.parseInt(request.getParameter("id"));
                Usuario usuario = usuarioDAO.buscarPorId(id);

                if (usuario == null) {
                    response.sendError(
                            HttpServletResponse.SC_NOT_FOUND,
                            "El usuario no existe."
                    );
                    return;
                }

                request.setAttribute("usuarioEditar", usuario);
                request.setAttribute("accionFormulario", "editar");

                request.getRequestDispatcher("/formulario-usuario.jsp")
                        .forward(request, response);
                return;
            }

            request.setAttribute("usuarios", usuarioDAO.listar());

            request.getRequestDispatcher("/usuarios.jsp")
                    .forward(request, response);

        } catch (NumberFormatException e) {
            response.sendError(
                    HttpServletResponse.SC_BAD_REQUEST,
                    "El identificador del usuario no es válido."
            );

        } catch (SQLException e) {
            throw new ServletException(
                    "Error al consultar los usuarios.", e);
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
                    "No tienes permiso para administrar usuarios."
            );
            return;
        }

        // Validar el token CSRF.
        if (!CsrfUtil.validarToken(
                request,
                request.getParameter("csrfToken"))) {

            response.sendError(
                    HttpServletResponse.SC_FORBIDDEN,
                    "La solicitud no es válida o el token de seguridad ha caducado. "
                            + "Recarga la página e inténtalo nuevamente."
            );
            return;
        }

        String accion = request.getParameter("accion");
        HttpSession sesion = request.getSession(false);

        Usuario usuarioSesion =
                (Usuario) sesion.getAttribute("usuario");

        try {
            if ("crear".equals(accion)) {
                crearUsuario(request, response);
                return;
            }

            if ("editar".equals(accion)) {
                editarUsuario(request, response);
                return;
            }

            if ("estado".equals(accion)) {
                cambiarEstado(request, response, usuarioSesion);
                return;
            }

            response.sendError(
                    HttpServletResponse.SC_BAD_REQUEST,
                    "La operación solicitada no es válida."
            );

        } catch (NumberFormatException e) {
            mostrarError(
                    request, response,
                    "El identificador o los datos numéricos no son válidos."
            );

        } catch (SQLException e) {
            if ("23000".equals(e.getSQLState())) {
                mostrarError(
                        request, response,
                        "No se pudo completar la operación. "
                                + "Comprueba que el correo no esté registrado."
                );
            } else {
                throw new ServletException(
                        "Error al administrar usuarios.", e);
            }
        }
    }

    private void crearUsuario(
            HttpServletRequest request,
            HttpServletResponse response)
            throws SQLException, ServletException, IOException {

        String nombre = request.getParameter("nombre");
        String correo = request.getParameter("correo");
        String contraseña = request.getParameter("contraseña");
        String rol = request.getParameter("rol");

        if (nombre == null || nombre.isBlank()
                || correo == null || correo.isBlank()
                || contraseña == null || contraseña.length() < 8
                || rol == null || !rolValido(rol)) {

            mostrarError(
                    request, response,
                    "Completa los campos correctamente. "
                            + "La contraseña debe tener al menos 8 caracteres."
            );
            return;
        }

        nombre = nombre.trim();
        correo = correo.trim();

        if (nombre.length() > 45 || correo.length() > 45
                || !correo.matches("^[^\\s@]+@[^\\s@]+\\.[^\\s@]+$")) {

            mostrarError(
                    request, response,
                    "Revisa el nombre y el formato del correo electrónico."
            );
            return;
        }

        if (usuarioDAO.buscarPorCorreo(correo) != null) {
            mostrarError(
                    request, response,
                    "Ya existe una cuenta registrada con ese correo."
            );
            return;
        }

        Usuario usuario = new Usuario(
                nombre,
                correo,
                PasswordUtil.generarHash(contraseña),
                rol
        );

        if (usuarioDAO.registrar(usuario)) {
            response.sendRedirect(
                    request.getContextPath()
                            + "/usuarios?resultado=creado"
            );
        } else {
            mostrarError(
                    request, response,
                    "No fue posible registrar el usuario."
            );
        }
    }

    private void editarUsuario(
            HttpServletRequest request,
            HttpServletResponse response)
            throws SQLException, ServletException, IOException {

        int id = Integer.parseInt(request.getParameter("id"));

        String nombre = request.getParameter("nombre");
        String correo = request.getParameter("correo");
        String rol = request.getParameter("rol");
        String nuevaContraseña = request.getParameter("contraseña");

        if (nombre == null || nombre.isBlank()
                || correo == null || correo.isBlank()
                || !rolValido(rol)) {

            mostrarError(
                    request, response,
                    "Completa los datos del usuario correctamente."
            );
            return;
        }

        nombre = nombre.trim();
        correo = correo.trim();

        if (nombre.length() > 45 || correo.length() > 45
                || !correo.matches("^[^\\s@]+@[^\\s@]+\\.[^\\s@]+$")) {

            mostrarError(
                    request, response,
                    "Revisa el nombre y el formato del correo electrónico."
            );
            return;
        }

        Usuario existente = usuarioDAO.buscarPorId(id);

        if (existente == null) {
            mostrarError(
                    request, response,
                    "El usuario que intentas editar no existe."
            );
            return;
        }

        // No permitir que se elimine el último administrador activo
        // cambiando su rol a Usuario.
        if ("Administrador".equals(existente.getRol())
                && "Usuario".equals(rol)
                && existente.isActivo()) {

            long administradoresActivos = usuarioDAO.listar().stream()
                    .filter(Usuario::isActivo)
                    .filter(u -> "Administrador".equals(u.getRol()))
                    .count();

            if (administradoresActivos <= 1) {
                mostrarError(
                        request, response,
                        "No puedes cambiar el rol del último administrador activo."
                );
                return;
            }
        }

        Usuario porCorreo = usuarioDAO.buscarPorCorreo(correo);

        if (porCorreo != null && porCorreo.getIdusuario() != id) {
            mostrarError(
                    request, response,
                    "Ese correo ya está registrado por otro usuario."
            );
            return;
        }

        HttpSession sesion = request.getSession(false);

        Usuario usuarioSesion =
                (Usuario) sesion.getAttribute("usuario");

        // No permitir que el administrador quite su propio rol.
        if (usuarioSesion != null
                && usuarioSesion.getIdusuario() == id
                && !"Administrador".equals(rol)) {

            mostrarError(
                    request, response,
                    "No puedes quitarte tu propio rol de administrador."
            );
            return;
        }

        boolean cambiarContraseña =
                nuevaContraseña != null && !nuevaContraseña.isBlank();

        // Validar antes de modificar cualquier dato.
        if (cambiarContraseña && nuevaContraseña.length() < 8) {
            mostrarError(
                    request, response,
                    "La nueva contraseña debe tener al menos 8 caracteres."
            );
            return;
        }

        existente.setNombre(nombre);
        existente.setCorreo(correo);
        existente.setRol(rol);

        usuarioDAO.actualizar(existente);

        // Actualizar la contraseña solo si se escribió una nueva.
        if (cambiarContraseña) {
            usuarioDAO.actualizarPassword(
                    id,
                    PasswordUtil.generarHash(nuevaContraseña)
            );
        }

        // Mantener actualizados los datos del administrador en su sesión.
        if (usuarioSesion != null
                && usuarioSesion.getIdusuario() == id) {

            usuarioSesion.setNombre(nombre);
            usuarioSesion.setCorreo(correo);
            usuarioSesion.setRol(rol);

            sesion.setAttribute("usuario", usuarioSesion);
            sesion.setAttribute("nombreUsuario", nombre);
            sesion.setAttribute("rolUsuario", rol);
        }

        response.sendRedirect(
                request.getContextPath()
                        + "/usuarios?resultado=actualizado"
        );
    }

    private void cambiarEstado(
            HttpServletRequest request,
            HttpServletResponse response,
            Usuario usuarioSesion)
            throws SQLException, ServletException, IOException {

        int id = Integer.parseInt(request.getParameter("id"));
        String estado = request.getParameter("activo");

        if (estado == null || !Set.of("0", "1").contains(estado)) {
            mostrarError(
                    request, response,
                    "El estado solicitado no es válido."
            );
            return;
        }

        boolean activar = "1".equals(estado);

        if (usuarioSesion != null
                && usuarioSesion.getIdusuario() == id
                && !activar) {

            mostrarError(
                    request, response,
                    "No puedes desactivar tu propia cuenta."
            );
            return;
        }

        Usuario existente = usuarioDAO.buscarPorId(id);

        if (existente == null) {
            mostrarError(
                    request, response,
                    "El usuario no existe."
            );
            return;
        }

        if (!activar
                && "Administrador".equals(existente.getRol())
                && existente.isActivo()) {

            long administradoresActivos = usuarioDAO.listar().stream()
                    .filter(Usuario::isActivo)
                    .filter(u -> "Administrador".equals(u.getRol()))
                    .count();

            if (administradoresActivos <= 1) {
                mostrarError(
                        request, response,
                        "No puedes desactivar al último administrador activo."
                );
                return;
            }
        }

        usuarioDAO.cambiarEstado(id, activar);

        response.sendRedirect(
                request.getContextPath()
                        + "/usuarios?resultado="
                        + (activar ? "activado" : "desactivado")
        );
    }
}


