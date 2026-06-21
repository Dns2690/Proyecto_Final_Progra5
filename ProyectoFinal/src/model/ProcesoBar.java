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
public class ProcesoBar {
    private int id_proceso;
    private int id_comanda;
    private LocalDateTime hora_recibida;
    private LocalDateTime hora_lista;
    private String codigo_bar;   

    public ProcesoBar() {
    }

    public ProcesoBar(int id_proceso, int id_comanda, LocalDateTime hora_recibida, LocalDateTime hora_lista, String codigo_bar) {
        this.id_proceso = id_proceso;
        this.id_comanda = id_comanda;
        this.hora_recibida = hora_recibida;
        this.hora_lista = hora_lista;
        this.codigo_bar = codigo_bar;
    }

    public int getId_proceso() {
        return id_proceso;
    }

    public void setId_proceso(int id_proceso) {
        this.id_proceso = id_proceso;
    }

    public int getId_comanda() {
        return id_comanda;
    }

    public void setId_comanda(int id_comanda) {
        this.id_comanda = id_comanda;
    }

    public LocalDateTime getHora_recibida() {
        return hora_recibida;
    }

    public void setHora_recibida(LocalDateTime hora_recibida) {
        this.hora_recibida = hora_recibida;
    }

    public LocalDateTime getHora_lista() {
        return hora_lista;
    }

    public void setHora_lista(LocalDateTime hora_lista) {
        this.hora_lista = hora_lista;
    }

    public String getCodigo_bar() {
        return codigo_bar;
    }

    public void setCodigo_bar(String codigo_bar) {
        this.codigo_bar = codigo_bar;
    }
    
    
    
}
