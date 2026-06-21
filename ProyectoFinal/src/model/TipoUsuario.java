/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package model;

/**
 *
 * @author denis
 */
public class TipoUsuario {
    
    private int id_tipo;
    private String nombre;
    private String prefijo;

    public TipoUsuario() {
    }

    public TipoUsuario(int id_tipo, String nombre, String prefijo) {
        this.id_tipo = id_tipo;
        this.nombre = nombre;
        this.prefijo = prefijo;
    }

    public int getId_tipo() {
        return id_tipo;
    }

    public void setId_tipo(int id_tipo) {
        this.id_tipo = id_tipo;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getPrefijo() {
        return prefijo;
    }

    public void setPrefijo(String prefijo) {
        this.prefijo = prefijo;
    }
    
    
    
}


