package com.gza.scp.model;

import java.util.List;
import com.google.gson.annotations.SerializedName;

public class UsuarioLogin {
    @SerializedName("Table")
    private List<Dt_mensaje> dtmensaje = null;
    @SerializedName("Table1")
    private List<Dt_usuario> dtusuario = null;

    public List<Dt_mensaje> getDtmensaje() {
        return dtmensaje;
    }

    public void setDtmensaje(List<Dt_mensaje> dtmensaje) {
        this.dtmensaje = dtmensaje;
    }

    public List<Dt_usuario> getDtusuario() {
        return dtusuario;
    }

    public void setDtusuario(List<Dt_usuario> dtusuario) {
        this.dtusuario = dtusuario;
    }
}


