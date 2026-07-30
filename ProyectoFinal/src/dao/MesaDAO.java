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

    // 1. CREATE (insert a new table)
    public boolean insert(Mesa mesa) {
        String sql = "INSERT INTO mesa (numero_mesa, id_seccion, disponible) VALUES (?, ?, ?)";
        try (Connection con = conexionDB.getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, mesa.getNumero_mesa());
            ps.setInt(2, mesa.getId_seccion());
            ps.setInt(3, mesa.getDisponible());

            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.out.println("Error al insertar mesa: " + e.getMessage());
            return false;
        }
    }

    // 2. UPDATE (change an existing table)
    public boolean update(Mesa mesa) {
        String sql = "UPDATE mesa SET numero_mesa = ?, id_seccion = ?, disponible = ? WHERE id_mesa = ?";
        try (Connection con = conexionDB.getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, mesa.getNumero_mesa());
            ps.setInt(2, mesa.getId_seccion());
            ps.setInt(3, mesa.getDisponible());
            ps.setInt(4, mesa.getId_mesa());

            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.out.println("Error al actualizar mesa: " + e.getMessage());
            return false;
        }
    }

    // 3. DELETE (remove a table by ID)
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

    // 4. READ ALL (list every table)
    public List<Mesa> findAll() {
        String sql = "SELECT * FROM mesa";
        List<Mesa> lista = new ArrayList<>();
        try (Connection con = conexionDB.getConexion();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                Mesa m = new Mesa();
                m.setId_mesa(rs.getInt("id_mesa"));
                m.setNumero_mesa(rs.getInt("numero_mesa"));
                m.setId_seccion(rs.getInt("id_seccion"));
                m.setDisponible(rs.getInt("disponible"));
                lista.add(m);
            }
        } catch (SQLException e) {
            System.out.println("Error al listar mesas: " + e.getMessage());
        }
        return lista;
    }

    // 5. FIND BY SECTION (find tables by id_seccion, the real foreign key of the table)
    public List<Mesa> findBySeccion(int idSeccion) {
        String sql = "SELECT * FROM mesa WHERE id_seccion = ?";
        List<Mesa> lista = new ArrayList<>();
        try (Connection con = conexionDB.getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, idSeccion);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Mesa m = new Mesa();
                    m.setId_mesa(rs.getInt("id_mesa"));
                    m.setNumero_mesa(rs.getInt("numero_mesa"));
                    m.setId_seccion(rs.getInt("id_seccion"));
                    m.setDisponible(rs.getInt("disponible"));
                    lista.add(m);
                }
            }
        } catch (SQLException e) {
            System.out.println("Error al buscar mesas por sección: " + e.getMessage());
        }
        return lista;
    }
}