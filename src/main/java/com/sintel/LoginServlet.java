package com.sintel;

import com.sintel.dao.UsuarioDAO;
import com.sintel.modelo.Usuario;

import java.io.IOException;
import java.sql.SQLException;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

@WebServlet("/login")
public class LoginServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request,
                         HttpServletResponse response)
            throws ServletException, IOException {

        // Evitar almacenar en caché la página de inicio de sesión.
        response.setHeader("Cache-Control", "no-store, no-cache, must-revalidate");
        response.setHeader("Pragma", "no-cache");
        response.setDateHeader("Expires", 0);

        // Si ya existe una sesión autenticada, ir al dashboard.
        HttpSession sesion = request.getSession(false);

        if (sesion != null && sesion.getAttribute("usuario") != null) {
            response.sendRedirect(request.getContextPath() + "/");
            return;
        }

        request.getRequestDispatcher("/login.jsp")
                .forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request,
                          HttpServletResponse response)
            throws ServletException, IOException {

        request.setCharacterEncoding("UTF-8");

        response.setHeader("Cache-Control", "no-store, no-cache, must-revalidate");
        response.setHeader("Pragma", "no-cache");
        response.setDateHeader("Expires", 0);

        String correo = request.getParameter("correo");
        String contraseña = request.getParameter("contraseña");

        // Validar que los campos existan y no estén vacíos.
        if (correo == null || correo.isBlank()
                || contraseña == null || contraseña.isEmpty()) {

            request.setAttribute(
                    "error",
                    "Debes ingresar el correo y la contraseña."
            );

            request.getRequestDispatcher("/login.jsp")
                    .forward(request, response);
            return;
        }

        correo = correo.trim();

        try {
            UsuarioDAO usuarioDAO = new UsuarioDAO();
            Usuario usuario = usuarioDAO.buscarPorCorreo(correo);

            // Verificar las credenciales con BCrypt.
            if (usuario == null
                    || !PasswordUtil.verificar(
                            contraseña,
                            usuario.getPasswordHash())) {

                request.setAttribute(
                        "error",
                        "Correo o contraseña incorrectos."
                );

                request.getRequestDispatcher("/login.jsp")
                        .forward(request, response);
                return;
            }

            // No permitir el acceso a cuentas desactivadas.
            if (!usuario.isActivo()) {
                request.setAttribute(
                        "error",
                        "Tu cuenta está desactivada. Contacta al administrador."
                );

                request.getRequestDispatcher("/login.jsp")
                        .forward(request, response);
                return;
            }

            /*
             * Renovar el identificador de la sesión después
             * de autenticar al usuario.
             */
            HttpSession sesion = request.getSession();

            request.changeSessionId();

            // Guardar los datos necesarios para la autenticación.
            sesion.setAttribute("usuario", usuario);
            sesion.setAttribute("nombreUsuario", usuario.getNombre());
            sesion.setAttribute("rolUsuario", usuario.getRol());

            // Redirigir al dashboard.
            response.sendRedirect(request.getContextPath() + "/");

        } catch (SQLException e) {
            throw new ServletException(
                    "Error al iniciar sesión.",
                    e
            );
        }
    }
}

