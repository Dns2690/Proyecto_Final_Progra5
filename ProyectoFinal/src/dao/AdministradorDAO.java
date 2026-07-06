package dao;

import connection.ConnectionDB;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import model.Administrador;

public class AdministradorDAO {
    
    private final ConnectionDB conexionDB = new ConnectionDB();

    public boolean insert(Administrador admin) {
        String sql = "INSERT INTO administrador (usuario, contrasena, nombre) VALUES (?, ?, ?)";

        try (Connection con = conexionDB.getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, admin.getUsuario());
            ps.setString(2, admin.getContrasena());
            ps.setString(3, admin.getNombre());

            int filasAfectadas = ps.executeUpdate();
            return filasAfectadas > 0;

        } catch (SQLException e) {
            System.out.println("Error al insertar administrador en DAO: " + e.getMessage());
            return false;
        }
    }

    public Administrador login(String usuario, String contrasenaEncriptada) {
        String sql = "SELECT * FROM administrador WHERE usuario = ? AND contrasena = ?";
        Administrador admin = null;
        
        try (Connection con = conexionDB.getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {
            
            ps.setString(1, usuario);
            ps.setString(2, contrasenaEncriptada);
            
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    // Se extraen los datos de las columnas de la BD
                    int id = rs.getInt("id_admin");
                    String user = rs.getString("usuario");
                    String pass = rs.getString("contrasena");
                    String nom = rs.getString("nombre");
                    
                    
                    admin = new Administrador(id, user, pass, nom);
                }
            }
        } catch (SQLException e) {
            System.out.println("Error en el login DAO: " + e.getMessage());
        }
        return admin; 
    }
}
 
   