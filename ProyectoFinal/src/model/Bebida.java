/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package model;

import java.math.BigDecimal;

/**
 *
 * @author denis
 */
public class Bebida {
    private int id_bebida;
    private String nombre;
    private int id_categoria;
    private String descripcion;
    private BigDecimal precio;
    private int activo;

    public Bebida() {
    }

    public Bebida(int id_bebida, String nombre, int id_categoria, String descripcion, BigDecimal precio, int activo) {
        this.id_bebida = id_bebida;
        this.nombre = nombre;
        this.id_categoria = id_categoria;
        this.descripcion = descripcion;
        this.precio = precio;
        this.activo = activo;
    }

    public int getId_bebida() {
        return id_bebida;
    }

    public void setId_bebida(int id_bebida) {
        this.id_bebida = id_bebida;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public int getId_categoria() {
        return id_categoria;
    }

    public void setId_categoria(int id_categoria) {
        this.id_categoria = id_categoria;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public BigDecimal getPrecio() {
        return precio;
    }

    public void setPrecio(BigDecimal precio) {
        this.precio = precio;
    }

    public int getActivo() {
        return activo;
    }

    public void setActivo(int activo) {
        this.activo = activo;
    }
    
    
    
}
