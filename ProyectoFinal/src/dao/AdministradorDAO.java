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
 * DAO para la entidad Administrador, con operaciones CRUD y login.
 */
public class AdministradorDAO {

    private final ConnectionDB conexionDB = new ConnectionDB();

    // 1. CREATE (Insertar admin; la contraseña llega en texto plano y se hashea aquí)
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

    // 2. UPDATE (Modificar datos del admin; NO toca la contraseña — usar updateContrasena)
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

    // 3. UPDATE contraseña (recibe texto plano y guarda el hash MD5)
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

    // 4. DELETE (Eliminar admin por ID)
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

    // 5. READ ALL (Listar todos los admins)
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

    // 6. READ BY ID (Buscar un admin específico)
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
     * Valida el login de un administrador comparando el usuario y la contraseña (en texto plano) con la base de datos.
     * @param usuario nombre de usuario del administrador
     * @param contrasena contraseña en texto plano del administrador
     * @return objeto Administrador si las credenciales son correctas, o null si no son válidas
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

    /** Retorna un objeto Administrador mapeado desde una fila del ResultSet. */
    private Administrador mapRow(ResultSet rs) throws SQLException {
        Administrador a = new Administrador();
        a.setId_admin(rs.getInt("id_admin"));
        a.setUsuario(rs.getString("usuario"));
        a.setContrasena(rs.getString("contrasena")); // ya viene hasheada
        a.setNombre(rs.getString("nombre"));
        return a;
    }
}
