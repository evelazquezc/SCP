package com.gza.scp.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class Dt_ticket implements Serializable {
    @SerializedName("Caja")
    private String Caja;
    @SerializedName("Transaccion")
    private String Transaccion;
    @SerializedName("Importe")
    private String Importe;
    @SerializedName("Fec_cob")
    private String Fec_cob;
    @SerializedName("Hra_cob")
    private String Hra_cob;
    @SerializedName("Suc")
    private String Suc;
    private boolean productosAlerta;

    public Dt_ticket(String caja, String transaccion, String importe, String fec_cob, String hra_cob) {
        Caja = caja;
        Transaccion = transaccion;
        Importe = importe;
        Fec_cob = fec_cob;
        Hra_cob = hra_cob;
        productosAlerta=true;
    }

    public boolean isProductosAlerta() {
        return productosAlerta;
    }

    public void setProductosAlerta(boolean productosAlerta) {
        this.productosAlerta = productosAlerta;
    }

    public String getCaja() {
        return Caja;
    }

    public void setCaja(String caja) {
        Caja = caja;
    }

    public String getTransaccion() {
        return Transaccion;
    }

    public void setTransaccion(String transaccion) {
        Transaccion = transaccion;
    }

    public String getImporte() {
        return Importe;
    }

    public void setImporte(String importe) {
        Importe = importe;
    }

    public String getFec_cob() {
        return Fec_cob;
    }

    public void setFec_cob(String fec_cob) {
        Fec_cob = fec_cob;
    }

    public String getHra_cob() {
        return Hra_cob;
    }

    public void setHra_cob(String hra_cob) {
        Hra_cob = hra_cob;
    }

    public String getSuc() {
        return Suc;
    }

    public void setSuc(String suc) {
        Suc = suc;
    }
}

