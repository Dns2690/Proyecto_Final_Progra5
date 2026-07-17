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
import model.ProcesoBar;

/**
 * DAO para la entidad ProcesoBar, con operaciones CRUD y consultas por comanda.
 */
public class ProcesoBarDAO {

    private final ConnectionDB conexionDB = new ConnectionDB();

    // 1. CREATE (El bar recibe una comanda)
    public boolean insert(ProcesoBar proceso) {
        String sql = "INSERT INTO proceso_bar (id_comanda, hora_recibida, hora_lista, codigo_bar) VALUES (?, ?, ?, ?)";
        try (Connection con = conexionDB.getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, proceso.getId_comanda());
            ps.setTimestamp(2, proceso.getHora_recibida() != null
                    ? Timestamp.valueOf(proceso.getHora_recibida())
                    : new Timestamp(System.currentTimeMillis()));
            ps.setTimestamp(3, proceso.getHora_lista() != null
                    ? Timestamp.valueOf(proceso.getHora_lista())
                    : null);
            ps.setString(4, proceso.getCodigo_bar());

            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.out.println("Error al insertar proceso de bar: " + e.getMessage());
            return false;
        }
    }

    // 2. UPDATE (Modificar proceso existente)
    public boolean update(ProcesoBar proceso) {
        String sql = "UPDATE proceso_bar SET id_comanda = ?, hora_recibida = ?, hora_lista = ?, codigo_bar = ? WHERE id_proceso = ?";
        try (Connection con = conexionDB.getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, proceso.getId_comanda());
            ps.setTimestamp(2, proceso.getHora_recibida() != null
                    ? Timestamp.valueOf(proceso.getHora_recibida())
                    : null);
            ps.setTimestamp(3, proceso.getHora_lista() != null
                    ? Timestamp.valueOf(proceso.getHora_lista())
                    : null);
            ps.setString(4, proceso.getCodigo_bar());
            ps.setInt(5, proceso.getId_proceso());

            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.out.println("Error al actualizar proceso de bar: " + e.getMessage());
            return false;
        }
    }

    // 3. UPDATE hora_lista (Marcar comanda como lista en el bar)
    public boolean updateHoraLista(int idProceso, LocalDateTime horaLista) {
        String sql = "UPDATE proceso_bar SET hora_lista = ? WHERE id_proceso = ?";
        try (Connection con = conexionDB.getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setTimestamp(1, horaLista != null
                    ? Timestamp.valueOf(horaLista)
                    : new Timestamp(System.currentTimeMillis()));
            ps.setInt(2, idProceso);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.out.println("Error al actualizar hora lista de bar: " + e.getMessage());
            return false;
        }
    }

    // 4. DELETE (Eliminar proceso por ID)
    public boolean delete(int idProceso) {
        String sql = "DELETE FROM proceso_bar WHERE id_proceso = ?";
        try (Connection con = conexionDB.getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, idProceso);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.out.println("Error al eliminar proceso de bar: " + e.getMessage());
            return false;
        }
    }

    // 5. READ ALL (Listar todos los procesos)
    public List<ProcesoBar> findAll() {
        String sql = "SELECT * FROM proceso_bar";
        List<ProcesoBar> lista = new ArrayList<>();
        try (Connection con = conexionDB.getConexion();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                lista.add(mapRow(rs));
            }
        } catch (SQLException e) {
            System.out.println("Error al listar procesos de bar: " + e.getMessage());
        }
        return lista;
    }

    // 6. READ BY ID (Buscar un proceso específico)
    public ProcesoBar findById(int idProceso) {
        String sql = "SELECT * FROM proceso_bar WHERE id_proceso = ?";
        try (Connection con = conexionDB.getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, idProceso);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapRow(rs);
                }
            }
        } catch (SQLException e) {
            System.out.println("Error al buscar proceso de bar por ID: " + e.getMessage());
        }
        return null;
    }

    // 7. READ por comanda (id_comanda es UNIQUE: a lo sumo un proceso)
    public ProcesoBar findByComanda(int idComanda) {
        String sql = "SELECT * FROM proceso_bar WHERE id_comanda = ?";
        try (Connection con = conexionDB.getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, idComanda);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapRow(rs);
                }
            }
        } catch (SQLException e) {
            System.out.println("Error al buscar proceso de bar por comanda: " + e.getMessage());
        }
        return null;
    }

    // 8. READ pendientes (cola del bartender: aún sin hora_lista)
    public List<ProcesoBar> findPendientes() {
        String sql = "SELECT * FROM proceso_bar WHERE hora_lista IS NULL ORDER BY hora_recibida";
        List<ProcesoBar> lista = new ArrayList<>();
        try (Connection con = conexionDB.getConexion();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                lista.add(mapRow(rs));
            }
        } catch (SQLException e) {
            System.out.println("Error al listar procesos de bar pendientes: " + e.getMessage());
        }
        return lista;
    }

    /** Mapea la fila actual del ResultSet a un modelo ProcesoBar. */
    private ProcesoBar mapRow(ResultSet rs) throws SQLException {
        ProcesoBar p = new ProcesoBar();
        p.setId_proceso(rs.getInt("id_proceso"));
        p.setId_comanda(rs.getInt("id_comanda"));
        Timestamp horaRecibida = rs.getTimestamp("hora_recibida");
        p.setHora_recibida(horaRecibida != null ? horaRecibida.toLocalDateTime() : null);
        Timestamp horaLista = rs.getTimestamp("hora_lista");
        p.setHora_lista(horaLista != null ? horaLista.toLocalDateTime() : null);
        p.setCodigo_bar(rs.getString("codigo_bar"));
        return p;
    }
}
