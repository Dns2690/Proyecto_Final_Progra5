package dao;

import connection.ConnectionDB;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import model.Mesa;

public class MesaDAO {

    private final ConnectionDB conexionDB = new ConnectionDB();

    // 1. CREATE (Insertar nueva mesa de forma simple)
    public boolean insert(Mesa mesa) {
        String sql = "INSERT INTO mesa VALUES ()"; // Inserta una fila usando valores por defecto/autoincrementales
        try (Connection con = conexionDB.getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {

            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.out.println("Error al insertar mesa: " + e.getMessage());
            return false;
        }
    }

    // 2. UPDATE (Modificar mesa - se deja la estructura limpia)
    public boolean update(Mesa mesa) {
        String sql = "UPDATE mesa SET id_mesa = ? WHERE id_mesa = ?";
        try (Connection con = conexionDB.getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {
            
            ps.setInt(1, mesa.getId_mesa());
            ps.setInt(2, mesa.getId_mesa());

            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.out.println("Error al actualizar mesa: " + e.getMessage());
            return false;
        }
    }

    // 3. DELETE (Eliminar mesa por ID)
    public boolean delete(int idMesa) {
        String sql = "DELETE FROM mesa WHERE id_mesa = ?";
        try (Connection con = conexionDB.getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {
            
            ps.setInt(1, idMesa);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.out.println("Error al eliminar mesa: " + e.getMessage());
            return false;
        }
    }

    // 4. READ ALL (Listar todas las mesas obteniendo solo el ID)
    public List<Mesa> findAll() {
        String sql = "SELECT * FROM mesa";
        List<Mesa> lista = new ArrayList<>();
        try (Connection con = conexionDB.getConexion();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            
            while (rs.next()) {
                Mesa m = new Mesa();
                m.setId_mesa(rs.getInt("id_mesa"));
                lista.add(m);
            }
        } catch (SQLException e) {
            System.out.println("Error al listar mesas: " + e.getMessage());
        }
        return lista;
    }

    // 5. FIND BY SECCION (Requisito de la pizarra, buscando de forma directa)
    public List<Mesa> findBySeccion(String seccion) {
        String sql = "SELECT * FROM mesa WHERE seccion = ?";
        List<Mesa> lista = new ArrayList<>();
        try (Connection con = conexionDB.getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {
            
            ps.setString(1, seccion); 
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Mesa m = new Mesa();
                    m.setId_mesa(rs.getInt("id_mesa"));
                    lista.add(m);
                }
            }
        } catch (SQLException e) {
            System.out.println("Error al buscar mesas por sección: " + e.getMessage());
        }
        return lista;
    }
}