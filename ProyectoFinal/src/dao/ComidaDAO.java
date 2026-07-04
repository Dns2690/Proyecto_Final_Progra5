package dao;

import connection.ConnectionDB;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import model.Comida;

public class ComidaDAO {

    private final ConnectionDB conexionDB = new ConnectionDB();

    // 1. CREATE (Insertar nueva comida)
    public boolean insert(Comida comida) {
        String sql = "INSERT INTO comida (nombre, id_categoria, descripcion, activo) VALUES (?, ?, ?, ?)";
        try (Connection con = conexionDB.getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {
            
            ps.setString(1, comida.getNombre());
            ps.setInt(2, comida.getId_categoria());
            ps.setString(3, comida.getDescripcion());
            ps.setInt(4, comida.getActivo());

            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.out.println("Error al insertar comida: " + e.getMessage());
            return false;
        }
    }

    // 2. UPDATE (Modificar comida existente)
    public boolean update(Comida comida) {
        String sql = "UPDATE comida SET nombre = ?, id_categoria = ?, descripcion = ?, activo = ? WHERE id_comida = ?";
        try (Connection con = conexionDB.getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {
            
            ps.setString(1, comida.getNombre());
            ps.setInt(2, comida.getId_categoria());
            ps.setString(3, comida.getDescripcion());
            ps.setInt(4, comida.getActivo());
            ps.setInt(5, comida.getId_comida());

            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.out.println("Error al actualizar comida: " + e.getMessage());
            return false;
        }
    }

    // 3. DELETE (Eliminar comida por ID)
    public boolean delete(int idComida) {
        String sql = "DELETE FROM comida WHERE id_comida = ?";
        try (Connection con = conexionDB.getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {
            
            ps.setInt(1, idComida);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.out.println("Error al eliminar comida: " + e.getMessage());
            return false;
        }
    }

    // 4. READ ALL (Listar todas las comidas en la tabla)
    public List<Comida> findAll() {
        String sql = "SELECT * FROM comida";
        List<Comida> lista = new ArrayList<>();
        try (Connection con = conexionDB.getConexion();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            
            while (rs.next()) {
                Comida c = new Comida();
                c.setId_comida(rs.getInt("id_comida"));
                c.setNombre(rs.getString("nombre"));
                c.setId_categoria(rs.getInt("id_categoria"));
                c.setDescripcion(rs.getString("descripcion"));
                c.setActivo(rs.getInt("activo"));
                lista.add(c);
            }
        } catch (SQLException e) {
            System.out.println("Error al listar comidas: " + e.getMessage());
        }
        return lista;
    }

    // 5. READ BY ID (Buscar una comida específica)
    public Comida findById(int idComida) {
        String sql = "SELECT * FROM comida WHERE id_comida = ?";
        try (Connection con = conexionDB.getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {
            
            ps.setInt(1, idComida);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    Comida c = new Comida();
                    c.setId_comida(rs.getInt("id_comida"));
                    c.setNombre(rs.getString("nombre"));
                    c.setId_categoria(rs.getInt("id_categoria"));
                    c.setDescripcion(rs.getString("descripcion"));
                    c.setActivo(rs.getInt("activo"));
                    return c;
                }
            }
        } catch (SQLException e) {
            System.out.println("Error al buscar comida por ID: " + e.getMessage());
        }
        return null;
    }
}
