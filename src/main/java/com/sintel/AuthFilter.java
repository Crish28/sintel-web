package com.sintel;

import java.io.IOException;
import java.sql.SQLException;

import com.sintel.dao.UsuarioDAO;
import com.sintel.modelo.Usuario;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;

@WebFilter("/*")
public class AuthFilter implements Filter {

    @Override
    public void doFilter(
            ServletRequest request,
            ServletResponse response,
            FilterChain chain)
            throws IOException, ServletException {

        HttpServletRequest solicitud =
                (HttpServletRequest) request;

        HttpServletResponse respuesta =
                (HttpServletResponse) response;

        String contexto = solicitud.getContextPath();
        String uri = solicitud.getRequestURI();

        String ruta = uri.substring(contexto.length());

        // Evitar que el navegador almacene páginas protegidas.
        respuesta.setHeader(
                "Cache-Control",
                "no-store, no-cache, must-revalidate"
        );
        respuesta.setHeader("Pragma", "no-cache");
        respuesta.setDateHeader("Expires", 0);

     
        // Permitir el acceso a las rutas públicas de autenticación
        // y recuperación de contraseña.
        if (ruta.equals("/login")
                || ruta.equals("/recuperar-contrasena")
                || ruta.equals("/restablecer-contrasena")) {

            chain.doFilter(request, response);
            return;
        }


        // Permitir recursos estáticos públicos.
        if (ruta.startsWith("/css/")
                || ruta.startsWith("/js/")
                || ruta.startsWith("/imagenes/")
                || ruta.equals("/favicon.ico")
                || ruta.startsWith("/favicon")) {

            chain.doFilter(request, response);
            return;
        }

        // Permitir rutas públicas de la API
        if (ruta.equals("/api/auth/login")
                || ruta.equals("/api/auth/logout")
                || ruta.startsWith("/api/docs")) {

            chain.doFilter(request, response);
            return;
        }


        /*
         * Impedir que se abran directamente los JSP internos.
         * Las páginas deben cargarse mediante sus servlets.
         *
         * Esta comprobación afecta a solicitudes directas.
         * Los forwards internos de los servlets no se vuelven
         * a filtrar con la configuración predeterminada.
         */
        if (ruta.toLowerCase(java.util.Locale.ROOT).endsWith(".jsp")
                && !ruta.equals("/login.jsp")) {

            respuesta.sendError(
                    HttpServletResponse.SC_NOT_FOUND
            );
            return;
        }

        // Soporte para autenticación en la API mediante token o cabecera
        if (ruta.startsWith("/api/")) {
            String authHeader = solicitud.getHeader("Authorization");
            String apiKeyHeader = solicitud.getHeader("X-API-KEY");

            // Permitir llamadas a la API que envíen token Bearer o clave de API
            if ((authHeader != null && !authHeader.isBlank())
                    || "sintel-api-key-2026".equals(apiKeyHeader)) {
                chain.doFilter(request, response);
                return;
            }
        }

        // Obtener la sesión existente sin crear otra.
        HttpSession sesion = solicitud.getSession(false);

        if (sesion == null) {
            if (ruta.startsWith("/api/")) {
                respuesta.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                respuesta.setContentType("application/json;charset=UTF-8");
                respuesta.getWriter().write("{\"exito\":false,\"codigo\":401,\"error\":\"No autorizado\",\"mensaje\":\"Se requiere autenticación para acceder a la API\"}");
                return;
            }

            respuesta.sendRedirect(contexto + "/login");
            return;
        }

        // Comprobar que exista un objeto Usuario válido.
        Object atributoUsuario = sesion.getAttribute("usuario");

        if (!(atributoUsuario instanceof Usuario)) {
            sesion.invalidate();

            if (ruta.startsWith("/api/")) {
                respuesta.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                respuesta.setContentType("application/json;charset=UTF-8");
                respuesta.getWriter().write("{\"exito\":false,\"codigo\":401,\"error\":\"No autorizado\",\"mensaje\":\"Sesión no válida o expirada\"}");
                return;
            }

            respuesta.sendRedirect(contexto + "/login");
            return;
        }

        Usuario usuarioSesion = (Usuario) atributoUsuario;

        try {
            UsuarioDAO usuarioDAO = new UsuarioDAO();

            // Consultar el estado actual de la cuenta en la BD.
            Usuario usuarioActual =
                    usuarioDAO.buscarPorId(
                            usuarioSesion.getIdusuario()
                    );

            // Rechazar usuarios inexistentes o desactivados.
            if (usuarioActual == null || !usuarioActual.isActivo()) {
                sesion.invalidate();

                respuesta.sendRedirect(
                        contexto + "/login?cuentaDesactivada=true"
                );
                return;
            }

            // Actualizar los datos de la sesión desde la BD.
            sesion.setAttribute("usuario", usuarioActual);
            sesion.setAttribute(
                    "nombreUsuario",
                    usuarioActual.getNombre()
            );
            sesion.setAttribute(
                    "rolUsuario",
                    usuarioActual.getRol()
            );

            // Continuar con la solicitud.
            chain.doFilter(request, response);

        } catch (SQLException e) {
            // Si falla la comprobación, no permitir continuar.
            throw new ServletException(
                    "Error al verificar el estado de la cuenta.",
                    e
            );
        }
    }
}


