package com.gza.scp.model;

import java.util.List;

public class SectionProducto {
    private String titulo;
    private List<Dt_articulo> articulos;

    public SectionProducto(String titulo, List<Dt_articulo> articulos) {
        this.titulo = titulo;
        this.articulos = articulos;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public List<Dt_articulo> getArticulos() {
        return articulos;
    }

    public void setArticulos(List<Dt_articulo> articulos) {
        this.articulos = articulos;
    }
}
