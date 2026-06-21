/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package model;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

/**
 *
 * @author denis
 */
public class Reserva {
    private int id_reserva;
    private String nombre_cliente;
    private String telefono;
    private LocalDate fecha_reserva;
    private LocalTime hora_reserva;
    private int cantidad_pers;
    private boolean incluye_ninos;
    private int id_mesa;
    private String estado;
    private LocalDateTime fecha_creacion;

    public Reserva() {
    }

    public Reserva(int id_reserva, String nombre_cliente, String telefono, LocalDate fecha_reserva, LocalTime hora_reserva, int cantidad_pers, boolean incluye_ninos, int id_mesa, String estado, LocalDateTime fecha_creacion) {
        this.id_reserva = id_reserva;
        this.nombre_cliente = nombre_cliente;
        this.telefono = telefono;
        this.fecha_reserva = fecha_reserva;
        this.hora_reserva = hora_reserva;
        this.cantidad_pers = cantidad_pers;
        this.incluye_ninos = incluye_ninos;
        this.id_mesa = id_mesa;
        this.estado = estado;
        this.fecha_creacion = fecha_creacion;
    }

    public int getId_reserva() {
        return id_reserva;
    }

    public void setId_reserva(int id_reserva) {
        this.id_reserva = id_reserva;
    }

    public String getNombre_cliente() {
        return nombre_cliente;
    }

    public void setNombre_cliente(String nombre_cliente) {
        this.nombre_cliente = nombre_cliente;
    }

    public String getTelefono() {
        return telefono;
    }

    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }

    public LocalDate getFecha_reserva() {
        return fecha_reserva;
    }

    public void setFecha_reserva(LocalDate fecha_reserva) {
        this.fecha_reserva = fecha_reserva;
    }

    public LocalTime getHora_reserva() {
        return hora_reserva;
    }

    public void setHora_reserva(LocalTime hora_reserva) {
        this.hora_reserva = hora_reserva;
    }

    public int getCantidad_pers() {
        return cantidad_pers;
    }

    public void setCantidad_pers(int cantidad_pers) {
        this.cantidad_pers = cantidad_pers;
    }

    public boolean isIncluye_ninos() {
        return incluye_ninos;
    }

    public void setIncluye_ninos(boolean incluye_ninos) {
        this.incluye_ninos = incluye_ninos;
    }

    public int getId_mesa() {
        return id_mesa;
    }

    public void setId_mesa(int id_mesa) {
        this.id_mesa = id_mesa;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public LocalDateTime getFecha_creacion() {
        return fecha_creacion;
    }

    public void setFecha_creacion(LocalDateTime fecha_creacion) {
        this.fecha_creacion = fecha_creacion;
    }
    
    
    
}
