package com.example.lab06.api;

public class RegistroRequest {
    private String dni;
    private String correo;

    public RegistroRequest(String dni, String correo) {
        this.dni = dni;
        this.correo = correo;
    }

    public String getDni() {
        return dni;
    }

    public void setDni(String dni) {
        this.dni = dni;
    }

    public String getCorreo() {
        return correo;
    }

    public void setCorreo(String correo) {
        this.correo = correo;
    }
}
