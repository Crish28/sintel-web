<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="com.sintel.CsrfUtil" %>
<%@ page import="com.sintel.HtmlUtil" %>

<%
    if (request.getAttribute("totalProyectos") == null) {
        response.sendRedirect(request.getContextPath() + "/dashboard");
        return;
    }

    String nombreUsuario =
            (String) session.getAttribute("nombreUsuario");

    String rolUsuario =
            (String) session.getAttribute("rolUsuario");

    boolean esAdministrador =
            "Administrador".equals(rolUsuario);

    if (nombreUsuario == null || nombreUsuario.isBlank()) {
        nombreUsuario = "Usuario";
    }

    String csrfToken = CsrfUtil.obtenerToken(request);
%>

<!DOCTYPE html>
<html lang="es">

<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Sintel | Dashboard</title>

    <style>
        * {
            box-sizing: border-box;
        }

        :root {
            --primario: #00274F;
            --secundario: #49CCCC;
            --fondo: #F4F7FA;
            --blanco: #FFFFFF;
            --texto: #263746;
            --texto-secundario: #6B7A89;
            --borde: #E1E8EF;
            --advertencia: #D4B415;
            --error: #B8201F;
        }

        body {
            margin: 0;
            font-family: Arial, Helvetica, sans-serif;
            background: var(--fondo);
            color: var(--texto);
        }

        /* ENCABEZADO */

        .encabezado {
            background: var(--primario);
            color: var(--blanco);
            padding: 22px 5%;
            display: flex;
            justify-content: space-between;
            align-items: center;
            gap: 20px;
            flex-wrap: wrap;
        }

        .marca h1 {
            margin: 0;
            font-size: 27px;
            letter-spacing: 0.5px;
        }

        .marca p {
            margin: 6px 0 0;
            color: #D8E5F0;
            font-size: 14px;
        }

        .usuario {
            text-align: right;
            line-height: 1.5;
        }

        .usuario small {
            display: block;
            color: #D8E5F0;
        }

        .usuario strong {
            color: var(--secundario);
        }

        /* CONTENIDO PRINCIPAL */

        .contenido {
            width: 100%;
            max-width: 1450px;
            margin: 0 auto;
            padding: 35px 5% 45px;
        }

        .bienvenida {
            display: flex;
            justify-content: space-between;
            align-items: center;
            gap: 20px;
            flex-wrap: wrap;
            margin-bottom: 28px;
        }

        .bienvenida h2 {
            margin: 0 0 8px;
            color: var(--primario);
            font-size: 28px;
        }

        .bienvenida p {
            margin: 0;
            color: var(--texto-secundario);
            line-height: 1.5;
        }

        .etiqueta-panel {
            display: inline-block;
            padding: 8px 13px;
            border-radius: 20px;
            background: #DDF7F7;
            color: var(--primario);
            font-size: 13px;
            font-weight: bold;
        }

        /* TARJETAS */

        .titulo-seccion {
            color: var(--primario);
            font-size: 18px;
            margin: 0 0 17px;
        }

        .tarjetas {
            display: grid;
            grid-template-columns: repeat(3, minmax(0, 1fr));
            gap: 20px;
        }

        .tarjeta {
            position: relative;
            overflow: hidden;
            background: var(--blanco);
            border: 1px solid var(--borde);
            border-radius: 12px;
            padding: 24px;
            box-shadow: 0 3px 12px rgba(0, 39, 79, 0.04);
            transition: transform 0.2s ease,
                        box-shadow 0.2s ease;
        }

        .tarjeta:hover {
            transform: translateY(-3px);
            box-shadow: 0 7px 18px rgba(0, 39, 79, 0.09);
        }

        .tarjeta::before {
            content: "";
            position: absolute;
            top: 0;
            left: 0;
            width: 5px;
            height: 100%;
            background: var(--primario);
        }

        .tarjeta-pendientes::before {
            background: #D4B415;
        }

        .tarjeta-proceso::before {
            background: #2388C9;
        }

        .tarjeta-finalizados::before {
            background: #25966B;
        }

        .tarjeta-atrasados::before {
            background: var(--error);
        }

        .tarjeta-superior {
            display: flex;
            align-items: center;
            justify-content: space-between;
            gap: 10px;
            margin-bottom: 20px;
        }

        .tarjeta h3 {
            margin: 0;
            font-size: 15px;
            font-weight: 600;
            color: var(--texto-secundario);
        }

        .icono {
            display: flex;
            justify-content: center;
            align-items: center;
            width: 40px;
            height: 40px;
            border-radius: 10px;
            background: #EAF0F7;
            color: var(--primario);
            font-size: 21px;
            font-weight: bold;
        }

        .tarjeta-pendientes .icono {
            background: #FFF8D9;
            color: #796400;
        }

        .tarjeta-proceso .icono {
            background: #E5F3FC;
            color: #17699E;
        }

        .tarjeta-finalizados .icono {
            background: #E3F6EC;
            color: #18734E;
        }

        .tarjeta-atrasados .icono {
            background: #FCE8E7;
            color: var(--error);
        }

        .numero {
            color: var(--primario);
            font-size: 35px;
            font-weight: bold;
            line-height: 1.2;
            overflow-wrap: anywhere;
        }

        .descripcion-tarjeta {
            margin: 8px 0 0;
            color: var(--texto-secundario);
            font-size: 13px;
            line-height: 1.5;
        }

        .tarjeta-atrasados .numero {
            color: var(--error);
        }

        /* ACCESOS */

        .accesos {
            margin-top: 35px;
        }

        .accesos-grid {
            display: grid;
            grid-template-columns: repeat(2, minmax(0, 1fr));
            gap: 18px;
        }

        .acceso {
            display: flex;
            align-items: center;
            gap: 16px;
            padding: 22px;
            background: var(--blanco);
            border: 1px solid var(--borde);
            border-radius: 12px;
            text-decoration: none;
            color: var(--texto);
            transition: border-color 0.2s ease,
                        box-shadow 0.2s ease,
                        transform 0.2s ease;
        }

        .acceso:hover {
            border-color: var(--secundario);
            box-shadow: 0 5px 16px rgba(0, 39, 79, 0.07);
            transform: translateY(-2px);
        }

        .acceso-icono {
            flex-shrink: 0;
            display: flex;
            justify-content: center;
            align-items: center;
            width: 48px;
            height: 48px;
            background: #E1F8F8;
            color: var(--primario);
            border-radius: 10px;
            font-size: 24px;
        }

        .acceso-texto {
            flex: 1;
        }

        .acceso h3 {
            margin: 0 0 6px;
            color: var(--primario);
            font-size: 16px;
        }

        .acceso p {
            margin: 0;
            color: var(--texto-secundario);
            font-size: 13px;
            line-height: 1.5;
        }

        .flecha {
            color: var(--primario);
            font-size: 24px;
        }

        /* CERRAR SESIÓN */

        .pie {
            display: flex;
            justify-content: flex-end;
            margin-top: 30px;
        }

        .formulario-salir {
            margin: 0;
        }

        .boton-salir {
            padding: 11px 18px;
            border: 1px solid #E7B8B6;
            border-radius: 7px;
            background: var(--blanco);
            color: var(--error);
            font-size: 14px;
            font-weight: bold;
            cursor: pointer;
            transition: background 0.2s ease;
        }

        .boton-salir:hover {
            background: #FCE8E7;
        }

        /* PIE DE PÁGINA */

        footer {
            padding: 20px;
            border-top: 1px solid var(--borde);
            text-align: center;
            color: var(--texto-secundario);
            font-size: 12px;
        }

        /* DISEÑO ADAPTABLE */

        @media (max-width: 900px) {
            .tarjetas {
                grid-template-columns: repeat(2, minmax(0, 1fr));
            }

            .contenido {
                padding: 28px 4%;
            }
        }

        @media (max-width: 560px) {
            .encabezado {
                padding: 20px;
                align-items: flex-start;
            }

            .usuario {
                text-align: left;
            }

            .contenido {
                padding: 25px 18px 35px;
            }

            .bienvenida h2 {
                font-size: 24px;
            }

            .tarjetas,
            .accesos-grid {
                grid-template-columns: 1fr;
            }

            .tarjeta {
                padding: 20px;
            }

            .pie {
                justify-content: stretch;
            }

            .formulario-salir,
            .boton-salir {
                width: 100%;
            }
        }

        @media (prefers-reduced-motion: reduce) {
            *,
            *::before,
            *::after {
                transition: none !important;
                scroll-behavior: auto !important;
            }
        }
    </style>
</head>

<body>

    <header class="encabezado">
        <div class="marca">
            <h1>Sintel</h1>
            <p>Gestión de proyectos de seguridad electrónica</p>
        </div>

        <div class="usuario">
            <small>Sesión iniciada como</small>
            <strong><%= HtmlUtil.escapar(nombreUsuario) %></strong>
        </div>
    </header>

    <main class="contenido">

        <section class="bienvenida">
            <div>
                <h2>Dashboard</h2>
                <p>Consulta el estado general de tus proyectos.</p>
            </div>

            <span class="etiqueta-panel">Panel de control</span>
        </section>

        <section aria-labelledby="titulo-resumen">
            <h3 class="titulo-seccion" id="titulo-resumen">
                Resumen de proyectos
            </h3>

            <div class="tarjetas">

                <article class="tarjeta">
                    <div class="tarjeta-superior">
                        <h3>Total proyectos</h3>
                        <span class="icono" aria-hidden="true">▦</span>
                    </div>

                    <div class="numero">
                        <%= request.getAttribute("totalProyectos") %>
                    </div>

                    <p class="descripcion-tarjeta">
                        Proyectos registrados en el sistema
                    </p>
                </article>

                <article class="tarjeta tarjeta-pendientes">
                    <div class="tarjeta-superior">
                        <h3>Pendientes</h3>
                        <span class="icono" aria-hidden="true">◷</span>
                    </div>

                    <div class="numero">
                        <%= request.getAttribute("pendientes") %>
                    </div>

                    <p class="descripcion-tarjeta">
                        Pendientes de iniciar o gestionar
                    </p>
                </article>

                <article class="tarjeta tarjeta-proceso">
                    <div class="tarjeta-superior">
                        <h3>En proceso</h3>
                        <span class="icono" aria-hidden="true">↻</span>
                    </div>

                    <div class="numero">
                        <%= request.getAttribute("enProceso") %>
                    </div>

                    <p class="descripcion-tarjeta">
                        Proyectos actualmente en ejecución
                    </p>
                </article>

                <article class="tarjeta tarjeta-finalizados">
                    <div class="tarjeta-superior">
                        <h3>Finalizados</h3>
                        <span class="icono" aria-hidden="true">✓</span>
                    </div>

                    <div class="numero">
                        <%= request.getAttribute("finalizados") %>
                    </div>

                    <p class="descripcion-tarjeta">
                        Proyectos completados
                    </p>
                </article>

                <article class="tarjeta tarjeta-atrasados">
                    <div class="tarjeta-superior">
                        <h3>Atrasados</h3>
                        <span class="icono" aria-hidden="true">!</span>
                    </div>

                    <div class="numero">
                        <%= request.getAttribute("atrasados") %>
                    </div>

                    <p class="descripcion-tarjeta">
                        Proyectos pendientes cuya entrega se ha retrasado
                    </p>
                </article>

            </div>
        </section>

        <section class="accesos" aria-labelledby="titulo-accesos">
            <h3 class="titulo-seccion" id="titulo-accesos">
                Accesos rápidos
            </h3>

            <div class="accesos-grid">

                <a class="acceso"
                   href="${pageContext.request.contextPath}/proyectos">

                    <span class="acceso-icono" aria-hidden="true">▤</span>

                    <span class="acceso-texto">
                        <h3>Gestionar proyectos</h3>
                        <p>Consultar, registrar y actualizar proyectos.</p>
                    </span>

                    <span class="flecha" aria-hidden="true">›</span>
                </a>

                <a class="acceso"
                   href="${pageContext.request.contextPath}/clientes">

                    <span class="acceso-icono" aria-hidden="true">♙</span>

                    <span class="acceso-texto">
                        <h3>Gestionar clientes</h3>
                        <p>Consultar y administrar la información de los clientes.</p>
                    </span>

                    <span class="flecha" aria-hidden="true">›</span>
                </a>

                <% if (esAdministrador) { %>

               <a class="acceso"
                  href="${pageContext.request.contextPath}/usuarios">

                  <span class="acceso-icono" aria-hidden="true">♙</span>

                  <span class="acceso-texto">
                      <h3>Gestionar usuarios</h3>
                       <p>Crear, consultar y administrar las cuentas de acceso.</p>
                  </span>

                  <span class="flecha" aria-hidden="true">›</span>
               </a>

               <a class="acceso"
                  href="${pageContext.request.contextPath}/configuracion">

                  <span class="acceso-icono" aria-hidden="true">⚙</span>

                  <span class="acceso-texto">
                     <h3>Configuración</h3>
                     <p>Administrar los ajustes generales del sistema.</p>
                 </span>

                 <span class="flecha" aria-hidden="true">›</span>
               </a>

               <% } %>

            </div>
        </section>

        <div class="pie">
            <form class="formulario-salir"
                  action="${pageContext.request.contextPath}/logout"
                  method="post">

                <input type="hidden"
                       name="csrfToken"
                       value="<%= HtmlUtil.escapar(csrfToken) %>">

                <button type="submit" class="boton-salir">
                    Cerrar sesión
                </button>
            </form>
        </div>

    </main>

    <footer>
        Sintel · Sistema de gestión de proyectos
    </footer>

</body>
</html>