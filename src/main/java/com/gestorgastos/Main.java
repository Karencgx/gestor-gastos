package com.gestorgastos;

import com.gestorgastos.model.Categoria;
import com.gestorgastos.model.Gasto;
import com.gestorgastos.repository.GastoRepository;
import com.gestorgastos.repository.JsonGastoRepository;
import com.gestorgastos.service.GestorGastos;

import java.math.BigDecimal;
import java.nio.file.Path;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Optional;
import java.util.Scanner;

public class Main {

    private static final Scanner scanner = new Scanner(System.in);

    public static void main(String[] args) {

        GastoRepository repository =
                new JsonGastoRepository(
                        Path.of("data/gastos.json")
                );

        GestorGastos gestor = new GestorGastos(repository);

        int opcion;

        do {
            mostrarMenu();
            opcion = leerEntero("Seleccione una opción: ");

            try {

                switch (opcion) {

                    case 1:
                        registrarGasto(gestor);
                        break;

                    case 2:
                        listarGastos(gestor);
                        break;

                    case 3:
                        buscarGastoPorId(gestor);
                        break;

                    case 4:
                        eliminarGasto(gestor);
                        break;

                    case 5:
                        calcularTotal(gestor);
                        break;

                    case 6:
                        calcularTotalPorCategoria(gestor);
                        break;

                    case 0:
                        System.out.println("\n¡Hasta luego!");
                        break;

                    default:
                        System.out.println("\nOpción no válida.");
                }

            } catch (IllegalArgumentException e) {

                System.out.println("\nError: " + e.getMessage());

            } catch (Exception e) {

                System.out.println(
                        "\nOcurrió un error: " + e.getMessage()
                );
            }

        } while (opcion != 0);

        scanner.close();
    }

    private static void mostrarMenu() {

        System.out.println("\n==============================");
        System.out.println("       GESTOR DE GASTOS");
        System.out.println("==============================");
        System.out.println("1. Registrar gasto");
        System.out.println("2. Listar gastos");
        System.out.println("3. Buscar gasto por ID");
        System.out.println("4. Eliminar gasto");
        System.out.println("5. Calcular total");
        System.out.println("6. Calcular total por categoría");
        System.out.println("0. Salir");
        System.out.println("==============================");
    }

    private static void registrarGasto(GestorGastos gestor) {

        System.out.println("\n--- Registrar gasto ---");

        System.out.print("Descripción: ");
        String descripcion = scanner.nextLine();

        System.out.print("Monto: ");
        String valor = scanner.nextLine();

        BigDecimal monto = new BigDecimal(valor);

        System.out.print("Categoría: ");
        Categoria categoria = seleccionarCategoria(scanner);

        LocalDate fecha = leerFecha();

        Gasto gasto = gestor.registrarGasto(
                descripcion,
                monto,
                categoria,
                fecha
        );

        System.out.println("\nGasto registrado correctamente:");
        System.out.println(gasto);
    }

    private static Categoria seleccionarCategoria(Scanner scanner) {

        System.out.println("\n--- Categorías ---");

        Categoria[] categorias = Categoria.values();

        for (int i = 0; i < categorias.length; i++) {
            System.out.println(
                    (i + 1) + ". " + categorias[i]
            );
        }

        System.out.print("Seleccione una categoría: ");

        int opcion = Integer.parseInt(scanner.nextLine());

        if (opcion < 1 || opcion > categorias.length) {
            throw new IllegalArgumentException(
                    "Categoría inválida"
            );
        }

        return categorias[opcion - 1];
    }

    private static void listarGastos(GestorGastos gestor) {

        System.out.println("\n--- Lista de gastos ---");

        List<Gasto> gastos = gestor.listarGastos();

        if (gastos.isEmpty()) {
            System.out.println("No hay gastos registrados.");
            return;
        }

        for (Gasto gasto : gastos) {

            System.out.println("------------------------------");
            System.out.println("ID: " + gasto.getId());
            System.out.println(
                    "Descripción: " + gasto.getDescripcion()
            );
            System.out.println(
                    "Monto: $" + gasto.getMonto()
            );
            System.out.println(
                    "Categoría: " + gasto.getCategoria()
            );
            System.out.println(
                    "Fecha: " + gasto.getFecha()
            );
        }

        System.out.println("------------------------------");
    }

    private static void buscarGastoPorId(GestorGastos gestor) {

        System.out.println("\n--- Buscar gasto ---");

        int id = leerEntero("Ingrese el ID: ");

        Optional<Gasto> resultado =
                gestor.buscarGastoPorId(id);

        if (resultado.isPresent()) {

            Gasto gasto = resultado.get();

            System.out.println("\nGasto encontrado:");
            System.out.println("------------------------------");
            System.out.println("ID: " + gasto.getId());
            System.out.println(
                    "Descripción: " + gasto.getDescripcion()
            );
            System.out.println(
                    "Monto: $" + gasto.getMonto()
            );
            System.out.println(
                    "Categoría: " + gasto.getCategoria()
            );
            System.out.println(
                    "Fecha: " + gasto.getFecha()
            );
            System.out.println("------------------------------");

        } else {

            System.out.println(
                    "\nNo se encontró un gasto con el ID " + id + "."
            );
        }
    }

    private static void eliminarGasto(GestorGastos gestor) {

        System.out.println("\n--- Eliminar gasto ---");

        int id = leerEntero(
                "Ingrese el ID del gasto: "
        );

        boolean eliminado =
                gestor.eliminarGasto(id);

        if (eliminado) {

            System.out.println(
                    "Gasto eliminado correctamente."
            );

        } else {

            System.out.println(
                    "No existe un gasto con el ID " + id + "."
            );
        }
    }

    private static void calcularTotal(GestorGastos gestor) {

        System.out.println("\n--- Total de gastos ---");

        BigDecimal total = gestor.calcularTotal();

        System.out.println(
                "Total gastado: $" + total
        );
    }

    private static void calcularTotalPorCategoria(
            GestorGastos gestor) {

        System.out.println(
                "\n--- Total por categoría ---"
        );

        System.out.print(
                "Ingrese la categoría: "
        );

        Categoria categoria = seleccionarCategoria(scanner);

        BigDecimal total =
                gestor.calcularTotalPorCategoria(
                        categoria
                );

        System.out.println(
                "Total en " + categoria + ": $" + total
        );
    }

    private static LocalDate leerFecha() {

        while (true) {

            System.out.print(
                    "Fecha (YYYY-MM-DD): "
            );

            String texto = scanner.nextLine();

            try {

                return LocalDate.parse(texto);

            } catch (DateTimeParseException e) {

                System.out.println(
                        "Formato inválido. " +
                                "Ejemplo: 2026-08-17"
                );
            }
        }
    }

    private static int leerEntero(String mensaje) {

        while (true) {

            System.out.print(mensaje);

            String valor = scanner.nextLine();

            try {

                return Integer.parseInt(valor);

            } catch (NumberFormatException e) {

                System.out.println(
                        "Ingrese un número válido."
                );
            }
        }
    }
}