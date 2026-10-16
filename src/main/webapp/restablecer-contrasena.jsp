<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Nueva contraseña | Sintel</title>

    <style>
        * {
            box-sizing: border-box;
        }

        body {
            margin: 0;
            min-height: 100vh;
            display: flex;
            justify-content: center;
            align-items: center;
            padding: 20px;
            background: #f3f6f8;
            font-family: Arial, sans-serif;
            color: #263238;
        }

        .contenedor {
            width: 100%;
            max-width: 440px;
            padding: 35px;
            background: white;
            border-radius: 12px;
            box-shadow: 0 5px 24px rgba(0, 39, 79, 0.12);
        }

        h1 {
            margin-top: 0;
            color: #00274F;
            text-align: center;
            font-size: 25px;
        }

        p {
            color: #5f6b73;
            line-height: 1.5;
        }

        label {
            display: block;
            margin: 18px 0 8px;
            font-weight: bold;
        }

        input {
            width: 100%;
            padding: 12px;
            border: 1px solid #cbd5dc;
            border-radius: 6px;
            font-size: 15px;
        }

        input:focus {
            outline: 2px solid #49CCCC;
            border-color: #49CCCC;
        }

        button {
            width: 100%;
            margin-top: 24px;
            padding: 13px;
            background: #00274F;
            color: white;
            border: none;
            border-radius: 6px;
            font-size: 15px;
            font-weight: bold;
            cursor: pointer;
        }

        button:hover {
            background: #064477;
        }

        .error {
            padding: 12px;
            background: #fff0ef;
            border: 1px solid #e6b5b2;
            border-radius: 6px;
            color: #9b2420;
            line-height: 1.5;
        }

        .volver {
            display: block;
            margin-top: 22px;
            text-align: center;
            color: #00274F;
            text-decoration: none;
        }

        .volver:hover {
            text-decoration: underline;
        }
    </style>
</head>
<body>

    <main class="contenedor">
        <h1>Nueva contraseña</h1>

        <p>
            Crea una contraseña de al menos 12 caracteres.
            Después podrás iniciar sesión con ella.
        </p>

        <% if (request.getAttribute("error") != null) { %>
            <div class="error" role="alert">
                <%= com.sintel.HtmlUtil.escapar(
                    String.valueOf(request.getAttribute("error"))) %>
            </div>
        <% } %>

        <% if (request.getAttribute("token") != null) { %>

            <form action="${pageContext.request.contextPath}/restablecer-contrasena"
                  method="post">

                <input type="hidden" name="csrfToken"
                       value="<%= com.sintel.HtmlUtil.escapar(
                           String.valueOf(request.getAttribute("csrfToken"))) %>">

                <input type="hidden" name="token"
                       value="<%= com.sintel.HtmlUtil.escapar(
                           String.valueOf(request.getAttribute("token"))) %>">

                <label for="password">Nueva contraseña</label>
                <input type="password"
                       id="password"
                       name="password"
                       minlength="12"
                       maxlength="72"
                       autocomplete="new-password"
                       required>

                <label for="confirmarPassword">Confirmar contraseña</label>
                <input type="password"
                       id="confirmarPassword"
                       name="confirmarPassword"
                       minlength="12"
                       maxlength="72"
                       autocomplete="new-password"
                       required>

                <button type="submit">Guardar nueva contraseña</button>
            </form>

        <% } %>

        <a class="volver"
           href="${pageContext.request.contextPath}/login">
            Volver al inicio de sesión
        </a>
    </main>

</body>
</html>
