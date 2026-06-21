package com.bookpoint.logistic.controller;

import com.bookpoint.logistic.model.Traslado;
import com.bookpoint.logistic.service.TrasladoService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/traslados")
public class TrasladoController {
    
    @Autowired
    private TrasladoService trasladoService;
    
    private static final Logger logger = LoggerFactory.getLogger(TrasladoController.class);
    
    @PostMapping
    public ResponseEntity<Traslado> registrarTraslado(@Valid @RequestBody Traslado traslado) {
        logger.info("POST /api/traslados - Registrando nuevo traslado");
        try {
            Traslado nuevo = trasladoService.registrarTraslado(traslado);
            return new ResponseEntity<>(nuevo, HttpStatus.CREATED);
        } catch (Exception e) {
            logger.error("Error al registrar traslado: {}", e.getMessage());
            return new ResponseEntity<>(HttpStatus.CONFLICT);
        }
    }
    
    @PutMapping("/{trasladoId}/asignar-transportista")
    public ResponseEntity<Traslado> asignarTransportista(@PathVariable Long trasladoId, @RequestParam Long transportistaId) {
        logger.info("PUT /api/traslados/{}/asignar-transportista - transportista {}", trasladoId, transportistaId);
        try {
            Traslado traslado = trasladoService.asignarTransportista(trasladoId, transportistaId);
            return new ResponseEntity<>(traslado, HttpStatus.OK);
        } catch (RuntimeException e) {
            logger.warn("Error al asignar transportista: {}", e.getMessage());
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }
    
    @PutMapping("/{trasladoId}/confirmar-salida")
    public ResponseEntity<Traslado> confirmarSalida(@PathVariable Long trasladoId) {
        logger.info("PUT /api/traslados/{}/confirmar-salida", trasladoId);
        try {
            Traslado traslado = trasladoService.confirmarSalida(trasladoId);
            return new ResponseEntity<>(traslado, HttpStatus.OK);
        } catch (RuntimeException e) {
            logger.warn("Error al confirmar salida: {}", e.getMessage());
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }
    
    @PutMapping("/{trasladoId}/confirmar-recepcion")
    public ResponseEntity<Traslado> confirmarRecepcion(@PathVariable Long trasladoId, @RequestParam(required = false) String observaciones) {
        logger.info("PUT /api/traslados/{}/confirmar-recepcion", trasladoId);
        try {
            Traslado traslado = trasladoService.confirmarRecepcion(trasladoId, observaciones);
            return new ResponseEntity<>(traslado, HttpStatus.OK);
        } catch (RuntimeException e) {
            logger.warn("Error al confirmar recepción: {}", e.getMessage());
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }
    
    @GetMapping("/{trasladoId}")
    public ResponseEntity<Traslado> obtenerTraslado(@PathVariable Long trasladoId) {
        logger.info("GET /api/traslados/{}", trasladoId);
        Traslado traslado = trasladoService.obtenerPorId(trasladoId);
        if (traslado == null) {
            logger.warn("Traslado con id {} no encontrado", trasladoId);
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(traslado, HttpStatus.OK);
    }
}