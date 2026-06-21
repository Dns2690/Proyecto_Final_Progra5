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
public class DetalleComanda {
    private int id_detalle;
    private int id_comanda;
    private String tipo_item;
    private int id_item;
    private int cantidad;
    private BigDecimal precio_unit;

    public DetalleComanda() {
    }

    public DetalleComanda(int id_detalle, int id_comanda, String tipo_item, int id_item, int cantidad, BigDecimal precio_unit) {
        this.id_detalle = id_detalle;
        this.id_comanda = id_comanda;
        this.tipo_item = tipo_item;
        this.id_item = id_item;
        this.cantidad = cantidad;
        this.precio_unit = precio_unit;
    }

    public int getId_detalle() {
        return id_detalle;
    }

    public void setId_detalle(int id_detalle) {
        this.id_detalle = id_detalle;
    }

    public int getId_comanda() {
        return id_comanda;
    }

    public void setId_comanda(int id_comanda) {
        this.id_comanda = id_comanda;
    }

    public String getTipo_item() {
        return tipo_item;
    }

    public void setTipo_item(String tipo_item) {
        this.tipo_item = tipo_item;
    }

    public int getId_item() {
        return id_item;
    }

    public void setId_item(int id_item) {
        this.id_item = id_item;
    }

    public int getCantidad() {
        return cantidad;
    }

    public void setCantidad(int cantidad) {
        this.cantidad = cantidad;
    }

    public BigDecimal getPrecio_unit() {
        return precio_unit;
    }

    public void setPrecio_unit(BigDecimal precio_unit) {
        this.precio_unit = precio_unit;
    }
    
    
    
}
