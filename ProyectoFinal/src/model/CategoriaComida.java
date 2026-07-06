/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package model;

/**
 *
 * @author denis
 */
public class CategoriaComida {
    
    private int id_categoria;
    private String nombre;
    
    
    //Constructor vacio
    public CategoriaComida(){}
    
    //Construuctor Completo
    public CategoriaComida(int id_categoria, String nombre){
        this.id_categoria = id_categoria;
        this.nombre = nombre;
    }
    
    // Getters y Setters
    
    public int getId_categoria(){ return id_categoria;}
    public String getNombre(){return nombre;}
    
    public void setId_categoria(int id_categoria){this.id_categoria = id_categoria;}
    public void setNombre(String nombre){this.nombre = nombre;}
    
}



