package dao;

import connection.ConnectionDB;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import model.AsignacionSeccion;
import model.SeccionSalon;

/**
 * DAO for the {@code asignacion_seccion} table: which salonero works which
 * dining-room section on a given date. The DB enforces one section per
 * salonero per day via UNIQUE(codigo_sal, fecha); the daily rotation rule
 * (each salonero moves to the next section every day) is implemented here in
 * {@link #generarRotacionDiaria(LocalDate)}.
 */
public class AsignacionSeccionDAO {

    private final ConnectionDB conexionDB = new ConnectionDB();

    // 1. CREATE (Insertar asignación; la BD rechaza duplicados salonero+fecha)
    public boolean insert(AsignacionSeccion asignacion) {
        String sql = "INSERT INTO asignacion_seccion (codigo_sal, id_seccion, fecha) VALUES (?, ?, ?)";
        try (Connection con = conexionDB.getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, asignacion.getCodigoSal());
            ps.setInt(2, asignacion.getIdSeccion());
            ps.setDate(3, Date.valueOf(asignacion.getFecha()));

            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.out.println("Error al insertar asignación: " + e.getMessage());
            return false;
        }
    }

    // 2. UPDATE (Modificar asignación existente)
    public boolean update(AsignacionSeccion asignacion) {
        String sql = "UPDATE asignacion_seccion SET codigo_sal = ?, id_seccion = ?, fecha = ? WHERE id_asignacion = ?";
        try (Connection con = conexionDB.getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, asignacion.getCodigoSal());
            ps.setInt(2, asignacion.getIdSeccion());
            ps.setDate(3, Date.valueOf(asignacion.getFecha()));
            ps.setInt(4, asignacion.getIdAsignacion());

            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.out.println("Error al actualizar asignación: " + e.getMessage());
            return false;
        }
    }

    // 3. DELETE (Eliminar asignación por ID)
    public boolean delete(int idAsignacion) {
        String sql = "DELETE FROM asignacion_seccion WHERE id_asignacion = ?";
        try (Connection con = conexionDB.getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, idAsignacion);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.out.println("Error al eliminar asignación: " + e.getMessage());
            return false;
        }
    }

    // 4. DELETE por fecha (para regenerar la rotación de un día)
    public boolean deleteByFecha(LocalDate fecha) {
        String sql = "DELETE FROM asignacion_seccion WHERE fecha = ?";
        try (Connection con = conexionDB.getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setDate(1, Date.valueOf(fecha));
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.out.println("Error al eliminar asignaciones por fecha: " + e.getMessage());
            return false;
        }
    }

    // 5. READ ALL (Listar todas las asignaciones)
    public List<AsignacionSeccion> findAll() {
        String sql = "SELECT * FROM asignacion_seccion";
        List<AsignacionSeccion> lista = new ArrayList<>();
        try (Connection con = conexionDB.getConexion();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                lista.add(mapRow(rs));
            }
        } catch (SQLException e) {
            System.out.println("Error al listar asignaciones: " + e.getMessage());
        }
        return lista;
    }

    // 6. READ BY ID (Buscar una asignación específica)
    public AsignacionSeccion findById(int idAsignacion) {
        String sql = "SELECT * FROM asignacion_seccion WHERE id_asignacion = ?";
        try (Connection con = conexionDB.getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, idAsignacion);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapRow(rs);
                }
            }
        } catch (SQLException e) {
            System.out.println("Error al buscar asignación por ID: " + e.getMessage());
        }
        return null;
    }

    // 7. READ por fecha (la planilla de secciones de un día)
    public List<AsignacionSeccion> findByFecha(LocalDate fecha) {
        String sql = "SELECT * FROM asignacion_seccion WHERE fecha = ? ORDER BY codigo_sal";
        List<AsignacionSeccion> lista = new ArrayList<>();
        try (Connection con = conexionDB.getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setDate(1, Date.valueOf(fecha));
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    lista.add(mapRow(rs));
                }
            }
        } catch (SQLException e) {
            System.out.println("Error al buscar asignaciones por fecha: " + e.getMessage());
        }
        return lista;
    }

    // 8. READ la sección de un salonero en una fecha (para la vista del salonero)
    public AsignacionSeccion findBySaloneroFecha(String codigoSal, LocalDate fecha) {
        String sql = "SELECT * FROM asignacion_seccion WHERE codigo_sal = ? AND fecha = ?";
        try (Connection con = conexionDB.getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, codigoSal);
            ps.setDate(2, Date.valueOf(fecha));
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapRow(rs);
                }
            }
        } catch (SQLException e) {
            System.out.println("Error al buscar asignación de salonero: " + e.getMessage());
        }
        return null;
    }

    /**
     * Generates the daily section rotation for the given date and returns
     * the day's assignments. Idempotent: if the date already has assignments
     * they are returned as-is (the DB's UNIQUE(codigo_sal, fecha) also
     * guards against duplicates).
     *
     * Rotation rule: each active salonero moves to the section AFTER the one
     * they had on the most recent assigned day (wrapping around). Saloneros
     * with no previous assignment get the first sections still unused that
     * day, in order.
     *
     * @return the assignments for that date (generated or pre-existing);
     *         empty list if there are no active saloneros/sections or on error
     */
    public List<AsignacionSeccion> generarRotacionDiaria(LocalDate fecha) {
        List<AsignacionSeccion> existentes = findByFecha(fecha);
        if (!existentes.isEmpty()) {
            System.out.println("La fecha " + fecha + " ya tiene asignaciones; no se regenera.");
            return existentes;
        }

        List<String> saloneros = findSalonerosActivos();
        List<SeccionSalon> secciones = new SeccionSalonDAO().findAll();
        secciones.sort(Comparator.comparingInt(SeccionSalon::getId_seccion));
        if (saloneros.isEmpty() || secciones.isEmpty()) {
            System.out.println("No hay saloneros activos o secciones para rotar.");
            return new ArrayList<>();
        }

        // Última asignación previa de cada salonero (día asignado más reciente antes de la fecha)
        Map<String, Integer> seccionAnterior = findUltimaSeccionPorSalonero(fecha);

        List<AsignacionSeccion> generadas = new ArrayList<>();
        Set<Integer> usadas = new HashSet<>();

        // Primero rotan los que ya tenían sección: pasan a la siguiente
        for (String codigoSal : saloneros) {
            Integer previa = seccionAnterior.get(codigoSal);
            if (previa == null) {
                continue;
            }
            int pos = 0;
            for (int i = 0; i < secciones.size(); i++) {
                if (secciones.get(i).getId_seccion() == previa) {
                    pos = i;
                    break;
                }
            }
            int nueva = secciones.get((pos + 1) % secciones.size()).getId_seccion();
            generadas.add(new AsignacionSeccion(codigoSal, nueva, fecha));
            usadas.add(nueva);
        }

        // Los saloneros nuevos toman las secciones que quedaron libres, en orden
        for (String codigoSal : saloneros) {
            if (seccionAnterior.containsKey(codigoSal)) {
                continue;
            }
            int nueva = secciones.get(0).getId_seccion();
            for (SeccionSalon s : secciones) {
                if (!usadas.contains(s.getId_seccion())) {
                    nueva = s.getId_seccion();
                    break;
                }
            }
            generadas.add(new AsignacionSeccion(codigoSal, nueva, fecha));
            usadas.add(nueva);
        }

        for (AsignacionSeccion a : generadas) {
            insert(a);
        }
        return findByFecha(fecha);
    }

    /** Active employees whose role prefix is SAL, ordered by code. */
    private List<String> findSalonerosActivos() {
        String sql = "SELECT u.codigo FROM usuario u "
                   + "JOIN tipo_usuario t ON u.id_tipo = t.id_tipo "
                   + "WHERE t.prefijo = 'SAL' AND u.activo = 1 "
                   + "ORDER BY u.codigo";
        List<String> lista = new ArrayList<>();
        try (Connection con = conexionDB.getConexion();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                lista.add(rs.getString("codigo"));
            }
        } catch (SQLException e) {
            System.out.println("Error al listar saloneros activos: " + e.getMessage());
        }
        return lista;
    }

    /**
     * For each salonero, the section they had on their most recent assigned
     * day strictly before the given date.
     */
    private Map<String, Integer> findUltimaSeccionPorSalonero(LocalDate fecha) {
        String sql = "SELECT a.codigo_sal, a.id_seccion FROM asignacion_seccion a "
                   + "JOIN (SELECT codigo_sal, MAX(fecha) AS max_fecha "
                   + "      FROM asignacion_seccion WHERE fecha < ? "
                   + "      GROUP BY codigo_sal) ult "
                   + "ON a.codigo_sal = ult.codigo_sal AND a.fecha = ult.max_fecha";
        Map<String, Integer> mapa = new HashMap<>();
        try (Connection con = conexionDB.getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setDate(1, Date.valueOf(fecha));
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    mapa.put(rs.getString("codigo_sal"), rs.getInt("id_seccion"));
                }
            }
        } catch (SQLException e) {
            System.out.println("Error al buscar últimas secciones: " + e.getMessage());
        }
        return mapa;
    }

    /** Maps the current ResultSet row to an AsignacionSeccion model. */
    private AsignacionSeccion mapRow(ResultSet rs) throws SQLException {
        AsignacionSeccion a = new AsignacionSeccion();
        a.setIdAsignacion(rs.getInt("id_asignacion"));
        a.setCodigoSal(rs.getString("codigo_sal"));
        a.setIdSeccion(rs.getInt("id_seccion"));
        Date fecha = rs.getDate("fecha");
        a.setFecha(fecha != null ? fecha.toLocalDate() : null);
        return a;
    }
}
