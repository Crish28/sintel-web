package com.sintel.dao;

import com.sintel.Conexion;
import com.sintel.modelo.EstadoProyecto;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class EstadoProyectoDAO {

    // Listar todos los estados
    public List<EstadoProyecto> listarTodos()
            throws SQLException {

        List<EstadoProyecto> estados =
                new ArrayList<>();

        String sql = """
                SELECT idestados, nombre
                FROM estados_proyecto
                ORDER BY idestados
                """;

        try (
            Connection conexion = Conexion.conectar();
            PreparedStatement consulta =
                    conexion.prepareStatement(sql);
            ResultSet resultado =
                    consulta.executeQuery()
        ) {

            while (resultado.next()) {

                EstadoProyecto estado =
                        new EstadoProyecto();

                estado.setIdestados(
                        resultado.getInt("idestados")
                );

                estado.setNombre(
                        resultado.getString("nombre")
                );

                estados.add(estado);
            }
        }

        return estados;
    }
}