package dao;

import connection.ConnectionDB;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * DAO that builds the reports about orders, reservations and preparation processes.
 */
public class ReporteDAO {

    private final ConnectionDB conexionDB = new ConnectionDB();

    /**
     * Report: number of people served per day in the DINING ROOM.
  
     */
    public Map<String, Integer> personasAtendidaXDiaSalon() throws Exception {
        return personasAtendidaXDia("salon");
    }

    /**
     * Report: number of people served per day in the BAR.
  
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
     * Report: number of orders CREATED in the bar,
     * grouped by day. It includes every order whose origin is 'bar',
     * no matter its current status.
     * Key = date (yyyy-MM-dd), Value = total orders created that day.
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
     * Report: number of orders that were SERVED
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
     * Report: number of orders served by the KITCHEN
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