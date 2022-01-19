package com.gza.scp.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class Dt_articulo implements Serializable
{
    @SerializedName("cve_art")
    private String cve_art;
    @SerializedName("des_art")
    private String des_art;
    @SerializedName("msg")
    private String msg;
    @SerializedName("cantidad")
    private String cantidad;
    @SerializedName("devueltas")
    private String devueltas;
    @SerializedName("subtotal")
    private String subtotal;
    @SerializedName("NumTra")
    private String NumTra;
    @SerializedName("Caja")
    private String Caja;
    @SerializedName("FecOpe")
    private String FecOpe;
    @SerializedName("EMP")
    private String EMP;
    @SerializedName("tipo")
    private String tipo;
    @SerializedName("Excepcion")
    private String Excepcion;
    private boolean productoEscaneado;
    private boolean productoAlerta;
    private boolean coincideCan;
    private boolean pesable;
    private boolean noEncontrado;
    private boolean excepcion;

    public Dt_articulo()
    {
        cve_art = "";
        des_art = "";
        msg = "";
        cantidad = "0";
        devueltas = "";
        subtotal = "";
        NumTra = "";
        Caja = "";
        FecOpe = "";
        EMP="";
        Excepcion="";
        productoEscaneado=false;
        productoAlerta=false;
        coincideCan=false;
        pesable=false;
        excepcion=false;
    }

    public Dt_articulo(String cve_art, String des_art, String msg, String cantidad, String devueltas, String subtotal, String numTra, String caja, String fecOpe, String EMP, String tipo, String excepcion, boolean productoEscaneado, boolean productoAlerta, boolean coincideCan, boolean pesable, boolean noEncontrado, boolean excepcion1) {
        this.cve_art = cve_art;
        this.des_art = des_art;
        this.msg = msg;
        this.cantidad = cantidad;
        this.devueltas = devueltas;
        this.subtotal = subtotal;
        NumTra = numTra;
        Caja = caja;
        FecOpe = fecOpe;
        this.EMP = EMP;
        this.tipo = tipo;
        Excepcion = excepcion;
        this.productoEscaneado = productoEscaneado;
        this.productoAlerta = productoAlerta;
        this.coincideCan = coincideCan;
        this.pesable = pesable;
        this.noEncontrado = noEncontrado;
        this.excepcion = excepcion1;
    }

    public boolean isNoEncontrado() {
        return noEncontrado;
    }

    public void setNoEncontrado(boolean noEncontrado) {
        this.noEncontrado = noEncontrado;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public String getEMP() {
        return EMP;
    }

    public void setEMP(String EMP) {
        this.EMP = EMP;
    }

    public boolean isPesable() {
        return pesable;
    }

    public void setPesable(boolean pesable) {
        this.pesable = pesable;
    }

    public boolean isProductoAlerta() {
        return productoAlerta;
    }

    public void setProductoAlerta(boolean productoAlerta) {
        this.productoAlerta = productoAlerta;
    }

    public boolean isProductoEscaneado() {
        return productoEscaneado;
    }

    public void setProductoEscaneado(boolean productoEscaneado) {
        this.productoEscaneado = productoEscaneado;
    }

    public String getCve_art() {
        return cve_art;
    }

    public void setCve_art(String cve_art) {
        this.cve_art = cve_art;
    }

    public String getDes_art() {
        return des_art;
    }

    public void setDes_art(String des_art) {
        this.des_art = des_art;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public String getCantidad() {
        return cantidad;
    }

    public void setCantidad(String cantidad) {
        this.cantidad = cantidad;
    }

    public String getDevueltas() {
        return devueltas;
    }

    public void setDevueltas(String devueltas) {
        this.devueltas = devueltas;
    }

    public String getSubtotal() {
        return subtotal;
    }

    public void setSubtotal(String subtotal) {
        this.subtotal = subtotal;
    }

    public String getNumTra() {
        return NumTra;
    }

    public void setNumTra(String numTra) {
        NumTra = numTra;
    }

    public String getCaja() {
        return Caja;
    }

    public void setCaja(String caja) {
        Caja = caja;
    }

    public String getFecOpe() {
        return FecOpe;
    }

    public void setFecOpe(String fecOpe) {
        FecOpe = fecOpe;
    }

    public boolean isCoincideCan() {
        return coincideCan;
    }

    public void setCoincideCan(boolean coincideCan) {
        this.coincideCan = coincideCan;
    }

    public String getExcepcion() {
        return Excepcion;
    }

    public void setExcepcion(String excepcion) {
        Excepcion = excepcion;
    }

    public boolean isExcepcion() {
        return excepcion;
    }

    public void setExcepcion(boolean excepcion) {
        this.excepcion = excepcion;
    }
}
