package com.sintel;

public final class HtmlUtil {

    private HtmlUtil() {
        // Evita crear objetos de esta clase.
    }

    public static String escapar(String texto) {

        if (texto == null) {
            return "";
        }

        StringBuilder resultado = new StringBuilder();

        for (int i = 0; i < texto.length(); i++) {

            char caracter = texto.charAt(i);

            switch (caracter) {
                case '&':
                    resultado.append("&amp;");
                    break;
                case '<':
                    resultado.append("&lt;");
                    break;
                case '>':
                    resultado.append("&gt;");
                    break;
                case '"':
                    resultado.append("&quot;");
                    break;
                case '\'':
                    resultado.append("&#39;");
                    break;
                default:
                    resultado.append(caracter);
            }
        }

        return resultado.toString();
    }
}
