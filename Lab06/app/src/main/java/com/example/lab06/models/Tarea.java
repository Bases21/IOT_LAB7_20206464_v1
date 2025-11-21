package com.example.lab06.models;

import java.io.Serializable;

public class Tarea implements Serializable {
    private String id;
    private String titulo;
    private String descripcion;
    private long fechaLimite;
    private boolean estado;
    private String userId;

    public Tarea() {
    }

    public Tarea(String titulo, String descripcion, long fechaLimite, boolean estado, String userId) {
        this.titulo = titulo;
        this.descripcion = descripcion;
        this.fechaLimite = fechaLimite;
        this.estado = estado;
        this.userId = userId;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public long getFechaLimite() {
        return fechaLimite;
    }

    public void setFechaLimite(long fechaLimite) {
        this.fechaLimite = fechaLimite;
    }

    public boolean isEstado() {
        return estado;
    }

    public void setEstado(boolean estado) {
        this.estado = estado;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getEstadoTexto() {
        return estado ? "Completada" : "Pendiente";
    }
}
