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
public class Comida {


    
    private int id_comida;
    private String nombre;
    private int id_categoria; // FK categoria_comida
    private String descripcion;
    private BigDecimal precio;
    private int activo;

    public Comida() {}

    public Comida(int id_comida, String nombre, int id_categoria, String descripcion, BigDecimal precio, int activo) {
        this.id_comida = id_comida;
        this.nombre = nombre;
        this.id_categoria = id_categoria;
        this.descripcion = descripcion;
        this.precio = precio;
        this.activo = activo;
    }
    
    // Getters and Setters
        public int getId_comida() {
        return id_comida;
    }

    public void setId_comida(int id_comida) {
        this.id_comida = id_comida;
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

