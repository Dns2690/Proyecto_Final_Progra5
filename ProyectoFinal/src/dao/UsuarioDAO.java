package dao;

import connection.ConnectionDB;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class UsuarioDAO {

    private final ConnectionDB conexionDB = new ConnectionDB();

    // Método directo para validar el login del usuario
    public boolean login(String usuario, String contrasena) {
        String sql = "SELECT * FROM usuario WHERE codigo = ? AND contrasena = ?";
        try (Connection con = conexionDB.getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {
            
            ps.setString(1, usuario);
            ps.setString(2, contrasena);

            try (ResultSet rs = ps.executeQuery()) {
                return rs.next(); // Devuelve true si encuentra al usuario
            }
        } catch (SQLException e) {
            System.out.println("Error en el login de usuario: " + e.getMessage());
            return false;
        }
    }
}