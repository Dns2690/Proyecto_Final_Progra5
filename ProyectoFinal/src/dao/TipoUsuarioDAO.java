package dao;

import connection.ConnectionDB;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import model.TipoUsuario;

/**
 * DAO for the {@code tipo_usuario} table (employee roles). Each role has a
 * UNIQUE 3-letter {@code prefijo} (e.g. SAL, COS, BAR, CAJ) used to build
 * employee codes: prefix + last 3 digits of the cedula, e.g. SAL001.
 */
public class TipoUsuarioDAO {

    private final ConnectionDB conexionDB = new ConnectionDB();

    // 1. CREATE (Insertar nuevo tipo de usuario)
    public boolean insert(TipoUsuario tipo) {
        String sql = "INSERT INTO tipo_usuario (nombre, prefijo) VALUES (?, ?)";
        try (Connection con = conexionDB.getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, tipo.getNombre());
            ps.setString(2, tipo.getPrefijo());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.out.println("Error al insertar tipo de usuario: " + e.getMessage());
            return false;
        }
    }

    // 2. UPDATE (Modificar tipo existente)
    public boolean update(TipoUsuario tipo) {
        String sql = "UPDATE tipo_usuario SET nombre = ?, prefijo = ? WHERE id_tipo = ?";
        try (Connection con = conexionDB.getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, tipo.getNombre());
            ps.setString(2, tipo.getPrefijo());
            ps.setInt(3, tipo.getId_tipo());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.out.println("Error al actualizar tipo de usuario: " + e.getMessage());
            return false;
        }
    }

    // 3. DELETE (Eliminar tipo por ID)
    public boolean delete(int idTipo) {
        String sql = "DELETE FROM tipo_usuario WHERE id_tipo = ?";
        try (Connection con = conexionDB.getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, idTipo);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.out.println("Error al eliminar tipo de usuario: " + e.getMessage());
            return false;
        }
    }

    // 4. READ ALL (Listar todos los tipos)
    public List<TipoUsuario> findAll() {
        String sql = "SELECT * FROM tipo_usuario";
        List<TipoUsuario> lista = new ArrayList<>();
        try (Connection con = conexionDB.getConexion();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                lista.add(mapRow(rs));
            }
        } catch (SQLException e) {
            System.out.println("Error al listar tipos de usuario: " + e.getMessage());
        }
        return lista;
    }

    // 5. READ BY ID (Buscar un tipo específico)
    public TipoUsuario findById(int idTipo) {
        String sql = "SELECT * FROM tipo_usuario WHERE id_tipo = ?";
        try (Connection con = conexionDB.getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, idTipo);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapRow(rs);
                }
            }
        } catch (SQLException e) {
            System.out.println("Error al buscar tipo de usuario por ID: " + e.getMessage());
        }
        return null;
    }

    // 6. READ por prefijo (para generar códigos de empleado, ej. SAL001)
    public TipoUsuario findByPrefijo(String prefijo) {
        String sql = "SELECT * FROM tipo_usuario WHERE prefijo = ?";
        try (Connection con = conexionDB.getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, prefijo);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapRow(rs);
                }
            }
        } catch (SQLException e) {
            System.out.println("Error al buscar tipo de usuario por prefijo: " + e.getMessage());
        }
        return null;
    }

    /** Maps the current ResultSet row to a TipoUsuario model. */
    private TipoUsuario mapRow(ResultSet rs) throws SQLException {
        TipoUsuario t = new TipoUsuario();
        t.setId_tipo(rs.getInt("id_tipo"));
        t.setNombre(rs.getString("nombre"));
        t.setPrefijo(rs.getString("prefijo"));
        return t;
    }
}
