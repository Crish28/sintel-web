package com.sintel.dao;

import com.sintel.Conexion;
import com.sintel.modelo.Usuario;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class UsuarioDAO {

    // Buscar un usuario por correo.
    // Incluye las cuentas desactivadas para que el login
    // pueda comprobar su estado.
    public Usuario buscarPorCorreo(String correo)
            throws SQLException {

        String sql = """
                SELECT idusuario, nombre, correo,
                       password_hash, rol, activo
                FROM usuarios
                WHERE correo = ?
                """;

        try (Connection conexion = Conexion.conectar();
             PreparedStatement consulta =
                     conexion.prepareStatement(sql)) {

            consulta.setString(1, correo);

            try (ResultSet resultado = consulta.executeQuery()) {
                if (resultado.next()) {
                    return mapearUsuario(resultado);
                }
            }
        }

        return null;
    }

    // Registrar un usuario nuevo.
    public boolean registrar(Usuario usuario)
            throws SQLException {

        String sql = """
                INSERT INTO usuarios
                    (nombre, correo, password_hash, rol, activo)
                VALUES (?, ?, ?, ?, ?)
                """;

        try (Connection conexion = Conexion.conectar();
             PreparedStatement consulta =
                     conexion.prepareStatement(sql)) {

            consulta.setString(1, usuario.getNombre());
            consulta.setString(2, usuario.getCorreo());
            consulta.setString(3, usuario.getPasswordHash());
            consulta.setString(4, usuario.getRol());
            consulta.setBoolean(5, true);

            return consulta.executeUpdate() > 0;
        }
    }

    // Listar todos los usuarios sin mostrar sus contraseñas.
    public List<Usuario> listar()
            throws SQLException {

        List<Usuario> usuarios = new ArrayList<>();

        String sql = """
                SELECT idusuario, nombre, correo, rol, activo
                FROM usuarios
                ORDER BY nombre ASC
                """;

        try (Connection conexion = Conexion.conectar();
             PreparedStatement consulta =
                     conexion.prepareStatement(sql);
             ResultSet resultado = consulta.executeQuery()) {

            while (resultado.next()) {
                Usuario usuario = new Usuario();

                usuario.setIdusuario(
                        resultado.getInt("idusuario"));
                usuario.setNombre(
                        resultado.getString("nombre"));
                usuario.setCorreo(
                        resultado.getString("correo"));
                usuario.setRol(
                        resultado.getString("rol"));
                usuario.setActivo(
                        resultado.getBoolean("activo"));

                usuarios.add(usuario);
            }
        }

        return usuarios;
    }

    // Buscar un usuario por su ID.
    public Usuario buscarPorId(int idusuario)
            throws SQLException {

        String sql = """
                SELECT idusuario, nombre, correo,
                       password_hash, rol, activo
                FROM usuarios
                WHERE idusuario = ?
                """;

        try (Connection conexion = Conexion.conectar();
             PreparedStatement consulta =
                     conexion.prepareStatement(sql)) {

            consulta.setInt(1, idusuario);

            try (ResultSet resultado = consulta.executeQuery()) {
                if (resultado.next()) {
                    return mapearUsuario(resultado);
                }
            }
        }

        return null;
    }

    // Actualizar nombre, correo y rol.
    // La contraseña se modifica mediante otro método.
    public boolean actualizar(Usuario usuario)
            throws SQLException {

        String sql = """
                UPDATE usuarios
                SET nombre = ?, correo = ?, rol = ?
                WHERE idusuario = ?
                """;

        try (Connection conexion = Conexion.conectar();
             PreparedStatement consulta =
                     conexion.prepareStatement(sql)) {

            consulta.setString(1, usuario.getNombre());
            consulta.setString(2, usuario.getCorreo());
            consulta.setString(3, usuario.getRol());
            consulta.setInt(4, usuario.getIdusuario());

            return consulta.executeUpdate() > 0;
        }
    }

    // Cambiar el estado de una cuenta.
    public boolean cambiarEstado(int idusuario, boolean activo)
            throws SQLException {

        String sql = """
                UPDATE usuarios
                SET activo = ?
                WHERE idusuario = ?
                """;

        try (Connection conexion = Conexion.conectar();
             PreparedStatement consulta =
                     conexion.prepareStatement(sql)) {

            consulta.setBoolean(1, activo);
            consulta.setInt(2, idusuario);

            return consulta.executeUpdate() > 0;
        }
    }

    // Actualizar la contraseña ya cifrada con BCrypt.
    public boolean actualizarPassword(
            int idusuario, String passwordHash)
            throws SQLException {

        String sql = """
                UPDATE usuarios
                SET password_hash = ?
                WHERE idusuario = ?
                """;

        try (Connection conexion = Conexion.conectar();
             PreparedStatement consulta =
                     conexion.prepareStatement(sql)) {

            consulta.setString(1, passwordHash);
            consulta.setInt(2, idusuario);

            return consulta.executeUpdate() > 0;
        }
    }

    // Convertir una fila de la base de datos en un objeto Usuario.
    private Usuario mapearUsuario(ResultSet resultado)
            throws SQLException {

        Usuario usuario = new Usuario();

        usuario.setIdusuario(resultado.getInt("idusuario"));
        usuario.setNombre(resultado.getString("nombre"));
        usuario.setCorreo(resultado.getString("correo"));
        usuario.setPasswordHash(
                resultado.getString("password_hash"));
        usuario.setRol(resultado.getString("rol"));
        usuario.setActivo(resultado.getBoolean("activo"));

        return usuario;
    }
}
