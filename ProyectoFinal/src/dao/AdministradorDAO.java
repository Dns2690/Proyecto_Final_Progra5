package dao;

import connection.ConnectionDB;
import connection.util.EncriptadorUtil;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import model.Administrador;

/**
 * DAO for the Administrador entity, with CRUD operations and login.
 */
public class AdministradorDAO {

    private final ConnectionDB conexionDB = new ConnectionDB();

    // 1. CREATE (insert an admin; the password arrives as plain text and is hashed here)
    public boolean insert(Administrador admin) {
        String sql = "INSERT INTO administrador (usuario, contrasena, nombre) VALUES (?, ?, ?)";
        try (Connection con = conexionDB.getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, admin.getUsuario());
            ps.setString(2, EncriptadorUtil.md5(admin.getContrasena()));
            ps.setString(3, admin.getNombre());

            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.out.println("Error al insertar administrador: " + e.getMessage());
            return false;
        }
    }

    // 2. UPDATE (change the admin data; does NOT touch the password, use updateContrasena for that)
    public boolean update(Administrador admin) {
        String sql = "UPDATE administrador SET usuario = ?, nombre = ? WHERE id_admin = ?";
        try (Connection con = conexionDB.getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, admin.getUsuario());
            ps.setString(2, admin.getNombre());
            ps.setInt(3, admin.getId_admin());

            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.out.println("Error al actualizar administrador: " + e.getMessage());
            return false;
        }
    }

    // 3. UPDATE password (takes plain text and stores the MD5 hash)
    public boolean updateContrasena(int idAdmin, String nuevaContrasena) {
        String sql = "UPDATE administrador SET contrasena = ? WHERE id_admin = ?";
        try (Connection con = conexionDB.getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, EncriptadorUtil.md5(nuevaContrasena));
            ps.setInt(2, idAdmin);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.out.println("Error al actualizar contraseña de administrador: " + e.getMessage());
            return false;
        }
    }

    // 4. DELETE (remove an admin by ID)
    public boolean delete(int idAdmin) {
        String sql = "DELETE FROM administrador WHERE id_admin = ?";
        try (Connection con = conexionDB.getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, idAdmin);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.out.println("Error al eliminar administrador: " + e.getMessage());
            return false;
        }
    }

    // 5. READ ALL (list every admin)
    public List<Administrador> findAll() {
        String sql = "SELECT * FROM administrador";
        List<Administrador> lista = new ArrayList<>();
        try (Connection con = conexionDB.getConexion();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                lista.add(mapRow(rs));
            }
        } catch (SQLException e) {
            System.out.println("Error al listar administradores: " + e.getMessage());
        }
        return lista;
    }

    // 6. READ BY ID (find one admin)
    public Administrador findById(int idAdmin) {
        String sql = "SELECT * FROM administrador WHERE id_admin = ?";
        try (Connection con = conexionDB.getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, idAdmin);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapRow(rs);
                }
            }
        } catch (SQLException e) {
            System.out.println("Error al buscar administrador por ID: " + e.getMessage());
        }
        return null;
    }

    /**
     * Validates an administrator login by comparing the user name and the plain text password against the database.
     * @param usuario administrator user name
     * @param contrasena administrator password as plain text
     * @return an Administrador object when the credentials are correct, or null when they are not
     */
    public Administrador login(String usuario, String contrasena) {
        String sql = "SELECT * FROM administrador WHERE usuario = ? AND contrasena = ?";
        try (Connection con = conexionDB.getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, usuario);
            ps.setString(2, EncriptadorUtil.md5(contrasena));

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapRow(rs);
                }
            }
        } catch (SQLException e) {
            System.out.println("Error en el login de administrador: " + e.getMessage());
        }
        return null;
    }

    /** Maps the current ResultSet row into an Administrador object. */
    private Administrador mapRow(ResultSet rs) throws SQLException {
        Administrador a = new Administrador();
        a.setId_admin(rs.getInt("id_admin"));
        a.setUsuario(rs.getString("usuario"));
        a.setContrasena(rs.getString("contrasena")); // it is already hashed
        a.setNombre(rs.getString("nombre"));
        return a;
    }
}
