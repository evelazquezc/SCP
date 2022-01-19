package com.gza.scp.model;

import com.google.gson.annotations.SerializedName;

public class Dt_artAlerta {

    @SerializedName("suc")
    private String suc;
    @SerializedName("cve_art")
    private String cve_art;
    @SerializedName("sub_fam")
    private String sub_fam;

    public Dt_artAlerta(String suc, String cve_art, String sub_fam) {
        this.suc = suc;
        this.cve_art = cve_art;
        this.sub_fam = sub_fam;
    }

    public String getSuc() {
        return suc;
    }

    public void setSuc(String suc) {
        this.suc = suc;
    }

    public String getCve_art() {
        return cve_art;
    }

    public void setCve_art(String cve_art) {
        this.cve_art = cve_art;
    }

    public String getSub_fam() {
        return sub_fam;
    }

    public void setSub_fam(String sub_fam) {
        this.sub_fam = sub_fam;
    }
}
