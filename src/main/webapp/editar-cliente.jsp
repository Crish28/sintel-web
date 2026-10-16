<%@ page language="java"
         contentType="text/html; charset=UTF-8"
         pageEncoding="UTF-8" %>

<%@ page import="com.sintel.modelo.Cliente" %>
<%@ page import="com.sintel.CsrfUtil" %>
<%@ page import="com.sintel.HtmlUtil" %>

<%
    Cliente cliente = (Cliente) request.getAttribute("cliente");
    String error = (String) request.getAttribute("error");
    String csrfToken = CsrfUtil.obtenerToken(request);
%>

<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Editar cliente | Sintel</title>

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

        .contenido {
            width: 100%;
            padding: 32px 24px 48px;
        }

        .contenedor {
            width: 100%;
            max-width: 900px;
            margin: 0 auto;
        }

        .miga-pan {
            margin-bottom: 17px;
            color: var(--texto-secundario);
            font-size: 13px;
        }

        .miga-pan a {
            color: var(--azul-principal);
            font-weight: 700;
            text-decoration: none;
        }

        .miga-pan a:hover { text-decoration: underline; }

        .encabezado-pagina {
            display: flex;
            align-items: flex-start;
            gap: 18px;
            margin-bottom: 25px;
        }

        .icono-encabezado {
            display: grid;
            place-items: center;
            width: 52px;
            height: 52px;
            flex: 0 0 52px;
            border-radius: 13px;
            background: #E5F9F9;
            color: var(--azul-principal);
            font-size: 25px;
            font-weight: 700;
        }

        .sobrelinea {
            margin: 0 0 6px;
            color: #168C95;
            font-size: 12px;
            font-weight: 700;
            letter-spacing: 1.2px;
            text-transform: uppercase;
        }

        h1 {
            margin: 0;
            color: var(--azul-principal);
            font-size: clamp(26px, 3vw, 32px);
            line-height: 1.2;
        }

        .descripcion-pagina {
            margin: 9px 0 0;
            color: var(--texto-secundario);
            font-size: 14px;
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

        .tarjeta-formulario {
            overflow: hidden;
            border: 1px solid var(--borde);
            border-radius: 14px;
            background: var(--blanco);
            box-shadow: var(--sombra);
        }

        .tarjeta-encabezado {
            display: flex;
            align-items: flex-start;
            justify-content: space-between;
            gap: 16px;
            padding: 22px 26px;
            border-bottom: 1px solid var(--borde);
        }

        .tarjeta-encabezado h2 {
            margin: 0;
            color: var(--azul-principal);
            font-size: 18px;
        }

        .tarjeta-encabezado p {
            margin: 5px 0 0;
            color: var(--texto-secundario);
            font-size: 13px;
        }

        .identificador-cliente {
            display: inline-flex;
            align-items: center;
            flex-shrink: 0;
            padding: 6px 11px;
            border: 1px solid #BDECEC;
            border-radius: 20px;
            background: #EAFafa;
            color: var(--azul-principal);
            font-size: 12px;
            font-weight: 700;
        }

        .formulario-contenido { padding: 26px; }

        .nota-campos {
            margin: 0 0 23px;
            color: var(--texto-secundario);
            font-size: 13px;
        }

        .campos-grid {
            display: grid;
            grid-template-columns: repeat(2, minmax(0, 1fr));
            gap: 21px 22px;
        }

        .grupo-campo { min-width: 0; }

        .grupo-campo label {
            display: block;
            margin-bottom: 7px;
            color: #263B50;
            font-size: 13px;
            font-weight: 700;
        }

        .grupo-campo input {
            display: block;
            width: 100%;
            min-height: 45px;
            padding: 11px 13px;
            border: 1px solid #CBD5E1;
            border-radius: 8px;
            background: #FFFFFF;
            color: var(--azul-principal);
            font-family: inherit;
            font-size: 14px;
            transition: border-color .15s ease, box-shadow .15s ease;
        }

        .grupo-campo input:focus {
            outline: none;
            border-color: var(--turquesa);
            box-shadow: 0 0 0 3px rgba(73, 204, 204, .2);
        }

        .ayuda-campo {
            display: block;
            margin-top: 6px;
            color: var(--texto-secundario);
            font-size: 11px;
        }

        .separador {
            height: 1px;
            margin: 27px 0 21px;
            border: 0;
            background: #EDF1F5;
        }

        .acciones {
            display: flex;
            align-items: center;
            justify-content: space-between;
            gap: 12px;
        }

        .boton {
            display: inline-flex;
            align-items: center;
            justify-content: center;
            gap: 8px;
            min-height: 44px;
            padding: 11px 18px;
            border: 1px solid transparent;
            border-radius: 8px;
            font-family: inherit;
            font-size: 14px;
            font-weight: 700;
            text-align: center;
            text-decoration: none;
            cursor: pointer;
            transition: background .15s ease, border-color .15s ease, transform .15s ease;
        }

        .boton:active { transform: translateY(1px); }

        .boton-guardar {
            background: var(--azul-principal);
            color: #FFFFFF;
        }

        .boton-guardar:hover { background: var(--azul-hover); }

        .boton-volver {
            border-color: #CBD5E1;
            background: #FFFFFF;
            color: var(--azul-principal);
        }

        .boton-volver:hover {
            border-color: #9FB0C1;
            background: #F5F8FB;
        }

        .mensaje-no-encontrado {
            padding: 30px;
            border: 1px solid var(--borde);
            border-radius: 14px;
            background: var(--blanco);
            box-shadow: var(--sombra);
        }

        .mensaje-no-encontrado h2 {
            margin: 0 0 8px;
            color: var(--azul-principal);
            font-size: 20px;
        }

        .mensaje-no-encontrado p {
            margin: 0 0 22px;
            color: var(--texto-secundario);
            font-size: 14px;
        }

        @media (max-width: 650px) {
            .contenido { padding: 24px 16px 36px; }

            .encabezado-pagina { gap: 12px; }

            .icono-encabezado {
                width: 44px;
                height: 44px;
                flex-basis: 44px;
                font-size: 21px;
            }

            .tarjeta-encabezado,
            .formulario-contenido { padding: 19px; }

            .tarjeta-encabezado {
                flex-direction: column;
            }

            .campos-grid { grid-template-columns: minmax(0, 1fr); }

            .acciones {
                flex-direction: column-reverse;
                align-items: stretch;
            }

            .boton { width: 100%; }
        }

        @media (prefers-reduced-motion: reduce) {
            *, *::before, *::after { transition: none !important; }
        }
    </style>
</head>

<body>

    <jsp:include page="navbar.jsp" />

    <main class="contenido">
        <div class="contenedor">

            <nav class="miga-pan" aria-label="Ruta de navegación">
                <a href="${pageContext.request.contextPath}/clientes">Clientes</a>
                <span aria-hidden="true"> / </span>
                <span>Editar cliente</span>
            </nav>

            <header class="encabezado-pagina">
                <div class="icono-encabezado" aria-hidden="true">&#9998;</div>
                <div>
                    <p class="sobrelinea">Administración</p>
                    <h1>Editar cliente</h1>
                    <p class="descripcion-pagina">
                        Actualiza los datos del cliente y guarda los cambios cuando termines.
                    </p>
                </div>
            </header>

            <% if (error != null) { %>
                <div class="mensaje-error" role="alert">
                    <strong>No se pudieron guardar los cambios.</strong>
                    <%= HtmlUtil.escapar(error) %>
                </div>
            <% } %>

            <% if (cliente != null) { %>

                <section class="tarjeta-formulario" aria-labelledby="titulo-formulario">
                    <div class="tarjeta-encabezado">
                        <div>
                            <h2 id="titulo-formulario">Información del cliente</h2>
                            <p>Modifica los campos que necesites actualizar.</p>
                        </div>
                        <span class="identificador-cliente">
                            Cliente #<%= cliente.getIdclientes() %>
                        </span>
                    </div>

                    <div class="formulario-contenido">
                        <p class="nota-campos">
                            Los campos de nombre, correo y teléfono son obligatorios.
                        </p>

                        <form action="${pageContext.request.contextPath}/clientes"
                              method="POST">

                            <input type="hidden"
                                   name="csrfToken"
                                   value="<%= HtmlUtil.escapar(csrfToken) %>">

                            <input type="hidden"
                                   name="id"
                                   value="<%= cliente.getIdclientes() %>">

                            <div class="campos-grid">
                                <div class="grupo-campo">
                                    <label for="nombre">Nombre completo</label>
                                    <input type="text"
                                           id="nombre"
                                           name="nombre"
                                           value="<%= HtmlUtil.escapar(cliente.getNombre()) %>"
                                           maxlength="50"
                                           autocomplete="name"
                                           required>
                                </div>

                                <div class="grupo-campo">
                                    <label for="correo">Correo electrónico</label>
                                    <input type="email"
                                           id="correo"
                                           name="correo"
                                           value="<%= HtmlUtil.escapar(cliente.getCorreo()) %>"
                                           maxlength="50"
                                           autocomplete="email"
                                           required>
                                </div>

                                <div class="grupo-campo">
                                    <label for="telefono">Teléfono</label>
                                    <input type="tel"
                                           id="telefono"
                                           name="telefono"
                                           value="<%= HtmlUtil.escapar(cliente.getTelefono()) %>"
                                           maxlength="50"
                                           autocomplete="tel"
                                           required>
                                </div>

                                <div class="grupo-campo">
                                    <label for="direccion">Dirección <span style="font-weight: normal; color: #64748B;">(opcional)</span></label>
                                    <input type="text"
                                           id="direccion"
                                           name="direccion"
                                           value="<%= HtmlUtil.escapar(cliente.getDireccion() == null ? "" : cliente.getDireccion()) %>"
                                           maxlength="150"
                                           autocomplete="street-address">
                                    <span class="ayuda-campo">Puedes dejar este campo vacío si no tienes la dirección.</span>
                                </div>
                            </div>

                            <hr class="separador">

                            <div class="acciones">
                                <a href="${pageContext.request.contextPath}/clientes"
                                   class="boton boton-volver">
                                    <span aria-hidden="true">&#8592;</span>
                                    Volver a clientes
                                </a>

                                <button type="submit" class="boton boton-guardar">
                                    <span aria-hidden="true">&#10003;</span>
                                    Guardar cambios
                                </button>
                            </div>
                        </form>
                    </div>
                </section>

            <% } else { %>

                <section class="mensaje-no-encontrado" role="status">
                    <h2>Cliente no encontrado</h2>
                    <p>
                        No se encontró la información del cliente que intentas editar.
                        Es posible que el registro ya no exista o que el enlace haya caducado.
                    </p>
                    <a href="${pageContext.request.contextPath}/clientes"
                       class="boton boton-guardar">
                        Volver a clientes
                    </a>
                </section>

            <% } %>

        </div>
    </main>

</body>
</html>

