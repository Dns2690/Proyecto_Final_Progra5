package dao;

import connection.ConnectionDB;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import model.Bebida;
import model.Comida;
import model.DetalleComanda;

/**
 * DAO para la entidad DetalleComanda, con operaciones CRUD y consultas por comanda.
 */
public class DetalleComandaDAO {

    private final ConnectionDB conexionDB = new ConnectionDB();

    // 1. CREATE (Agregar un ítem al detalle de la comanda)
    public boolean insert(DetalleComanda detalle) {
        String sql = "INSERT INTO detalle_comanda (id_comanda, tipo_item, id_item, cantidad, precio_unit) VALUES (?, ?, ?, ?, ?)";
        try (Connection con = conexionDB.getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, detalle.getId_comanda());
            ps.setString(2, detalle.getTipo_item());
            ps.setInt(3, detalle.getId_item());
            ps.setInt(4, detalle.getCantidad());
            ps.setBigDecimal(5, detalle.getPrecio_unit());

            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.out.println("Error al insertar detalle de comanda: " + e.getMessage());
            return false;
        }
    }

    // 2. UPDATE (Modificar un ítem del detalle)
    public boolean update(DetalleComanda detalle) {
        String sql = "UPDATE detalle_comanda SET id_comanda = ?, tipo_item = ?, id_item = ?, cantidad = ?, precio_unit = ? WHERE id_detalle = ?";
        try (Connection con = conexionDB.getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, detalle.getId_comanda());
            ps.setString(2, detalle.getTipo_item());
            ps.setInt(3, detalle.getId_item());
            ps.setInt(4, detalle.getCantidad());
            ps.setBigDecimal(5, detalle.getPrecio_unit());
            ps.setInt(6, detalle.getId_detalle());

            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.out.println("Error al actualizar detalle de comanda: " + e.getMessage());
            return false;
        }
    }

    // 3. DELETE (Eliminar un ítem del detalle)
    public boolean delete(int idDetalle) {
        String sql = "DELETE FROM detalle_comanda WHERE id_detalle = ?";
        try (Connection con = conexionDB.getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, idDetalle);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.out.println("Error al eliminar detalle de comanda: " + e.getMessage());
            return false;
        }
    }

    // 4. READ BY ID (Buscar un detalle específico)
    public DetalleComanda findById(int idDetalle) {
        String sql = "SELECT * FROM detalle_comanda WHERE id_detalle = ?";
        try (Connection con = conexionDB.getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, idDetalle);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapRow(rs);
                }
            }
        } catch (SQLException e) {
            System.out.println("Error al buscar detalle por ID: " + e.getMessage());
        }
        return null;
    }

    // 5. READ por comanda (todos los ítems de una comanda)
    public List<DetalleComanda> findByComanda(int idComanda) {
        String sql = "SELECT * FROM detalle_comanda WHERE id_comanda = ?";
        List<DetalleComanda> lista = new ArrayList<>();
        try (Connection con = conexionDB.getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, idComanda);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    lista.add(mapRow(rs));
                }
            }
        } catch (SQLException e) {
            System.out.println("Error al buscar detalles por comanda: " + e.getMessage());
        }
        return lista;
    }

    /**
     * Resuelve el nombre del ítem (comida o bebida) dado un DetalleComanda.
     * @param detalle objeto DetalleComanda
     * @return nombre del ítem, o null si no se encuentra
     */
    public String getNombreItem(DetalleComanda detalle) {
        if ("comida".equals(detalle.getTipo_item())) {
            Comida comida = new ComidaDAO().findById(detalle.getId_item());
            return comida != null ? comida.getNombre() : null;
        }
        if ("bebida".equals(detalle.getTipo_item())) {
            Bebida bebida = new BebidaDAO().findById(detalle.getId_item());
            return bebida != null ? bebida.getNombre() : null;
        }
        return null;
    }

    /** Mapea la fila actual del ResultSet a un modelo DetalleComanda. */
    private DetalleComanda mapRow(ResultSet rs) throws SQLException {
        DetalleComanda d = new DetalleComanda();
        d.setId_detalle(rs.getInt("id_detalle"));
        d.setId_comanda(rs.getInt("id_comanda"));
        d.setTipo_item(rs.getString("tipo_item"));
        d.setId_item(rs.getInt("id_item"));
        d.setCantidad(rs.getInt("cantidad"));
        d.setPrecio_unit(rs.getBigDecimal("precio_unit"));
        return d;
    }
}
