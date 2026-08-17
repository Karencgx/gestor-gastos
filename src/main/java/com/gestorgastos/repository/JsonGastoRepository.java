package com.gestorgastos.repository;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.gestorgastos.model.Gasto;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class JsonGastoRepository implements GastoRepository {

    private final Path archivo;
    private final ObjectMapper objectMapper;

    public JsonGastoRepository(Path archivo) {
        this.archivo = archivo;

        this.objectMapper = new ObjectMapper();
        this.objectMapper.registerModule(new JavaTimeModule());
    }

    @Override
    public void guardar(Gasto gasto) {
        List<Gasto> gastos = obtenerTodos();

        gastos.add(gasto);

        guardarTodos(gastos);
    }

    @Override
    public List<Gasto> obtenerTodos() {
        try {
            if (!Files.exists(archivo)) {
                return new ArrayList<>();
            }

            return objectMapper.readValue(
                    archivo.toFile(),
                    new TypeReference<List<Gasto>>() {}
            );

        } catch (IOException e) {
            throw new RuntimeException("No se pudieron leer los gastos", e);
        }
    }

    @Override
    public Optional<Gasto> buscarPorId(int id) {

        return obtenerTodos()
                .stream()
                .filter(gasto -> gasto.getId() == id)
                .findFirst();
    }

    @Override
    public void eliminar(int id) {

        List<Gasto> gastos = obtenerTodos();

        gastos.removeIf(gasto -> gasto.getId() == id);

        guardarTodos(gastos);
    }

    private void guardarTodos(List<Gasto> gastos) {
        try {
            Path directorio = archivo.getParent();

            if (directorio != null) {
                Files.createDirectories(directorio);
            }

            objectMapper
                    .writerWithDefaultPrettyPrinter()
                    .writeValue(archivo.toFile(), gastos);

        } catch (IOException e) {
            throw new RuntimeException("No se pudieron guardar los gastos", e);
        }
    }
}