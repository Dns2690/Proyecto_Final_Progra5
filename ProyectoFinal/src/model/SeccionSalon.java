/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package model;

/**
 *
 * @author denis
 */
public class SeccionSalon {
    
    private int id_seccion;
    private String nombre;

    public SeccionSalon() {
    }

    public SeccionSalon(int id_seccion, String nombre) {
        this.id_seccion = id_seccion;
        this.nombre = nombre;
    }

    public int getId_seccion() {
        return id_seccion;
    }

    public void setId_seccion(int id_seccion) {
        this.id_seccion = id_seccion;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }
    
    
    
}

