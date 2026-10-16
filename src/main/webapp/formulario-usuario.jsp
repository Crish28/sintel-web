<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ page import="com.sintel.modelo.Usuario" %>
<%@ page import="com.sintel.CsrfUtil" %>

<%
    Usuario usuarioEditar =
            (Usuario) request.getAttribute("usuarioEditar");

    boolean edicion = usuarioEditar != null;

    String nombre = edicion ? usuarioEditar.getNombre() : "";
    String correo = edicion ? usuarioEditar.getCorreo() : "";
    String rol = edicion ? usuarioEditar.getRol() : "Usuario";

    String id = edicion
            ? String.valueOf(usuarioEditar.getIdusuario())
            : "";

    String csrfToken = CsrfUtil.obtenerToken(request);
%>

<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">

    <title><%= edicion ? "Editar usuario" : "Crear usuario" %> | Sintel</title>

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
            max-width: 850px;
            margin: 35px auto;
            padding: 0 24px;
        }

        .encabezado {
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

        .tarjeta {
            background: #FFFFFF;
            padding: 28px;
            border-radius: 12px;
            box-shadow: 0 3px 12px rgba(0, 39, 79, 0.08);
        }

        .formulario {
            display: grid;
            grid-template-columns: 1fr 1fr;
            gap: 22px;
        }

        .campo {
            display: flex;
            flex-direction: column;
            gap: 8px;
        }

        .campo.completo {
            grid-column: 1 / -1;
        }

        .campo label {
            font-weight: bold;
            font-size: 14px;
        }

        .campo input,
        .campo select {
            width: 100%;
            padding: 12px;
            border: 1px solid #CBD5E1;
            border-radius: 7px;
            font-size: 15px;
            color: #00274F;
            background: #FFFFFF;
        }

        .campo input:focus,
        .campo select:focus {
            outline: 2px solid #49CCCC;
            outline-offset: 1px;
        }

        .ayuda {
            color: #667085;
            font-size: 13px;
            line-height: 1.5;
        }

        .botones {
            grid-column: 1 / -1;
            display: flex;
            gap: 12px;
            flex-wrap: wrap;
            margin-top: 5px;
        }

        .boton {
            display: inline-block;
            padding: 12px 18px;
            border: none;
            border-radius: 7px;
            background: #00274F;
            color: #FFFFFF;
            text-decoration: none;
            font-size: 14px;
            font-weight: bold;
            cursor: pointer;
        }

        .boton:hover {
            background: #004078;
        }

        .boton-cancelar {
            background: #E8EDF2;
            color: #00274F;
        }

        .boton-cancelar:hover {
            background: #D7E0E8;
        }

        @media (max-width: 600px) {
            .contenido {
                margin: 24px auto;
                padding: 0 15px;
            }

            .tarjeta {
                padding: 20px;
            }

            .formulario {
                grid-template-columns: 1fr;
            }

            .campo.completo,
            .botones {
                grid-column: 1;
            }
        }
    </style>
</head>

<body>

    <jsp:include page="navbar.jsp" />

    <main class="contenido">

        <header class="encabezado">
            <h1><%= edicion ? "Editar usuario" : "Crear usuario" %></h1>

            <p>
                <%= edicion
                    ? "Actualiza los datos y permisos de la cuenta."
                    : "Registra una nueva cuenta para acceder a Sintel." %>
            </p>
        </header>

        <section class="tarjeta">

            <form class="formulario"
                  action="${pageContext.request.contextPath}/usuarios"
                  method="post">

                <input type="hidden"
                       name="csrfToken"
                       value="<%= csrfToken %>">  

                <input type="hidden"
                       name="accion"
                       value="<%= edicion ? "editar" : "crear" %>">

                <% if (edicion) { %>
                    <input type="hidden"
                           name="id"
                           value="<%= id %>">
                <% } %>

                <div class="campo">
                    <label for="nombre">Nombre completo</label>
                    <input type="text"
                           id="nombre"
                           name="nombre"
                           maxlength="45"
                           value="<%= nombre.replace("&", "&amp;")
                                           .replace("\"", "&quot;")
                                           .replace("<", "&lt;")
                                           .replace(">", "&gt;") %>"
                           required>
                </div>

                <div class="campo">
                    <label for="correo">Correo electrónico</label>
                    <input type="email"
                           id="correo"
                           name="correo"
                           maxlength="45"
                           value="<%= correo.replace("&", "&amp;")
                                           .replace("\"", "&quot;")
                                           .replace("<", "&lt;")
                                           .replace(">", "&gt;") %>"
                           required>
                </div>

                <div class="campo">
                    <label for="rol">Rol de usuario</label>
                    <select id="rol" name="rol" required>
                        <option value="Usuario"
                            <%= "Usuario".equals(rol) ? "selected" : "" %>>
                            Usuario
                        </option>

                        <option value="Administrador"
                            <%= "Administrador".equals(rol) ? "selected" : "" %>>
                            Administrador
                        </option>
                    </select>
                </div>

                <div class="campo">
                    <label for="contraseña">
                        <%= edicion ? "Nueva contraseña (opcional)" : "Contraseña" %>
                    </label>

                    <input type="password"
                           id="contraseña"
                           name="contraseña"
                           minlength="8"
                           autocomplete="<%= edicion ? "new-password" : "new-password" %>"
                           <%= edicion ? "" : "required" %>>

                    <span class="ayuda">
                        <%= edicion
                            ? "Déjalo en blanco para conservar la contraseña actual."
                            : "Debe tener al menos 8 caracteres." %>
                    </span>
                </div>

                <div class="botones">
                    <button type="submit" class="boton">
                        <%= edicion ? "Guardar cambios" : "Crear usuario" %>
                    </button>

                    <a href="${pageContext.request.contextPath}/usuarios"
                       class="boton boton-cancelar">
                        Cancelar
                    </a>
                </div>

            </form>

        </section>
    </main>

</body>
</html>
