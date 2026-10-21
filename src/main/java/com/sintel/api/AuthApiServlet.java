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
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.sql.SQLException;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.UUID;

@WebServlet("/api/auth/*")
public class AuthApiServlet extends HttpServlet {

    private UsuarioDAO usuarioDAO;

    @Override
    public void init() throws ServletException {
        this.usuarioDAO = new UsuarioDAO();
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String pathInfo = request.getPathInfo();

        if (pathInfo == null || "/login".equals(pathInfo)) {
            procesarLogin(request, response);
        } else if ("/logout".equals(pathInfo)) {
            procesarLogout(request, response);
        } else {
            JsonUtil.responderError(response, HttpServletResponse.SC_NOT_FOUND,
                    "Endpoint de autenticación no reconocido: " + pathInfo);
        }
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String pathInfo = request.getPathInfo();

        if ("/perfil".equals(pathInfo) || "/me".equals(pathInfo)) {
            HttpSession sesion = request.getSession(false);
            if (sesion != null && sesion.getAttribute("usuario") instanceof Usuario usuario) {
                Usuario u = new Usuario();
                u.setIdusuario(usuario.getIdusuario());
                u.setNombre(usuario.getNombre());
                u.setCorreo(usuario.getCorreo());
                u.setRol(usuario.getRol());
                u.setActivo(usuario.isActivo());
                JsonUtil.responder(response, HttpServletResponse.SC_OK, u);
                return;
            }
            JsonUtil.responderError(response, HttpServletResponse.SC_UNAUTHORIZED,
                    "No hay una sesión activa.");
        } else {
            JsonUtil.responderError(response, HttpServletResponse.SC_NOT_FOUND,
                    "Recurso no encontrado.");
        }
    }

    private void procesarLogin(HttpServletRequest request, HttpServletResponse response)
            throws IOException {
        try {
            JsonObject json = JsonUtil.leerCuerpo(request, JsonObject.class);

            if (json == null || !json.has("correo") || !json.has("password")) {
                JsonUtil.responderError(response, HttpServletResponse.SC_BAD_REQUEST,
                        "Debe enviar 'correo' y 'password'.");
                return;
            }

            String correo = json.get("correo").getAsString().trim().toLowerCase();
            String password = json.get("password").getAsString();

            Usuario usuario = usuarioDAO.buscarPorCorreo(correo);

            if (usuario == null || !PasswordUtil.verificar(password, usuario.getPasswordHash())) {
                JsonUtil.responderError(response, HttpServletResponse.SC_UNAUTHORIZED,
                        "Credenciales incorrectas (correo o contraseña no válidos).");
                return;
            }

            if (!usuario.isActivo()) {
                JsonUtil.responderError(response, HttpServletResponse.SC_FORBIDDEN,
                        "La cuenta de usuario se encuentra desactivada.");
                return;
            }

            // Iniciar sesión HTTP para navegación web y cookies
            HttpSession sesion = request.getSession(true);
            sesion.setAttribute("usuario", usuario);
            sesion.setAttribute("nombreUsuario", usuario.getNombre());
            sesion.setAttribute("rolUsuario", usuario.getRol());

            // Generar token simulado para pruebas Bearer de API / Postman
            String token = "sintel-token-" + UUID.randomUUID();

            Usuario datosUsuario = new Usuario();
            datosUsuario.setIdusuario(usuario.getIdusuario());
            datosUsuario.setNombre(usuario.getNombre());
            datosUsuario.setCorreo(usuario.getCorreo());
            datosUsuario.setRol(usuario.getRol());
            datosUsuario.setActivo(usuario.isActivo());

            Map<String, Object> resp = new LinkedHashMap<>();
            resp.put("exito", true);
            resp.put("mensaje", "Autenticación exitosa.");
            resp.put("token", token);
            resp.put("usuario", datosUsuario);

            JsonUtil.responder(response, HttpServletResponse.SC_OK, resp);

        } catch (SQLException e) {
            JsonUtil.responderError(response, HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
                    "Error de base de datos durante el inicio de sesión: " + e.getMessage());
        }
    }

    private void procesarLogout(HttpServletRequest request, HttpServletResponse response)
            throws IOException {
        HttpSession sesion = request.getSession(false);
        if (sesion != null) {
            sesion.invalidate();
        }

        Map<String, Object> resp = new LinkedHashMap<>();
        resp.put("exito", true);
        resp.put("mensaje", "Sesión cerrada exitosamente.");

        JsonUtil.responder(response, HttpServletResponse.SC_OK, resp);
    }
}
