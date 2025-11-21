package com.example.lab06.api;

import com.google.gson.annotations.SerializedName;

public class RegistroResponse {
    @SerializedName("valido")
    private boolean exito;
    private String mensaje;

    public boolean isExito() {
        return exito;
    }

    public void setExito(boolean exito) {
        this.exito = exito;
    }

    public String getMensaje() {
        return mensaje;
    }

    public void setMensaje(String mensaje) {
        this.mensaje = mensaje;
    }
}
