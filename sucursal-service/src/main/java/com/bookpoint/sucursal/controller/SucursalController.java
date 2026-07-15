package com.bookpoint.sucursal.controller;

import com.bookpoint.sucursal.model.Sucursal;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/sucursales")
public class SucursalController {

    private static final List<Sucursal> SUCURSALES = Arrays.asList(
        new Sucursal(1L, "Sucursal Central", "Av. Principal 123, Santiago", "Región Metropolitana"),
        new Sucursal(2L, "Sucursal Norte", "Calle Norte 456, La Serena", "Región de Coquimbo"),
        new Sucursal(3L, "Sucursal Sur", "Calle Sur 789, Concepción", "Región del Biobío"),
        new Sucursal(4L, "Sucursal Bodega", "Camino a la Bodega, Rancagua", "Región del Libertador")
    );

    @GetMapping
    public ResponseEntity<List<Sucursal>> listarSucursales() {
        return new ResponseEntity<>(SUCURSALES, HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Sucursal> obtenerSucursalPorId(@PathVariable Long id) {
        Optional<Sucursal> sucursal = SUCURSALES.stream()
                .filter(s -> s.getId().equals(id))
                .findFirst();
        return sucursal.map(ResponseEntity::ok)
                .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }
}