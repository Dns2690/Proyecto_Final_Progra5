package dao;

import connection.ConnectionDB;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import model.Factura;

/**
 * DAO for the {@code factura} table. Applies the Costa Rica sales tax
 * (IVA 13%) when {@code impuesto}/{@code total} are not provided: they are
 * computed from {@code subtotal} at insert time. A comanda may have several
 * facturas when the bill is split per person, hence
 * {@link #findByComanda(int)} returns a list.
 */
public class FacturaDAO {

    /** Costa Rica sales tax rate (13%). */
    public static final BigDecimal IVA = new BigDecimal("0.13");

    private final ConnectionDB conexionDB = new ConnectionDB();

    // 1. CREATE (Insertar nueva factura)
    public boolean insert(Factura factura) {
        return insertGetId(factura) > 0;
    }

    /**
     * Inserts the factura and returns the generated {@code id_factura},
     * needed right away to insert its {@code detalle_factura} rows.
     * If {@code impuesto} or {@code total} are null they are computed from
     * {@code subtotal} applying IVA 13%.
     *
     * @return generated id, or -1 on failure
     */
    public int insertGetId(Factura factura) {
        aplicarIvaSiFalta(factura);
        String sql = "INSERT INTO factura (id_comanda, codigo_cajero, fecha_emision, subtotal, impuesto, total, tipo, estado) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection con = conexionDB.getConexion();
             PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setInt(1, factura.getId_comanda());
            ps.setString(2, factura.getCodigo_cajero());
            ps.setTimestamp(3, factura.getFecha_emision() != null
                    ? Timestamp.valueOf(factura.getFecha_emision())
                    : new Timestamp(System.currentTimeMillis()));
            ps.setBigDecimal(4, factura.getSubtotal());
            ps.setBigDecimal(5, factura.getImpuesto());
            ps.setBigDecimal(6, factura.getTotal());
            ps.setString(7, factura.getTipo() != null ? factura.getTipo() : "provisional");
            ps.setString(8, factura.getEstado() != null ? factura.getEstado() : "pendiente");

            if (ps.executeUpdate() > 0) {
                try (ResultSet rs = ps.getGeneratedKeys()) {
                    if (rs.next()) {
                        return rs.getInt(1);
                    }
                }
            }
        } catch (SQLException e) {
            System.out.println("Error al insertar factura: " + e.getMessage());
        }
        return -1;
    }

    // 2. UPDATE (Modificar factura existente)
    public boolean update(Factura factura) {
        aplicarIvaSiFalta(factura);
        String sql = "UPDATE factura SET id_comanda = ?, codigo_cajero = ?, fecha_emision = ?, subtotal = ?, impuesto = ?, total = ?, tipo = ?, estado = ? WHERE id_factura = ?";
        try (Connection con = conexionDB.getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, factura.getId_comanda());
            ps.setString(2, factura.getCodigo_cajero());
            ps.setTimestamp(3, factura.getFecha_emision() != null
                    ? Timestamp.valueOf(factura.getFecha_emision())
                    : null);
            ps.setBigDecimal(4, factura.getSubtotal());
            ps.setBigDecimal(5, factura.getImpuesto());
            ps.setBigDecimal(6, factura.getTotal());
            ps.setString(7, factura.getTipo());
            ps.setString(8, factura.getEstado());
            ps.setInt(9, factura.getId_factura());

            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.out.println("Error al actualizar factura: " + e.getMessage());
            return false;
        }
    }

    // 3. UPDATE estado (pendiente / pagada)
    public boolean updateEstado(int idFactura, String nuevoEstado) {
        String sql = "UPDATE factura SET estado = ? WHERE id_factura = ?";
        try (Connection con = conexionDB.getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, nuevoEstado);
            ps.setInt(2, idFactura);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.out.println("Error al actualizar estado de factura: " + e.getMessage());
            return false;
        }
    }

    // 4. DELETE (Eliminar factura por ID)
    public boolean delete(int idFactura) {
        String sql = "DELETE FROM factura WHERE id_factura = ?";
        try (Connection con = conexionDB.getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, idFactura);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.out.println("Error al eliminar factura: " + e.getMessage());
            return false;
        }
    }

    // 5. READ ALL (Listar todas las facturas)
    public List<Factura> findAll() {
        String sql = "SELECT * FROM factura";
        List<Factura> lista = new ArrayList<>();
        try (Connection con = conexionDB.getConexion();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                lista.add(mapRow(rs));
            }
        } catch (SQLException e) {
            System.out.println("Error al listar facturas: " + e.getMessage());
        }
        return lista;
    }

    // 6. READ BY ID (Buscar una factura específica)
    public Factura findById(int idFactura) {
        String sql = "SELECT * FROM factura WHERE id_factura = ?";
        try (Connection con = conexionDB.getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, idFactura);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapRow(rs);
                }
            }
        } catch (SQLException e) {
            System.out.println("Error al buscar factura por ID: " + e.getMessage());
        }
        return null;
    }

    // 7. READ por comanda (varias si la cuenta se dividió por persona)
    public List<Factura> findByComanda(int idComanda) {
        String sql = "SELECT * FROM factura WHERE id_comanda = ?";
        List<Factura> lista = new ArrayList<>();
        try (Connection con = conexionDB.getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, idComanda);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    lista.add(mapRow(rs));
                }
            }
        } catch (SQLException e) {
            System.out.println("Error al buscar facturas por comanda: " + e.getMessage());
        }
        return lista;
    }

    /**
     * Fills {@code impuesto} (subtotal * 13%) and {@code total}
     * (subtotal + impuesto) when they are missing, rounding to 2 decimals.
     */
    private void aplicarIvaSiFalta(Factura factura) {
        if (factura.getSubtotal() == null) {
            return;
        }
        if (factura.getImpuesto() == null) {
            factura.setImpuesto(factura.getSubtotal().multiply(IVA).setScale(2, RoundingMode.HALF_UP));
        }
        if (factura.getTotal() == null) {
            factura.setTotal(factura.getSubtotal().add(factura.getImpuesto()));
        }
    }

    /** Maps the current ResultSet row to a Factura model. */
    private Factura mapRow(ResultSet rs) throws SQLException {
        Factura f = new Factura();
        f.setId_factura(rs.getInt("id_factura"));
        f.setId_comanda(rs.getInt("id_comanda"));
        f.setCodigo_cajero(rs.getString("codigo_cajero"));
        Timestamp fechaEmision = rs.getTimestamp("fecha_emision");
        f.setFecha_emision(fechaEmision != null ? fechaEmision.toLocalDateTime() : null);
        f.setSubtotal(rs.getBigDecimal("subtotal"));
        f.setImpuesto(rs.getBigDecimal("impuesto"));
        f.setTotal(rs.getBigDecimal("total"));
        f.setTipo(rs.getString("tipo"));
        f.setEstado(rs.getString("estado"));
        return f;
    }
}
