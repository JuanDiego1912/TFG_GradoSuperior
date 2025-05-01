package jdrb.banco.simulador.utils;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBConnection {
    private static final String URL = "jdbc:sqlite:src/main/resources/db/simuladorBancario.db";
    private static Connection connection;

    private DBConnection() {}

    public static Connection getConnection() {
        if (connection == null) {
            try {
                connection = DriverManager.getConnection(URL);
            } catch (SQLException sqlEx) {
                throw new RuntimeException("Error al conectar con la base de datos SQLite", sqlEx);
            }
        }
        return connection;
    }

    public static void closeConnection() {
        if (connection != null) {
            try {
                connection.close();
                connection = null;
            } catch (SQLException sqlEx) {
                throw new RuntimeException("Error al cerrar la conexión con la base de datos SQLite", sqlEx);
            }
        }
    }
}
