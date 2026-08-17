package com.gestorgastos.service;

import com.gestorgastos.model.Gasto;
import com.gestorgastos.repository.GastoRepository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import com.gestorgastos.model.Categoria;

public class GestorGastos {

    private final GastoRepository repository;

    public GestorGastos(GastoRepository repository) {
        this.repository = repository;
    }

    public Gasto registrarGasto(
            String descripcion,
            BigDecimal monto,
            Categoria categoria,
            LocalDate fecha
    ) {
        validarMonto(monto);
        validarDescripcion(descripcion);
        validarCategoria(categoria);
        validarFecha(fecha);

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

    private void validarMonto(BigDecimal monto) {
        if (monto == null || monto.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException(
                    "El monto debe ser mayor que cero"
            );
        }
    }

    private void validarDescripcion(String descripcion) {
        if (descripcion == null || descripcion.isBlank()) {
            throw new IllegalArgumentException(
                    "La descripción no puede estar vacía"
            );
        }
    }

    private void validarCategoria(Categoria categoria) {
        if (categoria == null ) {
            throw new IllegalArgumentException(
                    "La categoría no puede estar vacía"
            );
        }
    }

    private void validarFecha(LocalDate fecha) {
        if (fecha == null) {
            throw new IllegalArgumentException(
                    "La fecha no puede ser nula"
            );
        }
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

    public List<Gasto> listarGastos() {
        return repository.obtenerTodos();
    }

    public BigDecimal calcularTotalPorCategoria(Categoria categoria) {

        if (categoria == null ) {
            throw new IllegalArgumentException(
                    "La categoría no puede estar vacía"
            );
        }

        return repository.obtenerTodos()
                .stream()
                .filter(gasto ->
                        gasto.getCategoria()  == categoria
                )
                .map(Gasto::getMonto)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

}