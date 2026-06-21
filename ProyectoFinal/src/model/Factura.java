/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package model;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 *
 * @author denis
 */
public class Factura {
    private int id_factura;
    private int id_comanda;
    private String codigo_cajero;
    private LocalDateTime fecha_emision;
    private BigDecimal subtotal;
    private BigDecimal impuesto;
    private BigDecimal total;
    private String tipo;
    private String estado;

    public Factura() {
    }

    public Factura(int id_factura, int id_comanda, String codigo_cajero, LocalDateTime fecha_emision, BigDecimal subtotal, BigDecimal impuesto, BigDecimal total, String tipo, String estado) {
        this.id_factura = id_factura;
        this.id_comanda = id_comanda;
        this.codigo_cajero = codigo_cajero;
        this.fecha_emision = fecha_emision;
        this.subtotal = subtotal;
        this.impuesto = impuesto;
        this.total = total;
        this.tipo = tipo;
        this.estado = estado;
    }

    public int getId_factura() {
        return id_factura;
    }

    public void setId_factura(int id_factura) {
        this.id_factura = id_factura;
    }

    public int getId_comanda() {
        return id_comanda;
    }

    public void setId_comanda(int id_comanda) {
        this.id_comanda = id_comanda;
    }

    public String getCodigo_cajero() {
        return codigo_cajero;
    }

    public void setCodigo_cajero(String codigo_cajero) {
        this.codigo_cajero = codigo_cajero;
    }

    public LocalDateTime getFecha_emision() {
        return fecha_emision;
    }

    public void setFecha_emision(LocalDateTime fecha_emision) {
        this.fecha_emision = fecha_emision;
    }

    public BigDecimal getSubtotal() {
        return subtotal;
    }

    public void setSubtotal(BigDecimal subtotal) {
        this.subtotal = subtotal;
    }

    public BigDecimal getImpuesto() {
        return impuesto;
    }

    public void setImpuesto(BigDecimal impuesto) {
        this.impuesto = impuesto;
    }

    public BigDecimal getTotal() {
        return total;
    }

    public void setTotal(BigDecimal total) {
        this.total = total;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }
    
    
    
}
