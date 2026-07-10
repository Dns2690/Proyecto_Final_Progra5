package dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;

public class ProcesoCocinaDAO {

    private final String url = "jdbc:mysql://localhost:3306/registro";
    private final String user = "root";
    private final String pass = "123456";

    // 1. Método para INSERTAR en cocina
    public boolean insert(int idDetalleComanda, String horaInicio) throws Exception {
        String sql = "INSERT INTO proceso_cocina (id_detalle_comanda, hora_inicio) VALUES (?, ?)";
        
        Connection con = DriverManager.getConnection(url, user, pass);
        PreparedStatement ps = con.prepareStatement(sql);
        
        ps.setInt(1, idDetalleComanda);
        ps.setString(2, horaInicio);
        
        int filas = ps.executeUpdate();
        
        ps.close();
        con.close();
        
        return filas > 0;
    }

    // 2. Método para ACTUALIZAR LA HORA LISTA en cocina
    public boolean updateHoraLista(int idProcesoCocina, String horaLista) throws Exception {
        String sql = "UPDATE proceso_cocina SET hora_lista = ? WHERE id_proceso_cocina = ?";
        
        Connection con = DriverManager.getConnection(url, user, pass);
        PreparedStatement ps = con.prepareStatement(sql);
        
        ps.setString(1, horaLista);
        ps.setInt(2, idProcesoCocina);
        
        int filas = ps.executeUpdate();
        
        ps.close();
        con.close();
        
        return filas > 0;
    }
}
