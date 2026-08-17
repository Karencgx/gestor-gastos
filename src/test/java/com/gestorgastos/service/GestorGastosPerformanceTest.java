package com.gestorgastos.service;

import com.gestorgastos.model.Categoria;
import com.gestorgastos.model.Gasto;
import org.junit.jupiter.api.Test;
import com.gestorgastos.service.GestorGastosTest.FakeGastoRepository;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

class GestorGastosPerformanceTest {

    private GestorGastosTest.FakeGastoRepository crearRepositoryConGastos(int cantidad) {

        GestorGastosTest.FakeGastoRepository repository = new GestorGastosTest.FakeGastoRepository();

        for (int i = 1; i <= cantidad; i++) {
            repository.guardar(
                    new Gasto(
                            i,
                            "Gasto " + i,
                            new BigDecimal("10000"),
                            Categoria.ALIMENTACION,
                            LocalDate.of(2026, 8, 16)
                    )
            );
        }

        return repository;
    }

    private double medirTiempoCalcularTotalPorCategoria(
            GestorGastos gestor) {

        int ejecuciones = 100;

        long inicio = System.nanoTime();

        for (int i = 0; i < ejecuciones; i++) {
            gestor.calcularTotalPorCategoria(Categoria.ALIMENTACION);
        }

        long fin = System.nanoTime();

        return (double) (fin - inicio)
                / ejecuciones
                / 1_000_000;
    }

    private double medirTiempoCalcularTotal(GestorGastos gestor) {

        int ejecuciones = 100;

        long inicio = System.nanoTime();

        for (int i = 0; i < ejecuciones; i++) {
            gestor.calcularTotal();
        }

        long fin = System.nanoTime();

        return (double) (fin - inicio)
                / ejecuciones
                / 1_000_000;
    }

    private double medirTiempoRegistrarGasto(GestorGastos gestor) {

        long inicio = System.nanoTime();

        for (int i = 0; i < 100; i++) {
            gestor.registrarGasto(
                    "Gasto de prueba " + i,
                    new BigDecimal("18000"),
                    Categoria.ALIMENTACION,
                    LocalDate.of(2026, 8, 16)
            );
        }

        long fin = System.nanoTime();

        long tiempoTotal = fin - inicio;

        return (double) tiempoTotal / 100 / 1_000_000;
    }

    @Test
    void medirRendimientoRegistrarGasto() {

        // Arrange
        GestorGastosTest.FakeGastoRepository repository1000 =
                crearRepositoryConGastos(1000);

        GestorGastosTest.FakeGastoRepository repository10000 =
                crearRepositoryConGastos(10000);

        GestorGastosTest.FakeGastoRepository repository100000 =
                crearRepositoryConGastos(100000);

        GestorGastos gestor1000 = new GestorGastos(repository1000);
        GestorGastos gestor10000 = new GestorGastos(repository10000);
        GestorGastos gestor100000 = new GestorGastos(repository100000);

        // Act
        double tiempo1000 = medirTiempoRegistrarGasto(gestor1000);
        double tiempo10000 = medirTiempoRegistrarGasto(gestor10000);
        double tiempo100000 = medirTiempoRegistrarGasto(gestor100000);

        // Assert
        assertTrue(tiempo1000 > 0);
        assertTrue(tiempo10000 > 0);
        assertTrue(tiempo100000 > 0);

        System.out.println("Registrar gasto - 1.000 registros: " + tiempo1000 + " ms");
        System.out.println("Registrar gasto - 10.000 registros: " + tiempo10000 + " ms");
        System.out.println("Registrar gasto - 100.000 registros: " + tiempo100000 + " ms");
    }

    @Test
    void medirRendimientoCalcularTotal() {

        // Arrange
        GestorGastosTest.FakeGastoRepository repository1000 = crearRepositoryConGastos(1000);
        GestorGastosTest.FakeGastoRepository repository10000 = crearRepositoryConGastos(10000);
        GestorGastosTest.FakeGastoRepository repository100000 = crearRepositoryConGastos(100000);

        GestorGastos gestor1000 = new GestorGastos(repository1000);
        GestorGastos gestor10000 = new GestorGastos(repository10000);
        GestorGastos gestor100000 = new GestorGastos(repository100000);

        // Act
        double tiempo1000 = medirTiempoCalcularTotal(gestor1000);
        double tiempo10000 = medirTiempoCalcularTotal(gestor10000);
        double tiempo100000 = medirTiempoCalcularTotal(gestor100000);

        // Assert
        assertTrue(tiempo1000 > 0);
        assertTrue(tiempo10000 > 0);
        assertTrue(tiempo100000 > 0);

        System.out.println("Calcular total - 1.000 registros: " + tiempo1000 + " ms");
        System.out.println("Calcular total - 10.000 registros: " + tiempo10000 + " ms");
        System.out.println("Calcular total - 100.000 registros: " + tiempo100000 + " ms");
    }

    @Test
    void medirRendimientoCalcularTotalPorCategoria() {

        // Arrange
        GestorGastosTest.FakeGastoRepository repository1000 = crearRepositoryConGastos(1000);
        GestorGastosTest.FakeGastoRepository repository10000 = crearRepositoryConGastos(10000);
        GestorGastosTest.FakeGastoRepository repository100000 = crearRepositoryConGastos(100000);

        GestorGastos gestor1000 = new GestorGastos(repository1000);
        GestorGastos gestor10000 = new GestorGastos(repository10000);
        GestorGastos gestor100000 = new GestorGastos(repository100000);

        // Act
        double tiempo1000 =
                medirTiempoCalcularTotalPorCategoria(gestor1000);

        double tiempo10000 =
                medirTiempoCalcularTotalPorCategoria(gestor10000);

        double tiempo100000 =
                medirTiempoCalcularTotalPorCategoria(gestor100000);

        // Assert
        assertTrue(tiempo1000 > 0);
        assertTrue(tiempo10000 > 0);
        assertTrue(tiempo100000 > 0);

        System.out.println(
                "Total por categoría - 1.000 registros: "
                        + tiempo1000 + " ms"
        );

        System.out.println(
                "Total por categoría - 10.000 registros: "
                        + tiempo10000 + " ms"
        );

        System.out.println(
                "Total por categoría - 100.000 registros: "
                        + tiempo100000 + " ms"
        );
    }

}