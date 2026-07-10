package dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class ReservaDAO {
    
    // Configuración básica de tu base de datos
    private final String url = "jdbc:mysql://localhost:3306/registro";
    private final String user = "root";
    private final String pass = "123456"; // Pon aquí tu contraseña de MySQL

    // 1. Método para INSERTAR una reserva (pasando los datos directos y sin catch)
    public boolean insert(String nombreCliente, String fecha, String hora, int personas, String estado) throws Exception {
        String sql = "INSERT INTO reserva (nombre_cliente, fecha, hora, personas, estado) VALUES (?, ?, ?, ?, ?)";
        
        Connection con = DriverManager.getConnection(url, user, pass);
        PreparedStatement ps = con.prepareStatement(sql);
        
        ps.setString(1, nombreCliente);
        ps.setString(2, fecha);
        ps.setString(3, hora);
        ps.setInt(4, personas);
        ps.setString(5, estado);
        
        int filas = ps.executeUpdate();
        
        ps.close();
        con.close();
        
        return filas > 0;
    }

    // 2. Método para BUSCAR DISPONIBILIDAD (sin catch)
    public boolean findDisponibilidad(String fecha, String hora, int personas) throws Exception {
        String sql = "SELECT COUNT(*) FROM reserva WHERE fecha = ? AND hora = ?";
        boolean disponible = false;
        
        Connection con = DriverManager.getConnection(url, user, pass);
        PreparedStatement ps = con.prepareStatement(sql);
        
        ps.setString(1, fecha);
        ps.setString(2, hora);
        
        ResultSet rs = ps.executeQuery();
        if (rs.next()) {
            int reservasExistentes = rs.getInt(1);
            // Si hay menos de 10 reservas, hay espacio
            if (reservasExistentes < 10) {
                disponible = true;
            }
        }
        
        rs.close();
        ps.close();
        con.close();
        
        return disponible;
    }

    // 3. Método para BUSCAR TODAS las reservas (sin catch)
    public ResultSet findAll() throws Exception {
        String sql = "SELECT * FROM reserva";
        
        Connection con = DriverManager.getConnection(url, user, pass);
        PreparedStatement ps = con.prepareStatement(sql);
        
        return ps.executeQuery(); 
    }
}