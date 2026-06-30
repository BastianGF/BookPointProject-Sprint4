package com.bookpoint.logistic.controller;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.core.type.TypeReference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.InputStream;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/productos")
public class ProductoSimuladoController {

    private static final Logger logger = LoggerFactory.getLogger(ProductoSimuladoController.class);
    private static List<Map<String, Object>> PRODUCTOS = null;

    // Método para cargar los productos desde el JSON
    private List<Map<String, Object>> getProductos() {
        if (PRODUCTOS == null) {
            try {
                ObjectMapper mapper = new ObjectMapper();
                InputStream inputStream = getClass().getResourceAsStream("/productos.json");
                if (inputStream == null) {
                    logger.error("No se encontró el archivo productos.json en resources");
                    return List.of();
                }
                JsonNode root = mapper.readTree(inputStream);
                PRODUCTOS = mapper.convertValue(
                    root.findValue("productos"),
                    new TypeReference<List<Map<String, Object>>>() {}
                );
                logger.info("Productos cargados correctamente: {}", PRODUCTOS.size());
            } catch (Exception e) {
                logger.error("Error al cargar productos: {}", e.getMessage());
                PRODUCTOS = List.of();
            }
        }
        return PRODUCTOS;
    }

    @GetMapping
    public ResponseEntity<List<Map<String, Object>>> listarProductos() {
        List<Map<String, Object>> productos = getProductos();
        if (productos.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }
        return new ResponseEntity<>(productos, HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Map<String, Object>> obtenerProductoPorId(@PathVariable Integer id) {
        List<Map<String, Object>> productos = getProductos();
        Optional<Map<String, Object>> producto = productos.stream()
                .filter(p -> p.get("id").equals(id))
                .findFirst();
        return producto.map(ResponseEntity::ok)
                .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }
}