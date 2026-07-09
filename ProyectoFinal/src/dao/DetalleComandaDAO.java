package dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class DetalleComandaDAO {

    // Configuración básica de tu base de datos
    private final String url = "jdbc:mysql://localhost:3306/registro";
    private final String user = "root";
    private final String pass = "123456"; // Pon tu contraseña de MySQL

    // 1. Método para INSERTAR un producto al detalle de la comanda
    public boolean insert(int idComanda, int idProducto, int cantidad, double precioUnitario) throws Exception {
        String sql = "INSERT INTO detalle_comanda (id_comanda, id_producto, cantidad, precio_unitario) VALUES (?, ?, ?, ?)";
        
        Connection con = DriverManager.getConnection(url, user, pass);
        PreparedStatement ps = con.prepareStatement(sql);
        
        ps.setInt(1, idComanda);
        ps.setInt(2, idProducto);
        ps.setInt(3, cantidad);
        ps.setDouble(4, precioUnitario);
        
        int filas = ps.executeUpdate();
        
        ps.close();
        con.close();
        
        return filas > 0;
    }

    // 2. Método para BUSCAR TODO EL DETALLE de una comanda específica
    public ResultSet findByComanda(int idComanda) throws Exception {
        String sql = "SELECT * FROM detalle_comanda WHERE id_comanda = ?";
        
        Connection con = DriverManager.getConnection(url, user, pass);
        PreparedStatement ps = con.prepareStatement(sql);
        ps.setInt(1, idComanda);
        
        return ps.executeQuery(); // Devuelve los platos/bebidas de esa comanda para tu tabla
    }
}