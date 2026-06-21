/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package model;

/**
 *
 * @author denis
 */
public class DetalleFactura {
    private int id_det_fac;
    private int id_factura;
    private int id_detalle;

    public DetalleFactura() {
    }

    public DetalleFactura(int id_det_fac, int id_factura, int id_detalle) {
        this.id_det_fac = id_det_fac;
        this.id_factura = id_factura;
        this.id_detalle = id_detalle;
    }

    public int getId_det_fac() {
        return id_det_fac;
    }

    public void setId_det_fac(int id_det_fac) {
        this.id_det_fac = id_det_fac;
    }

    public int getId_factura() {
        return id_factura;
    }

    public void setId_factura(int id_factura) {
        this.id_factura = id_factura;
    }

    public int getId_detalle() {
        return id_detalle;
    }

    public void setId_detalle(int id_detalle) {
        this.id_detalle = id_detalle;
    }
    
    
}
