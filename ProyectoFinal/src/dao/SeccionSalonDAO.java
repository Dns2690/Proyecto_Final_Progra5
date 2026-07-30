package dao;

import connection.ConnectionDB;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import model.SeccionSalon;

/**
 * DAO for the SeccionSalon entity, with CRUD operations.
 */
public class SeccionSalonDAO {

    private final ConnectionDB conexionDB = new ConnectionDB();

    // 1. CREATE (insert a new section)
    public boolean insert(SeccionSalon seccion) {
        String sql = "INSERT INTO seccion_salon (nombre) VALUES (?)";
        try (Connection con = conexionDB.getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, seccion.getNombre());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.out.println("Error al insertar sección de salón: " + e.getMessage());
            return false;
        }
    }

    // 2. UPDATE (change an existing section)
    public boolean update(SeccionSalon seccion) {
        String sql = "UPDATE seccion_salon SET nombre = ? WHERE id_seccion = ?";
        try (Connection con = conexionDB.getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, seccion.getNombre());
            ps.setInt(2, seccion.getId_seccion());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.out.println("Error al actualizar sección de salón: " + e.getMessage());
            return false;
        }
    }

    // 3. DELETE (remove a section by ID)
    public boolean delete(int idSeccion) {
        String sql = "DELETE FROM seccion_salon WHERE id_seccion = ?";
        try (Connection con = conexionDB.getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, idSeccion);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.out.println("Error al eliminar sección de salón: " + e.getMessage());
            return false;
        }
    }

    // 4. READ ALL (list every section)
    public List<SeccionSalon> findAll() {
        String sql = "SELECT * FROM seccion_salon";
        List<SeccionSalon> lista = new ArrayList<>();
        try (Connection con = conexionDB.getConexion();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                lista.add(mapRow(rs));
            }
        } catch (SQLException e) {
            System.out.println("Error al listar secciones de salón: " + e.getMessage());
        }
        return lista;
    }

    // 5. READ BY ID (find one section)
    public SeccionSalon findById(int idSeccion) {
        String sql = "SELECT * FROM seccion_salon WHERE id_seccion = ?";
        try (Connection con = conexionDB.getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, idSeccion);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapRow(rs);
                }
            }
        } catch (SQLException e) {
            System.out.println("Error al buscar sección de salón por ID: " + e.getMessage());
        }
        return null;
    }

    /** Maps the current ResultSet row into a SeccionSalon object. */
    private SeccionSalon mapRow(ResultSet rs) throws SQLException {
        SeccionSalon s = new SeccionSalon();
        s.setId_seccion(rs.getInt("id_seccion"));
        s.setNombre(rs.getString("nombre"));
        return s;
    }
}
