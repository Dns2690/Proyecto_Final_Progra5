/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package model;

import java.time.LocalDateTime;

/**
 *
 * @author denis
 */
public class Comanda {
    private int id_comanda;
    private String ORIGEN;
    private String codigo_emp;
    private int id_mesa;
    private LocalDateTime hora_orden;
    private LocalDateTime hora_generada;
    private String estado;

    public Comanda() {
    }

    public Comanda(int id_comanda, String ORIGEN, String codigo_emp, int id_mesa, LocalDateTime hora_orden, LocalDateTime hora_generada, String estado) {
        this.id_comanda = id_comanda;
        this.ORIGEN = ORIGEN;
        this.codigo_emp = codigo_emp;
        this.id_mesa = id_mesa;
        this.hora_orden = hora_orden;
        this.hora_generada = hora_generada;
        this.estado = estado;
    }

    public int getId_comanda() {
        return id_comanda;
    }

    public void setId_comanda(int id_comanda) {
        this.id_comanda = id_comanda;
    }

    public String getORIGEN() {
        return ORIGEN;
    }

    public void setORIGEN(String ORIGEN) {
        this.ORIGEN = ORIGEN;
    }

    public String getCodigo_emp() {
        return codigo_emp;
    }

    public void setCodigo_emp(String codigo_emp) {
        this.codigo_emp = codigo_emp;
    }

    public int getId_mesa() {
        return id_mesa;
    }

    public void setId_mesa(int id_mesa) {
        this.id_mesa = id_mesa;
    }

    public LocalDateTime getHora_orden() {
        return hora_orden;
    }

    public void setHora_orden(LocalDateTime hora_orden) {
        this.hora_orden = hora_orden;
    }

    public LocalDateTime getHora_generada() {
        return hora_generada;
    }

    public void setHora_generada(LocalDateTime hora_generada) {
        this.hora_generada = hora_generada;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }
    
    
    
}
