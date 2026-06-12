/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package conexion;

/**
 *
 * @author denis
 */

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;


public class Conexion {
    
        private Connection cn;
    
    public Connection getConexion() {
        try {
            // Connection String
            String url = "jdbc:mysql://localhost:3306/db_universidad?allowPublicKeyRetrieval=true&useSSL=false";
            String user = "root";
            String pass = "Denis2690@";
            
            cn = DriverManager.getConnection(url, user, pass);
            System.out.println("¡Conexión exitosa a la base de datos!"); 
            
        } catch (SQLException e) {
            System.out.println("Error en conexión: " + e.getMessage());
        }
        return cn;
    }
    
}
