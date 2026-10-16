<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ page import="java.util.List" %>
<%@ page import="com.sintel.modelo.Usuario" %>
<%@ page import="com.sintel.CsrfUtil" %>

<%!
    private String escaparHTML(String texto) {
        if (texto == null) return "";
        return texto.replace("&", "&amp;")
                    .replace("<", "&lt;")
                    .replace(">", "&gt;")
                    .replace("\"", "&quot;")
                    .replace("'", "&#39;");
    }
%>

<%
    List<Usuario> usuarios =
            (List<Usuario>) request.getAttribute("usuarios");

    String error = (String) request.getAttribute("error");
    String resultado = request.getParameter("resultado");

    String csrfToken = CsrfUtil.obtenerToken(request);
%>

<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Gestión de usuarios | Sintel</title>

    <style>
        * {
            box-sizing: border-box;
        }

        body {
            margin: 0;
            font-family: Arial, Helvetica, sans-serif;
            background: #F5F7FA;
            color: #00274F;
        }

        .contenido {
            max-width: 1200px;
            margin: 35px auto;
            padding: 0 24px;
        }

        .encabezado {
            display: flex;
            justify-content: space-between;
            align-items: center;
            gap: 20px;
            flex-wrap: wrap;
            margin-bottom: 25px;
        }

        .encabezado h1 {
            margin: 0 0 8px;
            font-size: 28px;
        }

        .encabezado p {
            margin: 0;
            color: #667085;
            line-height: 1.5;
        }

        .boton {
            display: inline-block;
            padding: 11px 17px;
            border: none;
            border-radius: 7px;
            background: #00274F;
            color: #FFFFFF;
            text-decoration: none;
            font-size: 14px;
            font-weight: bold;
            cursor: pointer;
            transition: background 0.2s;
        }

        .boton:hover {
            background: #004078;
        }

        .boton-secundario {
            background: #49CCCC;
            color: #00274F;
        }

        .boton-secundario:hover {
            background: #2DB7B7;
        }

        .tarjeta {
            background: #FFFFFF;
            border-radius: 12px;
            padding: 24px;
            box-shadow: 0 3px 12px rgba(0, 39, 79, 0.08);
        }

        .tarjeta h2 {
            margin: 0 0 20px;
            font-size: 20px;
        }

        .mensaje {
            padding: 13px 16px;
            border-radius: 7px;
            margin-bottom: 20px;
            line-height: 1.5;
        }

        .exito {
            background: #DDF7E8;
            color: #166534;
            border: 1px solid #86D6A5;
        }

        .error {
            background: #FDE8E7;
            color: #991B1B;
            border: 1px solid #E8A5A1;
        }

        .tabla-contenedor {
            width: 100%;
            overflow-x: auto;
        }

        table {
            width: 100%;
            border-collapse: collapse;
            min-width: 760px;
        }

        th, td {
            padding: 15px 12px;
            text-align: left;
            border-bottom: 1px solid #E5EAF0;
        }

        th {
            background: #F0F5F9;
            color: #00274F;
            font-size: 13px;
            text-transform: uppercase;
            letter-spacing: 0.3px;
        }

        td {
            font-size: 14px;
            vertical-align: middle;
        }

        tbody tr:hover {
            background: #F8FBFD;
        }

        .etiqueta {
            display: inline-block;
            padding: 6px 10px;
            border-radius: 20px;
            font-size: 12px;
            font-weight: bold;
            white-space: nowrap;
        }

        .activo {
            background: #DDF7E8;
            color: #166534;
        }

        .inactivo {
            background: #FDE8E7;
            color: #991B1B;
        }

        .rol-admin {
            background: #E0EAF5;
            color: #00274F;
        }

        .rol-usuario {
            background: #E4F8F8;
            color: #176B6B;
        }

        .acciones {
            display: flex;
            align-items: center;
            gap: 8px;
            flex-wrap: wrap;
        }

        .acciones .boton {
            padding: 8px 11px;
            font-size: 12px;
        }

        .boton-desactivar {
            background: #B8201F;
        }

        .boton-desactivar:hover {
            background: #921A19;
        }

        .boton-activar {
            background: #167647;
        }

        .boton-activar:hover {
            background: #105B35;
        }

        .form-estado {
            margin: 0;
        }

        .vacio {
            padding: 30px;
            text-align: center;
            color: #667085;
        }

        @media (max-width: 600px) {
            .contenido {
                margin: 24px auto;
                padding: 0 15px;
            }

            .tarjeta {
                padding: 16px;
            }

            .encabezado h1 {
                font-size: 24px;
            }
        }
    </style>
</head>

<body>

    <jsp:include page="navbar.jsp" />

    <main class="contenido">

        <header class="encabezado">
            <div>
                <h1>Gestión de usuarios</h1>
                <p>Administra las cuentas y los permisos de acceso a Sintel.</p>
            </div>

            <a class="boton"
               href="${pageContext.request.contextPath}/usuarios?accion=nuevo">
                + Crear usuario
            </a>
        </header>

        <% if (error != null) { %>
            <div class="mensaje error" role="alert">
                <%= escaparHTML(error) %>
            </div>
        <% } %>

        <% if ("creado".equals(resultado)) { %>
            <div class="mensaje exito" role="status">
                El usuario se registró correctamente.
            </div>
        <% } else if ("actualizado".equals(resultado)) { %>
            <div class="mensaje exito" role="status">
                Los datos del usuario se actualizaron correctamente.
            </div>
        <% } else if ("activado".equals(resultado)) { %>
            <div class="mensaje exito" role="status">
                La cuenta se activó correctamente.
            </div>
        <% } else if ("desactivado".equals(resultado)) { %>
            <div class="mensaje exito" role="status">
                La cuenta se desactivó correctamente.
            </div>
        <% } %>

        <section class="tarjeta">
            <h2>Usuarios registrados</h2>

            <div class="tabla-contenedor">
                <table>
                    <thead>
                        <tr>
                            <th>ID</th>
                            <th>Nombre</th>
                            <th>Correo electrónico</th>
                            <th>Rol</th>
                            <th>Estado</th>
                            <th>Acciones</th>
                        </tr>
                    </thead>

                    <tbody>
                        <% if (usuarios != null && !usuarios.isEmpty()) { %>

                            <% for (Usuario usuario : usuarios) { %>
                                <tr>
                                    <td><%= usuario.getIdusuario() %></td>

                                    <td>
                                        <%= escaparHTML(usuario.getNombre()) %>
                                    </td>

                                    <td>
                                        <%= escaparHTML(usuario.getCorreo()) %>
                                    </td>

                                    <td>
                                        <% if ("Administrador".equals(usuario.getRol())) { %>
                                            <span class="etiqueta rol-admin">
                                                Administrador
                                            </span>
                                        <% } else { %>
                                            <span class="etiqueta rol-usuario">
                                                Usuario
                                            </span>
                                        <% } %>
                                    </td>

                                    <td>
                                        <% if (usuario.isActivo()) { %>
                                            <span class="etiqueta activo">
                                                Activo
                                            </span>
                                        <% } else { %>
                                            <span class="etiqueta inactivo">
                                                Inactivo
                                            </span>
                                        <% } %>
                                    </td>

                                    <td>
                                        <div class="acciones">
                                            <a class="boton boton-secundario"
                                               href="${pageContext.request.contextPath}/usuarios?accion=editar&amp;id=<%= usuario.getIdusuario() %>">
                                                Editar
                                            </a>

                                            <form class="form-estado"
                                                  action="${pageContext.request.contextPath}/usuarios"
                                                  method="post"
                                                  onsubmit="return confirm('¿Estás seguro de cambiar el estado de esta cuenta?');">

                                                  <input type="hidden"
                                                         name="csrfToken"
                                                         value="<%= csrfToken %>">

                                                <input type="hidden"
                                                       name="accion"
                                                       value="estado">

                                                <input type="hidden"
                                                       name="id"
                                                       value="<%= usuario.getIdusuario() %>">

                                                <input type="hidden"
                                                       name="activo"
                                                       value="<%= usuario.isActivo() ? "0" : "1" %>">

                                                <% if (usuario.isActivo()) { %>
                                                    <button type="submit"
                                                            class="boton boton-desactivar">
                                                        Desactivar
                                                    </button>
                                                <% } else { %>
                                                    <button type="submit"
                                                            class="boton boton-activar">
                                                        Activar
                                                    </button>
                                                <% } %>
                                            </form>
                                        </div>
                                    </td>
                                </tr>
                            <% } %>

                        <% } else { %>
                            <tr>
                                <td colspan="6" class="vacio">
                                    No hay usuarios registrados.
                                </td>
                            </tr>
                        <% } %>
                    </tbody>
                </table>
            </div>
        </section>

    </main>

</body>
</html>
