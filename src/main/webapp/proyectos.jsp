<%@ page language="java"
         contentType="text/html; charset=UTF-8"
         pageEncoding="UTF-8" %>

<%@ page import="java.util.List" %>
<%@ page import="com.sintel.modelo.Proyecto" %>
<%@ page import="com.sintel.modelo.Cliente" %>
<%@ page import="com.sintel.modelo.EstadoProyecto" %>
<%@ page import="java.text.NumberFormat" %>
<%@ page import="java.time.LocalDate" %>
<%@ page import="java.time.format.DateTimeFormatter" %>
<%@ page import="com.sintel.CsrfUtil" %>

<%
    List<Proyecto> proyectos =
            (List<Proyecto>) request.getAttribute("proyectos");

    List<Cliente> clientes =
            (List<Cliente>) request.getAttribute("clientes");

    List<EstadoProyecto> estados =
            (List<EstadoProyecto>) request.getAttribute("estados");

    NumberFormat formatoMoneda =
            NumberFormat.getCurrencyInstance(
                    new java.util.Locale("es", "CO")
            );

    DateTimeFormatter formatoFecha =
            DateTimeFormatter.ofPattern("dd/MM/yyyy");

    String tasaConfigurada =
            String.valueOf(request.getAttribute("tasaIVA"));

    String error = request.getParameter("error");

    String csrfToken = CsrfUtil.obtenerToken(request);
%>

<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">

    <title>Proyectos - Sintel</title>

    <style>
        :root {
            --azul: #00274F;
            --turquesa: #49CCCC;
            --fondo: #F4F7FA;
            --borde: #DDE5EC;
            --texto-secundario: #5D6B7A;
            --error: #B8201F;
            --alerta: #D4B415;
        }

        * { box-sizing: border-box; }

        body {
            margin: 0;
            font-family: Arial, Helvetica, sans-serif;
            background: var(--fondo);
            color: var(--azul);
        }

        .contenido {
            width: min(100% - 40px, 1240px);
            margin: 32px auto 48px;
        }

        .encabezado-pagina {
            display: flex;
            justify-content: space-between;
            align-items: flex-end;
            flex-wrap: wrap;
            gap: 16px;
            margin-bottom: 26px;
        }

        .encabezado-pagina h1 {
            margin: 0 0 8px;
            font-size: clamp(26px, 3vw, 32px);
            letter-spacing: -0.5px;
        }

        .encabezado-pagina p {
            margin: 0;
            color: var(--texto-secundario);
            line-height: 1.5;
        }

        .panel {
            background: #FFFFFF;
            border: 1px solid var(--borde);
            border-radius: 12px;
            padding: 24px;
            margin-bottom: 24px;
            box-shadow: 0 3px 12px rgba(0, 39, 79, 0.045);
        }

        .titulo-seccion {
            margin: 0 0 6px;
            font-size: 20px;
        }

        .descripcion-seccion {
            margin: 0 0 22px;
            color: var(--texto-secundario);
            font-size: 14px;
            line-height: 1.5;
        }

        .mensaje-error {
            padding: 14px 16px;
            background: #FDECEC;
            color: var(--error);
            border: 1px solid #F3C8C7;
            border-left: 4px solid var(--error);
            border-radius: 7px;
            margin: 0 0 18px;
            line-height: 1.5;
        }

        .rejilla-formulario {
            display: grid;
            grid-template-columns: repeat(2, minmax(0, 1fr));
            gap: 18px 20px;
        }

        .grupo-campo { min-width: 0; }
        .grupo-campo.completo { grid-column: 1 / -1; }

        label {
            display: block;
            margin-bottom: 8px;
            font-size: 14px;
            font-weight: 700;
        }

        input, select {
            display: block;
            width: 100%;
            min-height: 44px;
            padding: 11px 12px;
            border: 1px solid #C9D4DF;
            border-radius: 7px;
            background: #FFFFFF;
            color: var(--azul);
            font: inherit;
            font-size: 14px;
            transition: border-color .15s ease, box-shadow .15s ease;
        }

        input:focus, select:focus {
            outline: none;
            border-color: var(--turquesa);
            box-shadow: 0 0 0 3px rgba(73, 204, 204, .2);
        }

        input[readonly] {
            background: #EFF9F9;
            font-weight: 700;
        }

        .resumen-financiero {
            grid-column: 1 / -1;
            padding: 18px;
            background: #F7FAFC;
            border: 1px solid var(--borde);
            border-radius: 9px;
        }

        .resumen-financiero .tasa {
            display: flex;
            justify-content: space-between;
            align-items: center;
            gap: 12px;
            margin: 0 0 16px;
            font-size: 14px;
        }

        .resumen-financiero .campos-resumen {
            display: grid;
            grid-template-columns: repeat(2, minmax(0, 1fr));
            gap: 16px;
        }

        .resumen-financiero .total label { color: var(--azul); }

        .nota {
            margin: 12px 0 0;
            color: var(--texto-secundario);
            font-size: 13px;
            line-height: 1.5;
        }

        .boton {
            display: inline-flex;
            align-items: center;
            justify-content: center;
            gap: 8px;
            min-height: 42px;
            padding: 11px 17px;
            border: 1px solid transparent;
            border-radius: 7px;
            font: inherit;
            font-size: 14px;
            font-weight: 700;
            text-decoration: none;
            cursor: pointer;
            transition: background .15s ease, border-color .15s ease, transform .15s ease;
        }

        .boton:focus-visible, .acciones a:focus-visible {
            outline: 3px solid rgba(73, 204, 204, .55);
            outline-offset: 2px;
        }

        .boton-principal {
            background: var(--azul);
            color: #FFFFFF;
        }

        .boton-principal:hover { background: #004078; }

        .boton-secundario {
            background: #FFFFFF;
            color: var(--azul);
            border-color: #C9D4DF;
        }

        .boton-secundario:hover { background: #F0F5F9; }

        .acciones-formulario {
            display: flex;
            align-items: center;
            flex-wrap: wrap;
            gap: 12px;
            margin-top: 20px;
        }

        .filtros {
            display: grid;
            grid-template-columns: minmax(0, 1fr) minmax(0, 1fr) auto;
            align-items: end;
            gap: 16px;
        }

        .filtros label { margin-top: 0; }
        .filtros .boton { width: 100%; }
        .filtros .grupo-boton { min-width: 145px; }

        .cabecera-listado {
            display: flex;
            justify-content: space-between;
            align-items: flex-end;
            flex-wrap: wrap;
            gap: 12px;
            margin-bottom: 20px;
        }

        .cabecera-listado .titulo-seccion { margin-bottom: 5px; }
        .contador { color: var(--texto-secundario); font-size: 13px; }

        .tabla-contenedor {
            width: 100%;
            overflow-x: auto;
            border: 1px solid var(--borde);
            border-radius: 9px;
            background: #FFFFFF;
        }

        table {
            width: 100%;
            min-width: 1050px;
            border-collapse: separate;
            border-spacing: 0;
            font-size: 13px;
        }

        th, td {
            padding: 14px 13px;
            text-align: left;
            vertical-align: middle;
            border-bottom: 1px solid #E7EDF2;
        }

        th {
            background: var(--azul);
            color: #FFFFFF;
            font-size: 12px;
            font-weight: 700;
            letter-spacing: .2px;
            white-space: nowrap;
        }

        tbody tr:last-child td { border-bottom: 0; }
        tbody tr:hover { background: #F6FBFC; }
        tbody tr.fila-atrasada { background: #FFF9DF; }
        tbody tr.fila-atrasada:hover { background: #FFF4C5; }

        .columna-id { color: var(--texto-secundario); }
        .columna-proyecto { min-width: 160px; font-weight: 700; }
        .columna-moneda { white-space: nowrap; font-variant-numeric: tabular-nums; }

        .estado-etiqueta {
            display: inline-block;
            padding: 5px 9px;
            border-radius: 999px;
            background: #EAF0F6;
            color: var(--azul);
            font-size: 12px;
            font-weight: 700;
            white-space: nowrap;
        }

        .alerta-atrasado {
            display: inline-block;
            margin-top: 6px;
            padding: 5px 8px;
            border-radius: 5px;
            background: #F8E79A;
            color: #5B4800;
            font-size: 11px;
            font-weight: 700;
            white-space: nowrap;
        }

        .acciones {
            display: flex;
            align-items: center;
            gap: 12px;
            white-space: nowrap;
        }

        .acciones a, .boton-eliminar {
            color: #005B78;
            font: inherit;
            font-size: 13px;
            font-weight: 700;
            text-decoration: none;
        }

        .acciones a:hover, .boton-eliminar:hover {
            text-decoration: underline;
        }

        .form-eliminar {
            display: inline;
            margin: 0;
            padding: 0;
            background: none;
            border: 0;
        }

        .boton-eliminar {
            min-height: auto;
            padding: 0;
            background: none;
            border: 0;
            color: var(--error);
            cursor: pointer;
        }

        .sin-resultados {
            padding: 34px 20px;
            text-align: center;
            color: var(--texto-secundario);
        }

        .pie-tabla {
            margin: 12px 0 0;
            color: var(--texto-secundario);
            font-size: 12px;
        }

        @media (max-width: 760px) {
            .contenido { width: calc(100% - 28px); margin: 24px auto 36px; }
            .panel { padding: 19px; }
            .filtros { grid-template-columns: repeat(2, minmax(0, 1fr)); }
            .filtros .grupo-boton { grid-column: 1 / -1; }
        }

        @media (max-width: 560px) {
            .contenido { width: calc(100% - 22px); margin-top: 20px; }
            .panel { padding: 16px; border-radius: 10px; }
            .rejilla-formulario, .resumen-financiero .campos-resumen,
            .filtros { grid-template-columns: 1fr; }
            .grupo-campo.completo, .filtros .grupo-boton { grid-column: auto; }
            .acciones-formulario { align-items: stretch; flex-direction: column; }
            .acciones-formulario .boton { width: 100%; }
            .resumen-financiero .tasa { align-items: flex-start; flex-direction: column; gap: 6px; }
            .cabecera-listado { align-items: flex-start; flex-direction: column; }
        }
    </style>
</head>

<body>

    <jsp:include page="navbar.jsp" />

    <main class="contenido">

        <header class="encabezado-pagina">
            <div>
                <h1>Gestión de proyectos</h1>
                <p>Registra, consulta y administra los proyectos de Sintel.</p>
            </div>
        </header>

        <% if ("fecha".equals(error)) { %>
            <p class="mensaje-error" role="alert">
                La fecha de entrega no puede ser anterior a hoy.
            </p>
        <% } else if ("valor".equals(error)) { %>
            <p class="mensaje-error" role="alert">
                El valor del proyecto debe ser mayor que cero.
            </p>
        <% } %>

        <% if (request.getAttribute("tasaIVA") == null) { %>
            <p class="mensaje-error" role="alert">
                No se pudo cargar la tasa de IVA. Recarga la página
                o verifica la configuración del sistema.
            </p>
        <% } %>

        <section class="panel" aria-labelledby="titulo-registro">
            <h2 class="titulo-seccion" id="titulo-registro">Registrar proyecto</h2>
            <p class="descripcion-seccion">
                Completa la información del proyecto. El IVA y el total se calcularán automáticamente.
            </p>

            <form action="proyectos" method="POST" id="formProyecto">
                <input type="hidden" name="csrfToken" value="<%= csrfToken %>">

                <div class="rejilla-formulario">
                    <div class="grupo-campo completo">
                        <label for="nombre">Nombre del proyecto</label>
                        <input
                            type="text"
                            id="nombre"
                            name="nombre"
                            maxlength="100"
                            placeholder="Escribe el nombre del proyecto"
                            required
                        >
                    </div>

                    <div class="grupo-campo">
                        <label for="idclientes">Cliente</label>
                        <select id="idclientes" name="idclientes" required>
                            <option value="">Seleccione un cliente</option>
                            <% if (clientes != null) {
                                for (Cliente cliente : clientes) { %>
                                    <option value="<%= cliente.getIdclientes() %>">
                                        <%= cliente.getNombre() %>
                                    </option>
                            <%  }
                            } %>
                        </select>
                    </div>

                    <div class="grupo-campo">
                        <label for="idestados">Estado</label>
                        <select id="idestados" name="idestados" required>
                            <option value="">Seleccione un estado</option>
                            <% if (estados != null) {
                                for (EstadoProyecto estado : estados) { %>
                                    <option value="<%= estado.getIdestados() %>">
                                        <%= estado.getNombre() %>
                                    </option>
                            <%  }
                            } %>
                        </select>
                    </div>

                    <div class="grupo-campo">
                        <label for="fechaEntrega">Fecha de entrega</label>
                        <input
                            type="date"
                            id="fechaEntrega"
                            name="fechaEntrega"
                            min="<%= LocalDate.now() %>"
                            required
                        >
                    </div>

                    <div class="grupo-campo">
                        <label for="valorTotal">Valor antes del IVA (COP)</label>
                        <input
                            type="number"
                            id="valorTotal"
                            name="valorTotal"
                            step="0.01"
                            min="0.01"
                            placeholder="Ej. 1000000"
                            required
                        >
                    </div>

                    <section class="resumen-financiero" aria-label="Resumen financiero">
                        <p class="tasa">
                            <span>Porcentaje de IVA configurado</span>
                            <strong id="porcentajeIVA"><%= tasaConfigurada %>%</strong>
                        </p>

                        <div class="campos-resumen">
                            <div class="grupo-campo">
                                <label for="ivaCalculado">IVA calculado</label>
                                <input
                                    type="text"
                                    id="ivaCalculado"
                                    value="$ 0,00"
                                    readonly
                                    aria-live="polite"
                                >
                            </div>

                            <div class="grupo-campo total">
                                <label for="totalCalculado">Total con IVA</label>
                                <input
                                    type="text"
                                    id="totalCalculado"
                                    value="$ 0,00"
                                    readonly
                                    aria-live="polite"
                                >
                            </div>
                        </div>

                        <p class="nota">
                            Los importes se calculan a partir del valor base y la tasa de IVA configurada en Sintel.
                        </p>
                    </section>
                </div>

                <div class="acciones-formulario">
                    <button type="submit" class="boton boton-principal">
                        Registrar proyecto
                    </button>
                </div>
            </form>
        </section>

        <section class="panel" aria-labelledby="titulo-listado">
            <div class="cabecera-listado">
                <div>
                    <h2 class="titulo-seccion" id="titulo-listado">Proyectos registrados</h2>
                    <p class="descripcion-seccion" style="margin-bottom:0;">
                        Consulta los proyectos y utiliza los filtros para encontrar uno rápidamente.
                    </p>
                </div>
            </div>

            <form action="proyectos" method="GET" class="filtros">
                <div class="grupo-campo">
                    <label for="filtroEstado">Filtrar por estado</label>
                    <select id="filtroEstado" name="estado">
                        <option
                            value=""
                            <%= request.getParameter("estado") == null
                                || request.getParameter("estado").isEmpty()
                                ? "selected" : "" %>>
                            Todos los estados
                        </option>

                        <% if (estados != null) {
                            for (EstadoProyecto estado : estados) { %>
                                <option
                                    value="<%= estado.getIdestados() %>"
                                    <%= String.valueOf(estado.getIdestados())
                                        .equals(request.getParameter("estado"))
                                        ? "selected" : "" %>>
                                    <%= estado.getNombre() %>
                                </option>
                        <%  }
                        } %>
                    </select>
                </div>

                <div class="grupo-campo">
                    <label for="filtroCliente">Filtrar por cliente</label>
                    <select id="filtroCliente" name="cliente">
                        <option
                            value=""
                            <%= request.getParameter("cliente") == null
                                || request.getParameter("cliente").isEmpty()
                                ? "selected" : "" %>>
                            Todos los clientes
                        </option>

                        <% if (clientes != null) {
                            for (Cliente cliente : clientes) { %>
                                <option
                                    value="<%= cliente.getIdclientes() %>"
                                    <%= String.valueOf(cliente.getIdclientes())
                                        .equals(request.getParameter("cliente"))
                                        ? "selected" : "" %>>
                                    <%= cliente.getNombre() %>
                                </option>
                        <%  }
                        } %>
                    </select>
                </div>

                <div class="grupo-boton">
                    <button type="submit" class="boton boton-principal">Aplicar filtros</button>
                </div>
            </form>

            <div class="tabla-contenedor" style="margin-top:22px;">
                <table>
                    <thead>
                        <tr>
                            <th>ID</th>
                            <th>Proyecto</th>
                            <th>Cliente</th>
                            <th>Estado</th>
                            <th>Fecha de entrega</th>
                            <th>Valor antes del IVA</th>
                            <th>IVA</th>
                            <th>Total</th>
                            <th>Acciones</th>
                        </tr>
                    </thead>

                    <tbody>
                        <% if (proyectos != null && !proyectos.isEmpty()) {
                            for (Proyecto proyecto : proyectos) {

                                boolean proyectoAtrasado =
                                        proyecto.getIdestados() == 1
                                        && proyecto.getFechaEntrega()
                                            .isBefore(LocalDate.now());

                                String nombreCliente = "Sin cliente";

                                if (clientes != null) {
                                    for (Cliente cliente : clientes) {
                                        if (cliente.getIdclientes()
                                                == proyecto.getIdclientes()) {
                                            nombreCliente = cliente.getNombre();
                                            break;
                                        }
                                    }
                                }

                                String nombreEstado = "Sin estado";

                                if (estados != null) {
                                    for (EstadoProyecto estado : estados) {
                                        if (estado.getIdestados()
                                                == proyecto.getIdestados()) {
                                            nombreEstado = estado.getNombre();
                                            break;
                                        }
                                    }
                                }

                                java.math.BigDecimal total =
                                        proyecto.getValorTotal().add(proyecto.getIva());
                        %>
                            <tr class="<%= proyectoAtrasado ? "fila-atrasada" : "" %>">
                                <td class="columna-id"><%= proyecto.getIdproyectos() %></td>
                                <td class="columna-proyecto"><%= proyecto.getNombre() %></td>
                                <td><%= nombreCliente %></td>
                                <td><span class="estado-etiqueta"><%= nombreEstado %></span></td>
                                <td>
                                    <%= proyecto.getFechaEntrega().format(formatoFecha) %>
                                    <% if (proyectoAtrasado) { %>
                                        <br>
                                        <span class="alerta-atrasado">⚠ Entrega atrasada</span>
                                    <% } %>
                                </td>
                                <td class="columna-moneda">
                                    <%= formatoMoneda.format(proyecto.getValorTotal()) %>
                                </td>
                                <td class="columna-moneda">
                                    <%= formatoMoneda.format(proyecto.getIva()) %>
                                </td>
                                <td class="columna-moneda">
                                    <strong><%= formatoMoneda.format(total) %></strong>
                                </td>
                                <td>
                                    <div class="acciones">
                                        <a href="proyectos?accion=editar&id=<%= proyecto.getIdproyectos() %>">
                                            Editar
                                        </a>

                                        <form action="proyectos" method="POST" class="form-eliminar"
                                              onsubmit="return confirm('¿Está seguro de eliminar este proyecto?');">
                                            <input type="hidden" name="csrfToken" value="<%= csrfToken %>">
                                            <input type="hidden" name="accion" value="eliminar">
                                            <input type="hidden" name="id" value="<%= proyecto.getIdproyectos() %>">
                                            <button type="submit" class="boton-eliminar">Eliminar</button>
                                        </form>
                                    </div>
                                </td>
                            </tr>
                        <%  }
                        } else { %>
                            <tr>
                                <td colspan="9" class="sin-resultados">
                                    No hay proyectos para mostrar con los filtros seleccionados.
                                </td>
                            </tr>
                        <% } %>
                    </tbody>
                </table>
            </div>

            <p class="pie-tabla">
                En pantallas pequeñas puedes deslizar la tabla horizontalmente para consultar todas las columnas.
            </p>
        </section>
    </main>

    <script>
        const campoBase = document.getElementById("valorTotal");
        const campoIVA = document.getElementById("ivaCalculado");
        const campoTotal = document.getElementById("totalCalculado");

        const tasaIVA = Number("<%= tasaConfigurada %>");

        const formatoMoneda = new Intl.NumberFormat("es-CO", {
            style: "currency",
            currency: "COP",
            minimumFractionDigits: 2,
            maximumFractionDigits: 2
        });

        function calcularIVA() {
            const base = Number(campoBase.value);

            if (!Number.isFinite(base) || base <= 0
                    || !Number.isFinite(tasaIVA)) {
                campoIVA.value = formatoMoneda.format(0);
                campoTotal.value = formatoMoneda.format(0);
                return;
            }

            const iva = Math.round(
                (base * tasaIVA / 100 + Number.EPSILON) * 100
            ) / 100;

            const total = Math.round(
                (base + iva + Number.EPSILON) * 100
            ) / 100;

            campoIVA.value = formatoMoneda.format(iva);
            campoTotal.value = formatoMoneda.format(total);
        }

        campoBase.addEventListener("input", calcularIVA);

        document.getElementById("formProyecto")
            .addEventListener("submit", function (evento) {
                if (!Number.isFinite(tasaIVA)) {
                    evento.preventDefault();
                    alert("No se pudo obtener la tasa de IVA configurada.");
                }
            });

        calcularIVA();
    </script>

</body>
</html>