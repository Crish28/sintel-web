package com.sintel.api;

import com.google.gson.JsonObject;
import com.sintel.PasswordUtil;
import com.sintel.dao.UsuarioDAO;
import com.sintel.modelo.Usuario;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;
import java.util.regex.Pattern;

@WebServlet(urlPatterns = {"/api/usuarios", "/api/usuarios/*"})
public class UsuarioApiServlet extends HttpServlet {

    private static final Pattern PATRON_CORREO =
            Pattern.compile("^[^\\s@]+@[^\\s@]+\\.[^\\s@]+$");

    private UsuarioDAO usuarioDAO;

    @Override
    public void init() throws ServletException {
        this.usuarioDAO = new UsuarioDAO();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        try {
            Integer id = JsonUtil.extraerId(request);

            if (id != null) {
                Usuario usuario = usuarioDAO.buscarPorId(id);
                if (usuario == null) {
                    JsonUtil.responderError(response, HttpServletResponse.SC_NOT_FOUND,
                            "Usuario con ID " + id + " no encontrado.");
                    return;
                }
                usuario.setPasswordHash(null);
                JsonUtil.responder(response, HttpServletResponse.SC_OK, usuario);
            } else {
                List<Usuario> usuarios = usuarioDAO.listar();
                for (Usuario u : usuarios) {
                    u.setPasswordHash(null);
                }
                JsonUtil.responder(response, HttpServletResponse.SC_OK, usuarios);
            }
        } catch (SQLException e) {
            JsonUtil.responderError(response, HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
                    "Error al consultar usuarios: " + e.getMessage());
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        try {
            JsonObject json = JsonUtil.leerCuerpo(request, JsonObject.class);

            if (json == null) {
                JsonUtil.responderError(response, HttpServletResponse.SC_BAD_REQUEST,
                        "El cuerpo de la solicitud no contiene un JSON válido.");
                return;
            }

            String nombre = json.has("nombre") && !json.get("nombre").isJsonNull()
                    ? json.get("nombre").getAsString().trim() : null;
            String correo = json.has("correo") && !json.get("correo").isJsonNull()
                    ? json.get("correo").getAsString().trim().toLowerCase() : null;
            String password = json.has("password") && !json.get("password").isJsonNull()
                    ? json.get("password").getAsString() : null;
            String rol = json.has("rol") && !json.get("rol").isJsonNull()
                    ? json.get("rol").getAsString().trim().toLowerCase() : "operador";

            if (nombre == null || nombre.isEmpty()) {
                JsonUtil.responderError(response, HttpServletResponse.SC_BAD_REQUEST, "El nombre es obligatorio.");
                return;
            }
            if (nombre.length() > 50) {
                JsonUtil.responderError(response, HttpServletResponse.SC_BAD_REQUEST, "El nombre no puede superar 50 caracteres.");
                return;
            }
            if (correo == null || !PATRON_CORREO.matcher(correo).matches()) {
                JsonUtil.responderError(response, HttpServletResponse.SC_BAD_REQUEST, "Correo electrónico inválido.");
                return;
            }
            if (password == null || password.length() < 6) {
                JsonUtil.responderError(response, HttpServletResponse.SC_BAD_REQUEST, "La contraseña debe tener al menos 6 caracteres.");
                return;
            }
            if (!"administrador".equals(rol) && !"operador".equals(rol)) {
                JsonUtil.responderError(response, HttpServletResponse.SC_BAD_REQUEST, "El rol debe ser 'administrador' u 'operador'.");
                return;
            }

            // Verificar si el correo ya existe
            if (usuarioDAO.buscarPorCorreo(correo) != null) {
                JsonUtil.responderError(response, HttpServletResponse.SC_CONFLICT,
                        "El correo " + correo + " ya está registrado en el sistema.");
                return;
            }

            String passwordHash = PasswordUtil.hashear(password);
            Usuario nuevo = new Usuario(nombre, correo, passwordHash, rol);
            nuevo.setActivo(true);

            int idGenerado = usuarioDAO.registrarRetornandoId(nuevo);
            if (idGenerado > 0) {
                Usuario creado = usuarioDAO.buscarPorId(idGenerado);
                creado.setPasswordHash(null);
                JsonUtil.responderExito(response, HttpServletResponse.SC_CREATED,
                        "Usuario registrado exitosamente.", creado);
            } else {
                JsonUtil.responderError(response, HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
                        "No se pudo registrar el usuario.");
            }
        } catch (SQLException e) {
            JsonUtil.responderError(response, HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
                    "Error de base de datos al registrar usuario: " + e.getMessage());
        }
    }

    @Override
    protected void doPut(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        try {
            JsonObject json = JsonUtil.leerCuerpo(request, JsonObject.class);
            Integer idRuta = JsonUtil.extraerId(request);

            if (json == null) {
                JsonUtil.responderError(response, HttpServletResponse.SC_BAD_REQUEST,
                        "El cuerpo de la solicitud no contiene un JSON válido.");
                return;
            }

            int idUsuario = idRuta != null ? idRuta
                    : (json.has("idusuario") ? json.get("idusuario").getAsInt() : 0);

            if (idUsuario <= 0) {
                JsonUtil.responderError(response, HttpServletResponse.SC_BAD_REQUEST,
                        "Debe especificar un ID de usuario válido.");
                return;
            }

            Usuario existente = usuarioDAO.buscarPorId(idUsuario);
            if (existente == null) {
                JsonUtil.responderError(response, HttpServletResponse.SC_NOT_FOUND,
                        "El usuario con ID " + idUsuario + " no existe.");
                return;
            }

            // Caso 1: Cambio exclusivo de estado activo/inactivo
            String accion = request.getParameter("accion");
            String pathInfo = request.getPathInfo();
            boolean esCambioEstado = (accion != null && "estado".equalsIgnoreCase(accion))
                    || (pathInfo != null && pathInfo.endsWith("/estado"))
                    || (json.has("soloEstado") && json.get("soloEstado").getAsBoolean());

            if (esCambioEstado && json.has("activo")) {
                boolean nuevoEstado = json.get("activo").getAsBoolean();
                boolean cambiado = usuarioDAO.cambiarEstado(idUsuario, nuevoEstado);
                if (cambiado) {
                    Usuario modificado = usuarioDAO.buscarPorId(idUsuario);
                    modificado.setPasswordHash(null);
                    JsonUtil.responderExito(response, HttpServletResponse.SC_OK,
                            "Estado del usuario actualizado correctamente.", modificado);
                    return;
                }
            }

            // Caso 2: Actualización de datos generales (nombre, correo, rol)
            String nombre = json.has("nombre") && !json.get("nombre").isJsonNull()
                    ? json.get("nombre").getAsString().trim() : existente.getNombre();
            String correo = json.has("correo") && !json.get("correo").isJsonNull()
                    ? json.get("correo").getAsString().trim().toLowerCase() : existente.getCorreo();
            String rol = json.has("rol") && !json.get("rol").isJsonNull()
                    ? json.get("rol").getAsString().trim().toLowerCase() : existente.getRol();

            if (nombre.isEmpty() || nombre.length() > 50) {
                JsonUtil.responderError(response, HttpServletResponse.SC_BAD_REQUEST, "Nombre de usuario inválido.");
                return;
            }
            if (!PATRON_CORREO.matcher(correo).matches()) {
                JsonUtil.responderError(response, HttpServletResponse.SC_BAD_REQUEST, "Formato de correo no válido.");
                return;
            }
            if (!"administrador".equals(rol) && !"operador".equals(rol)) {
                JsonUtil.responderError(response, HttpServletResponse.SC_BAD_REQUEST, "Rol debe ser administrador u operador.");
                return;
            }

            // Verificar si el correo pertenece a otro usuario
            Usuario usuarioMismoCorreo = usuarioDAO.buscarPorCorreo(correo);
            if (usuarioMismoCorreo != null && usuarioMismoCorreo.getIdusuario() != idUsuario) {
                JsonUtil.responderError(response, HttpServletResponse.SC_CONFLICT,
                        "El correo ya está en uso por otro usuario.");
                return;
            }

            existente.setNombre(nombre);
            existente.setCorreo(correo);
            existente.setRol(rol);

            boolean actualizado = usuarioDAO.actualizar(existente);
            if (actualizado) {
                Usuario actualizadoObj = usuarioDAO.buscarPorId(idUsuario);
                actualizadoObj.setPasswordHash(null);
                JsonUtil.responderExito(response, HttpServletResponse.SC_OK,
                        "Usuario actualizado exitosamente.", actualizadoObj);
            } else {
                JsonUtil.responderError(response, HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
                        "No se pudo actualizar el usuario.");
            }
        } catch (SQLException e) {
            JsonUtil.responderError(response, HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
                    "Error de base de datos al actualizar usuario: " + e.getMessage());
        }
    }
}
