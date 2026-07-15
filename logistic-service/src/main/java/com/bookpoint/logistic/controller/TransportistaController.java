package com.bookpoint.logistic.controller;

import com.bookpoint.logistic.model.Ruta;
import com.bookpoint.logistic.model.Transportista;
import com.bookpoint.logistic.service.RutaService;
import com.bookpoint.logistic.service.TransportistaService;

import jakarta.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

@RestController
@RequestMapping("/api/transportistas")
public class TransportistaController {
    private static final Logger logger = LoggerFactory.getLogger(TransportistaController.class);

    @Autowired 
    private TransportistaService transportistaService;

    @Autowired 
    private RutaService rutaService;

    @GetMapping
    public ResponseEntity<List<Transportista>> listar() {
        List<Transportista> lista = transportistaService.listarTransportistas();
        if (lista.isEmpty()) return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        return new ResponseEntity<>(lista, HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Transportista> buscar(@PathVariable("id") Long id) {
        Transportista t = transportistaService.obtainPorId(id);
        if (t == null) return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        return new ResponseEntity<>(t, HttpStatus.OK);
    }

    @PostMapping
    public ResponseEntity<Transportista> registrar(@Valid @RequestBody Transportista transportista) {
        try {
            Transportista nuevo = transportistaService.registerTransportista(transportista);
            return new ResponseEntity<>(nuevo, HttpStatus.CREATED);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.CONFLICT);
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<Transportista> editar(@Valid @PathVariable("id") Long id, @RequestBody Transportista datos) {
        Transportista actualizado = transportistaService.updateTransportista(id, datos);
        if (actualizado == null) return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        return new ResponseEntity<>(actualizado, HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable("id") Long id) {
        if (!transportistaService.deleteTransportista(id))
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @PostMapping("/{transportistaId}/rutas")
    public ResponseEntity<Ruta> asignarRuta(@PathVariable("transportistaId") Long transportistaId, @Valid @RequestBody Ruta ruta) {
        logger.info("POST /api/transportistas/{}/rutas", transportistaId);
        return new ResponseEntity<>(rutaService.asignarRutaATransportista(transportistaId, ruta), HttpStatus.CREATED);
    }

    @GetMapping("/rutas")
    public ResponseEntity<List<Ruta>> listarRutas(@RequestParam(required = false) Long sucursalId) {
        logger.info("GET /api/transportistas/rutas" + (sucursalId != null ? "?sucursalId=" + sucursalId : ""));
        List<Ruta> rutas = (sucursalId != null) 
            ? rutaService.listarPorSucursal(sucursalId) 
            : rutaService.listarRutas();
        if (rutas.isEmpty()) return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        return new ResponseEntity<>(rutas, HttpStatus.OK);
    }

    @GetMapping("/{rutaId}/ruta")
    public ResponseEntity<Ruta> obtenerRuta(@PathVariable("rutaId") Long rutaId) {
        logger.info("GET /api/transportistas/{}/ruta", rutaId);
        Ruta ruta = rutaService.buscarPorId(rutaId).orElse(null);
        if (ruta == null) return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        return new ResponseEntity<>(ruta, HttpStatus.OK);
    }

    @PutMapping("/{rutaId}/ruta")
    public ResponseEntity<Ruta> actualizarRuta(@PathVariable("rutaId") Long rutaId, @Valid @RequestBody Ruta datos) {
        try {
            return new ResponseEntity<>(rutaService.actualizarRuta(rutaId, datos), HttpStatus.OK);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(HttpStatus.CONFLICT);
        }
    }

    @PatchMapping("/{id}/reactivar")
    public ResponseEntity<Transportista> reactivarTransportista(@PathVariable("id") Long id) {
        try {
            Transportista reactivado = transportistaService.reactivarTransportista(id);
            return new ResponseEntity<>(reactivado, HttpStatus.OK);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }
}