package com.gza.scp.model;

public class RevisionArticulo {
    private String cve_art;
    private int canTot;
    private int canEsc;

    public RevisionArticulo() {
    }

    public RevisionArticulo(String cve_art, int canTot, int canEsc) {
        this.cve_art = cve_art;
        this.canTot = canTot;
        this.canEsc = canEsc;
    }

    public String getCve_art() {
        return cve_art;
    }

    public void setCve_art(String cve_art) {
        this.cve_art = cve_art;
    }

    public int getCanTot() {
        return canTot;
    }

    public void setCanTot(int canTot) {
        this.canTot = canTot;
    }

    public int getCanEsc() {
        return canEsc;
    }

    public void setCanEsc(int canEsc) {
        this.canEsc = canEsc;
    }
}

