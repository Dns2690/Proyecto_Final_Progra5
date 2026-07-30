package dao;

import connection.ConnectionDB;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;
import model.Comanda;

/**
 * DAO for the Comanda entity, with CRUD operations and queries by table, employee, origin and status.
 */
public class ComandaDAO {

    private final ConnectionDB conexionDB = new ConnectionDB();

    // 1. CREATE (insert a new order)
    public boolean insert(Comanda comanda) {
        return insertGetId(comanda) > 0;
    }

    /**
     * Inserts a new order and returns the generated ID, or -1 when it fails.
     * @param comanda the Comanda object to insert
     * @return the ID of the inserted order, or -1 on error
     */
    public int insertGetId(Comanda comanda) {
        String sql = "INSERT INTO comanda (ORIGEN, codigo_emp, id_mesa, hora_orden, hora_generada, estado) VALUES (?, ?, ?, ?, ?, ?)";
        try (Connection con = conexionDB.getConexion();
             PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setString(1, comanda.getORIGEN());
            ps.setString(2, comanda.getCodigo_emp());
            if (comanda.getId_mesa() > 0) {
                ps.setInt(3, comanda.getId_mesa());
            } else {
                ps.setNull(3, Types.INTEGER); // bar orders have no table
            }
            ps.setTimestamp(4, comanda.getHora_orden() != null
                    ? Timestamp.valueOf(comanda.getHora_orden())
                    : new Timestamp(System.currentTimeMillis()));
            ps.setTimestamp(5, comanda.getHora_generada() != null
                    ? Timestamp.valueOf(comanda.getHora_generada())
                    : null);
            ps.setString(6, comanda.getEstado() != null ? comanda.getEstado() : "abierta");

            if (ps.executeUpdate() > 0) {
                try (ResultSet rs = ps.getGeneratedKeys()) {
                    if (rs.next()) {
                        return rs.getInt(1);
                    }
                }
            }
        } catch (SQLException e) {
            System.out.println("Error al insertar comanda: " + e.getMessage());
        }
        return -1;
    }

    // 2. UPDATE (change an existing order)
    public boolean update(Comanda comanda) {
        String sql = "UPDATE comanda SET ORIGEN = ?, codigo_emp = ?, id_mesa = ?, hora_orden = ?, hora_generada = ?, estado = ? WHERE id_comanda = ?";
        try (Connection con = conexionDB.getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, comanda.getORIGEN());
            ps.setString(2, comanda.getCodigo_emp());
            if (comanda.getId_mesa() > 0) {
                ps.setInt(3, comanda.getId_mesa());
            } else {
                ps.setNull(3, Types.INTEGER);
            }
            ps.setTimestamp(4, comanda.getHora_orden() != null
                    ? Timestamp.valueOf(comanda.getHora_orden())
                    : null);
            ps.setTimestamp(5, comanda.getHora_generada() != null
                    ? Timestamp.valueOf(comanda.getHora_generada())
                    : null);
            ps.setString(6, comanda.getEstado());
            ps.setInt(7, comanda.getId_comanda());

            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.out.println("Error al actualizar comanda: " + e.getMessage());
            return false;
        }
    }

    // 3. UPDATE status (abierta / en_proceso / lista / cerrada)
    public boolean updateEstado(int idComanda, String nuevoEstado) {
        String sql = "UPDATE comanda SET estado = ? WHERE id_comanda = ?";
        try (Connection con = conexionDB.getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, nuevoEstado);
            ps.setInt(2, idComanda);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.out.println("Error al actualizar estado de comanda: " + e.getMessage());
            return false;
        }
    }

    // 4. DELETE (remove an order by ID)
    public boolean delete(int idComanda) {
        String sql = "DELETE FROM comanda WHERE id_comanda = ?";
        try (Connection con = conexionDB.getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, idComanda);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.out.println("Error al eliminar comanda: " + e.getMessage());
            return false;
        }
    }

    // 5. READ ALL (list every order)
    public List<Comanda> findAll() {
        String sql = "SELECT * FROM comanda";
        List<Comanda> lista = new ArrayList<>();
        try (Connection con = conexionDB.getConexion();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                lista.add(mapRow(rs));
            }
        } catch (SQLException e) {
            System.out.println("Error al listar comandas: " + e.getMessage());
        }
        return lista;
    }

    // 6. READ BY ID (find one order)
    public Comanda findById(int idComanda) {
        String sql = "SELECT * FROM comanda WHERE id_comanda = ?";
        try (Connection con = conexionDB.getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, idComanda);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapRow(rs);
                }
            }
        } catch (SQLException e) {
            System.out.println("Error al buscar comanda por ID: " + e.getMessage());
        }
        return null;
    }

    // 7. READ by table (orders of one table)
    public List<Comanda> findByMesa(int idMesa) {
        String sql = "SELECT * FROM comanda WHERE id_mesa = ?";
        List<Comanda> lista = new ArrayList<>();
        try (Connection con = conexionDB.getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, idMesa);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    lista.add(mapRow(rs));
                }
            }
        } catch (SQLException e) {
            System.out.println("Error al buscar comandas por mesa: " + e.getMessage());
        }
        return lista;
    }

    // 8. READ by employee (employee code, e.g. SAL001)
    public List<Comanda> findByEmpleado(String codigoEmp) {
        String sql = "SELECT * FROM comanda WHERE codigo_emp = ?";
        List<Comanda> lista = new ArrayList<>();
        try (Connection con = conexionDB.getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, codigoEmp);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    lista.add(mapRow(rs));
                }
            }
        } catch (SQLException e) {
            System.out.println("Error al buscar comandas por empleado: " + e.getMessage());
        }
        return lista;
    }

    // 9. READ by origin and status (e.g. the ones pending in the kitchen or the bar)
    public List<Comanda> findByOrigenEstado(String origen, String estado) {
        String sql = "SELECT * FROM comanda WHERE ORIGEN = ? AND estado = ?";
        List<Comanda> lista = new ArrayList<>();
        try (Connection con = conexionDB.getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, origen);
            ps.setString(2, estado);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    lista.add(mapRow(rs));
                }
            }
        } catch (SQLException e) {
            System.out.println("Error al buscar comandas por origen/estado: " + e.getMessage());
        }
        return lista;
    }

    /** Maps the current ResultSet row into a Comanda object. */
    private Comanda mapRow(ResultSet rs) throws SQLException {
        Comanda c = new Comanda();
        c.setId_comanda(rs.getInt("id_comanda"));
        c.setORIGEN(rs.getString("ORIGEN"));
        c.setCodigo_emp(rs.getString("codigo_emp"));
        c.setId_mesa(rs.getInt("id_mesa")); // 0 when it is NULL (bar order)
        Timestamp horaOrden = rs.getTimestamp("hora_orden");
        c.setHora_orden(horaOrden != null ? horaOrden.toLocalDateTime() : null);
        Timestamp horaGenerada = rs.getTimestamp("hora_generada");
        c.setHora_generada(horaGenerada != null ? horaGenerada.toLocalDateTime() : null);
        c.setEstado(rs.getString("estado"));
        return c;
    }
}
