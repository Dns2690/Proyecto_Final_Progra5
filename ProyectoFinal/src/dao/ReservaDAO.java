package dao;

import connection.ConnectionDB;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Time;
import java.sql.Timestamp;
import java.sql.Types;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import model.Mesa;
import model.Reserva;

/**
 * DAO para la entidad Reserva, con operaciones CRUD y consultas por fecha y disponibilidad.
 */
public class ReservaDAO {

    /** Cuanto le damos de duracion a una reserva, para saber si dos se chocan. */
    public static final int MINUTOS_RESERVA = 120;

    /** Hora a la que cierra el restaurante, hasta ahi se busca mesa. */
    private static final LocalTime HORA_CIERRE = LocalTime.of(22, 0);

    private final ConnectionDB conexionDB = new ConnectionDB();

    // 1. CREATE (Insertar nueva reserva)
    public boolean insert(Reserva reserva) {
        String sql = "INSERT INTO reserva (nombre_cliente, telefono, fecha_reserva, hora_reserva, cantidad_pers, incluye_ninos, id_mesa, estado) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection con = conexionDB.getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, reserva.getNombre_cliente());
            ps.setString(2, reserva.getTelefono());
            ps.setDate(3, Date.valueOf(reserva.getFecha_reserva()));
            ps.setTime(4, Time.valueOf(reserva.getHora_reserva()));
            ps.setInt(5, reserva.getCantidad_pers());
            ps.setBoolean(6, reserva.isIncluye_ninos());
            if (reserva.getId_mesa() > 0) {
                ps.setInt(7, reserva.getId_mesa());
            } else {
                ps.setNull(7, Types.INTEGER); // aún sin mesa asignada
            }
            ps.setString(8, reserva.getEstado() != null ? reserva.getEstado() : "pendiente");

            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.out.println("Error al insertar reserva: " + e.getMessage());
            return false;
        }
    }

    // 2. UPDATE (Modificar reserva existente)
    public boolean update(Reserva reserva) {
        String sql = "UPDATE reserva SET nombre_cliente = ?, telefono = ?, fecha_reserva = ?, hora_reserva = ?, cantidad_pers = ?, incluye_ninos = ?, id_mesa = ?, estado = ? WHERE id_reserva = ?";
        try (Connection con = conexionDB.getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, reserva.getNombre_cliente());
            ps.setString(2, reserva.getTelefono());
            ps.setDate(3, Date.valueOf(reserva.getFecha_reserva()));
            ps.setTime(4, Time.valueOf(reserva.getHora_reserva()));
            ps.setInt(5, reserva.getCantidad_pers());
            ps.setBoolean(6, reserva.isIncluye_ninos());
            if (reserva.getId_mesa() > 0) {
                ps.setInt(7, reserva.getId_mesa());
            } else {
                ps.setNull(7, Types.INTEGER);
            }
            ps.setString(8, reserva.getEstado());
            ps.setInt(9, reserva.getId_reserva());

            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.out.println("Error al actualizar reserva: " + e.getMessage());
            return false;
        }
    }

    // 3. UPDATE estado (pendiente / confirmada / cancelada / atendida)
    public boolean updateEstado(int idReserva, String nuevoEstado) {
        String sql = "UPDATE reserva SET estado = ? WHERE id_reserva = ?";
        try (Connection con = conexionDB.getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, nuevoEstado);
            ps.setInt(2, idReserva);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.out.println("Error al actualizar estado de reserva: " + e.getMessage());
            return false;
        }
    }

    // 4. DELETE (Eliminar reserva por ID)
    public boolean delete(int idReserva) {
        String sql = "DELETE FROM reserva WHERE id_reserva = ?";
        try (Connection con = conexionDB.getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, idReserva);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.out.println("Error al eliminar reserva: " + e.getMessage());
            return false;
        }
    }

    // 5. READ ALL (Listar todas las reservas)
    public List<Reserva> findAll() {
        String sql = "SELECT * FROM reserva";
        List<Reserva> lista = new ArrayList<>();
        try (Connection con = conexionDB.getConexion();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                lista.add(mapRow(rs));
            }
        } catch (SQLException e) {
            System.out.println("Error al listar reservas: " + e.getMessage());
        }
        return lista;
    }

    // 6. READ BY ID (Buscar una reserva específica)
    public Reserva findById(int idReserva) {
        String sql = "SELECT * FROM reserva WHERE id_reserva = ?";
        try (Connection con = conexionDB.getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, idReserva);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapRow(rs);
                }
            }
        } catch (SQLException e) {
            System.out.println("Error al buscar reserva por ID: " + e.getMessage());
        }
        return null;
    }

    // 7. READ por fecha (agenda del día)
    public List<Reserva> findByFecha(LocalDate fecha) {
        String sql = "SELECT * FROM reserva WHERE fecha_reserva = ?";
        List<Reserva> lista = new ArrayList<>();
        try (Connection con = conexionDB.getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setDate(1, Date.valueOf(fecha));
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    lista.add(mapRow(rs));
                }
            }
        } catch (SQLException e) {
            System.out.println("Error al buscar reservas por fecha: " + e.getMessage());
        }
        return lista;
    }

    /**
     * Chequea si hay disponibilidad para una nueva reserva en la fecha y hora dadas.
     * Devuelve true si hay menos de 10 reservas activas (no canceladas)
     * para esa fecha y hora, false si ya hay 10 o más.
     */
    public boolean findDisponibilidad(LocalDate fecha, LocalTime hora) {
        String sql = "SELECT COUNT(*) FROM reserva WHERE fecha_reserva = ? AND hora_reserva = ? AND estado <> 'cancelada'";
        try (Connection con = conexionDB.getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setDate(1, Date.valueOf(fecha));
            ps.setTime(2, Time.valueOf(hora));
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) < 10;
                }
            }
        } catch (SQLException e) {
            System.out.println("Error al consultar disponibilidad: " + e.getMessage());
        }
        return false;
    }

    /**
     * Devuelve las mesas que no tienen reserva a esa fecha y hora.
     * Se considera que una reserva ocupa la mesa por MINUTOS_RESERVA, asi que
     * dos reservas se chocan si sus horas estan mas cerca que eso.
     */
    public List<Mesa> findMesasLibres(LocalDate fecha, LocalTime hora) {
        // se usa TIMEDIFF y no TIMESTAMPDIFF porque con columnas TIME este ultimo devuelve NULL
        String sql = "SELECT m.* FROM mesa m WHERE m.id_mesa NOT IN ("
                   + "  SELECT r.id_mesa FROM reserva r "
                   + "  WHERE r.fecha_reserva = ? AND r.estado <> 'cancelada' AND r.id_mesa IS NOT NULL "
                   + "    AND ABS(TIME_TO_SEC(TIMEDIFF(r.hora_reserva, ?))) < ? * 60 "
                   + ") ORDER BY m.numero_mesa";

        List<Mesa> lista = new ArrayList<>();
        try (Connection con = conexionDB.getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setDate(1, Date.valueOf(fecha));
            ps.setTime(2, Time.valueOf(hora));
            ps.setInt(3, MINUTOS_RESERVA);

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
            System.out.println("Error al buscar mesas libres: " + e.getMessage());
        }
        return lista;
    }

    /**
     * Busca la primera hora despues de la pedida en que se desocupan
     * suficientes mesas. Devuelve null si ya no queda campo ese dia.
     */
    public LocalTime proximaHoraLibre(LocalDate fecha, LocalTime desde, int mesasNecesarias) {
        LocalTime hora = desde.plusHours(1);
        while (!hora.isAfter(HORA_CIERRE)) {
            if (findMesasLibres(fecha, hora).size() >= mesasNecesarias) {
                return hora;
            }
            hora = hora.plusHours(1);
        }
        return null;
    }

    /** Mapea la fila actual del ResultSet a un modelo Reserva. */
    private Reserva mapRow(ResultSet rs) throws SQLException {
        Reserva r = new Reserva();
        r.setId_reserva(rs.getInt("id_reserva"));
        r.setNombre_cliente(rs.getString("nombre_cliente"));
        r.setTelefono(rs.getString("telefono"));
        Date fechaReserva = rs.getDate("fecha_reserva");
        r.setFecha_reserva(fechaReserva != null ? fechaReserva.toLocalDate() : null);
        Time horaReserva = rs.getTime("hora_reserva");
        r.setHora_reserva(horaReserva != null ? horaReserva.toLocalTime() : null);
        r.setCantidad_pers(rs.getInt("cantidad_pers"));
        r.setIncluye_ninos(rs.getBoolean("incluye_ninos"));
        r.setId_mesa(rs.getInt("id_mesa")); // 0 cuando aún no hay mesa
        r.setEstado(rs.getString("estado"));
        Timestamp fechaCreacion = rs.getTimestamp("fecha_creacion");
        r.setFecha_creacion(fechaCreacion != null ? fechaCreacion.toLocalDateTime() : null);
        return r;
    }
}
