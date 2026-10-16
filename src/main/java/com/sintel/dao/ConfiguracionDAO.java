package com.sintel.dao;

import com.sintel.Conexion;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class ConfiguracionDAO {

    public double obtenerTasaIVA() throws SQLException {

        String sql = "SELECT tasa_iva FROM configuracion "
                   + "ORDER BY idconfiguracion DESC LIMIT 1";

        try (Connection conexion = Conexion.conectar();
             PreparedStatement ps = conexion.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            if (rs.next()) {
                return rs.getDouble("tasa_iva");
            }

            throw new SQLException(
                "No se encontró una tasa de IVA configurada."
            );
        }
    }

public void actualizarTasaIVA(java.math.BigDecimal nuevaTasa) throws SQLException {
    String sqlActualizar = """
            UPDATE configuracion
            SET tasa_iva = ?
            ORDER BY idconfiguracion DESC
            LIMIT 1
            """;

    try (Connection conexion = Conexion.conectar();
         PreparedStatement ps = conexion.prepareStatement(sqlActualizar)) {

        ps.setBigDecimal(1, nuevaTasa);
        int filasActualizadas = ps.executeUpdate();

        // Si todavía no existe una configuración, crea la primera.
        if (filasActualizadas == 0) {
            String sqlInsertar =
                    "INSERT INTO configuracion (tasa_iva) VALUES (?)";

            try (PreparedStatement insertar =
                         conexion.prepareStatement(sqlInsertar)) {
                insertar.setBigDecimal(1, nuevaTasa);
                insertar.executeUpdate();
            }
        }
    }
}

}
