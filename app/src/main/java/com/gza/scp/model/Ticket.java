package com.gza.scp.model;

import java.io.Serializable;
import java.util.Comparator;
import java.util.List;

public class Ticket implements Serializable {
    private Dt_ticket ticket;
    private List<Dt_articulo> artAlert;
    private List<Dt_articulo> artGral;

    public Ticket(Dt_ticket ticket, List<Dt_articulo> artAlert, List<Dt_articulo> artGral) {
        this.ticket = ticket;
        this.artAlert = artAlert;
        this.artGral = artGral;
    }

    public Dt_ticket getTicket() {
        return ticket;
    }

    public void setTicket(Dt_ticket ticket) {
        this.ticket = ticket;
    }

    public List<Dt_articulo> getArtAlert() {
        return artAlert;
    }

    public void setArtAlert(List<Dt_articulo> artAlert) {
        this.artAlert = artAlert;
    }

    public List<Dt_articulo> getArtGral() {
        return artGral;
    }

    public void setArtGral(List<Dt_articulo> artGral) {
        this.artGral = artGral;
    }

}
