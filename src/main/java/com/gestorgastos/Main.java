package com.gestorgastos;

import com.gestorgastos.model.Gasto;
import com.gestorgastos.repository.GastoRepository;
import com.gestorgastos.repository.JsonGastoRepository;

import java.math.BigDecimal;
import java.nio.file.Path;
import java.time.LocalDate;

public class Main {

    public static void main(String[] args) {

        GastoRepository repository =
                new JsonGastoRepository(
                        Path.of("data/gastos.json")
                );

        Gasto gasto = new Gasto(
                1,
                "Almuerzo",
                BigDecimal.valueOf(18000),
                "Alimentación",
                LocalDate.of(2026, 8, 16)
        );

        repository.guardar(gasto);

        System.out.println(repository.obtenerTodos());
    }
}