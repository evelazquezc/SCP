package com.gza.scp.model;

import com.google.gson.annotations.SerializedName;

public class Dt_usuario {
    @SerializedName("nombre")
    private String nombre;
    @SerializedName("pwd")
    private String pwd;
    @SerializedName("puesto")
    private String puesto;
    @SerializedName("cia_ventas")
    private String ciaVentas;
    @SerializedName("cve_suc")
    private String cveSuc;

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getPwd() {
        return pwd;
    }

    public void setPwd(String pwd) {
        this.pwd = pwd;
    }

    public String getPuesto() {
        return puesto;
    }

    public void setPuesto(String puesto) {
        this.puesto = puesto;
    }

    public String getCiaVentas() {
        return ciaVentas;
    }

    public void setCiaVentas(String ciaVentas) {
        this.ciaVentas = ciaVentas;
    }

    public String getCveSuc() {
        return cveSuc;
    }

    public void setCveSuc(String cveSuc) {
        this.cveSuc = cveSuc;
    }
}
