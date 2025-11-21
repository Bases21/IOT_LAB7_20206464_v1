package com.example.lab06.models;

public class Usuario {
    private String id;
    private String nombre;
    private String correo;
    private String dni;
    private String fotoPerfil;

    public Usuario() {
    }

    public Usuario(String id, String nombre, String correo, String dni) {
        this.id = id;
        this.nombre = nombre;
        this.correo = correo;
        this.dni = dni;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getCorreo() {
        return correo;
    }

    public void setCorreo(String correo) {
        this.correo = correo;
    }

    public String getDni() {
        return dni;
    }

    public void setDni(String dni) {
        this.dni = dni;
    }

    public String getFotoPerfil() {
        return fotoPerfil;
    }

    public void setFotoPerfil(String fotoPerfil) {
        this.fotoPerfil = fotoPerfil;
    }
}
