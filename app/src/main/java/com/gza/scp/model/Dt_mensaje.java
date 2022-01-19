package com.gza.scp.model;

import com.google.gson.annotations.SerializedName;

public class Dt_mensaje {
    @SerializedName("Mensaje")
    private String mensaje;

    public String getMensaje() {
        return mensaje;
    }

    public void setMensaje(String mensaje) {
        this.mensaje = mensaje;
    }
}
