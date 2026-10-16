package com.sintel;

import com.sintel.dao.ClienteDAO;
import com.sintel.dao.ProyectoDAO;
import com.sintel.modelo.Cliente;
import com.sintel.modelo.Proyecto;
import com.sintel.dao.EstadoProyectoDAO;
import com.sintel.modelo.EstadoProyecto;
import com.sintel.dao.ConfiguracionDAO;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@WebServlet("/proyectos")
public class ProyectoServlet extends HttpServlet {


@Override
protected void doGet(HttpServletRequest request,
                     HttpServletResponse response)
        throws ServletException, IOException {

    String accion = request.getParameter("accion");
    String filtroEstado = request.getParameter("estado");
    String filtroCliente = request.getParameter("cliente");

    try {

        ProyectoDAO proyectoDAO = new ProyectoDAO();
        ClienteDAO clienteDAO = new ClienteDAO();
        EstadoProyectoDAO estadoProyectoDAO =
                new EstadoProyectoDAO();

        // Obtener la tasa de IVA desde MySQL.
        ConfiguracionDAO configuracionDAO =
                new ConfiguracionDAO();

        double tasaIVA = configuracionDAO.obtenerTasaIVA();

     

        // EDITAR PROYECTO
        if ("editar".equals(accion)) {

            int id = Integer.parseInt(
                    request.getParameter("id")
            );

            Proyecto proyecto =
                    proyectoDAO.buscarPorId(id);

            if (proyecto == null) {
                response.sendError(
                        HttpServletResponse.SC_NOT_FOUND,
                        "Proyecto no encontrado."
                );
                return;
            }

            List<Cliente> clientes =
                    clienteDAO.listarTodos();

            List<EstadoProyecto> estados =
                    estadoProyectoDAO.listarTodos();

            request.setAttribute("proyecto", proyecto);
            request.setAttribute("clientes", clientes);
            request.setAttribute("estados", estados);
            request.setAttribute("tasaIVA", tasaIVA);

            request.getRequestDispatcher(
                    "/editar-proyecto.jsp"
            ).forward(request, response);

            return;
        }

        // MOSTRAR TODOS LOS PROYECTOS O APLICAR FILTROS

        Integer idEstado = null;
        Integer idCliente = null;

        if (filtroEstado != null
                && !filtroEstado.isEmpty()) {

            idEstado = Integer.parseInt(filtroEstado);
        }

        if (filtroCliente != null
                && !filtroCliente.isEmpty()) {

            idCliente = Integer.parseInt(filtroCliente);
        }

        List<Proyecto> proyectos;

        if (idEstado != null || idCliente != null) {

            proyectos = proyectoDAO.listarPorFiltros(
                    idEstado,
                    idCliente
            );

        } else {

            proyectos = proyectoDAO.listarTodos();
        }

        List<Cliente> clientes =
                clienteDAO.listarTodos();

        List<EstadoProyecto> estados =
                estadoProyectoDAO.listarTodos();

        request.setAttribute("proyectos", proyectos);
        request.setAttribute("clientes", clientes);
        request.setAttribute("estados", estados);
        request.setAttribute("tasaIVA", tasaIVA);

        request.getRequestDispatcher(
                "/proyectos.jsp"
        ).forward(request, response);

    } catch (SQLException e) {

        throw new ServletException(
                "Error al gestionar los proyectos.",
                e
        );

    } catch (NumberFormatException e) {

        throw new ServletException(
                "El ID o los filtros del proyecto no son válidos.",
                e
        );
    }
}



@Override
protected void doPost(HttpServletRequest request,
                      HttpServletResponse response)
        throws ServletException, IOException {

    request.setCharacterEncoding("UTF-8");

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

    if ("eliminar".equals(accion)) {
        try {
              String idTexto = request.getParameter("id");
              int id = Integer.parseInt(idTexto);

              ProyectoDAO proyectoDAO = new ProyectoDAO();
              proyectoDAO.eliminar(id);

              response.sendRedirect(
                     request.getContextPath() + "/proyectos"
              );
        } catch (NumberFormatException e) {
            response.sendError(
                    HttpServletResponse.SC_BAD_REQUEST,
                    "El identificador del proyecto no es válido."
         );
        } catch (SQLException e) {
            throw new ServletException(
                "Error al eliminar el proyecto.",
                      e
           );
        }
        return;
    }

    try {
        String idParametro = request.getParameter("id");

        String nombre = request.getParameter("nombre");

        int idclientes = Integer.parseInt(
                request.getParameter("idclientes")
        );

        int idestados = Integer.parseInt(
                request.getParameter("idestados")
        );

        java.time.LocalDate fechaEntrega =
                java.time.LocalDate.parse(
                        request.getParameter("fechaEntrega")
                );

        // El valor introducido es la base antes del IVA.
        java.math.BigDecimal valorTotal =
                new java.math.BigDecimal(
                        request.getParameter("valorTotal")
                );

        // Validar que la base sea positiva.
        if (valorTotal.compareTo(java.math.BigDecimal.ZERO) <= 0) {
            response.sendRedirect(
                    request.getContextPath()
                    + "/proyectos?error=valor"
            );
            return;
        }

        ProyectoDAO proyectoDAO = new ProyectoDAO();

        boolean esEdicion = idParametro != null
                && !idParametro.isEmpty();

        // Solo se rechazan fechas pasadas al registrar.
        if (!esEdicion
                && fechaEntrega.isBefore(
                        java.time.LocalDate.now())) {

            response.sendRedirect(
                    request.getContextPath()
                    + "/proyectos?error=fecha"
            );
            return;
        }

        // Obtener la tasa desde la base de datos.
        ConfiguracionDAO configuracionDAO =
                new ConfiguracionDAO();

        java.math.BigDecimal tasaIVA =
                java.math.BigDecimal.valueOf(
                        configuracionDAO.obtenerTasaIVA()
                );

        // Calcular el IVA con dos decimales.
        java.math.BigDecimal iva =
                valorTotal.multiply(tasaIVA)
                        .divide(
                                new java.math.BigDecimal("100"),
                                2,
                                java.math.RoundingMode.HALF_UP
                        );

        Proyecto proyecto = new Proyecto(
                nombre,
                idclientes,
                idestados,
                fechaEntrega,
                valorTotal,
                iva
        );

        if (esEdicion) {
            int id = Integer.parseInt(idParametro);
            proyecto.setIdproyectos(id);
            proyectoDAO.actualizar(proyecto);
        } else {
            proyectoDAO.registrar(proyecto);
        }

        response.sendRedirect(
                request.getContextPath() + "/proyectos"
        );

    } catch (SQLException e) {
        throw new ServletException(
                "Error al guardar el proyecto.",
                e
        );

    } catch (NumberFormatException e) {
        throw new ServletException(
                "Los valores numéricos no son válidos.",
                e
        );

    } catch (java.time.format.DateTimeParseException e) {
        throw new ServletException(
                "La fecha de entrega no es válida.",
                e
        );
    }
}


}