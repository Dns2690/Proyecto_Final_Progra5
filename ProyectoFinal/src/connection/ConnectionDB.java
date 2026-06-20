package connection;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class ConnectionDB {

    private static final String URL  = "jdbc:mysql://localhost:3306/restaurante_db"
                                     + "?allowPublicKeyRetrieval=true&useSSL=false";
    private static final String USER = "root";
    //private static final String PASS = "Denis2690@";
    private static final String PASS = "123456VALERIAvsOLGA";

    private Connection cn;

    public Connection getConexion() {
        try {
            cn = DriverManager.getConnection(URL, USER, PASS);
            System.out.println("Conexión exitosa a restaurante_db");
        } catch (SQLException e) {
            System.out.println("Error al conectar: " + e.getMessage());
        }
        return cn;
    }

    public void closeConexion() {
        try {
            if (cn != null && !cn.isClosed()) {
                cn.close();
                System.out.println("Conexión cerrada.");
            }
        } catch (SQLException e) {
            System.out.println("Error al cerrar: " + e.getMessage());
        }
    }
}