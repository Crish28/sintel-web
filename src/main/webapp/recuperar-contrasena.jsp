<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Recuperar contraseña | Sintel</title>

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
            max-width: 420px;
            padding: 35px;
            background: #ffffff;
            border-radius: 12px;
            box-shadow: 0 5px 24px rgba(0, 39, 79, 0.12);
        }

        h1 {
            margin-top: 0;
            color: #00274F;
            text-align: center;
            font-size: 25px;
        }

        .descripcion {
            margin-bottom: 25px;
            color: #5f6b73;
            line-height: 1.6;
            text-align: center;
        }

        label {
            display: block;
            margin-bottom: 8px;
            font-weight: bold;
        }

        input[type="email"] {
            width: 100%;
            padding: 12px;
            margin-bottom: 18px;
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

        .mensaje {
            padding: 12px;
            margin-bottom: 18px;
            background: #e5f7ef;
            border: 1px solid #9ad9bd;
            border-radius: 6px;
            color: #17633d;
            line-height: 1.5;
        }

        .error {
            padding: 12px;
            margin-bottom: 18px;
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
        <h1>Recuperar contraseña</h1>

        <p class="descripcion">
            Ingresa el correo electrónico asociado a tu cuenta.
            Si corresponde a una cuenta activa, recibirás un enlace
            para crear una nueva contraseña.
        </p>

        <% if (request.getAttribute("mensaje") != null) { %>
            <div class="mensaje" role="status">
                <%= com.sintel.HtmlUtil.escapar(
                    String.valueOf(request.getAttribute("mensaje"))) %>
            </div>
        <% } %>

        <% if (request.getAttribute("error") != null) { %>
            <div class="error" role="alert">
                <%= com.sintel.HtmlUtil.escapar(
                    String.valueOf(request.getAttribute("error"))) %>
            </div>
        <% } %>

        <form action="${pageContext.request.contextPath}/recuperar-contrasena"
              method="post">

            <input type="hidden" name="csrfToken"
                   value="<%= com.sintel.HtmlUtil.escapar(
                       String.valueOf(request.getAttribute("csrfToken"))) %>">

            <label for="correo">Correo electrónico</label>

            <input type="email"
                   id="correo"
                   name="correo"
                   maxlength="45"
                   autocomplete="email"
                   placeholder="ejemplo@correo.com"
                   required>

            <button type="submit">Enviar enlace de recuperación</button>
        </form>

        <a class="volver"
           href="${pageContext.request.contextPath}/login">
            Volver al inicio de sesión
        </a>
    </main>

</body>
</html>
