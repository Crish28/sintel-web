<%@ page language="java"
         contentType="text/html; charset=UTF-8"
         pageEncoding="UTF-8" %>

<%@ page import="com.sintel.CsrfUtil" %>
<%@ page import="com.sintel.modelo.Usuario" %>

<%
    String csrfToken = CsrfUtil.obtenerToken(request);

    Object atributoUsuario = session.getAttribute("usuario");
    Usuario usuarioActual = null;

    if (atributoUsuario instanceof Usuario) {
        usuarioActual = (Usuario) atributoUsuario;
    }

    boolean esAdministrador =
            usuarioActual != null
            && "Administrador".equals(usuarioActual.getRol());
%>

<nav class="barra-navegacion">

    <a class="marca-sintel"
       href="${pageContext.request.contextPath}/">
        Sintel
    </a>

    <div class="enlaces-navegacion">

        <a href="${pageContext.request.contextPath}/proyectos">
            Proyectos
        </a>

        <a href="${pageContext.request.contextPath}/clientes">
            Clientes
        </a>

        <% if (esAdministrador) { %>

            <a href="${pageContext.request.contextPath}/configuracion">
                Configuración
            </a>

            <a href="${pageContext.request.contextPath}/usuarios">
                Usuarios
            </a>

        <% } %>

    </div>

    <form class="formulario-cerrar-sesion"
          action="${pageContext.request.contextPath}/logout"
          method="POST">

        <input type="hidden"
               name="csrfToken"
               value="<%= com.sintel.HtmlUtil.escapar(csrfToken) %>">

        <button type="submit" class="boton-cerrar-sesion">
            Cerrar sesión
        </button>

    </form>

</nav>

<style>
    .barra-navegacion {
        display: flex;
        align-items: center;
        justify-content: space-between;
        gap: 24px;
        padding: 18px 30px;
        background-color: #00274F;
        color: #FFFFFF;
        box-sizing: border-box;
        width: 100%;
        margin: 0;
    }

    .marca-sintel {
        color: #FFFFFF;
        text-decoration: none;
        font-size: 26px;
        font-weight: bold;
        flex-shrink: 0;
    }

    .enlaces-navegacion {
        display: flex;
        align-items: center;
        justify-content: center;
        gap: 24px;
        flex-wrap: wrap;
    }

    .enlaces-navegacion a {
        color: #FFFFFF;
        text-decoration: none;
        font-weight: bold;
        transition: color 0.2s;
    }

    .enlaces-navegacion a:hover,
    .marca-sintel:hover {
        color: #49CCCC;
    }

    .formulario-cerrar-sesion {
        display: inline-flex;
        align-items: center;
        margin: 0;
        flex-shrink: 0;
    }

    .boton-cerrar-sesion {
        background-color: #B8201F;
        color: #FFFFFF;
        border: none;
        text-decoration: none;
        padding: 10px 16px;
        border-radius: 5px;
        font-family: inherit;
        font-size: 14px;
        font-weight: bold;
        white-space: nowrap;
        cursor: pointer;
        transition: background-color 0.2s;
    }

    .boton-cerrar-sesion:hover {
        background-color: #8F1918;
    }

    .boton-cerrar-sesion:focus-visible,
    .enlaces-navegacion a:focus-visible,
    .marca-sintel:focus-visible {
        outline: 2px solid #49CCCC;
        outline-offset: 4px;
    }

    @media (max-width: 700px) {
        .barra-navegacion {
            flex-wrap: wrap;
            padding: 16px 20px;
            gap: 16px;
        }

        .enlaces-navegacion {
            order: 3;
            width: 100%;
            justify-content: flex-start;
            gap: 16px;
        }

        .formulario-cerrar-sesion {
            margin-left: auto;
        }
    }

    @media (max-width: 400px) {
        .barra-navegacion {
            padding: 14px 16px;
        }

        .marca-sintel {
            font-size: 23px;
        }

        .enlaces-navegacion {
            gap: 14px;
        }
    }
</style>

