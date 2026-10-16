package com.sintel.dao;

import com.sintel.Conexion;
import com.sintel.modelo.Proyecto;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class ProyectoDAO {

    // Listar todos los proyectos
    public List<Proyecto> listarTodos() throws SQLException {

        List<Proyecto> proyectos = new ArrayList<>();

        String sql = """
                SELECT idproyectos, nombre, idclientes,
                       idestados, fecha_entrega,
                       valor_total, iva
                FROM proyectos
                ORDER BY fecha_entrega
                """;

        try (
            Connection conexion = Conexion.conectar();
            PreparedStatement consulta =
                    conexion.prepareStatement(sql);
            ResultSet resultado = consulta.executeQuery()
        ) {

            while (resultado.next()) {

                Proyecto proyecto = new Proyecto();

                proyecto.setIdproyectos(
                        resultado.getInt("idproyectos")
                );

                proyecto.setNombre(
                        resultado.getString("nombre")
                );

                proyecto.setIdclientes(
                        resultado.getInt("idclientes")
                );

                proyecto.setIdestados(
                        resultado.getInt("idestados")
                );

                proyecto.setFechaEntrega(
                        resultado.getDate("fecha_entrega")
                                .toLocalDate()
                );

                proyecto.setValorTotal(
                        resultado.getBigDecimal("valor_total")
                );

                proyecto.setIva(
                        resultado.getBigDecimal("iva")
                );

                proyectos.add(proyecto);
            }
        }

        return proyectos;
    }

    // Registrar un nuevo proyecto
    public boolean registrar(Proyecto proyecto)
            throws SQLException {

        String sql = """
                INSERT INTO proyectos
                    (nombre, idclientes, idestados,
                     fecha_entrega, valor_total, iva)
                VALUES (?, ?, ?, ?, ?, ?)
                """;

        try (
            Connection conexion = Conexion.conectar();
            PreparedStatement consulta =
                    conexion.prepareStatement(sql)
        ) {

            consulta.setString(
                    1, proyecto.getNombre()
            );

            consulta.setInt(
                    2, proyecto.getIdclientes()
            );

            consulta.setInt(
                    3, proyecto.getIdestados()
            );

            consulta.setDate(
                    4,
                    java.sql.Date.valueOf(
                            proyecto.getFechaEntrega()
                    )
            );

            consulta.setBigDecimal(
                    5, proyecto.getValorTotal()
            );

            consulta.setBigDecimal(
                    6, proyecto.getIva()
            );

            int filasAfectadas =
                    consulta.executeUpdate();

            return filasAfectadas > 0;
        }
    }
    // Buscar un proyecto por su ID
    public Proyecto buscarPorId(int id) throws SQLException {

    String sql = """
            SELECT idproyectos, nombre, idclientes,
                   idestados, fecha_entrega,
                   valor_total, iva
            FROM proyectos
            WHERE idproyectos = ?
            """;

    try (
        Connection conexion = Conexion.conectar();
        PreparedStatement consulta =
                conexion.prepareStatement(sql)
        ) {

        consulta.setInt(1, id);

        try (ResultSet resultado = consulta.executeQuery()) {

            if (resultado.next()) {

                Proyecto proyecto = new Proyecto();

                proyecto.setIdproyectos(
                        resultado.getInt("idproyectos")
                );

                proyecto.setNombre(
                        resultado.getString("nombre")
                );

                proyecto.setIdclientes(
                        resultado.getInt("idclientes")
                );

                proyecto.setIdestados(
                        resultado.getInt("idestados")
                );

                proyecto.setFechaEntrega(
                        resultado.getDate("fecha_entrega")
                                .toLocalDate()
                );

                proyecto.setValorTotal(
                        resultado.getBigDecimal("valor_total")
                );

                proyecto.setIva(
                        resultado.getBigDecimal("iva")
                );

                return proyecto;
            }
          }
        }

        return null;
   }
   
   // Actualizar un proyecto existente
   public boolean actualizar(Proyecto proyecto)
        throws SQLException {

    String sql = """
            UPDATE proyectos
            SET nombre = ?,
                idclientes = ?,
                idestados = ?,
                fecha_entrega = ?,
                valor_total = ?,
                iva = ?
            WHERE idproyectos = ?
            """;

    try (
        Connection conexion = Conexion.conectar();
        PreparedStatement consulta =
                conexion.prepareStatement(sql)
        ) {

        consulta.setString(
                1,
                proyecto.getNombre()
        );

        consulta.setInt(
                2,
                proyecto.getIdclientes()
        );

        consulta.setInt(
                3,
                proyecto.getIdestados()
        );

        consulta.setDate(
                4,
                java.sql.Date.valueOf(
                        proyecto.getFechaEntrega()
                )
        );

        consulta.setBigDecimal(
                5,
                proyecto.getValorTotal()
        );

        consulta.setBigDecimal(
                6,
                proyecto.getIva()
        );

        consulta.setInt(
                7,
                proyecto.getIdproyectos()
        );

        int filasAfectadas =
                consulta.executeUpdate();

        return filasAfectadas > 0;
        }
    }


    // Eliminar un proyecto por su ID
   public boolean eliminar(int id) throws SQLException {

    String sql = """
            DELETE FROM proyectos
            WHERE idproyectos = ?
            """;

    try (
        Connection conexion = Conexion.conectar();
        PreparedStatement consulta =
                conexion.prepareStatement(sql)
        ) {

        consulta.setInt(1, id);

        int filasAfectadas =
                consulta.executeUpdate();

        return filasAfectadas > 0;
       }
   }
  
    // Listar proyectos por estado
  public List<Proyecto> listarPorEstado(int idEstado)
        throws SQLException {

    List<Proyecto> proyectos =
            new ArrayList<>();

    String sql = """
            SELECT idproyectos, nombre, idclientes,
                   idestados, fecha_entrega,
                   valor_total, iva
            FROM proyectos
            WHERE idestados = ?
            ORDER BY fecha_entrega
            """;

    try (
        Connection conexion = Conexion.conectar();

        PreparedStatement consulta =
                conexion.prepareStatement(sql)
    ) {

        consulta.setInt(1, idEstado);

        try (ResultSet resultado =
                     consulta.executeQuery()) {

            while (resultado.next()) {

                Proyecto proyecto =
                        new Proyecto();

                proyecto.setIdproyectos(
                        resultado.getInt("idproyectos")
                );

                proyecto.setNombre(
                        resultado.getString("nombre")
                );

                proyecto.setIdclientes(
                        resultado.getInt("idclientes")
                );

                proyecto.setIdestados(
                        resultado.getInt("idestados")
                );

                proyecto.setFechaEntrega(
                        resultado.getDate("fecha_entrega")
                                .toLocalDate()
                );

                proyecto.setValorTotal(
                        resultado.getBigDecimal(
                                "valor_total"
                        )
                );

                proyecto.setIva(
                        resultado.getBigDecimal("iva")
                );

                proyectos.add(proyecto);
            }
        }
    }

    return proyectos;
}

public List<Proyecto> listarPorFiltros(
        Integer idEstado,
        Integer idCliente)
        throws SQLException {

    List<Proyecto> proyectos =
            new ArrayList<>();

    StringBuilder sql = new StringBuilder("""
            SELECT idproyectos, nombre, idclientes,
                   idestados, fecha_entrega,
                   valor_total, iva
            FROM proyectos
            WHERE 1 = 1
            """);

    List<Object> parametros =
            new ArrayList<>();


    if (idEstado != null) {

        sql.append(" AND idestados = ?");
        parametros.add(idEstado);
    }


    if (idCliente != null) {

        sql.append(" AND idclientes = ?");
        parametros.add(idCliente);
    }


    sql.append(" ORDER BY fecha_entrega");


    try (
        Connection conexion = Conexion.conectar();

        PreparedStatement consulta =
                conexion.prepareStatement(sql.toString())
    ) {

        for (int i = 0; i < parametros.size(); i++) {

            consulta.setInt(
                    i + 1,
                    (Integer) parametros.get(i)
            );
        }


        try (ResultSet resultado =
                     consulta.executeQuery()) {

            while (resultado.next()) {

                Proyecto proyecto =
                        new Proyecto();

                proyecto.setIdproyectos(
                        resultado.getInt("idproyectos")
                );

                proyecto.setNombre(
                        resultado.getString("nombre")
                );

                proyecto.setIdclientes(
                        resultado.getInt("idclientes")
                );

                proyecto.setIdestados(
                        resultado.getInt("idestados")
                );

                proyecto.setFechaEntrega(
                        resultado.getDate("fecha_entrega")
                                .toLocalDate()
                );

                proyecto.setValorTotal(
                        resultado.getBigDecimal(
                                "valor_total"
                        )
                );

                proyecto.setIva(
                        resultado.getBigDecimal("iva")
                );

                proyectos.add(proyecto);
            }
        }
    }

    return proyectos;
}

}