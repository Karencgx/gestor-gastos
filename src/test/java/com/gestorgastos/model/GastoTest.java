package com.gestorgastos.model;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;
import com.gestorgastos.model.Categoria;

import static org.junit.jupiter.api.Assertions.assertEquals;

class GastoTest {

    @Test
    void debeCrearUnGastoCorrectamente() {

        // Arrange
        int id = 1;
        String descripcion = "Almuerzo";
        BigDecimal monto = new BigDecimal("18000");
        Categoria categoria = Categoria.ALIMENTACION;
        LocalDate fecha = LocalDate.of(2026, 8, 16);

        // Act
        Gasto gasto = new Gasto(
                id,
                descripcion,
                monto,
                categoria,
                fecha
        );

        // Assert
        assertEquals(id, gasto.getId());
        assertEquals(descripcion, gasto.getDescripcion());
        assertEquals(monto, gasto.getMonto());
        assertEquals(categoria, gasto.getCategoria());
        assertEquals(fecha, gasto.getFecha());
    }
}