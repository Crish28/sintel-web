package com.sintel;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class Conexion {

    private static final String URL =
            "jdbc:mysql://localhost:3306/sintelv1"
            + "?useSSL=false&serverTimezone=UTC&characterEncoding=UTF-8";

    private static final String USUARIO = "root";
    private static final String PASSWORD = "";

    public static Connection conectar() throws SQLException {

        try {
            // Cargar el controlador de MySQL
            Class.forName("com.mysql.cj.jdbc.Driver");

        } catch (ClassNotFoundException e) {
            throw new SQLException(
                    "No se encontró el controlador JDBC de MySQL.",
                    e
            );
        }

        // Intentar establecer la conexión
        return DriverManager.getConnection(
                URL,
                USUARIO,
                PASSWORD
        );
    }
}
