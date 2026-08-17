package com.gestorgastos.service;

import com.gestorgastos.model.Gasto;
import com.gestorgastos.repository.GastoRepository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Optional;

public class GestorGastos {

    private final GastoRepository repository;

    public GestorGastos(GastoRepository repository) {
        this.repository = repository;
    }

    public Gasto registrarGasto(
            String descripcion,
            BigDecimal monto,
            String categoria,
            LocalDate fecha
    ) {

        if (monto == null || monto.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException(
                    "El monto debe ser mayor que cero"
            );
        }

        if (descripcion == null || descripcion.isBlank()) {
            throw new IllegalArgumentException(
                    "La descripción no puede estar vacía"
            );
        }

        if (categoria == null || categoria.isBlank()) {
            throw new IllegalArgumentException(
                    "La categoría no puede estar vacía"
            );
        }

        if (fecha == null) {
            throw new IllegalArgumentException(
                    "La fecha no puede ser nula"
            );
        }

        int siguienteId = repository.obtenerTodos()
                .stream()
                .mapToInt(Gasto::getId)
                .max()
                .orElse(0) + 1;

        Gasto gasto = new Gasto(
                siguienteId,
                descripcion,
                monto,
                categoria,
                fecha
        );

        repository.guardar(gasto);

        return gasto;
    }

    public Optional<Gasto> buscarGastoPorId(int id) {
        return repository.buscarPorId(id);
    }

    public boolean eliminarGasto(int id) {

        Optional<Gasto> gasto = repository.buscarPorId(id);

        if (gasto.isEmpty()) {
            return false;
        }

        repository.eliminar(id);
        return true;
    }

    public BigDecimal calcularTotal() {

        return repository.obtenerTodos()
                .stream()
                .map(Gasto::getMonto)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

}