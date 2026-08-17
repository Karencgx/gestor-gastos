package com.gestorgastos.service;

import com.gestorgastos.model.Gasto;
import com.gestorgastos.repository.GastoRepository;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class GestorGastosTest {

    @Test
    void debeRegistrarUnGasto() {

        // Arrange
        FakeGastoRepository repository = new FakeGastoRepository();
        GestorGastos gestor = new GestorGastos(repository);
        //Act
        Gasto gasto = gestor.registrarGasto(
                "Almuerzo",
                new BigDecimal("18000"),
                "Alimentación",
                LocalDate.of(2026, 8, 16)
        );

        // Assert
        assertEquals(1, gasto.getId());
        assertEquals(1, repository.obtenerTodos().size());
        assertTrue(repository.obtenerTodos().contains(gasto));
    }

    private static class FakeGastoRepository implements GastoRepository {

        private final List<Gasto> gastos = new ArrayList<>();

        @Override
        public void guardar(Gasto gasto) {
            gastos.add(gasto);
        }

        @Override
        public List<Gasto> obtenerTodos() {
            return gastos;
        }

        @Override
        public Optional<Gasto> buscarPorId(int id) {
            return gastos.stream()
                    .filter(gasto -> gasto.getId() == id)
                    .findFirst();
        }

        @Override
        public void eliminar(int id) {
            gastos.removeIf(gasto -> gasto.getId() == id);
        }
    }

    @Test
    void noDebePermitirUnMontoMenorOIgualACero() {

        // Arrange
        FakeGastoRepository repository = new FakeGastoRepository();
        GestorGastos gestor = new GestorGastos(repository);

        // Act + Assert
        assertThrows(
                IllegalArgumentException.class,
                () -> gestor.registrarGasto(
                        "Almuerzo",
                        new BigDecimal("-5000"),
                        "Alimentación",
                        LocalDate.of(2026, 8, 16)
                )
        );
    }

    @Test
    void noDebePermitirMontoNulo() {

        // Arrange
        FakeGastoRepository repository = new FakeGastoRepository();
        GestorGastos gestor = new GestorGastos(repository);

        // Act + Assert
        assertThrows(
                IllegalArgumentException.class,
                () -> gestor.registrarGasto(
                        "Almuerzo",
                        null,
                        "Alimentación",
                        LocalDate.of(2026, 8, 16)
                )
        );
    }

    @Test
    void noDebePermitirDescripcionVacia() {

        // Arrange
        FakeGastoRepository repository = new FakeGastoRepository();
        GestorGastos gestor = new GestorGastos(repository);

        // Act + Assert
        assertThrows(
                IllegalArgumentException.class,
                () -> gestor.registrarGasto(
                        "",
                        new BigDecimal("18000"),
                        "Alimentación",
                        LocalDate.of(2026, 8, 16)
                )
        );
    }

    @Test
    void noDebePermitirDescripcionConSoloEspacios() {

        // Arrange
        FakeGastoRepository repository = new FakeGastoRepository();
        GestorGastos gestor = new GestorGastos(repository);

        // Act + Assert
        assertThrows(
                IllegalArgumentException.class,
                () -> gestor.registrarGasto(
                        "    ",
                        new BigDecimal("18000"),
                        "Alimentación",
                        LocalDate.of(2026, 8, 16)
                )
        );
    }

    @Test
    void noDebePermitirDescripcionNula() {

        // Arrange
        FakeGastoRepository repository = new FakeGastoRepository();
        GestorGastos gestor = new GestorGastos(repository);

        // Act + Assert
        assertThrows(
                IllegalArgumentException.class,
                () -> gestor.registrarGasto(
                        null,
                        new BigDecimal("18000"),
                        "Alimentación",
                        LocalDate.of(2026, 8, 16)
                )
        );
    }

    @Test
    void noDebePermitirCategoriaVacia() {

        // Arrange
        FakeGastoRepository repository = new FakeGastoRepository();
        GestorGastos gestor = new GestorGastos(repository);

        // Act + Assert
        assertThrows(
                IllegalArgumentException.class,
                () -> gestor.registrarGasto(
                        "Almuerzo",
                        new BigDecimal("18000"),
                        "",
                        LocalDate.of(2026, 8, 16)
                )
        );
    }

    @Test
    void noDebePermitirCategoriaConSoloEspacios() {

        // Arrange
        FakeGastoRepository repository = new FakeGastoRepository();
        GestorGastos gestor = new GestorGastos(repository);

        // Act + Assert
        assertThrows(
                IllegalArgumentException.class,
                () -> gestor.registrarGasto(
                        "Almuerzo",
                        new BigDecimal("18000"),
                        "    ",
                        LocalDate.of(2026, 8, 16)
                )
        );
    }

    @Test
    void noDebePermitirCategoriaNula() {

        // Arrange
        FakeGastoRepository repository = new FakeGastoRepository();
        GestorGastos gestor = new GestorGastos(repository);

        // Act + Assert
        assertThrows(
                IllegalArgumentException.class,
                () -> gestor.registrarGasto(
                        "Almuerzo",
                        new BigDecimal("18000"),
                        null,
                        LocalDate.of(2026, 8, 16)
                )
        );
    }

    @Test
    void debeGenerarIdsConsecutivos() {

        // Arrange
        FakeGastoRepository repository = new FakeGastoRepository();
        GestorGastos gestor = new GestorGastos(repository);

        // Act
        Gasto primerGasto = gestor.registrarGasto(
                "Almuerzo",
                new BigDecimal("18000"),
                "Alimentación",
                LocalDate.of(2026, 8, 16)
        );

        Gasto segundoGasto = gestor.registrarGasto(
                "Transporte",
                new BigDecimal("5000"),
                "Transporte",
                LocalDate.of(2026, 8, 16)
        );

        // Assert
        assertEquals(1, primerGasto.getId());
        assertEquals(2, segundoGasto.getId());
    }

    @Test
    void debeGenerarElSiguienteIdDisponible() {

        // Arrange
        FakeGastoRepository repository = new FakeGastoRepository();

        repository.guardar(new Gasto(
                5,
                "Gasto existente",
                new BigDecimal("10000"),
                "Otros",
                LocalDate.of(2026, 8, 15)
        ));

        GestorGastos gestor = new GestorGastos(repository);

        // Act
        Gasto nuevoGasto = gestor.registrarGasto(
                "Almuerzo",
                new BigDecimal("18000"),
                "Alimentación",
                LocalDate.of(2026, 8, 16)
        );

        // Assert
        assertEquals(6, nuevoGasto.getId());
    }

    @Test
    void noDebePermitirFechaNula() {

        // Arrange
        FakeGastoRepository repository = new FakeGastoRepository();
        GestorGastos gestor = new GestorGastos(repository);

        // Act + Assert
        assertThrows(
                IllegalArgumentException.class,
                () -> gestor.registrarGasto(
                        "Almuerzo",
                        new BigDecimal("18000"),
                        "Alimentación",
                        null
                )
        );
    }

    @Test
    void debePermitirUnaFechaValida() {

        // Arrange
        FakeGastoRepository repository = new FakeGastoRepository();
        GestorGastos gestor = new GestorGastos(repository);

        LocalDate fecha = LocalDate.of(2026, 8, 16);

        // Act
        Gasto gasto = gestor.registrarGasto(
                "Almuerzo",
                new BigDecimal("18000"),
                "Alimentación",
                fecha
        );

        // Assert
        assertEquals(fecha, gasto.getFecha());
    }

    @Test
    void debeEncontrarUnGastoPorId() {

        // Arrange
        FakeGastoRepository repository = new FakeGastoRepository();
        GestorGastos gestor = new GestorGastos(repository);

        Gasto gasto = gestor.registrarGasto(
                "Almuerzo",
                new BigDecimal("18000"),
                "Alimentación",
                LocalDate.of(2026, 8, 16)
        );

        // Act
        Optional<Gasto> resultado = gestor.buscarGastoPorId(gasto.getId());

        // Assert
        assertTrue(resultado.isPresent());
        assertEquals(gasto, resultado.get());
    }

    @Test
    void debeRetornarVacioSiElGastoNoExiste() {

        // Arrange
        FakeGastoRepository repository = new FakeGastoRepository();
        GestorGastos gestor = new GestorGastos(repository);

        // Act
        Optional<Gasto> resultado = gestor.buscarGastoPorId(999);

        // Assert
        assertTrue(resultado.isEmpty());
    }

    @Test
    void debeEliminarUnGastoExistente() {

        // Arrange
        FakeGastoRepository repository = new FakeGastoRepository();
        GestorGastos gestor = new GestorGastos(repository);

        Gasto gasto = gestor.registrarGasto(
                "Almuerzo",
                new BigDecimal("18000"),
                "Alimentación",
                LocalDate.of(2026, 8, 16)
        );

        // Act
        boolean eliminado = gestor.eliminarGasto(gasto.getId());

        // Assert
        assertTrue(eliminado);
        assertTrue(repository.obtenerTodos().isEmpty());
    }
    @Test
    void debeRetornarFalseSiElGastoNoExisteAlEliminarlo() {

        // Arrange
        FakeGastoRepository repository = new FakeGastoRepository();
        GestorGastos gestor = new GestorGastos(repository);

        // Act
        boolean eliminado = gestor.eliminarGasto(999);

        // Assert
        assertFalse(eliminado);
    }

    @Test
    void debeCalcularElTotalDeLosGastos() {

        // Arrange
        FakeGastoRepository repository = new FakeGastoRepository();
        GestorGastos gestor = new GestorGastos(repository);

        gestor.registrarGasto(
                "Almuerzo",
                new BigDecimal("18000"),
                "Alimentación",
                LocalDate.of(2026, 8, 16)
        );

        gestor.registrarGasto(
                "Transporte",
                new BigDecimal("5000"),
                "Transporte",
                LocalDate.of(2026, 8, 16)
        );

        gestor.registrarGasto(
                "Café",
                new BigDecimal("3500"),
                "Alimentación",
                LocalDate.of(2026, 8, 16)
        );

        // Act
        BigDecimal total = gestor.calcularTotal();

        // Assert
        assertEquals(
                new BigDecimal("26500"),
                total
        );
    }

    @Test
    void debeRetornarCeroCuandoNoHayGastos() {

        // Arrange
        FakeGastoRepository repository = new FakeGastoRepository();
        GestorGastos gestor = new GestorGastos(repository);

        // Act
        BigDecimal total = gestor.calcularTotal();

        // Assert
        assertEquals(
                BigDecimal.ZERO,
                total
        );
    }

    @Test
    void debeListarTodosLosGastos() {

        // Arrange
        FakeGastoRepository repository = new FakeGastoRepository();
        GestorGastos gestor = new GestorGastos(repository);

        Gasto primerGasto = gestor.registrarGasto(
                "Almuerzo",
                new BigDecimal("18000"),
                "Alimentación",
                LocalDate.of(2026, 8, 16)
        );

        Gasto segundoGasto = gestor.registrarGasto(
                "Transporte",
                new BigDecimal("5000"),
                "Transporte",
                LocalDate.of(2026, 8, 16)
        );

        // Act
        List<Gasto> gastos = gestor.listarGastos();

        // Assert
        assertEquals(2, gastos.size());
        assertTrue(gastos.contains(primerGasto));
        assertTrue(gastos.contains(segundoGasto));
    }

    @Test
    void debeRetornarListaVaciaCuandoNoHayGastos() {

        // Arrange
        FakeGastoRepository repository = new FakeGastoRepository();
        GestorGastos gestor = new GestorGastos(repository);

        // Act
        List<Gasto> gastos = gestor.listarGastos();

        // Assert
        assertTrue(gastos.isEmpty());
    }

    @Test
    void debeCalcularTotalPorCategoria() {

        // Arrange
        FakeGastoRepository repository = new FakeGastoRepository();
        GestorGastos gestor = new GestorGastos(repository);

        gestor.registrarGasto(
                "Almuerzo",
                new BigDecimal("18000"),
                "Alimentación",
                LocalDate.of(2026, 8, 16)
        );

        gestor.registrarGasto(
                "Café",
                new BigDecimal("3500"),
                "Alimentación",
                LocalDate.of(2026, 8, 16)
        );

        gestor.registrarGasto(
                "Transporte",
                new BigDecimal("5000"),
                "Transporte",
                LocalDate.of(2026, 8, 16)
        );

        // Act
        BigDecimal total = gestor.calcularTotalPorCategoria(
                "Alimentación"
        );

        // Assert
        assertEquals(
                new BigDecimal("21500"),
                total
        );
    }

    @Test
    void debeRetornarCeroSiNoHayGastosEnLaCategoria() {

        // Arrange
        FakeGastoRepository repository = new FakeGastoRepository();
        GestorGastos gestor = new GestorGastos(repository);

        gestor.registrarGasto(
                "Almuerzo",
                new BigDecimal("18000"),
                "Alimentación",
                LocalDate.of(2026, 8, 16)
        );

        // Act
        BigDecimal total = gestor.calcularTotalPorCategoria(
                "Educación"
        );

        // Assert
        assertEquals(
                BigDecimal.ZERO,
                total
        );
    }

    @Test
    void noDebePermitirCategoriaNulaAlCalcularTotal() {

        // Arrange
        FakeGastoRepository repository = new FakeGastoRepository();
        GestorGastos gestor = new GestorGastos(repository);

        // Act + Assert
        assertThrows(
                IllegalArgumentException.class,
                () -> gestor.calcularTotalPorCategoria(null)
        );
    }

    @Test
    void noDebePermitirCategoriaVaciaAlCalcularTotal() {

        // Arrange
        FakeGastoRepository repository = new FakeGastoRepository();
        GestorGastos gestor = new GestorGastos(repository);

        // Act + Assert
        assertThrows(
                IllegalArgumentException.class,
                () -> gestor.calcularTotalPorCategoria("")
        );
    }

    @Test
    void medirRendimientoRegistrarGasto() {

        FakeGastoRepository repository = new FakeGastoRepository();
        GestorGastos gestor = new GestorGastos(repository);

        // Cargar datos de prueba
        for (int i = 1; i <= 10000; i++) {
            repository.guardar(
                    new Gasto(
                            i,
                            "Gasto " + i,
                            new BigDecimal("10000"),
                            "Alimentación",
                            LocalDate.of(2026, 8, 16)
                    )
            );
        }

        // Medición
        long inicio = System.nanoTime();

        for (int i = 0; i < 100; i++) {
            gestor.registrarGasto(
                    "Gasto de prueba " + i,
                    new BigDecimal("18000"),
                    "Alimentación",
                    LocalDate.of(2026, 8, 16)
            );
        }

        long fin = System.nanoTime();

        long tiempoTotal = fin - inicio;
        double tiempoPromedio =
                (double) tiempoTotal / 100;

        System.out.println(
                "Tiempo promedio: "
                        + tiempoPromedio / 1_000_000
                        + " ms"
        );
    }

    @Test
    void medirRendimientoCalcularTotal() {

        // Arrange
        FakeGastoRepository repository1000 = crearRepositoryConGastos(1000);
        FakeGastoRepository repository10000 = crearRepositoryConGastos(10000);
        FakeGastoRepository repository100000 = crearRepositoryConGastos(100000);

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

        System.out.println("1.000 registros: " + tiempo1000 + " ms");
        System.out.println("10.000 registros: " + tiempo10000 + " ms");
        System.out.println("100.000 registros: " + tiempo100000 + " ms");
    }

    private FakeGastoRepository crearRepositoryConGastos(int cantidad) {

        FakeGastoRepository repository = new FakeGastoRepository();

        for (int i = 1; i <= cantidad; i++) {
            repository.guardar(
                    new Gasto(
                            i,
                            "Gasto " + i,
                            new BigDecimal("10000"),
                            "Alimentación",
                            LocalDate.of(2026, 8, 16)
                    )
            );
        }

        return repository;
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

    @Test
    void medirRendimientoCalcularTotalPorCategoria() {

        // Arrange
        FakeGastoRepository repository1000 = crearRepositoryConGastos(1000);
        FakeGastoRepository repository10000 = crearRepositoryConGastos(10000);
        FakeGastoRepository repository100000 = crearRepositoryConGastos(100000);

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
                "1.000 registros: "
                        + tiempo1000 + " ms"
        );

        System.out.println(
                "10.000 registros: "
                        + tiempo10000 + " ms"
        );

        System.out.println(
                "100.000 registros: "
                        + tiempo100000 + " ms"
        );
    }

    private double medirTiempoCalcularTotalPorCategoria(
            GestorGastos gestor) {

        int ejecuciones = 100;

        long inicio = System.nanoTime();

        for (int i = 0; i < ejecuciones; i++) {
            gestor.calcularTotalPorCategoria("Alimentación");
        }

        long fin = System.nanoTime();

        return (double) (fin - inicio)
                / ejecuciones
                / 1_000_000;
    }


}