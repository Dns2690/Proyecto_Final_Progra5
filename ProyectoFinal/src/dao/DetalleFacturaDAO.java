package dao;

import connection.ConnectionDB;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import model.DetalleFactura;

/**
 * DAO for the {@code detalle_factura} table, the link between a factura and
 * the {@code detalle_comanda} rows it bills. When a table's bill is split
 * per person, each factura gets its own subset of the comanda's detail rows
 * through this table.
 */
public class DetalleFacturaDAO {

    private final ConnectionDB conexionDB = new ConnectionDB();

    // 1. CREATE (Ligar un detalle de comanda a una factura)
    public boolean insert(DetalleFactura detalle) {
        String sql = "INSERT INTO detalle_factura (id_factura, id_detalle) VALUES (?, ?)";
        try (Connection con = conexionDB.getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, detalle.getId_factura());
            ps.setInt(2, detalle.getId_detalle());

            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.out.println("Error al insertar detalle de factura: " + e.getMessage());
            return false;
        }
    }

    // 2. UPDATE (Reasignar un detalle a otra factura)
    public boolean update(DetalleFactura detalle) {
        String sql = "UPDATE detalle_factura SET id_factura = ?, id_detalle = ? WHERE id_det_fac = ?";
        try (Connection con = conexionDB.getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, detalle.getId_factura());
            ps.setInt(2, detalle.getId_detalle());
            ps.setInt(3, detalle.getId_det_fac());

            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.out.println("Error al actualizar detalle de factura: " + e.getMessage());
            return false;
        }
    }

    // 3. DELETE (Eliminar un vínculo por ID)
    public boolean delete(int idDetFac) {
        String sql = "DELETE FROM detalle_factura WHERE id_det_fac = ?";
        try (Connection con = conexionDB.getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, idDetFac);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.out.println("Error al eliminar detalle de factura: " + e.getMessage());
            return false;
        }
    }

    // 4. DELETE por factura (al anular una factura se sueltan sus detalles)
    public boolean deleteByFactura(int idFactura) {
        String sql = "DELETE FROM detalle_factura WHERE id_factura = ?";
        try (Connection con = conexionDB.getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, idFactura);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.out.println("Error al eliminar detalles por factura: " + e.getMessage());
            return false;
        }
    }

    // 5. READ ALL (Listar todos los vínculos)
    public List<DetalleFactura> findAll() {
        String sql = "SELECT * FROM detalle_factura";
        List<DetalleFactura> lista = new ArrayList<>();
        try (Connection con = conexionDB.getConexion();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                lista.add(mapRow(rs));
            }
        } catch (SQLException e) {
            System.out.println("Error al listar detalles de factura: " + e.getMessage());
        }
        return lista;
    }

    // 6. READ BY ID (Buscar un vínculo específico)
    public DetalleFactura findById(int idDetFac) {
        String sql = "SELECT * FROM detalle_factura WHERE id_det_fac = ?";
        try (Connection con = conexionDB.getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, idDetFac);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapRow(rs);
                }
            }
        } catch (SQLException e) {
            System.out.println("Error al buscar detalle de factura por ID: " + e.getMessage());
        }
        return null;
    }

    // 7. READ por factura (los detalles de comanda que cobra esa factura)
    public List<DetalleFactura> findByFactura(int idFactura) {
        String sql = "SELECT * FROM detalle_factura WHERE id_factura = ?";
        List<DetalleFactura> lista = new ArrayList<>();
        try (Connection con = conexionDB.getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, idFactura);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    lista.add(mapRow(rs));
                }
            }
        } catch (SQLException e) {
            System.out.println("Error al buscar detalles por factura: " + e.getMessage());
        }
        return lista;
    }

    /** Maps the current ResultSet row to a DetalleFactura model. */
    private DetalleFactura mapRow(ResultSet rs) throws SQLException {
        DetalleFactura d = new DetalleFactura();
        d.setId_det_fac(rs.getInt("id_det_fac"));
        d.setId_factura(rs.getInt("id_factura"));
        d.setId_detalle(rs.getInt("id_detalle"));
        return d;
    }
}
