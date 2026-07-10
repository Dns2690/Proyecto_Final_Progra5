package dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class FacturaDAO {

    // Configuración básica de tu base de datos
    private final String url = "jdbc:mysql://localhost:3306/registro";
    private final String user = "root";
    private final String pass = "123456"; // Pon tu contraseña de MySQL

    // 1. Método para INSERTAR una factura
    public boolean insert(int idComanda, String fecha, double total, String estado) throws Exception {
        String sql = "INSERT INTO factura (id_comanda, fecha, total, estado) VALUES (?, ?, ?, ?)";
        
        Connection con = DriverManager.getConnection(url, user, pass);
        PreparedStatement ps = con.prepareStatement(sql);
        
        ps.setInt(1, idComanda);
        ps.setString(2, fecha);
        ps.setDouble(3, total);
        ps.setString(4, estado);
        
        int filas = ps.executeUpdate();
        
        ps.close();
        con.close();
        
        return filas > 0;
    }

    // 2. Método para BUSCAR LA FACTURA por el ID de la comanda
    public ResultSet findByComanda(int idComanda) throws Exception {
        String sql = "SELECT * FROM factura WHERE id_comanda = ?";
        
        Connection con = DriverManager.getConnection(url, user, pass);
        PreparedStatement ps = con.prepareStatement(sql);
        ps.setInt(1, idComanda);
        
        return ps.executeQuery(); // Devuelve los datos de la factura para tu pantalla
    }

    // 3. Método para ACTUALIZAR EL ESTADO de la factura (ej: cambiar a 'Pagada')
    public boolean updateEstado(int idFactura, String nuevoEstado) throws Exception {
        String sql = "UPDATE factura SET estado = ? WHERE id_factura = ?";
        
        Connection con = DriverManager.getConnection(url, user, pass);
        PreparedStatement ps = con.prepareStatement(sql);
        
        ps.setString(1, nuevoEstado);
        ps.setInt(2, idFactura);
        
        int filas = ps.executeUpdate();
        
        ps.close();
        con.close();
        
        return filas > 0;
    }

    // 4. Método para BUSCAR TODAS las facturas
    public ResultSet findAll() throws Exception {
        String sql = "SELECT * FROM factura";
        
        Connection con = DriverManager.getConnection(url, user, pass);
        PreparedStatement ps = con.prepareStatement(sql);
        
        return ps.executeQuery(); // Devuelve todo el historial de facturas para tu tabla
    }
}