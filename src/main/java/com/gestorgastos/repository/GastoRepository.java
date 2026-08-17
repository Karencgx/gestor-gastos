package com.gestorgastos.repository;

import com.gestorgastos.model.Gasto;

import java.util.List;
import java.util.Optional;

public interface GastoRepository {

    void guardar(Gasto gasto);

    List<Gasto> obtenerTodos();

    Optional<Gasto> buscarPorId(int id);

    void eliminar(int id);
}