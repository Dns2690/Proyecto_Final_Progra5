package dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class ComandaDAO {

    // Configuración básica de tu base de datos
    private final String url = "jdbc:mysql://localhost:3306/registro";
    private final String user = "root";
    private final String pass = "123456"; // Pon tu contraseña de MySQL

    // 1. Método para INSERTAR una comanda
    public boolean insert(int idMesa, int idEmpleado, String fecha, String estado) throws Exception {
        String sql = "INSERT INTO comanda (id_mesa, id_empleado, fecha, estado) VALUES (?, ?, ?, ?)";
        
        Connection con = DriverManager.getConnection(url, user, pass);
        PreparedStatement ps = con.prepareStatement(sql);
        
        ps.setInt(1, idMesa);
        ps.setInt(2, idEmpleado);
        ps.setString(3, fecha);
        ps.setString(4, estado);
        
        int filas = ps.executeUpdate();
        
        ps.close();
        con.close();
        
        return filas > 0;
    }

    // 2. Método para ACTUALIZAR EL ESTADO de la comanda
    public boolean updateEstado(int idComanda, String nuevoEstado) throws Exception {
        String sql = "UPDATE comanda SET estado = ? WHERE id_comanda = ?";
        
        Connection con = DriverManager.getConnection(url, user, pass);
        PreparedStatement ps = con.prepareStatement(sql);
        
        ps.setString(1, nuevoEstado);
        ps.setInt(2, idComanda);
        
        int filas = ps.executeUpdate();
        
        ps.close();
        con.close();
        
        return filas > 0;
    }

    // 3. Método para BUSCAR POR MESA
    public ResultSet findByMesa(int idMesa) throws Exception {
        String sql = "SELECT * FROM comanda WHERE id_mesa = ?";
        
        Connection con = DriverManager.getConnection(url, user, pass);
        PreparedStatement ps = con.prepareStatement(sql);
        ps.setInt(1, idMesa);
        
        return ps.executeQuery(); // Devuelve las comandas de esa mesa para tu tabla
    }

    // 4. Método para BUSCAR POR EMPLEADO
    public ResultSet findByEmpleado(int idEmpleado) throws Exception {
        String sql = "SELECT * FROM comanda WHERE id_empleado = ?";
        
        Connection con = DriverManager.getConnection(url, user, pass);
        PreparedStatement ps = con.prepareStatement(sql);
        ps.setInt(1, idEmpleado);
        
        return ps.executeQuery(); // Devuelve las comandas de ese empleado para tu tabla
    }
}
