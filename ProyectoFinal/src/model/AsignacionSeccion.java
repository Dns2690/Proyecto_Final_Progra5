/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package model;

import java.time.LocalDate;

/**
 *
 * @author denis
 */
public class AsignacionSeccion {
    private int id_asignacion;  
    private String codigo_sal; 
    private int id_seccion;   
    private LocalDate fecha;   
    

    public AsignacionSeccion() {
    }


    public AsignacionSeccion(String codigoSal, int idSeccion, LocalDate fecha) {
        this.codigo_sal = codigoSal;
        this.id_seccion = idSeccion;
        this.fecha = fecha;
    }


    public AsignacionSeccion(int idAsignacion, String codigoSal, int idSeccion, LocalDate fecha) {
        this.id_asignacion = idAsignacion;
        this.codigo_sal = codigoSal;
        this.id_seccion = idSeccion;
        this.fecha = fecha;
    }

    public int getIdAsignacion() {
        return id_asignacion;
    }

    public void setIdAsignacion(int idAsignacion) {
        this.id_asignacion = idAsignacion;
    }

    public String getCodigoSal() {
        return codigo_sal;
    }

    public void setCodigoSal(String codigoSal) {
        this.codigo_sal = codigoSal;
    }

    public int getIdSeccion() {
        return id_seccion;
    }

    public void setIdSeccion(int idSeccion) {
        this.id_seccion = idSeccion;
    }

    public LocalDate getFecha() {
        return fecha;
    }

    public void setFecha(LocalDate fecha) {
        this.fecha = fecha;
    }
    
    
}


