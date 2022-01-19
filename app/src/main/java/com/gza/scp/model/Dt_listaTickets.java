package com.gza.scp.model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class Dt_listaTickets {
    @SerializedName("Table")
    private List<Dt_ticket> dtTickets = null;
    @SerializedName("Table1")
    private List<Dt_articulo> dtArticulos = null;

    public Dt_listaTickets(List<Dt_ticket> dtTickets, List<Dt_articulo> dtArticulos) {
        this.dtTickets = dtTickets;
        this.dtArticulos = dtArticulos;
    }

    public List<Dt_ticket> getDtTickets() {
        return dtTickets;
    }

    public void setDtTickets(List<Dt_ticket> dtTickets) {
        this.dtTickets = dtTickets;
    }

    public List<Dt_articulo> getDtArticulos() {
        return dtArticulos;
    }

    public void setDtArticulos(List<Dt_articulo> dtArticulos) {
        this.dtArticulos = dtArticulos;
    }
}
