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
 * DAO for the TipoUsuario entity, with CRUD operations and queries by prefix.
 */
public class TipoUsuarioDAO {

    private final ConnectionDB conexionDB = new ConnectionDB();

    // 1. CREATE (insert a new user type)
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

    // 2. UPDATE (change an existing user type)
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

    // 3. DELETE (remove a user type by ID)
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

    // 4. READ ALL (list every user type)
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

    // 5. READ BY ID (find one user type)
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

    // 6. READ by prefix (used to build the employee codes, e.g. SAL001)
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

    /** Maps the current ResultSet row into a TipoUsuario object. */
    private TipoUsuario mapRow(ResultSet rs) throws SQLException {
        TipoUsuario t = new TipoUsuario();
        t.setId_tipo(rs.getInt("id_tipo"));
        t.setNombre(rs.getString("nombre"));
        t.setPrefijo(rs.getString("prefijo"));
        return t;
    }
}
