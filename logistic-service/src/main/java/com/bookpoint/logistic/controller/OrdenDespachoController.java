package com.bookpoint.logistic.controller;

import com.bookpoint.logistic.model.OrdenDespacho;
import com.bookpoint.logistic.service.OrdenDespachoService;

import jakarta.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

@RestController
@RequestMapping("/api/ordenes-despacho")
public class OrdenDespachoController {

    private static final Logger logger = LoggerFactory.getLogger(OrdenDespachoController.class);

    @Autowired
    private OrdenDespachoService ordenDespachoService;

    @GetMapping
    public ResponseEntity<List<OrdenDespacho>> listar() {
        List<OrdenDespacho> lista = ordenDespachoService.listarOrdenes();
        if (lista.isEmpty()) return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        return new ResponseEntity<>(lista, HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<OrdenDespacho> buscar(@PathVariable("id") Long id) {
        OrdenDespacho o = ordenDespachoService.buscarPorId(id).orElse(null);
        if (o == null) return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        return new ResponseEntity<>(o, HttpStatus.OK);
    }

    @PostMapping
    public ResponseEntity<OrdenDespacho> crear(
            @Valid @RequestBody OrdenDespacho orden,
            @RequestParam Long proveedorId,
            @RequestParam(required = false) List<Integer> productoIds) { 
        try {
            OrdenDespacho nueva = ordenDespachoService.crearOrden(orden, proveedorId, productoIds);
            return new ResponseEntity<>(nueva, HttpStatus.CREATED);
        } catch (RuntimeException e) {
            if (e.getMessage() != null && e.getMessage().contains("Proveedor no encontrado")) {
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }
            return new ResponseEntity<>(HttpStatus.CONFLICT);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.CONFLICT);
        }
    }

    @PutMapping("/{id}/confirmar")
    public ResponseEntity<OrdenDespacho> confirmar(@PathVariable("id") Long id) {
        OrdenDespacho orden = ordenDespachoService.confirmarDespacho(id);
        if (orden == null) return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        return new ResponseEntity<>(orden, HttpStatus.OK);
    }

    @PutMapping("/{id}/cancelar")
    public ResponseEntity<OrdenDespacho> cancelar(@PathVariable("id") Long id) {
        OrdenDespacho orden = ordenDespachoService.cancelarDespacho(id);
        if (orden == null) return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        return new ResponseEntity<>(orden, HttpStatus.OK);
    }

    @PutMapping("/{ordenId}/preparar-mercaderia")
    public ResponseEntity<OrdenDespacho> prepararMercaderia(
            @PathVariable("ordenId") Long ordenId,  // ✅ CORREGIDO
            @RequestParam String ubicacionBodega,
            @RequestParam Integer cantidadConfirmada) {
        logger.info("PUT /api/ordenes-despacho/{}/preparar-mercaderia", ordenId);
        return new ResponseEntity<>(ordenDespachoService.prepararMercaderia(ordenId, ubicacionBodega, cantidadConfirmada), HttpStatus.OK);
    }

    @PutMapping("/{ordenId}/registrar-salida-bodega")
    public ResponseEntity<OrdenDespacho> registrarSalidaBodega(
            @PathVariable("ordenId") Long ordenId,  // ✅ CORREGIDO
            @RequestParam Long transportistaId,
            @RequestParam Integer cantidadFinal) {
        logger.info("PUT /api/ordenes-despacho/{}/registrar-salida-bodega", ordenId);
        return new ResponseEntity<>(ordenDespachoService.registrarSalidaBodega(ordenId, transportistaId, cantidadFinal), HttpStatus.OK);
    }
}