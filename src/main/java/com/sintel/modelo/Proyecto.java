package com.sintel.modelo;

import java.math.BigDecimal;
import java.time.LocalDate;

public class Proyecto {

    private int idproyectos;
    private String nombre;
    private int idclientes;
    private int idestados;
    private LocalDate fechaEntrega;
    private BigDecimal valorTotal;
    private BigDecimal iva;

    public Proyecto() {
    }

    public Proyecto(String nombre,
                    int idclientes,
                    int idestados,
                    LocalDate fechaEntrega,
                    BigDecimal valorTotal,
                    BigDecimal iva) {

        this.nombre = nombre;
        this.idclientes = idclientes;
        this.idestados = idestados;
        this.fechaEntrega = fechaEntrega;
        this.valorTotal = valorTotal;
        this.iva = iva;
    }

    public int getIdproyectos() {
        return idproyectos;
    }

    public void setIdproyectos(int idproyectos) {
        this.idproyectos = idproyectos;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public int getIdclientes() {
        return idclientes;
    }

    public void setIdclientes(int idclientes) {
        this.idclientes = idclientes;
    }

    public int getIdestados() {
        return idestados;
    }

    public void setIdestados(int idestados) {
        this.idestados = idestados;
    }

    public LocalDate getFechaEntrega() {
        return fechaEntrega;
    }

    public void setFechaEntrega(LocalDate fechaEntrega) {
        this.fechaEntrega = fechaEntrega;
    }

    public BigDecimal getValorTotal() {
        return valorTotal;
    }

    public void setValorTotal(BigDecimal valorTotal) {
        this.valorTotal = valorTotal;
    }

    public BigDecimal getIva() {
        return iva;
    }

    public void setIva(BigDecimal iva) {
        this.iva = iva;
    }
}