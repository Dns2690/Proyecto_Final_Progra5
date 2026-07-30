/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package model;

/**
 *
 * @author denis
 */
public class Administrador {
    private int id_admin;
    private String usuario;
    private String contrasena;
    private String nombre;
    
    
    // Constructor vacio
    public Administrador() {}
    
    
    //Costructor Completo
    public Administrador(int id_admin, String usuario, String contrasena, String nombre){
        this.id_admin = id_admin;
        this.usuario = usuario;
        this.contrasena = contrasena;
        this.nombre = nombre;       
    }
    
    // Getters and setters
    public int getId_admin(){return id_admin;}
    public String getUsuario(){return usuario;}
    public String getContrasena(){return contrasena;}
    public String getNombre(){return nombre;}

    public void setId_admin(int id_admin){ this.id_admin = id_admin;}
    public void setUsuario(String usuario){ this.usuario = usuario;}
    public void setContrasena(String contrasena){ this.contrasena = contrasena;}
    public void setNombre(String nombre){this.nombre = nombre;}

}

