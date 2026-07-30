package dao;

import connection.ConnectionDB;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import model.ProcesoCocina;

/**
 * DAO for the ProcesoCocina entity, with CRUD operations and queries by order.
 */
public class ProcesoCocinaDAO {

    private final ConnectionDB conexionDB = new ConnectionDB();

    // 1. CREATE (the kitchen receives an order)
    public boolean insert(ProcesoCocina proceso) {
        String sql = "INSERT INTO proceso_cocina (id_comanda, hora_recibida, hora_lista, codigo_cos) VALUES (?, ?, ?, ?)";
        try (Connection con = conexionDB.getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, proceso.getId_comanda());
            ps.setTimestamp(2, proceso.getHora_recibida() != null
                    ? Timestamp.valueOf(proceso.getHora_recibida())
                    : new Timestamp(System.currentTimeMillis()));
            ps.setTimestamp(3, proceso.getHora_lista() != null
                    ? Timestamp.valueOf(proceso.getHora_lista())
                    : null);
            ps.setString(4, proceso.getCodigo_cos());

            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.out.println("Error al insertar proceso de cocina: " + e.getMessage());
            return false;
        }
    }

    // 2. UPDATE (change an existing process)
    public boolean update(ProcesoCocina proceso) {
        String sql = "UPDATE proceso_cocina SET id_comanda = ?, hora_recibida = ?, hora_lista = ?, codigo_cos = ? WHERE id_proceso = ?";
        try (Connection con = conexionDB.getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, proceso.getId_comanda());
            ps.setTimestamp(2, proceso.getHora_recibida() != null
                    ? Timestamp.valueOf(proceso.getHora_recibida())
                    : null);
            ps.setTimestamp(3, proceso.getHora_lista() != null
                    ? Timestamp.valueOf(proceso.getHora_lista())
                    : null);
            ps.setString(4, proceso.getCodigo_cos());
            ps.setInt(5, proceso.getId_proceso());

            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.out.println("Error al actualizar proceso de cocina: " + e.getMessage());
            return false;
        }
    }

    // 3. UPDATE hora_lista (mark the order as ready in the kitchen)
    public boolean updateHoraLista(int idProceso, LocalDateTime horaLista) {
        String sql = "UPDATE proceso_cocina SET hora_lista = ? WHERE id_proceso = ?";
        try (Connection con = conexionDB.getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setTimestamp(1, horaLista != null
                    ? Timestamp.valueOf(horaLista)
                    : new Timestamp(System.currentTimeMillis()));
            ps.setInt(2, idProceso);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.out.println("Error al actualizar hora lista de cocina: " + e.getMessage());
            return false;
        }
    }

    // 4. DELETE (remove a process by ID)
    public boolean delete(int idProceso) {
        String sql = "DELETE FROM proceso_cocina WHERE id_proceso = ?";
        try (Connection con = conexionDB.getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, idProceso);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.out.println("Error al eliminar proceso de cocina: " + e.getMessage());
            return false;
        }
    }

    // 5. READ ALL (list every process)
    public List<ProcesoCocina> findAll() {
        String sql = "SELECT * FROM proceso_cocina";
        List<ProcesoCocina> lista = new ArrayList<>();
        try (Connection con = conexionDB.getConexion();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                lista.add(mapRow(rs));
            }
        } catch (SQLException e) {
            System.out.println("Error al listar procesos de cocina: " + e.getMessage());
        }
        return lista;
    }

    // 6. READ BY ID (find one process)
    public ProcesoCocina findById(int idProceso) {
        String sql = "SELECT * FROM proceso_cocina WHERE id_proceso = ?";
        try (Connection con = conexionDB.getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, idProceso);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapRow(rs);
                }
            }
        } catch (SQLException e) {
            System.out.println("Error al buscar proceso de cocina por ID: " + e.getMessage());
        }
        return null;
    }

    // 7. READ by order (id_comanda is UNIQUE, so there is at most one process)
    public ProcesoCocina findByComanda(int idComanda) {
        String sql = "SELECT * FROM proceso_cocina WHERE id_comanda = ?";
        try (Connection con = conexionDB.getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, idComanda);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapRow(rs);
                }
            }
        } catch (SQLException e) {
            System.out.println("Error al buscar proceso de cocina por comanda: " + e.getMessage());
        }
        return null;
    }

    // 8. READ pending (the cook queue: the ones with no hora_lista yet)
    public List<ProcesoCocina> findPendientes() {
        String sql = "SELECT * FROM proceso_cocina WHERE hora_lista IS NULL ORDER BY hora_recibida";
        List<ProcesoCocina> lista = new ArrayList<>();
        try (Connection con = conexionDB.getConexion();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                lista.add(mapRow(rs));
            }
        } catch (SQLException e) {
            System.out.println("Error al listar procesos de cocina pendientes: " + e.getMessage());
        }
        return lista;
    }

    /** Maps the current ResultSet row into a ProcesoCocina object. */
    private ProcesoCocina mapRow(ResultSet rs) throws SQLException {
        ProcesoCocina p = new ProcesoCocina();
        p.setId_proceso(rs.getInt("id_proceso"));
        p.setId_comanda(rs.getInt("id_comanda"));
        Timestamp horaRecibida = rs.getTimestamp("hora_recibida");
        p.setHora_recibida(horaRecibida != null ? horaRecibida.toLocalDateTime() : null);
        Timestamp horaLista = rs.getTimestamp("hora_lista");
        p.setHora_lista(horaLista != null ? horaLista.toLocalDateTime() : null);
        p.setCodigo_cos(rs.getString("codigo_cos"));
        return p;
    }
}
