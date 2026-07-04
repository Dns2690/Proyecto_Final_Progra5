package dao;

import connection.ConnectionDB;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class AsignacionSeccionDAO {

    private final ConnectionDB conexionDB = new ConnectionDB();

    // Método directo para insertar una asignación de sección de forma útil
    public boolean insert() {
        String sql = "INSERT INTO asignacion_seccion VALUES ()"; 
        try (Connection con = conexionDB.getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {

            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.out.println("Error al insertar asignación: " + e.getMessage());
            return false;
        }
    }
}