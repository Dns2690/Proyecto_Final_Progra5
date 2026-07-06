package dao;

import connection.ConnectionDB;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import model.CategoriaComida;

public class CategoriaComidaDAO {

    private final ConnectionDB conexionDB = new ConnectionDB();

    // Método directo para listar categorías de comida
    public List<CategoriaComida> findAll() {
        String sql = "SELECT * FROM categoria_comida";
        List<CategoriaComida> lista = new ArrayList<>();
        try (Connection con = conexionDB.getConexion();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            
            while (rs.next()) {
                CategoriaComida cc = new CategoriaComida();
                cc.setId_categoria(rs.getInt("id_categoria"));
                cc.setNombre(rs.getString("nombre"));
                lista.add(cc);
            }
        } catch (SQLException e) {
            System.out.println("Error al listar categorías de comida: " + e.getMessage());
        }
        return lista;
    }
}