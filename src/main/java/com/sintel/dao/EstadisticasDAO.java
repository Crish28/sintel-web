
package com.sintel.dao;

import com.sintel.Conexion;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class EstadisticasDAO {

    public int[] obtenerEstadisticas() throws SQLException {

        String sql = """
                SELECT
                    COUNT(*) AS total,
                    COALESCE(SUM(
                        CASE WHEN idestados = 1
                        THEN 1 ELSE 0 END
                    ), 0) AS pendientes,
                    COALESCE(SUM(
                        CASE WHEN idestados = 2
                        THEN 1 ELSE 0 END
                    ), 0) AS en_proceso,
                    COALESCE(SUM(
                        CASE WHEN idestados = 3
                        THEN 1 ELSE 0 END
                    ), 0) AS finalizados,
                    COALESCE(SUM(
                        CASE WHEN idestados = 1
                        AND fecha_entrega < CURDATE()
                        THEN 1 ELSE 0 END
                    ), 0) AS atrasados
                FROM proyectos
                """;

        try (Connection conexion = Conexion.conectar();
             PreparedStatement consulta =
                     conexion.prepareStatement(sql);
             ResultSet resultado = consulta.executeQuery()) {

            if (resultado.next()) {

                return new int[] {
                    resultado.getInt("total"),
                    resultado.getInt("pendientes"),
                    resultado.getInt("en_proceso"),
                    resultado.getInt("finalizados"),
                    resultado.getInt("atrasados")
                };
            }
        }

        return new int[] {0, 0, 0, 0, 0};
    }
}
