package com.bookpoint.supplier.controller;

import com.bookpoint.supplier.model.HistorialCompras;
import com.bookpoint.supplier.model.Proveedor;
import com.bookpoint.supplier.service.ProveedorService;

import jakarta.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

@RestController
@RequestMapping("/api/proveedores")
public class ProveedorController {

    private static final Logger logger = LoggerFactory.getLogger(ProveedorController.class);

    @Autowired
    private ProveedorService proveedorService;

    @GetMapping
    public ResponseEntity<Page<Proveedor>> listar(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        logger.info("GET /api/proveedores?page={}&size={}", page, size);
        Page<Proveedor> lista = proveedorService.listarProveedores(PageRequest.of(page, size));
        if (lista.isEmpty()) return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        return new ResponseEntity<>(lista, HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Proveedor> buscar(@PathVariable Long id) {
        Proveedor p = proveedorService.buscarPorId(id).orElse(null);
        if (p == null) return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        return new ResponseEntity<>(p, HttpStatus.OK);
    }

    @PostMapping
    public ResponseEntity<Proveedor> registrar(@Valid @RequestBody Proveedor proveedor) {
        try {
            Proveedor nuevo = proveedorService.registrarProveedor(proveedor);
            return new ResponseEntity<>(nuevo, HttpStatus.CREATED);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.CONFLICT);
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<Proveedor> editar(@PathVariable Long id, @RequestBody Proveedor datos) {
        Proveedor actualizado = proveedorService.editarProveedor(id, datos);
        if (actualizado == null) return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        return new ResponseEntity<>(actualizado, HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        if (!proveedorService.eliminarProveedor(id))
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @GetMapping("/{proveedorId}/historial-compras")
    public ResponseEntity<List<HistorialCompras>> obtenerHistorial(@PathVariable Long proveedorId) {
        logger.info("GET /api/proveedores/{}/historial-compras", proveedorId);
        try {
            List<HistorialCompras> historial = proveedorService.obtenerHistorialComprasPorProveedor(proveedorId);
            return new ResponseEntity<>(historial, HttpStatus.OK);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @PostMapping("/{proveedorId}/historial-compras")
    public ResponseEntity<HistorialCompras> registrarCompra(@PathVariable Long proveedorId, @Valid @RequestBody HistorialCompras historial) {
        logger.info("POST /api/proveedores/{}/historial-compras", proveedorId);
        try {
            HistorialCompras nueva = proveedorService.registrarCompraAProveedor(proveedorId, historial);
            return new ResponseEntity<>(nueva, HttpStatus.CREATED);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }
}