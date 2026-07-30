package dao;

import connection.ConnectionDB;
import connection.util.EncriptadorUtil;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import model.Usuario;

/**
 * DAO for the Usuario entity, with CRUD operations and queries by code and credentials.
 */
public class UsuarioDAO {

    private final ConnectionDB conexionDB = new ConnectionDB();

    // 1. CREATE (insert a new employee; the password arrives as plain text and is hashed here)
    public boolean insert(Usuario usuario) {
        String sql = "INSERT INTO usuario (codigo, nombre, contrasena, id_tipo, activo) VALUES (?, ?, ?, ?, ?)";
        try (Connection con = conexionDB.getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, usuario.getCodigo());
            ps.setString(2, usuario.getNombre());
            ps.setString(3, EncriptadorUtil.md5(usuario.getContrasena()));
            ps.setInt(4, usuario.getId_tipo());
            ps.setInt(5, usuario.getActivo());

            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.out.println("Error al insertar usuario: " + e.getMessage());
            return false;
        }
    }

    // 2. UPDATE (change the employee data; does NOT touch the password, use updateContrasena for that)
    public boolean update(Usuario usuario) {
        String sql = "UPDATE usuario SET nombre = ?, id_tipo = ?, activo = ? WHERE codigo = ?";
        try (Connection con = conexionDB.getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, usuario.getNombre());
            ps.setInt(2, usuario.getId_tipo());
            ps.setInt(3, usuario.getActivo());
            ps.setString(4, usuario.getCodigo());

            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.out.println("Error al actualizar usuario: " + e.getMessage());
            return false;
        }
    }

    // 3. UPDATE password (takes plain text and stores the MD5 hash)
    public boolean updateContrasena(String codigo, String nuevaContrasena) {
        String sql = "UPDATE usuario SET contrasena = ? WHERE codigo = ?";
        try (Connection con = conexionDB.getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, EncriptadorUtil.md5(nuevaContrasena));
            ps.setString(2, codigo);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.out.println("Error al actualizar contraseña: " + e.getMessage());
            return false;
        }
    }

    // 4. DELETE (remove an employee by code)
    public boolean delete(String codigo) {
        String sql = "DELETE FROM usuario WHERE codigo = ?";
        try (Connection con = conexionDB.getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, codigo);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.out.println("Error al eliminar usuario: " + e.getMessage());
            return false;
        }
    }

    // 5. READ ALL (list every employee)
    public List<Usuario> findAll() {
        String sql = "SELECT * FROM usuario";
        List<Usuario> lista = new ArrayList<>();
        try (Connection con = conexionDB.getConexion();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                lista.add(mapRow(rs));
            }
        } catch (SQLException e) {
            System.out.println("Error al listar usuarios: " + e.getMessage());
        }
        return lista;
    }

    // 6. READ BY ID (find an employee by code, e.g. SAL001)
    public Usuario findById(String codigo) {
        String sql = "SELECT * FROM usuario WHERE codigo = ?";
        try (Connection con = conexionDB.getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, codigo);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapRow(rs);
                }
            }
        } catch (SQLException e) {
            System.out.println("Error al buscar usuario por código: " + e.getMessage());
        }
        return null;
    }

    /**
     * Validates credentials: hashes the plaintext password with MD5 and
     * compares it in the query, requiring the account to be active.
     *
     * @return the authenticated Usuario (for the session holder), or
     *         {@code null} if the credentials are invalid or inactive
     */
    public Usuario login(String codigo, String contrasena) {
        String sql = "SELECT * FROM usuario WHERE codigo = ? AND contrasena = ? AND activo = 1";
        try (Connection con = conexionDB.getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, codigo);
            ps.setString(2, EncriptadorUtil.md5(contrasena));

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapRow(rs);
                }
            }
        } catch (SQLException e) {
            System.out.println("Error en el login de usuario: " + e.getMessage());
        }
        return null;
    }

    /** Maps the current ResultSet row into a Usuario object. */
    private Usuario mapRow(ResultSet rs) throws SQLException {
        Usuario u = new Usuario();
        u.setCodigo(rs.getString("codigo"));
        u.setNombre(rs.getString("nombre"));
        u.setContrasena(rs.getString("contrasena")); // it is already hashed
        u.setId_tipo(rs.getInt("id_tipo"));
        u.setActivo(rs.getInt("activo"));
        return u;
    }
}
