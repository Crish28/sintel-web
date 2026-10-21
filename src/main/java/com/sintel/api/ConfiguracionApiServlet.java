package com.sintel.api;

import com.google.gson.JsonObject;
import com.sintel.dao.ConfiguracionDAO;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.SQLException;
import java.util.LinkedHashMap;
import java.util.Map;

@WebServlet("/api/configuracion")
public class ConfiguracionApiServlet extends HttpServlet {

    private ConfiguracionDAO configuracionDAO;

    @Override
    public void init() throws ServletException {
        this.configuracionDAO = new ConfiguracionDAO();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        try {
            double tasa = configuracionDAO.obtenerTasaIVA();
            BigDecimal tasaBd = BigDecimal.valueOf(tasa).setScale(4, RoundingMode.HALF_UP);
            BigDecimal porcentaje = tasaBd.multiply(BigDecimal.valueOf(100)).setScale(2, RoundingMode.HALF_UP);

            Map<String, Object> conf = new LinkedHashMap<>();
            conf.put("tasaIva", tasa);
            conf.put("porcentaje", porcentaje + "%");

            JsonUtil.responder(response, HttpServletResponse.SC_OK, conf);
        } catch (SQLException e) {
            JsonUtil.responderError(response, HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
                    "Error al obtener configuración de IVA: " + e.getMessage());
        }
    }

    @Override
    protected void doPut(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        try {
            JsonObject json = JsonUtil.leerCuerpo(request, JsonObject.class);

            if (json == null || !json.has("tasaIva")) {
                JsonUtil.responderError(response, HttpServletResponse.SC_BAD_REQUEST,
                        "Debe especificar el campo 'tasaIva' (por ejemplo 0.19 para 19%).");
                return;
            }

            BigDecimal nuevaTasa = json.get("tasaIva").getAsBigDecimal();

            if (nuevaTasa.compareTo(BigDecimal.ZERO) < 0 || nuevaTasa.compareTo(BigDecimal.ONE) > 0) {
                JsonUtil.responderError(response, HttpServletResponse.SC_BAD_REQUEST,
                        "La tasa de IVA debe estar en el rango de 0.00 a 1.00 (ejemplo: 0.19).");
                return;
            }

            configuracionDAO.actualizarTasaIVA(nuevaTasa);

            Map<String, Object> resp = new LinkedHashMap<>();
            resp.put("tasaIva", nuevaTasa.doubleValue());
            resp.put("porcentaje", nuevaTasa.multiply(BigDecimal.valueOf(100)).setScale(2, RoundingMode.HALF_UP) + "%");

            JsonUtil.responderExito(response, HttpServletResponse.SC_OK,
                    "Tasa de IVA actualizada exitosamente.", resp);
        } catch (Exception e) {
            JsonUtil.responderError(response, HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
                    "Error al actualizar la configuración: " + e.getMessage());
        }
    }
}
