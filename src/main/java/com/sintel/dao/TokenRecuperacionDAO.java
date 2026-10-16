package com.sintel.dao;

import com.sintel.Conexion;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class TokenRecuperacionDAO {

    /**
     * Registra un token nuevo e invalida los anteriores
     * dentro de una sola transacción.
     * El token expira 15 minutos después de su creación.
     */
    public void registrarNuevoToken(
            int idUsuario,
            String tokenHash) throws SQLException {

        String invalidarSQL = """
            UPDATE tokens_recuperacion
            SET usado = 1
            WHERE idusuario = ? AND usado = 0
            """;

        String insertarSQL = """
            INSERT INTO tokens_recuperacion
                (idusuario, token_hash, fecha_expiracion, usado)
            VALUES (?, ?, DATE_ADD(NOW(), INTERVAL 15 MINUTE), 0)
            """;

        try (Connection conexion = Conexion.conectar()) {
            conexion.setAutoCommit(false);

            try {
                try (PreparedStatement invalidar =
                        conexion.prepareStatement(invalidarSQL)) {

                    invalidar.setInt(1, idUsuario);
                    invalidar.executeUpdate();
                }

                try (PreparedStatement insertar =
                        conexion.prepareStatement(insertarSQL)) {

                    insertar.setInt(1, idUsuario);
                    insertar.setString(2, tokenHash);
                    insertar.executeUpdate();
                }

                conexion.commit();

            } catch (SQLException e) {
                try {
                    conexion.rollback();
                } catch (SQLException errorRollback) {
                    e.addSuppressed(errorRollback);
                }

                throw e;
            }
        }
    }

    /**
     * Restablece la contraseña y consume el token
     * dentro de una sola transacción.
     *
     * Devuelve false si el token no existe, expiró,
     * ya fue utilizado o la cuenta no está activa.
     */
    public boolean restablecerPassword(
            String tokenHash,
            String passwordHash) throws SQLException {

        String buscarSQL = """
            SELECT idusuario
            FROM tokens_recuperacion
            WHERE token_hash = ?
              AND usado = 0
              AND fecha_expiracion > NOW()
            FOR UPDATE
            """;

        String actualizarPasswordSQL = """
            UPDATE usuarios
            SET password_hash = ?
            WHERE idusuario = ?
              AND activo = 1
            """;

        String consumirTokenSQL = """
            UPDATE tokens_recuperacion
            SET usado = 1
            WHERE token_hash = ?
              AND usado = 0
              AND fecha_expiracion > NOW()
            """;

        String invalidarOtrosSQL = """
            UPDATE tokens_recuperacion
            SET usado = 1
            WHERE idusuario = ?
              AND usado = 0
            """;

        try (Connection conexion = Conexion.conectar()) {
            conexion.setAutoCommit(false);

            try {
                int idUsuario;

                // Bloquea el token mientras se procesa
                // la operación, evitando usos simultáneos.
                try (PreparedStatement buscar =
                        conexion.prepareStatement(buscarSQL)) {

                    buscar.setString(1, tokenHash);

                    try (ResultSet resultado = buscar.executeQuery()) {
                        if (!resultado.next()) {
                            conexion.rollback();
                            return false;
                        }

                        idUsuario = resultado.getInt("idusuario");
                    }
                }

                // Actualiza la contraseña de la cuenta activa.
                try (PreparedStatement actualizar =
                        conexion.prepareStatement(
                            actualizarPasswordSQL)) {

                    actualizar.setString(1, passwordHash);
                    actualizar.setInt(2, idUsuario);

                    if (actualizar.executeUpdate() != 1) {
                        conexion.rollback();
                        return false;
                    }
                }

                // Consume el token utilizado.
                try (PreparedStatement consumir =
                        conexion.prepareStatement(consumirTokenSQL)) {

                    consumir.setString(1, tokenHash);

                    if (consumir.executeUpdate() != 1) {
                        conexion.rollback();
                        return false;
                    }
                }

                // Invalida cualquier otro token pendiente
                // de la misma cuenta.
                try (PreparedStatement invalidar =
                        conexion.prepareStatement(invalidarOtrosSQL)) {

                    invalidar.setInt(1, idUsuario);
                    invalidar.executeUpdate();
                }

                conexion.commit();
                return true;

            } catch (SQLException e) {
                try {
                    conexion.rollback();
                } catch (SQLException errorRollback) {
                    e.addSuppressed(errorRollback);
                }

                throw e;
            }
        }
    }

    
    public boolean tokenValido(String tokenHash) throws SQLException {
        String sql = """
            SELECT 1
            FROM tokens_recuperacion
            WHERE token_hash = ?
              AND usado = 0
              AND fecha_expiracion > NOW()
            """;

        try (Connection conexion = Conexion.conectar();
             PreparedStatement consulta = conexion.prepareStatement(sql)) {

            consulta.setString(1, tokenHash);

            try (ResultSet resultado = consulta.executeQuery()) {
                return resultado.next();
            }
        }
    }

}

