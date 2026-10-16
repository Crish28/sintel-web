<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html lang="es">

<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Iniciar sesión - Sintel</title>

    <style>
        * {
            box-sizing: border-box;
        }

        body {
            margin: 0;
            font-family: Arial, sans-serif;
            background-color: #FFFFFF;
            display: flex;
            justify-content: center;
            align-items: center;
            min-height: 100vh;
            padding: 20px;
        }

        .login-container {
            width: 100%;
            max-width: 410px;
            padding: 30px;
            border-radius: 10px;
            box-shadow: 0 3px 10px rgba(0, 0, 0, 0.15);
            background-color: white;
        }

        h1 {
            text-align: center;
            color: #00274F;
            margin-bottom: 10px;
        }

        .subtitulo {
            text-align: center;
            color: #666;
            margin-bottom: 25px;
            line-height: 1.5;
        }

        label {
            display: block;
            margin-top: 15px;
            margin-bottom: 5px;
            font-weight: bold;
            color: #00274F;
        }

        input {
            width: 100%;
            padding: 11px;
            border: 1px solid #ccc;
            border-radius: 5px;
            font-size: 15px;
        }

        input:focus {
            outline: 2px solid #49CCCC;
            border-color: #49CCCC;
        }

        button {
            width: 100%;
            margin-top: 25px;
            padding: 12px;
            background-color: #00274F;
            color: white;
            border: none;
            border-radius: 5px;
            cursor: pointer;
            font-size: 15px;
            font-weight: bold;
        }

        button:hover {
            background-color: #49CCCC;
            color: #00274F;
        }

        .mensaje-error {
            background-color: #B8201F;
            color: white;
            padding: 12px;
            border-radius: 5px;
            margin-bottom: 15px;
            text-align: center;
            line-height: 1.5;
        }

        .mensaje-exito {
            background-color: #e5f7ef;
            color: #17633d;
            border: 1px solid #9ad9bd;
            padding: 12px;
            border-radius: 5px;
            margin-bottom: 15px;
            text-align: center;
            line-height: 1.5;
        }

        .recuperar {
            display: block;
            margin-top: 18px;
            text-align: center;
            color: #00274F;
            text-decoration: none;
            font-size: 14px;
        }

        .recuperar:hover {
            color: #087f91;
            text-decoration: underline;
        }

        @media (max-width: 480px) {
            .login-container {
                padding: 24px 20px;
            }
        }
    </style>
</head>

<body>

    <div class="login-container">

        <h1>Sintel</h1>

        <div class="subtitulo">
            Gestión de proyectos de seguridad electrónica
        </div>

        <%
            String error = (String) request.getAttribute("error");
            String mensaje = (String) request.getAttribute("mensaje");
        %>

        <% if (mensaje != null) { %>
            <div class="mensaje-exito" role="status">
                <%= com.sintel.HtmlUtil.escapar(mensaje) %>
            </div>
        <% } %>

        <% if (error != null) { %>
            <div class="mensaje-error" role="alert">
                <%= com.sintel.HtmlUtil.escapar(error) %>
            </div>
        <% } %>

        <form action="${pageContext.request.contextPath}/login" method="POST">

            <label for="correo">
                Correo electrónico:
            </label>

            <input
                type="email"
                id="correo"
                name="correo"
                maxlength="45"
                autocomplete="username"
                required
            >

            <label for="contrasena">
                Contraseña:
            </label>

            <input
                type="password"
                id="contrasena"
                name="contraseña"
                autocomplete="current-password"
                required
            >

            <button type="submit">
                Iniciar sesión
            </button>

        </form>

        <a class="recuperar"
           href="${pageContext.request.contextPath}/recuperar-contrasena">
            ¿Olvidaste tu contraseña?
        </a>

    </div>

</body>
</html>
