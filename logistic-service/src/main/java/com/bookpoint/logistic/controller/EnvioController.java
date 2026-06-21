package com.bookpoint.logistic.controller;

import com.bookpoint.logistic.model.Envio;
import com.bookpoint.logistic.model.Incidencia;
import com.bookpoint.logistic.service.EnvioService;
import com.bookpoint.logistic.service.IncidenciaService;

import jakarta.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

@RestController
@RequestMapping("/api/envios")
public class EnvioController {

    private static final Logger logger = LoggerFactory.getLogger(EnvioController.class);

    @Autowired
    private EnvioService envioService;

    @Autowired private IncidenciaService incidenciaService;

    @GetMapping
    public ResponseEntity<List<Envio>> listar() {
        List<Envio> lista = envioService.listarEnvios();
        if (lista.isEmpty()) return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        return new ResponseEntity<>(lista, HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Envio> buscar(@PathVariable Long id) {
        Envio e = envioService.buscarPorId(id).orElse(null);
        if (e == null) return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        return new ResponseEntity<>(e, HttpStatus.OK);
    }

    @PostMapping
    public ResponseEntity<Envio> crear(@Valid @RequestBody Envio envio) {
        try {
            Envio nuevo = envioService.crearEnvio(envio);
            return new ResponseEntity<>(nuevo, HttpStatus.CREATED);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.CONFLICT);
        }
    }

    @PutMapping("/{id}/estado")
    public ResponseEntity<Envio> actualizarEstado(@PathVariable Long id, @RequestParam String estado) {
        Envio envio = envioService.actualizarEstadoEnvio(id, estado);
        if (envio == null) return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        return new ResponseEntity<>(envio, HttpStatus.OK);
    }

    @PostMapping("/{envioId}/incidencias")
    public ResponseEntity<Incidencia> registrarIncidencia(@PathVariable Long envioId, @Valid @RequestBody Incidencia incidencia) {
        logger.info("POST /api/envios/{}/incidencias", envioId);
        Incidencia nuevaIncidencia = incidenciaService.registrarIncidenciaEnEnvio(envioId, incidencia);
        return new ResponseEntity<>(nuevaIncidencia, HttpStatus.CREATED);
    }

    @GetMapping("/incidencias")
    public ResponseEntity<List<Incidencia>> listarIncidencias() {
        logger.info("GET /api/envios/incidencias");
        return new ResponseEntity<>(incidenciaService.listarIncidencias(), HttpStatus.OK);
    }
}
