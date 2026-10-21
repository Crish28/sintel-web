package com.sintel.api;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonSerializer;
import com.google.gson.JsonPrimitive;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.BufferedReader;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.LinkedHashMap;
import java.util.Map;

public final class JsonUtil {

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ISO_LOCAL_DATE;

    private static final Gson GSON = new GsonBuilder()
            .registerTypeAdapter(LocalDate.class, (JsonSerializer<LocalDate>) (src, typeOfSrc, context) ->
                    new JsonPrimitive(src.format(DATE_FORMATTER)))
            .registerTypeAdapter(LocalDate.class, (JsonDeserializer<LocalDate>) (json, typeOfT, context) ->
                    LocalDate.parse(json.getAsString(), DATE_FORMATTER))
            .setPrettyPrinting()
            .serializeNulls()
            .create();

    private JsonUtil() {
    }

    public static Gson getGson() {
        return GSON;
    }

    public static void responder(HttpServletResponse response, int statusCode, Object data) throws IOException {
        response.setStatus(statusCode);
        response.setContentType("application/json;charset=UTF-8");
        response.setCharacterEncoding("UTF-8");
        response.setHeader("Cache-Control", "no-cache, no-store, must-revalidate");
        response.getWriter().write(GSON.toJson(data));
    }

    public static void responderExito(HttpServletResponse response, int statusCode, String mensaje, Object data) throws IOException {
        Map<String, Object> resp = new LinkedHashMap<>();
        resp.put("exito", true);
        resp.put("codigo", statusCode);
        if (mensaje != null) {
            resp.put("mensaje", mensaje);
        }
        if (data != null) {
            resp.put("datos", data);
        }
        responder(response, statusCode, resp);
    }

    public static void responderError(HttpServletResponse response, int statusCode, String mensaje) throws IOException {
        Map<String, Object> resp = new LinkedHashMap<>();
        resp.put("exito", false);
        resp.put("codigo", statusCode);
        resp.put("error", mensaje);
        responder(response, statusCode, resp);
    }

    public static <T> T leerCuerpo(HttpServletRequest request, Class<T> clazz) throws IOException {
        try (BufferedReader reader = request.getReader()) {
            return GSON.fromJson(reader, clazz);
        } catch (Exception e) {
            return null;
        }
    }

    public static Integer extraerId(HttpServletRequest request) {
        String pathInfo = request.getPathInfo();
        if (pathInfo != null && pathInfo.length() > 1) {
            String segmento = pathInfo.substring(1);
            if (segmento.contains("/")) {
                segmento = segmento.substring(0, segmento.indexOf("/"));
            }
            try {
                return Integer.parseInt(segmento.trim());
            } catch (NumberFormatException ignored) {
            }
        }
        String idParam = request.getParameter("id");
        if (idParam != null && !idParam.isBlank()) {
            try {
                return Integer.parseInt(idParam.trim());
            } catch (NumberFormatException ignored) {
            }
        }
        return null;
    }
}
