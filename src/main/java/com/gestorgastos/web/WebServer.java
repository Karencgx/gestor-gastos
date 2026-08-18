package com.gestorgastos.web;

import com.gestorgastos.model.Categoria;
import com.gestorgastos.model.Gasto;
import com.gestorgastos.repository.GastoRepository;
import com.gestorgastos.repository.JsonGastoRepository;
import com.gestorgastos.service.GestorGastos;
import io.javalin.Javalin;
import io.javalin.http.Context;
import io.javalin.json.JavalinJackson;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import java.math.BigDecimal;
import java.nio.file.Path;
import java.time.LocalDate;
import java.util.Map;
import io.javalin.http.HttpStatus;

public class WebServer {

    private final GestorGastos gestor;

    public WebServer(GestorGastos gestor) {
        this.gestor = gestor;
    }

    public Javalin crear() {
        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());

        Javalin app = Javalin.create(config -> {
            config.staticFiles.add("/public");
            config.jsonMapper(new JavalinJackson(mapper, false));
        });

        app.get("/api/gastos", this::listarGastos);
        app.post("/api/gastos", this::registrarGasto);
        app.get("/api/gastos/total", this::calcularTotal);
        app.get("/api/gastos/total/{categoria}", this::calcularTotalPorCategoria);
        app.get("/api/gastos/{id}", this::buscarPorId);
        app.delete("/api/gastos/{id}", this::eliminarGasto);
        app.get("/api/categorias", this::listarCategorias);

        app.exception(IllegalArgumentException.class, (e, ctx) -> {
            ctx.status(HttpStatus.BAD_REQUEST).json(Map.of("message", e.getMessage()));
        });

        app.exception(Exception.class, (e, ctx) -> {
            ctx.status(HttpStatus.INTERNAL_SERVER_ERROR).json(Map.of("message", "Error interno del servidor"));
        });

        return app;
    }

    private void listarGastos(Context ctx) {
        ctx.json(gestor.listarGastos());
    }

    private void registrarGasto(Context ctx) {
        GastoRequest request = ctx.bodyAsClass(GastoRequest.class);

        Gasto gasto = gestor.registrarGasto(
                request.descripcion(),
                request.monto(),
                request.categoria(),
                request.fecha()
        );

        ctx.status(201).json(gasto);
    }

    private void buscarPorId(Context ctx) {
        int id = Integer.parseInt(ctx.pathParam("id"));

        gestor.buscarGastoPorId(id).ifPresentOrElse(
                ctx::json,
                () -> ctx.status(404).json(Map.of("error", "Gasto no encontrado"))
        );
    }

    private void eliminarGasto(Context ctx) {
        int id = Integer.parseInt(ctx.pathParam("id"));

        if (gestor.eliminarGasto(id)) {
            ctx.status(204);
        } else {
            ctx.status(404).json(Map.of("error", "Gasto no encontrado"));
        }
    }

    private void calcularTotal(Context ctx) {
        ctx.json(Map.of("total", gestor.calcularTotal()));
    }

    private void calcularTotalPorCategoria(Context ctx) {
        String nombre = ctx.pathParam("categoria");
        Categoria categoria = Categoria.valueOf(nombre.toUpperCase());
        BigDecimal total = gestor.calcularTotalPorCategoria(categoria);
        ctx.json(Map.of("categoria", categoria, "total", total));
    }

    private void listarCategorias(Context ctx) {
        ctx.json(Categoria.values());
    }

    public static void main(String[] args) {
        GastoRepository repository = new JsonGastoRepository(Path.of("data/gastos.json"));
        GestorGastos gestorGastos = new GestorGastos(repository);

        WebServer server = new WebServer(gestorGastos);
        server.crear().start(8080);

        System.out.println("Servidor iniciado en http://localhost:8080");
    }

    public record GastoRequest(
            String descripcion,
            BigDecimal monto,
            Categoria categoria,
            LocalDate fecha
    ) {}
}
