package com.sintel.dao;

import com.sintel.Conexion;
import com.sintel.modelo.Cliente;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class ClienteDAO {

    // Consultar todos los clientes
    public List<Cliente> listarTodos() throws SQLException {

        List<Cliente> clientes = new ArrayList<>();

        String sql = """
                SELECT idclientes, nombre, correo,
                       telefono, direccion, fecha_registro
                FROM clientes
                ORDER BY nombre
                """;

        try (
            Connection conexion = Conexion.conectar();
            PreparedStatement consulta =
                    conexion.prepareStatement(sql);
            ResultSet resultado = consulta.executeQuery()
        ) {

            while (resultado.next()) {

                Cliente cliente = new Cliente();

                cliente.setIdclientes(
                        resultado.getInt("idclientes")
                );

                cliente.setNombre(
                        resultado.getString("nombre")
                );

                cliente.setCorreo(
                        resultado.getString("correo")
                );

                cliente.setTelefono(
                        resultado.getString("telefono")
                );

                cliente.setDireccion(
                        resultado.getString("direccion")
                );

                cliente.setFechaRegistro(
                        resultado.getDate("fecha_registro")
                                .toLocalDate()
                );

                clientes.add(cliente);
            }
        }

        return clientes;
    }

    // Registrar un nuevo cliente
    public boolean registrar(Cliente cliente)
            throws SQLException {

        String sql = """
                INSERT INTO clientes
                    (nombre, correo, telefono, direccion)
                VALUES (?, ?, ?, ?)
                """;

        try (
            Connection conexion = Conexion.conectar();
            PreparedStatement consulta =
                    conexion.prepareStatement(sql)
        ) {

            consulta.setString(1, cliente.getNombre());
            consulta.setString(2, cliente.getCorreo());
            consulta.setString(3, cliente.getTelefono());
            consulta.setString(4, cliente.getDireccion());

            int filasAfectadas = consulta.executeUpdate();

            return filasAfectadas > 0;
        }
    }

    // Buscar un cliente por su ID
    public Cliente buscarPorId(int id) throws SQLException {

        String sql = """
                SELECT idclientes, nombre, correo,
                       telefono, direccion, fecha_registro
                FROM clientes
                WHERE idclientes = ?
                """;

        try (
            Connection conexion = Conexion.conectar();
            PreparedStatement consulta =
                    conexion.prepareStatement(sql)
        ) {

            consulta.setInt(1, id);

            try (ResultSet resultado = consulta.executeQuery()) {

                if (resultado.next()) {

                    Cliente cliente = new Cliente();

                    cliente.setIdclientes(
                            resultado.getInt("idclientes")
                    );

                    cliente.setNombre(
                            resultado.getString("nombre")
                    );

                    cliente.setCorreo(
                            resultado.getString("correo")
                    );

                    cliente.setTelefono(
                            resultado.getString("telefono")
                    );

                    cliente.setDireccion(
                            resultado.getString("direccion")
                    );

                    cliente.setFechaRegistro(
                            resultado.getDate("fecha_registro")
                                    .toLocalDate()
                    );

                    return cliente;
                }
            }
        }

        return null;
    }

    // Actualizar los datos de un cliente
    public boolean actualizar(Cliente cliente) throws SQLException {

        String sql = """
                UPDATE clientes
                SET nombre = ?,
                    correo = ?,
                    telefono = ?,
                    direccion = ?
                WHERE idclientes = ?
                """;

        try (
            Connection conexion = Conexion.conectar();
            PreparedStatement consulta =
                    conexion.prepareStatement(sql)
        ) {

            consulta.setString(1, cliente.getNombre());
            consulta.setString(2, cliente.getCorreo());
            consulta.setString(3, cliente.getTelefono());
            consulta.setString(4, cliente.getDireccion());
            consulta.setInt(5, cliente.getIdclientes());

            int filasAfectadas = consulta.executeUpdate();

            return filasAfectadas > 0;
        }
    }

    // Comprobar si un cliente tiene proyectos asociados
    public boolean tieneProyectosAsociados(int idCliente)
            throws SQLException {

        String sql = """
                SELECT COUNT(*)
                FROM proyectos
                WHERE idclientes = ?
                """;

        try (
            Connection conexion = Conexion.conectar();
            PreparedStatement consulta =
                    conexion.prepareStatement(sql)
        ) {

            consulta.setInt(1, idCliente);

            try (ResultSet resultado = consulta.executeQuery()) {

                if (resultado.next()) {
                    return resultado.getInt(1) > 0;
                }
            }
        }

        return false;
    }

    // Eliminar un cliente solo si no tiene proyectos asociados
    public boolean eliminar(int id) throws SQLException {

        if (tieneProyectosAsociados(id)) {
            throw new SQLException(
                    "No se puede eliminar el cliente porque "
                    + "tiene proyectos asociados."
            );
        }

        String sql = """
                DELETE FROM clientes
                WHERE idclientes = ?
                """;

        try (
            Connection conexion = Conexion.conectar();
            PreparedStatement consulta =
                    conexion.prepareStatement(sql)
        ) {

            consulta.setInt(1, id);

            int filasAfectadas = consulta.executeUpdate();

            return filasAfectadas > 0;
        }
    }

    // Comprobar si un correo ya está registrado
    public boolean correoExiste(String correo) throws SQLException {

        String sql = """
                SELECT idclientes
                FROM clientes
                WHERE correo = ?
                """;

        try (
            Connection conexion = Conexion.conectar();
            PreparedStatement consulta =
                    conexion.prepareStatement(sql)
        ) {

            consulta.setString(1, correo);

            try (ResultSet resultado = consulta.executeQuery()) {
                return resultado.next();
            }
        }
    }

    // Comprobar si un correo pertenece a otro cliente
    public boolean correoExisteEnOtroCliente(
            String correo, int idCliente) throws SQLException {

        String sql = """
                SELECT idclientes
                FROM clientes
                WHERE correo = ?
                  AND idclientes <> ?
                """;

        try (
            Connection conexion = Conexion.conectar();
            PreparedStatement consulta =
                    conexion.prepareStatement(sql)
        ) {

            consulta.setString(1, correo);
            consulta.setInt(2, idCliente);

            try (ResultSet resultado = consulta.executeQuery()) {
                return resultado.next();
            }
        }
    }

    // Registrar un nuevo cliente y retornar el ID generado
    public int registrarRetornandoId(Cliente cliente) throws SQLException {
        String sql = """
                INSERT INTO clientes
                    (nombre, correo, telefono, direccion)
                VALUES (?, ?, ?, ?)
                """;

        try (
            Connection conexion = Conexion.conectar();
            PreparedStatement consulta =
                    conexion.prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS)
        ) {
            consulta.setString(1, cliente.getNombre());
            consulta.setString(2, cliente.getCorreo());
            consulta.setString(3, cliente.getTelefono());
            consulta.setString(4, cliente.getDireccion());

            int filasAfectadas = consulta.executeUpdate();
            if (filasAfectadas > 0) {
                try (ResultSet rs = consulta.getGeneratedKeys()) {
                    if (rs.next()) {
                        int idGenerado = rs.getInt(1);
                        cliente.setIdclientes(idGenerado);
                        return idGenerado;
                    }
                }
            }
            return 0;
        }
    }
}

