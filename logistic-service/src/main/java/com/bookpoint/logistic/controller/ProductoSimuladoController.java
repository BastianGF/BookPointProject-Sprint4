package com.bookpoint.logistic.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/productos")
public class ProductoSimuladoController {
    // Lista de productos para emular (por favor funciona)
    private static final List<Map<String, Object>> PRODUCTOS = Arrays.asList(
            Map.of("id", 1, "nombre", "Libro de Java", "precio", 25000, "stock", 50, "categoria", "Educación"),
            Map.of("id", 2, "nombre", "Cuaderno", "precio", 5000, "stock", 100, "categoria", "Papelería"),
            Map.of("id", 3, "nombre", "Lápiz", "precio", 1500, "stock", 200, "categoria", "Papelería"),
            Map.of("id", 4, "nombre", "Calculadora", "precio", 15000, "stock", 30, "categoria", "Oficina"),
            Map.of("id", 5, "nombre", "Libro de Python", "precio", 45000, "stock", 35, "categoria", "Educacion")
    );

    @GetMapping
    public ResponseEntity<List<Map<String, Object>>> listarProductos() {
        return new ResponseEntity<>(PRODUCTOS, HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Map<String, Object>> obtenerProductoPorId(@PathVariable Integer id) {
        Optional<Map<String, Object>> producto = PRODUCTOS.stream()
                .filter(p -> p.get("id").equals(id))
                .findFirst();
        return producto.map(ResponseEntity::ok)
                .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }
}
