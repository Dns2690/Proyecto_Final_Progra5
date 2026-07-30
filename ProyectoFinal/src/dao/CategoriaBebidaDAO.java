package dao;

import connection.ConnectionDB;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import model.CategoriaBebida;

public class CategoriaBebidaDAO {
    
    private final ConnectionDB conexionDB = new ConnectionDB();

    // 1. CREATE (insert)
    public boolean insert(CategoriaBebida cat) {
        String sql = "INSERT INTO categoria_bebida (nombre) VALUES (?)";
        try (Connection con = conexionDB.getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, cat.getNombre());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.out.println("Error al insertar categoría de bebida: " + e.getMessage());
            return false;
        }
    }

    // 2. READ ALL (list every category)
    public List<CategoriaBebida> findAll() {
        String sql = "SELECT * FROM categoria_bebida";
        List<CategoriaBebida> lista = new ArrayList<>();
        
        try (Connection con = conexionDB.getConexion();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            
            while (rs.next()) {
                CategoriaBebida cat = new CategoriaBebida(
                    rs.getInt("id_categoria"),
                    rs.getString("nombre")
                );
                lista.add(cat);
            }
        } catch (SQLException e) {
            System.out.println("Error al listar categorías de bebida: " + e.getMessage());
        }
        return lista;
    }

    // 3. UPDATE (change)
    public boolean update(CategoriaBebida cat) {
        String sql = "UPDATE categoria_bebida SET nombre = ? WHERE id_categoria = ?";
        try (Connection con = conexionDB.getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, cat.getNombre());
            ps.setInt(2, cat.getId_categoria());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.out.println("Error al modificar categoría de bebida: " + e.getMessage());
            return false;
        }
    }

    // 4. DELETE (remove)
    public boolean delete(int id) {
        String sql = "DELETE FROM categoria_bebida WHERE id_categoria = ?";
        try (Connection con = conexionDB.getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.out.println("Error al eliminar categoría de bebida: " + e.getMessage());
            return false;
        }
    }
}