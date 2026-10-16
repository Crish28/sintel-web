<%@ page language="java"
         contentType="text/html; charset=UTF-8"
         pageEncoding="UTF-8" %>

<%@ page import="java.util.List" %>
<%@ page import="com.sintel.modelo.Proyecto" %>
<%@ page import="com.sintel.modelo.Cliente" %>
<%@ page import="com.sintel.modelo.EstadoProyecto" %>
<%@ page import="com.sintel.CsrfUtil" %>

<%
    Proyecto proyecto =
            (Proyecto) request.getAttribute("proyecto");

    List<Cliente> clientes =
            (List<Cliente>) request.getAttribute("clientes");

    List<EstadoProyecto> estados =
            (List<EstadoProyecto>) request.getAttribute("estados");

    Object tasaObjeto = request.getAttribute("tasaIVA");

    String tasaConfigurada =
            tasaObjeto != null ? tasaObjeto.toString() : "null";

    String csrfToken = CsrfUtil.obtenerToken(request);
%>

<!DOCTYPE html>
<html lang="es">

<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">

    <title>Editar proyecto | Sintel</title>

    <style>
        :root {
            --sintel-azul: #00274F;
            --sintel-turquesa: #49CCCC;
            --fondo: #F3F6FA;
            --texto-secundario: #5D6B7A;
            --borde: #DCE4EC;
            --error: #B8201F;
        }

        * { box-sizing: border-box; }

        body {
            margin: 0;
            font-family: Arial, Helvetica, sans-serif;
            background: var(--fondo);
            color: var(--sintel-azul);
        }

        .contenido {
            width: 100%;
            max-width: 1180px;
            margin: 0 auto;
            padding: 38px 28px 48px;
        }

        .encabezado-pagina {
            max-width: 900px;
            margin: 0 auto 24px;
        }

        .encabezado-pagina h1 {
            margin: 0 0 9px;
            font-size: clamp(25px, 3vw, 32px);
            letter-spacing: -0.5px;
        }

        .encabezado-pagina p {
            margin: 0;
            color: var(--texto-secundario);
            line-height: 1.6;
            font-size: 15px;
        }

        .tarjeta-formulario {
            max-width: 900px;
            margin: 0 auto;
            padding: clamp(22px, 4vw, 36px);
            background: #FFFFFF;
            border: 1px solid var(--borde);
            border-radius: 14px;
            box-shadow: 0 8px 24px rgba(0, 39, 79, 0.07);
        }

        .tarjeta-formulario h2 {
            margin: 0 0 26px;
            padding-bottom: 17px;
            border-bottom: 1px solid #E9EEF3;
            font-size: 20px;
        }

        #formEditarProyecto {
            display: grid;
            grid-template-columns: repeat(2, minmax(0, 1fr));
            column-gap: 22px;
            row-gap: 2px;
        }

        .grupo-campo { margin-bottom: 18px; min-width: 0; }

        .grupo-campo label,
        .resumen-financiero label {
            display: block;
            margin-bottom: 8px;
            font-size: 13px;
            font-weight: 700;
            line-height: 1.4;
        }

        .grupo-campo input,
        .grupo-campo select {
            display: block;
            width: 100%;
            min-height: 46px;
            padding: 11px 13px;
            border: 1px solid #C9D4E0;
            border-radius: 8px;
            font-family: inherit;
            font-size: 15px;
            color: var(--sintel-azul);
            background: #FFFFFF;
            transition: border-color .2s, box-shadow .2s;
        }

        .grupo-campo input:hover,
        .grupo-campo select:hover { border-color: #91A6BA; }

        .grupo-campo input:focus,
        .grupo-campo select:focus {
            outline: 3px solid rgba(73, 204, 204, .2);
            border-color: var(--sintel-turquesa);
        }

        .grupo-campo input[readonly] {
            background: #EAF7F7;
            font-weight: 700;
            cursor: default;
        }

        .resumen-financiero {
            grid-column: 1 / -1;
            margin: 6px 0 8px;
            padding: 22px;
            background: #F7FBFC;
            border: 1px solid #D6E7EA;
            border-radius: 10px;
        }

        .resumen-financiero .tasa {
            display: flex;
            justify-content: space-between;
            align-items: center;
            gap: 15px;
            margin: 0;
            padding-bottom: 16px;
            border-bottom: 1px solid #DCE8EA;
            font-size: 14px;
        }

        .resumen-financiero .tasa strong {
            padding: 6px 10px;
            color: #005C61;
            background: #DDF7F5;
            border-radius: 6px;
        }

        .resumen-financiero label { margin-top: 17px; }

        .resumen-financiero input {
            width: 100%;
            min-height: 46px;
            padding: 11px 13px;
            border: 1px solid #D1E0E3;
            border-radius: 8px;
            font-size: 15px;
            color: var(--sintel-azul);
            background: #EAF7F7;
            font-weight: 700;
        }

        .resumen-financiero .total {
            margin-top: 18px;
            padding-top: 3px;
            border-top: 1px solid #DCE8EA;
        }

        .resumen-financiero .total input {
            font-size: 18px;
            border-color: #A7DCDC;
        }

        .nota {
            margin: 13px 0 0;
            color: var(--texto-secundario);
            font-size: 13px;
            line-height: 1.6;
        }

        .mensaje-error {
            margin: 0 0 20px;
            padding: 14px 16px;
            color: var(--error);
            background: #FDECEC;
            border: 1px solid #F3C7C6;
            border-left: 4px solid var(--error);
            border-radius: 8px;
            line-height: 1.5;
        }

        .acciones {
            grid-column: 1 / -1;
            display: flex;
            align-items: center;
            flex-wrap: wrap;
            gap: 12px;
            margin-top: 22px;
            padding-top: 22px;
            border-top: 1px solid #E9EEF3;
        }

        .boton-guardar,
        .boton-volver {
            display: inline-flex;
            align-items: center;
            justify-content: center;
            min-height: 46px;
            padding: 12px 21px;
            border-radius: 8px;
            font-family: inherit;
            font-size: 14px;
            font-weight: 700;
            text-decoration: none;
            cursor: pointer;
            transition: background .2s, border-color .2s, transform .2s;
        }

        .boton-guardar {
            color: #FFFFFF;
            background: var(--sintel-azul);
            border: 1px solid var(--sintel-azul);
        }

        .boton-guardar:hover {
            background: #004078;
            border-color: #004078;
        }

        .boton-volver {
            color: var(--sintel-azul);
            background: #FFFFFF;
            border: 1px solid #C9D4E0;
        }

        .boton-volver:hover { background: #F0F5F9; border-color: #91A6BA; }

        .boton-guardar:focus-visible,
        .boton-volver:focus-visible {
            outline: 3px solid rgba(73, 204, 204, .55);
            outline-offset: 3px;
        }

        .mensaje-no-encontrado {
            max-width: 900px;
            margin: 0 auto;
            padding: 28px;
            background: #FFFFFF;
            border: 1px solid var(--borde);
            border-radius: 12px;
            box-shadow: 0 8px 24px rgba(0, 39, 79, .05);
        }

        .mensaje-no-encontrado h2 { margin-top: 0; }
        .mensaje-no-encontrado p { color: var(--texto-secundario); line-height: 1.6; }

        @media (max-width: 650px) {
            .contenido { padding: 25px 15px 35px; }
            .tarjeta-formulario { padding: 22px 17px; }
            #formEditarProyecto { grid-template-columns: minmax(0, 1fr); }
            .resumen-financiero, .acciones { grid-column: 1; }
            .resumen-financiero { padding: 17px; }
            .resumen-financiero .tasa { align-items: flex-start; flex-direction: column; gap: 8px; }
            .acciones { align-items: stretch; flex-direction: column; }
            .boton-guardar, .boton-volver { width: 100%; }
        }

        @media (prefers-reduced-motion: reduce) {
            *, *::before, *::after { transition: none !important; }
        }
    </style>
</head>

<body>

    <jsp:include page="navbar.jsp" />

    <main class="contenido">

        <header class="encabezado-pagina">
            <h1>Editar proyecto</h1>
            <p>
                Actualiza la información del proyecto.
                El IVA y el total se calcularán automáticamente.
            </p>
        </header>

        <% if (proyecto != null) { %>

            <section class="tarjeta-formulario">

                <h2>Información del proyecto</h2>

                <% if (tasaObjeto == null) { %>
                    <p class="mensaje-error">
                        No se pudo cargar la tasa de IVA.
                        Verifica la configuración antes de guardar.
                    </p>
                <% } %>

                <form
                    action="${pageContext.request.contextPath}/proyectos"
                    method="POST"
                    id="formEditarProyecto">

                    <input type="hidden" name="csrfToken" value="<%= csrfToken %>">

                    <input
                        type="hidden"
                        name="id"
                        value="<%= proyecto.getIdproyectos() %>"
                    >

                    <div class="grupo-campo">
                        <label for="nombre">Nombre del proyecto</label>

                        <input
                            type="text"
                            id="nombre"
                            name="nombre"
                            value="<%= proyecto.getNombre() %>"
                            maxlength="100"
                            required
                        >
                    </div>

                    <div class="grupo-campo">
                        <label for="idclientes">Cliente</label>

                        <select
                            id="idclientes"
                            name="idclientes"
                            required>

                            <% if (clientes != null) {
                                for (Cliente cliente : clientes) { %>

                                    <option
                                        value="<%= cliente.getIdclientes() %>"
                                        <%= cliente.getIdclientes()
                                                == proyecto.getIdclientes()
                                                ? "selected" : "" %>>
                                        <%= cliente.getNombre() %>
                                    </option>

                            <%  }
                            } %>

                        </select>
                    </div>

                    <div class="grupo-campo">
                        <label for="idestados">Estado</label>

                        <select
                            id="idestados"
                            name="idestados"
                            required>

                            <% if (estados != null) {
                                for (EstadoProyecto estado : estados) { %>

                                    <option
                                        value="<%= estado.getIdestados() %>"
                                        <%= estado.getIdestados()
                                                == proyecto.getIdestados()
                                                ? "selected" : "" %>>
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
                            value="<%= proyecto.getFechaEntrega() %>"
                            required
                        >
                    </div>

                    <div class="grupo-campo">
                        <label for="valorTotal">
                            Valor del proyecto antes del IVA (COP)
                        </label>

                        <input
                            type="number"
                            id="valorTotal"
                            name="valorTotal"
                            step="0.01"
                            min="0.01"
                            value="<%= proyecto.getValorTotal() %>"
                            required
                        >
                    </div>

                    <section class="resumen-financiero">

                        <p class="tasa">
                            <span>Tasa de IVA configurada</span>
                            <strong id="porcentajeIVA">
                                <%= tasaObjeto != null
                                        ? tasaConfigurada + "%"
                                        : "No disponible" %>
                            </strong>
                        </p>

                        <label for="ivaCalculado">IVA calculado</label>

                        <input
                            type="text"
                            id="ivaCalculado"
                            value=""
                            readonly
                            aria-live="polite"
                        >

                        <div class="total">
                            <label for="totalCalculado">Total con IVA</label>

                            <input
                                type="text"
                                id="totalCalculado"
                                value=""
                                readonly
                                aria-live="polite"
                            >
                        </div>

                        <p class="nota">
                            Estos importes se calculan usando el valor base
                            y la tasa de IVA configurada en Sintel.
                        </p>

                    </section>

                    <div class="acciones">

                        <button
                            type="submit"
                            class="boton-guardar">
                            Guardar cambios
                        </button>

                        <a
                            href="${pageContext.request.contextPath}/proyectos"
                            class="boton-volver">
                            Volver a proyectos
                        </a>

                    </div>

                </form>

            </section>

        <% } else { %>

            <section class="mensaje-no-encontrado">

                <h2>Proyecto no encontrado</h2>

                <p>
                    No se encontró la información del proyecto
                    que intentas editar.
                </p>

                <a
                    href="${pageContext.request.contextPath}/proyectos"
                    class="boton-guardar">
                    Volver a proyectos
                </a>

            </section>

        <% } %>

    </main>

    <% if (proyecto != null) { %>

        <script>
            const campoBase =
                document.getElementById("valorTotal");

            const campoIVA =
                document.getElementById("ivaCalculado");

            const campoTotal =
                document.getElementById("totalCalculado");

            const tasaIVA = Number("<%= tasaConfigurada %>");

            const formatoMoneda = new Intl.NumberFormat("es-CO", {
                style: "currency",
                currency: "COP",
                minimumFractionDigits: 2,
                maximumFractionDigits: 2
            });

            function calcularIVA() {
                const base = Number(campoBase.value);

                if (!Number.isFinite(base)
                        || base <= 0
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

            document.getElementById("formEditarProyecto")
                .addEventListener("submit", function(evento) {

                    if (!Number.isFinite(tasaIVA)) {
                        evento.preventDefault();

                        alert(
                            "No se pudo obtener la tasa de IVA. "
                            + "Verifica la configuración de Sintel."
                        );
                    }
                });

            calcularIVA();
        </script>

    <% } %>

</body>
</html>

