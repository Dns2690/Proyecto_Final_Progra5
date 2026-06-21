/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package model;

/**
 *
 * @author denis
 */
public class Mesa {
    
    private int id_mesa;
    private int numero_mesa;
    private int id_seccion;
    private int disponible;

    public Mesa() {
    }

    public Mesa(int id_mesa, int numero_mesa, int id_seccion, int disponible) {
        this.id_mesa = id_mesa;
        this.numero_mesa = numero_mesa;
        this.id_seccion = id_seccion;
        this.disponible = disponible;
    }

    public int getId_mesa() {
        return id_mesa;
    }

    public void setId_mesa(int id_mesa) {
        this.id_mesa = id_mesa;
    }

    public int getNumero_mesa() {
        return numero_mesa;
    }

    public void setNumero_mesa(int numero_mesa) {
        this.numero_mesa = numero_mesa;
    }

    public int getId_seccion() {
        return id_seccion;
    }

    public void setId_seccion(int id_seccion) {
        this.id_seccion = id_seccion;
    }

    public int getDisponible() {
        return disponible;
    }

    public void setDisponible(int disponible) {
        this.disponible = disponible;
    }
    
    
    
}
