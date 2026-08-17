package com.gestorgastos.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.math.BigDecimal;
import java.time.LocalDate;

public class Gasto {

    private final int id;
    private final String descripcion;
    private final BigDecimal monto;
    private final String categoria;
    private final LocalDate fecha;

    @JsonCreator
    public Gasto(
            @JsonProperty("id") int id,
            @JsonProperty("descripcion") String descripcion,
            @JsonProperty("monto") BigDecimal monto,
            @JsonProperty("categoria") String categoria,
            @JsonProperty("fecha") LocalDate fecha
    ) {
        this.id = id;
        this.descripcion = descripcion;
        this.monto = monto;
        this.categoria = categoria;
        this.fecha = fecha;
    }

    public int getId() {
        return id;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public BigDecimal getMonto() {
        return monto;
    }

    public String getCategoria() {
        return categoria;
    }

    public LocalDate getFecha() {
        return fecha;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }

        if (!(o instanceof Gasto gasto)) {
            return false;
        }

        return id == gasto.id
                && descripcion.equals(gasto.descripcion)
                && monto.equals(gasto.monto)
                && categoria.equals(gasto.categoria)
                && fecha.equals(gasto.fecha);
    }

    @Override
    public int hashCode() {
        return java.util.Objects.hash(
                id,
                descripcion,
                monto,
                categoria,
                fecha
        );
    }
}