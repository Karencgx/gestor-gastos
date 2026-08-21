package com.gestorgastos.service;

import com.gestorgastos.model.Categoria;
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
                Categoria.ALIMENTACION,
                LocalDate.of(2026, 8, 16)
        );

        // Assert
        assertEquals(1, gasto.getId(), "El ID del gasto debería ser 1");
        assertEquals(1, repository.obtenerTodos().size(), "Debería haber exactamente 1 gasto en el repositorio");
        assertTrue(repository.obtenerTodos().contains(gasto), "El repositorio debería contener el gasto registrado");
    }

    public static class FakeGastoRepository implements GastoRepository {

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

        // Act
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> gestor.registrarGasto(
                        "Almuerzo",
                        new BigDecimal("5000"),
                        Categoria.ALIMENTACION,
                        LocalDate.of(2026, 8, 16)
                )
        );

        // Assert
        assertEquals("El monto debe ser mayor que cero", exception.getMessage());
    }

    @Test
    void noDebePermitirMontoNulo() {

        // Arrange
        FakeGastoRepository repository = new FakeGastoRepository();
        GestorGastos gestor = new GestorGastos(repository);

        // Act
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> gestor.registrarGasto(
                        "Almuerzo",
                        null,
                        Categoria.ALIMENTACION,
                        LocalDate.of(2026, 8, 16)
                )
        );

        // Assert
        assertEquals("El monto debe ser mayor que cero", exception.getMessage());
    }

    @Test
    void noDebePermitirDescripcionVacia() {

        // Arrange
        FakeGastoRepository repository = new FakeGastoRepository();
        GestorGastos gestor = new GestorGastos(repository);

        // Act
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> gestor.registrarGasto(
                        "",
                        new BigDecimal("18000"),
                        Categoria.ALIMENTACION,
                        LocalDate.of(2026, 8, 16)
                )
        );

        // Assert
        assertEquals("La descripción no puede estar vacía", exception.getMessage());
    }

    @Test
    void noDebePermitirDescripcionConSoloEspacios() {

        // Arrange
        FakeGastoRepository repository = new FakeGastoRepository();
        GestorGastos gestor = new GestorGastos(repository);

        // Act
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> gestor.registrarGasto(
                        "    ",
                        new BigDecimal("18000"),
                        Categoria.ALIMENTACION,
                        LocalDate.of(2026, 8, 16)
                )
        );

        // Assert
        assertEquals("La descripción no puede estar vacía", exception.getMessage());
    }

    @Test
    void noDebePermitirDescripcionNula() {

        // Arrange
        FakeGastoRepository repository = new FakeGastoRepository();
        GestorGastos gestor = new GestorGastos(repository);

        // Act
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> gestor.registrarGasto(
                        null,
                        new BigDecimal("18000"),
                        Categoria.ALIMENTACION,
                        LocalDate.of(2026, 8, 16)
                )
        );

        // Assert
        assertEquals("La descripción no puede estar vacía", exception.getMessage());
    }


    @Test
    void noDebePermitirCategoriaNula() {

        // Arrange
        FakeGastoRepository repository = new FakeGastoRepository();
        GestorGastos gestor = new GestorGastos(repository);

        // Act
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> gestor.registrarGasto(
                        "Almuerzo",
                        new BigDecimal("18000"),
                        null,
                        LocalDate.of(2026, 8, 16)
                )
        );

        // Assert
        assertEquals("La categoría no puede estar vacía", exception.getMessage());
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
                Categoria.ALIMENTACION,
                LocalDate.of(2026, 8, 16)
        );

        Gasto segundoGasto = gestor.registrarGasto(
                "Transporte",
                new BigDecimal("5000"),
                Categoria.TRANSPORTE,
                LocalDate.of(2026, 8, 16)
        );

        // Assert
        assertEquals(1, primerGasto.getId(), "El primer gasto debería tener ID 1");
        assertEquals(2, segundoGasto.getId(), "El segundo gasto debería tener ID 2");
    }

    @Test
    void debeGenerarElSiguienteIdDisponible() {

        // Arrange
        FakeGastoRepository repository = new FakeGastoRepository();

        repository.guardar(new Gasto(
                5,
                "Gasto existente",
                new BigDecimal("10000"),
                Categoria.OTROS,
                LocalDate.of(2026, 8, 15)
        ));

        GestorGastos gestor = new GestorGastos(repository);

        // Act
        Gasto nuevoGasto = gestor.registrarGasto(
                "Almuerzo",
                new BigDecimal("18000"),
                Categoria.ALIMENTACION,
                LocalDate.of(2026, 8, 16)
        );

        // Assert
        assertEquals(6, nuevoGasto.getId(), "El siguiente ID disponible debería ser 6");
    }

    @Test
    void noDebePermitirFechaNula() {

        // Arrange
        FakeGastoRepository repository = new FakeGastoRepository();
        GestorGastos gestor = new GestorGastos(repository);

        // Act
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> gestor.registrarGasto(
                        "Almuerzo",
                        new BigDecimal("18000"),
                        Categoria.ALIMENTACION,
                        null
                )
        );

        // Assert
        assertEquals("La fecha no puede ser nula", exception.getMessage());
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
                Categoria.ALIMENTACION,
                fecha
        );

        // Assert
        assertEquals(fecha, gasto.getFecha(), "La fecha del gasto debería coincidir con la fecha proporcionada");
    }

    @Test
    void debeEncontrarUnGastoPorId() {

        // Arrange
        FakeGastoRepository repository = new FakeGastoRepository();
        GestorGastos gestor = new GestorGastos(repository);

        Gasto gasto = gestor.registrarGasto(
                "Almuerzo",
                new BigDecimal("18000"),
                Categoria.ALIMENTACION,
                LocalDate.of(2026, 8, 16)
        );

        // Act
        Optional<Gasto> resultado = gestor.buscarGastoPorId(gasto.getId());

        // Assert
        assertTrue(resultado.isPresent(), "El gasto debería existir en el repositorio");
        assertEquals(gasto, resultado.get(), "El gasto encontrado debería coincidir con el registrado");
    }

    @Test
    void debeRetornarVacioSiElGastoNoExiste() {

        // Arrange
        FakeGastoRepository repository = new FakeGastoRepository();
        GestorGastos gestor = new GestorGastos(repository);

        // Act
        Optional<Gasto> resultado = gestor.buscarGastoPorId(999);

        // Assert
        assertTrue(resultado.isEmpty(), "No debería encontrar un gasto con ID inexistente");
    }

    @Test
    void debeEliminarUnGastoExistente() {

        // Arrange
        FakeGastoRepository repository = new FakeGastoRepository();
        GestorGastos gestor = new GestorGastos(repository);

        Gasto gasto = gestor.registrarGasto(
                "Almuerzo",
                new BigDecimal("18000"),
                Categoria.ALIMENTACION,
                LocalDate.of(2026, 8, 16)
        );

        // Act
        boolean eliminado = gestor.eliminarGasto(gasto.getId());

        // Assert
        assertTrue(eliminado, "El método debería retornar true al eliminar un gasto existente");
        assertTrue(repository.obtenerTodos().isEmpty(), "El repositorio debería quedar vacío después de eliminar");
    }
    @Test
    void debeRetornarFalseSiElGastoNoExisteAlEliminarlo() {

        // Arrange
        FakeGastoRepository repository = new FakeGastoRepository();
        GestorGastos gestor = new GestorGastos(repository);

        // Act
        boolean eliminado = gestor.eliminarGasto(999);

        // Assert
        assertFalse(eliminado, "El método debería retornar false al intentar eliminar un gasto inexistente");
    }

    @Test
    void debeCalcularElTotalDeLosGastos() {

        // Arrange
        FakeGastoRepository repository = new FakeGastoRepository();
        GestorGastos gestor = new GestorGastos(repository);

        gestor.registrarGasto(
                "Almuerzo",
                new BigDecimal("18000"),
                Categoria.ALIMENTACION,
                LocalDate.of(2026, 8, 16)
        );

        gestor.registrarGasto(
                "Transporte",
                new BigDecimal("5000"),
                Categoria.TRANSPORTE,
                LocalDate.of(2026, 8, 16)
        );

        gestor.registrarGasto(
                "Café",
                new BigDecimal("3500"),
                Categoria.ALIMENTACION,
                LocalDate.of(2026, 8, 16)
        );

        // Act
        BigDecimal total = gestor.calcularTotal();

        // Assert
        assertEquals(
                new BigDecimal("26500"),
                total,
                "El total de todos los gastos debería ser 26500"
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
                total,
                "El total debería ser cero cuando no hay gastos"
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
                Categoria.ALIMENTACION,
                LocalDate.of(2026, 8, 16)
        );

        Gasto segundoGasto = gestor.registrarGasto(
                "Transporte",
                new BigDecimal("5000"),
                Categoria.TRANSPORTE,
                LocalDate.of(2026, 8, 16)
        );

        // Act
        List<Gasto> gastos = gestor.listarGastos();

        // Assert
        assertEquals(2, gastos.size(), "Debería haber 2 gastos en la lista");
        assertTrue(gastos.contains(primerGasto), "La lista debería contener el primer gasto");
        assertTrue(gastos.contains(segundoGasto), "La lista debería contener el segundo gasto");
    }

    @Test
    void debeRetornarListaVaciaCuandoNoHayGastos() {

        // Arrange
        FakeGastoRepository repository = new FakeGastoRepository();
        GestorGastos gestor = new GestorGastos(repository);

        // Act
        List<Gasto> gastos = gestor.listarGastos();

        // Assert
        assertTrue(gastos.isEmpty(), "La lista debería estar vacía cuando no hay gastos");
    }

    @Test
    void debeCalcularTotalPorCategoria() {

        // Arrange
        FakeGastoRepository repository = new FakeGastoRepository();
        GestorGastos gestor = new GestorGastos(repository);

        gestor.registrarGasto(
                "Almuerzo",
                new BigDecimal("18000"),
                Categoria.ALIMENTACION,
                LocalDate.of(2026, 8, 16)
        );

        gestor.registrarGasto(
                "Café",
                new BigDecimal("3500"),
                Categoria.ALIMENTACION,
                LocalDate.of(2026, 8, 16)
        );

        gestor.registrarGasto(
                "Transporte",
                new BigDecimal("5000"),
                Categoria.TRANSPORTE,
                LocalDate.of(2026, 8, 16)
        );

        // Act
        BigDecimal total = gestor.calcularTotalPorCategoria(
                Categoria.ALIMENTACION
        );

        // Assert
        assertEquals(
                new BigDecimal("21500"),
                total,
                "El total de gastos en ALIMENTACION debería ser 21500"
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
                Categoria.ALIMENTACION,
                LocalDate.of(2026, 8, 16)
        );

        // Act
        BigDecimal total = gestor.calcularTotalPorCategoria(
                Categoria.EDUCACION
        );

        // Assert
        assertEquals(
                BigDecimal.ZERO,
                total,
                "El total debería ser cero si no hay gastos en la categoría"
        );
    }

    @Test
    void noDebePermitirCategoriaNulaAlCalcularTotal() {

        // Arrange
        FakeGastoRepository repository = new FakeGastoRepository();
        GestorGastos gestor = new GestorGastos(repository);

        // Act
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> gestor.calcularTotalPorCategoria(null)
        );

        // Assert
        assertEquals("La categoría no puede estar vacía", exception.getMessage());
    }


}