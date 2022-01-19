package com.gza.scp.clases;

import android.content.Context;

import com.gza.scp.R;

public class ParametrosWS {

    private String URL;
    private String METODO;
    private String SOAP_ACTION;
    private String NAMESPACES;
    private String KEY;
    private int TIMEOUT;

    public ParametrosWS(String METODO, Context context) {
        this.METODO = METODO;
        URL= String.format(context.getResources().getString(R.string.ws_url), Utils.GetHost(context) ) ;
        TIMEOUT = Integer.parseInt( context.getResources().getString(R.string.ws_to) );
        NAMESPACES = context.getResources().getString(R.string.ws_ns);
        SOAP_ACTION= NAMESPACES + METODO;
        KEY = context.getResources().getString(R.string.ws_key);
    }

    public String getKEY() {
        return KEY;
    }
    public int getTIMEOUT() {
        return TIMEOUT;
    }

    public String getURL() {
        return URL;
    }

    public String getMETODO() {
        return METODO;
    }

    public String getSOAP_ACTION() {
        return SOAP_ACTION;
    }

    public String getNAMESPACES() {
        return NAMESPACES;
    }

    public void setURL(String URL) {
        this.URL = URL;
    }

    public void setMETODO(String METODO) {
        this.METODO = METODO;
    }

    public void setSOAP_ACTION(String SOAP_ACTION) {
        this.SOAP_ACTION = SOAP_ACTION;
    }

    public void setNAMESPACES(String NAMESPACES) {
        this.NAMESPACES = NAMESPACES;
    }

    public void setKEY(String KEY) {
        this.KEY = KEY;
    }

    public void setTIMEOUT(int TIMEOUT) {
        this.TIMEOUT = TIMEOUT;
    }
}
