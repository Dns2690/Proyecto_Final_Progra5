package dao;

import connection.ConnectionDB;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import model.Bebida;

public class BebidaDAO {

    private final ConnectionDB conexionDB = new ConnectionDB();

    // 1. CREATE (Insertar nueva bebida)
    public boolean insert(Bebida bebida) {
        String sql = "INSERT INTO bebida (nombre, id_categoria, descripcion, precio, activo) VALUES (?, ?, ?, ?, ?)";
        try (Connection con = conexionDB.getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, bebida.getNombre());
            ps.setInt(2, bebida.getId_categoria());
            ps.setString(3, bebida.getDescripcion());
            ps.setBigDecimal(4, bebida.getPrecio());
            ps.setInt(5, bebida.getActivo());

            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.out.println("Error al insertar bebida: " + e.getMessage());
            return false;
        }
    }

    // 2. UPDATE (Modificar bebida existente)
    public boolean update(Bebida bebida) {
        String sql = "UPDATE bebida SET nombre = ?, id_categoria = ?, descripcion = ?, precio = ?, activo = ? WHERE id_bebida = ?";
        try (Connection con = conexionDB.getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, bebida.getNombre());
            ps.setInt(2, bebida.getId_categoria());
            ps.setString(3, bebida.getDescripcion());
            ps.setBigDecimal(4, bebida.getPrecio());
            ps.setInt(5, bebida.getActivo());
            ps.setInt(6, bebida.getId_bebida());

            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.out.println("Error al actualizar bebida: " + e.getMessage());
            return false;
        }
    }

    // 3. DELETE (Eliminar bebida por ID)
    public boolean delete(int idBebida) {
        String sql = "DELETE FROM bebida WHERE id_bebida = ?";
        try (Connection con = conexionDB.getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {
            
            ps.setInt(1, idBebida);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.out.println("Error al eliminar bebida: " + e.getMessage());
            return false;
        }
    }

    // 4. READ ALL (Listar todas las bebidas)
    public List<Bebida> findAll() {
        String sql = "SELECT * FROM bebida";
        List<Bebida> lista = new ArrayList<>();
        try (Connection con = conexionDB.getConexion();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            
            while (rs.next()) {
                Bebida b = new Bebida();
                b.setId_bebida(rs.getInt("id_bebida"));
                b.setNombre(rs.getString("nombre"));
                b.setId_categoria(rs.getInt("id_categoria"));
                b.setDescripcion(rs.getString("descripcion"));
                b.setPrecio(rs.getBigDecimal("precio"));
                b.setActivo(rs.getInt("activo"));
                lista.add(b);
            }
        } catch (SQLException e) {
            System.out.println("Error al listar bebidas: " + e.getMessage());
        }
        return lista;
    }
}