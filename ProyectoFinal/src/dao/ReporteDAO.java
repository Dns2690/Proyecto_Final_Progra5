package dao;

import connection.ConnectionDB;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * DAO responsible for generating aggregated data used in the chart-based
 * reports required by the project (Reportes section).
 * All methods return LinkedHashMap so the insertion order (usually by date)
 * is preserved for chart rendering (e.g. JFreeChart).
 */
public class ReporteDAO {

    private final ConnectionDB conexionDB = new ConnectionDB();

    /**
     * Reporte: Cantidad de personas atendidas por día en el SALÓN.
     * La tabla comanda no tiene columna id_reserva, así que la comanda se
     * liga a su reserva por mesa y fecha: r.id_mesa = c.id_mesa y
     * r.fecha_reserva = DATE(c.hora_orden). Se suman reserva.cantidad_pers.
     * Key = fecha (yyyy-MM-dd), Value = total de personas ese día.
     */
    public Map<String, Integer> personasAtendidaXDiaSalon() throws Exception {
        return personasAtendidaXDia("salon");
    }

    /**
     * Reporte: Cantidad de personas atendidas por día en el BAR.
     * NOTA: según las reglas del negocio, el bar no maneja reservaciones
     * (solo el salón), por lo que este reporte normalmente devolverá vacío
     * a menos que se generen comandas de bar ligadas a una reserva.
     * Key = fecha (yyyy-MM-dd), Value = total de personas ese día.
     */
    public Map<String, Integer> personasAtendidaXDiaBar() throws Exception {
        return personasAtendidaXDia("bar");
    }

    private Map<String, Integer> personasAtendidaXDia(String origen) throws Exception {
        Map<String, Integer> resultado = new LinkedHashMap<>();
        String sql = "SELECT DATE(c.hora_orden) AS fecha, SUM(r.cantidad_pers) AS total_personas "
                   + "FROM comanda c "
                   + "JOIN reserva r ON r.id_mesa = c.id_mesa "
                   + "AND r.fecha_reserva = DATE(c.hora_orden) "
                   + "WHERE c.origen = ? "
                   + "GROUP BY DATE(c.hora_orden) "
                   + "ORDER BY fecha";

        try (Connection con = conexionDB.getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, origen);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    String fecha = rs.getString("fecha");
                    int total = rs.getInt("total_personas");
                    resultado.put(fecha, total);
                }
            }
        } catch (SQLException e) {
            System.out.println("Error en personasAtendidaXDia (" + origen + "): " + e.getMessage());
            throw e;
        }
        return resultado;
    }

    /**
     * Reporte: Cantidad de comandas que se REALIZARON (creadas) en el bar,
     * agrupadas por día. Incluye todas las comandas de origen 'bar' sin
     * importar su estado actual.
     * Key = fecha (yyyy-MM-dd), Value = total de comandas creadas ese día.
     */
    public Map<String, Integer> comandasRealizadasBar() throws Exception {
        Map<String, Integer> resultado = new LinkedHashMap<>();
        String sql = "SELECT DATE(hora_orden) AS fecha, COUNT(*) AS total "
                   + "FROM comanda "
                   + "WHERE origen = 'bar' "
                   + "GROUP BY DATE(hora_orden) "
                   + "ORDER BY fecha";

        try (Connection con = conexionDB.getConexion();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                resultado.put(rs.getString("fecha"), rs.getInt("total"));
            }
        } catch (SQLException e) {
            System.out.println("Error en comandasRealizadasBar: " + e.getMessage());
            throw e;
        }
        return resultado;
    }

    /**
     * Reporte: Cantidad de comandas que fueron ATENDIDAS (cerradas) en el
     * bar, agrupadas por día. Solo cuenta las comandas con estado 'cerrada'.
     * Key = fecha (yyyy-MM-dd), Value = total de comandas cerradas ese día.
     */
    public Map<String, Integer> comandasAtendidasBar() throws Exception {
        Map<String, Integer> resultado = new LinkedHashMap<>();
        String sql = "SELECT DATE(hora_orden) AS fecha, COUNT(*) AS total "
                   + "FROM comanda "
                   + "WHERE origen = 'bar' AND estado = 'cerrada' "
                   + "GROUP BY DATE(hora_orden) "
                   + "ORDER BY fecha";

        try (Connection con = conexionDB.getConexion();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                resultado.put(rs.getString("fecha"), rs.getInt("total"));
            }
        } catch (SQLException e) {
            System.out.println("Error en comandasAtendidasBar: " + e.getMessage());
            throw e;
        }
        return resultado;
    }

    /**
     * Reporte: Cantidad de comandas atendidas en la COCINA (hora_lista no
     * nula en proceso_cocina), desglosadas según si la comanda original
     * era de salón o de bar.
     * Devuelve un mapa con exactamente dos llaves: "salon" y "bar".
     */
    public Map<String, Integer> comandasCocinaDesglosadas() throws Exception {
        Map<String, Integer> resultado = new LinkedHashMap<>();
        resultado.put("salon", 0);
        resultado.put("bar", 0);

        String sql = "SELECT c.origen, COUNT(*) AS total "
                   + "FROM proceso_cocina pc "
                   + "JOIN comanda c ON pc.id_comanda = c.id_comanda "
                   + "WHERE pc.hora_lista IS NOT NULL "
                   + "GROUP BY c.origen";

        try (Connection con = conexionDB.getConexion();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                resultado.put(rs.getString("origen"), rs.getInt("total"));
            }
        } catch (SQLException e) {
            System.out.println("Error en comandasCocinaDesglosadas: " + e.getMessage());
            throw e;
        }
        return resultado;
    }
}