<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ page import="java.math.BigDecimal" %>

<%
    BigDecimal tasaIVA = (BigDecimal) request.getAttribute("tasaIVA");

    if (tasaIVA == null) {
        tasaIVA = BigDecimal.valueOf(19.00);
    }

    String error = request.getParameter("error");
    String actualizado = request.getParameter("actualizado");
%>

<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Configuración financiera | Sintel</title>

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
            max-width: 1000px;
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
            border-radius: 12px;
            padding: 28px;
            box-shadow: 0 3px 12px rgba(0, 39, 79, 0.08);
            max-width: 650px;
        }

        .tarjeta h2 {
            margin-top: 0;
            margin-bottom: 10px;
            font-size: 21px;
        }

        .descripcion {
            color: #667085;
            line-height: 1.5;
            margin-bottom: 24px;
        }

        .campo {
            margin-bottom: 20px;
        }

        .campo label {
            display: block;
            font-weight: bold;
            margin-bottom: 9px;
        }

        .entrada-porcentaje {
            display: flex;
            align-items: center;
            max-width: 350px;
            border: 1px solid #CBD5E1;
            border-radius: 7px;
            overflow: hidden;
            background: #FFFFFF;
        }

        .entrada-porcentaje input {
            width: 100%;
            min-width: 0;
            border: none;
            outline: none;
            padding: 13px;
            font-size: 17px;
            color: #00274F;
        }

        .simbolo {
            padding: 13px 16px;
            background: #EAF7F7;
            color: #00274F;
            font-weight: bold;
        }

        .ayuda {
            margin-top: 8px;
            color: #667085;
            font-size: 13px;
        }

        .boton {
            border: none;
            border-radius: 7px;
            padding: 12px 20px;
            background: #00274F;
            color: #FFFFFF;
            font-size: 15px;
            font-weight: bold;
            cursor: pointer;
            transition: background 0.2s;
        }

        .boton:hover {
            background: #004078;
        }

        .mensaje {
            padding: 13px 16px;
            border-radius: 7px;
            margin-bottom: 20px;
            line-height: 1.5;
        }

        .mensaje-exito {
            background: #DDF7E8;
            color: #166534;
            border: 1px solid #86D6A5;
        }

        .mensaje-error {
            background: #FDE8E7;
            color: #991B1B;
            border: 1px solid #E8A5A1;
        }

        .nota {
            margin-top: 24px;
            padding: 15px;
            background: #EAF7F7;
            border-left: 4px solid #49CCCC;
            border-radius: 4px;
            color: #00274F;
            line-height: 1.6;
            font-size: 14px;
        }

        @media (max-width: 600px) {
            .contenido {
                margin: 24px auto;
                padding: 0 15px;
            }

            .tarjeta {
                padding: 20px;
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
            <h1>Configuración</h1>
            <p>Administra los parámetros financieros de Sintel.</p>
        </header>

        <section class="tarjeta">
            <h2>Configuración del IVA</h2>

            <p class="descripcion">
                Define el porcentaje de IVA que se utilizará para calcular
                los impuestos de los proyectos.
            </p>

            <% if ("true".equals(actualizado)) { %>
                <div class="mensaje mensaje-exito" role="status">
                    La tasa de IVA se actualizó correctamente.
                </div>
            <% } %>

            <% if ("valor".equals(error)) { %>
                <div class="mensaje mensaje-error" role="alert">
                    Introduce un porcentaje válido.
                </div>
            <% } else if ("rango".equals(error)) { %>
                <div class="mensaje mensaje-error" role="alert">
                    El porcentaje debe estar entre 0 % y 100 %.
                </div>
            <% } %>

            <form action="${pageContext.request.contextPath}/configuracion"
                  method="post">

                <input type="hidden" name="csrfToken"
                       value="<%= com.sintel.HtmlUtil.escapar(
                             com.sintel.CsrfUtil.obtenerToken(request)) %>">

                <div class="campo">
                    <label for="tasaIVA">Porcentaje de IVA</label>

                    <div class="entrada-porcentaje">
                        <input
                            type="number"
                            id="tasaIVA"
                            name="tasaIVA"
                            min="0"
                            max="100"
                            step="0.01"
                            value="<%= tasaIVA.toPlainString() %>"
                            required
                        >
                        <span class="simbolo">%</span>
                    </div>

                    <p class="ayuda">
                        Puedes ingresar valores entre 0 y 100, incluidos
                        los decimales.
                    </p>
                </div>

                <button type="submit" class="boton">
                    Guardar configuración
                </button>

            </form>

            <div class="nota">
                <strong>Importante:</strong> esta tasa se utilizará para
                calcular el IVA de los proyectos cuando se registren o
                editen. Los valores históricos guardados no se recalculan
                automáticamente.
            </div>
        </section>

    </main>

</body>
</html>
