package com.sintel.modelo;

public class EstadoProyecto {

    private int idestados;
    private String nombre;

    public EstadoProyecto() {
    }

    public EstadoProyecto(int idestados, String nombre) {
        this.idestados = idestados;
        this.nombre = nombre;
    }

    public int getIdestados() {
        return idestados;
    }

    public void setIdestados(int idestados) {
        this.idestados = idestados;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }
}