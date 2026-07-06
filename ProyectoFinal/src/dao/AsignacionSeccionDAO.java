package dao;

import connection.ConnectionDB;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import model.AsignacionSeccion;

public class AsignacionSeccionDAO {

    private final ConnectionDB conexionDB = new ConnectionDB();

    public boolean insert(AsignacionSeccion asignacion) {
        String sql = "INSERT INTO asignacion_seccion (codigo_sal, id_seccion, fecha) VALUES (?, ?, ?)";
        try (Connection con = conexionDB.getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, asignacion.getCodigoSal());
            ps.setInt(2, asignacion.getIdSeccion());
            ps.setDate(3, Date.valueOf(asignacion.getFecha()));

            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.out.println("Error al insertar asignación: " + e.getMessage());
            return false;
        }
    }
}