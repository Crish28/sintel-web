<%@ page language="java"
         contentType="text/html; charset=UTF-8"
         pageEncoding="UTF-8" %>

<%@ page import="java.util.List" %>
<%@ page import="com.sintel.modelo.Cliente" %>
<%@ page import="com.sintel.CsrfUtil" %>
<%@ page import="com.sintel.HtmlUtil" %>

<%
    String csrfToken = CsrfUtil.obtenerToken(request);
    String error = (String) request.getAttribute("error");

    List<Cliente> clientes =
            (List<Cliente>) request.getAttribute("clientes");

    String nombreIngresado =
            (String) request.getAttribute("nombreIngresado");
    String correoIngresado =
            (String) request.getAttribute("correoIngresado");
    String telefonoIngresado =
            (String) request.getAttribute("telefonoIngresado");
    String direccionIngresada =
            (String) request.getAttribute("direccionIngresada");

    if (nombreIngresado == null) nombreIngresado = "";
    if (correoIngresado == null) correoIngresado = "";
    if (telefonoIngresado == null) telefonoIngresado = "";
    if (direccionIngresada == null) direccionIngresada = "";
%>

<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Clientes | Sintel</title>

    <style>
        :root {
            --azul-principal: #00274F;
            --azul-hover: #003B70;
            --turquesa: #49CCCC;
            --fondo: #F4F7FB;
            --blanco: #FFFFFF;
            --texto: #23364A;
            --texto-secundario: #64748B;
            --borde: #DCE4ED;
            --rojo: #B8201F;
            --rojo-fondo: #FCE8E7;
            --turquesa-fondo: #EAFafa;
            --sombra: 0 8px 24px rgba(0, 39, 79, 0.06);
        }

        * { box-sizing: border-box; }

        body {
            margin: 0;
            background: var(--fondo);
            color: var(--texto);
            font-family: Arial, Helvetica, sans-serif;
            line-height: 1.5;
        }

        .contenido-principal {
            width: 100%;
            padding: 32px 24px 48px;
        }

        .contenedor {
            width: 100%;
            max-width: 1240px;
            margin: 0 auto;
        }

        .encabezado-pagina {
            display: flex;
            align-items: flex-start;
            justify-content: space-between;
            gap: 20px;
            margin-bottom: 26px;
        }

        .sobrelinea {
            margin: 0 0 7px;
            color: #168C95;
            font-size: 12px;
            font-weight: 700;
            letter-spacing: 1.2px;
            text-transform: uppercase;
        }

        h1 {
            margin: 0;
            color: var(--azul-principal);
            font-size: clamp(26px, 3vw, 34px);
            line-height: 1.2;
        }

        .descripcion-pagina {
            margin: 10px 0 0;
            color: var(--texto-secundario);
            font-size: 15px;
        }

        .resumen-clientes {
            display: flex;
            align-items: center;
            gap: 13px;
            min-width: 190px;
            padding: 16px 19px;
            background: var(--blanco);
            border: 1px solid var(--borde);
            border-radius: 12px;
            box-shadow: var(--sombra);
        }

        .icono-resumen {
            display: grid;
            place-items: center;
            width: 44px;
            height: 44px;
            flex: 0 0 44px;
            border-radius: 11px;
            background: #E5F9F9;
            color: var(--azul-principal);
            font-size: 21px;
            font-weight: 700;
        }

        .resumen-etiqueta {
            display: block;
            color: var(--texto-secundario);
            font-size: 12px;
        }

        .resumen-valor {
            display: block;
            color: var(--azul-principal);
            font-size: 23px;
            font-weight: 700;
            line-height: 1.2;
        }

        .mensaje-error {
            margin: 0 0 22px;
            padding: 14px 16px;
            border: 1px solid #E8B8B6;
            border-left: 4px solid var(--rojo);
            border-radius: 9px;
            background: var(--rojo-fondo);
            color: var(--rojo);
            font-size: 14px;
        }

        .panel {
            margin-bottom: 25px;
            overflow: hidden;
            border: 1px solid var(--borde);
            border-radius: 14px;
            background: var(--blanco);
            box-shadow: var(--sombra);
        }

        .panel-encabezado {
            display: flex;
            align-items: flex-start;
            gap: 12px;
            padding: 22px 24px;
            border-bottom: 1px solid var(--borde);
        }

        .panel-icono {
            display: grid;
            place-items: center;
            width: 38px;
            height: 38px;
            flex: 0 0 38px;
            border-radius: 10px;
            background: #E5F9F9;
            color: var(--azul-principal);
            font-size: 20px;
            font-weight: 700;
        }

        .panel-encabezado h2 {
            margin: 0;
            color: var(--azul-principal);
            font-size: 18px;
        }

        .panel-encabezado p {
            margin: 4px 0 0;
            color: var(--texto-secundario);
            font-size: 13px;
        }

        .formulario-contenido {
            padding: 24px;
        }

        .campos-grid {
            display: grid;
            grid-template-columns: repeat(2, minmax(0, 1fr));
            gap: 20px 22px;
        }

        .grupo-campo {
            min-width: 0;
        }

        .grupo-campo label {
            display: block;
            margin-bottom: 7px;
            color: #263B50;
            font-size: 13px;
            font-weight: 700;
        }

        .campo-opcional {
            color: var(--texto-secundario);
            font-size: 12px;
            font-weight: 400;
        }

        .grupo-campo input {
            display: block;
            width: 100%;
            min-height: 44px;
            padding: 11px 13px;
            border: 1px solid #CBD5E1;
            border-radius: 8px;
            background: #FFFFFF;
            color: var(--azul-principal);
            font-family: inherit;
            font-size: 14px;
            transition: border-color .15s ease, box-shadow .15s ease;
        }

        .grupo-campo input::placeholder {
            color: #9AA8B8;
        }

        .grupo-campo input:focus {
            outline: none;
            border-color: var(--turquesa);
            box-shadow: 0 0 0 3px rgba(73, 204, 204, .2);
        }

        .formulario-pie {
            display: flex;
            align-items: center;
            justify-content: flex-end;
            gap: 12px;
            margin-top: 24px;
            padding-top: 20px;
            border-top: 1px solid #EDF1F5;
        }

        .boton-principal {
            display: inline-flex;
            align-items: center;
            justify-content: center;
            gap: 9px;
            min-height: 44px;
            padding: 11px 19px;
            border: 0;
            border-radius: 8px;
            background: var(--azul-principal);
            color: #FFFFFF;
            font-family: inherit;
            font-size: 14px;
            font-weight: 700;
            cursor: pointer;
            transition: background .15s ease, transform .15s ease;
        }

        .boton-principal:hover {
            background: var(--azul-hover);
        }

        .boton-principal:active {
            transform: translateY(1px);
        }

        .panel-tabla {
            margin-top: 25px;
        }

        .encabezado-tabla {
            display: flex;
            align-items: center;
            justify-content: space-between;
            gap: 16px;
            padding: 22px 24px;
            border-bottom: 1px solid var(--borde);
        }

        .encabezado-tabla h2 {
            margin: 0;
            color: var(--azul-principal);
            font-size: 18px;
        }

        .encabezado-tabla p {
            margin: 4px 0 0;
            color: var(--texto-secundario);
            font-size: 13px;
        }

        .contador-tabla {
            display: inline-flex;
            align-items: center;
            justify-content: center;
            min-width: 34px;
            padding: 5px 10px;
            border: 1px solid #BDECEC;
            border-radius: 20px;
            background: #EAFafa;
            color: var(--azul-principal);
            font-size: 12px;
            font-weight: 700;
        }

        .tabla-contenedor {
            width: 100%;
            overflow-x: auto;
        }

        table {
            width: 100%;
            border-collapse: collapse;
            background: var(--blanco);
        }

        thead th {
            padding: 13px 16px;
            background: #F3F6FA;
            color: #536579;
            font-size: 11px;
            font-weight: 700;
            letter-spacing: .45px;
            text-align: left;
            text-transform: uppercase;
            white-space: nowrap;
        }

        tbody td {
            padding: 15px 16px;
            border-top: 1px solid #E9EEF4;
            color: #35485C;
            font-size: 13px;
            vertical-align: middle;
        }

        tbody tr {
            transition: background .12s ease;
        }

        tbody tr:hover {
            background: #F7FCFC;
        }

        .id-cliente {
            color: var(--texto-secundario);
            font-size: 12px;
            font-weight: 700;
        }

        .nombre-cliente {
            color: var(--azul-principal);
            font-weight: 700;
        }

        .correo-cliente {
            overflow-wrap: anywhere;
        }

        .texto-vacio {
            color: #9AA8B8;
            font-style: italic;
        }

        .acciones {
            white-space: nowrap;
        }

        .enlace-accion,
        .boton-eliminar {
            display: inline-flex;
            align-items: center;
            min-height: 32px;
            padding: 6px 9px;
            border-radius: 6px;
            font-family: inherit;
            font-size: 12px;
            font-weight: 700;
            text-decoration: none;
            cursor: pointer;
        }

        .enlace-accion {
            border: 1px solid #C9EDED;
            background: #EFFBFB;
            color: var(--azul-principal);
        }

        .enlace-accion:hover {
            border-color: var(--turquesa);
            background: #DDF7F7;
        }

        .formulario-eliminar {
            display: inline;
            margin: 0;
            padding: 0;
        }

        .boton-eliminar {
            border: 1px solid #F0C9C7;
            background: #FFF5F4;
            color: var(--rojo);
        }

        .boton-eliminar:hover {
            border-color: var(--rojo);
            background: var(--rojo-fondo);
        }

        .estado-vacio {
            padding: 42px 20px;
            text-align: center;
        }

        .estado-vacio-icono {
            display: grid;
            place-items: center;
            width: 54px;
            height: 54px;
            margin: 0 auto 14px;
            border-radius: 16px;
            background: #EAFafa;
            color: var(--azul-principal);
            font-size: 25px;
            font-weight: 700;
        }

        .estado-vacio h3 {
            margin: 0 0 7px;
            color: var(--azul-principal);
            font-size: 16px;
        }

        .estado-vacio p {
            max-width: 390px;
            margin: 0 auto;
            color: var(--texto-secundario);
            font-size: 13px;
        }

        .nota-tabla {
            padding: 12px 24px;
            border-top: 1px solid #EDF1F5;
            color: var(--texto-secundario);
            font-size: 12px;
        }

        @media (max-width: 760px) {
            .contenido-principal {
                padding: 24px 16px 36px;
            }

            .encabezado-pagina {
                flex-direction: column;
            }

            .resumen-clientes {
                width: 100%;
                min-width: 0;
            }

            .campos-grid {
                grid-template-columns: minmax(0, 1fr);
                gap: 16px;
            }

            .panel-encabezado,
            .formulario-contenido,
            .encabezado-tabla {
                padding: 18px;
            }

            .formulario-pie {
                justify-content: stretch;
            }

            .boton-principal {
                width: 100%;
            }

            thead th,
            tbody td {
                padding: 12px;
            }
        }

        @media (prefers-reduced-motion: reduce) {
            *, *::before, *::after {
                transition: none !important;
                scroll-behavior: auto !important;
            }
        }
    </style>
</head>

<body>

    <jsp:include page="navbar.jsp" />

    <main class="contenido-principal">
        <div class="contenedor">

            <header class="encabezado-pagina">
                <div>
                    <p class="sobrelinea">Administración</p>
                    <h1>Gestión de clientes</h1>
                    <p class="descripcion-pagina">
                        Registra, consulta y administra la información de tus clientes.
                    </p>
                </div>

                <div class="resumen-clientes" aria-label="Total de clientes registrados">
                    <div class="icono-resumen" aria-hidden="true">&#9823;</div>
                    <div>
                        <span class="resumen-etiqueta">Clientes registrados</span>
                        <span class="resumen-valor"><%= clientes == null ? 0 : clientes.size() %></span>
                    </div>
                </div>
            </header>

            <% if (error != null) { %>
                <div class="mensaje-error" role="alert">
                    <%= HtmlUtil.escapar(error) %>
                </div>
            <% } %>

            <section class="panel" aria-labelledby="titulo-registro">
                <div class="panel-encabezado">
                    <div class="panel-icono" aria-hidden="true">+</div>
                    <div>
                        <h2 id="titulo-registro">Registrar nuevo cliente</h2>
                        <p>Completa los datos para agregar un cliente al sistema.</p>
                    </div>
                </div>

                <div class="formulario-contenido">
                    <form action="${pageContext.request.contextPath}/clientes" method="POST">
                        <input type="hidden"
                               name="csrfToken"
                               value="<%= HtmlUtil.escapar(csrfToken) %>">

                        <!-- Indica al servlet que debe registrar un cliente -->
                        <input type="hidden" name="accion" value="guardar">

                        <div class="campos-grid">
                            <div class="grupo-campo">
                                <label for="nombre">Nombre completo</label>
                                <input type="text"
                                       id="nombre"
                                       name="nombre"
                                       maxlength="50"
                                       autocomplete="name"
                                       placeholder="Ej. María Rodríguez"
                                       value="<%= HtmlUtil.escapar(nombreIngresado) %>"
                                       required>
                            </div>

                            <div class="grupo-campo">
                                <label for="correo">Correo electrónico</label>
                                <input type="email"
                                       id="correo"
                                       name="correo"
                                       maxlength="50"
                                       autocomplete="email"
                                       placeholder="Ej. maria@correo.com"
                                       value="<%= HtmlUtil.escapar(correoIngresado) %>"
                                       required>
                            </div>

                            <div class="grupo-campo">
                                <label for="telefono">Teléfono</label>
                                <input type="tel"
                                       id="telefono"
                                       name="telefono"
                                       maxlength="50"
                                       autocomplete="tel"
                                       placeholder="Ej. 300 123 4567"
                                       value="<%= HtmlUtil.escapar(telefonoIngresado) %>"
                                       required>
                            </div>

                            <div class="grupo-campo">
                                <label for="direccion">
                                    Dirección <span class="campo-opcional">(opcional)</span>
                                </label>
                                <input type="text"
                                       id="direccion"
                                       name="direccion"
                                       maxlength="150"
                                       autocomplete="street-address"
                                       placeholder="Ej. Calle 10 # 20-30"
                                       value="<%= HtmlUtil.escapar(direccionIngresada) %>">
                            </div>
                        </div>

                        <div class="formulario-pie">
                            <button type="submit" class="boton-principal">
                                <span aria-hidden="true">+</span>
                                Registrar cliente
                            </button>
                        </div>
                    </form>
                </div>
            </section>

            <section class="panel panel-tabla" aria-labelledby="titulo-clientes">
                <div class="encabezado-tabla">
                    <div>
                        <h2 id="titulo-clientes">Clientes registrados</h2>
                        <p>Consulta la información y las acciones disponibles para cada cliente.</p>
                    </div>
                    <span class="contador-tabla"><%= clientes == null ? 0 : clientes.size() %> total</span>
                </div>

                <% if (clientes != null && !clientes.isEmpty()) { %>
                    <div class="tabla-contenedor">
                        <table>
                            <thead>
                                <tr>
                                    <th>ID</th>
                                    <th>Nombre</th>
                                    <th>Correo electrónico</th>
                                    <th>Teléfono</th>
                                    <th>Dirección</th>
                                    <th>Fecha de registro</th>
                                    <th>Acciones</th>
                                </tr>
                            </thead>

                            <tbody>
                                <% for (Cliente cliente : clientes) { %>
                                    <tr>
                                        <td class="id-cliente">
                                            #<%= cliente.getIdclientes() %>
                                        </td>

                                        <td class="nombre-cliente">
                                            <%= HtmlUtil.escapar(cliente.getNombre()) %>
                                        </td>

                                        <td class="correo-cliente">
                                            <%= HtmlUtil.escapar(cliente.getCorreo()) %>
                                        </td>

                                        <td>
                                            <%= HtmlUtil.escapar(cliente.getTelefono()) %>
                                        </td>

                                        <td>
                                            <% if (cliente.getDireccion() != null
                                                    && !cliente.getDireccion().trim().isEmpty()) { %>
                                                <%= HtmlUtil.escapar(cliente.getDireccion()) %>
                                            <% } else { %>
                                                <span class="texto-vacio">Sin dirección</span>
                                            <% } %>
                                        </td>

                                        <td>
                                            <%= cliente.getFechaRegistro() == null
                                                    ? "—"
                                                    : HtmlUtil.escapar(
                                                        cliente.getFechaRegistro().toString()) %>
                                        </td>

                                        <td class="acciones">
                                            <a class="enlace-accion"
                                               href="${pageContext.request.contextPath}/clientes?accion=editar&amp;id=<%= cliente.getIdclientes() %>">
                                                Editar
                                            </a>

                                            <form action="${pageContext.request.contextPath}/clientes"
                                                  method="POST"
                                                  class="formulario-eliminar"
                                                  onsubmit="return confirm('¿Está seguro de eliminar este cliente?');">

                                                <input type="hidden"
                                                       name="csrfToken"
                                                       value="<%= HtmlUtil.escapar(csrfToken) %>">

                                                <input type="hidden"
                                                       name="accion"
                                                       value="eliminar">

                                                <input type="hidden"
                                                       name="id"
                                                       value="<%= cliente.getIdclientes() %>">

                                                <button type="submit" class="boton-eliminar">
                                                    Eliminar
                                                </button>
                                            </form>
                                        </td>
                                    </tr>
                                <% } %>
                            </tbody>
                        </table>
                    </div>

                    <div class="nota-tabla">
                        Para editar o eliminar un registro, utiliza las acciones de la última columna.
                    </div>
                <% } else { %>
                    <div class="estado-vacio">
                        <div class="estado-vacio-icono" aria-hidden="true">&#9823;</div>
                        <h3>Aún no hay clientes registrados</h3>
                        <p>Cuando registres un cliente con el formulario, su información aparecerá aquí.</p>
                    </div>
                <% } %>
            </section>

        </div>
    </main>

</body>
</html>


