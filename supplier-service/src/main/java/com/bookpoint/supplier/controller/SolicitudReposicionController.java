package com.bookpoint.supplier.controller;

import com.bookpoint.supplier.model.SolicitudReposicion;
import com.bookpoint.supplier.service.SolicitudReposicionService;

import jakarta.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/solicitudes-reposicion")
public class SolicitudReposicionController {

    @Autowired
    private SolicitudReposicionService solicitudService;

    @GetMapping
    public ResponseEntity<List<SolicitudReposicion>> listar() {
        List<SolicitudReposicion> lista = solicitudService.listarSolicitudes();
        if (lista.isEmpty()) return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        return new ResponseEntity<>(lista, HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<SolicitudReposicion> buscar(@PathVariable("id") Long id) {
        SolicitudReposicion s = solicitudService.buscarPorId(id).orElse(null);
        if (s == null) return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        return new ResponseEntity<>(s, HttpStatus.OK);
    }

    @PostMapping
    public ResponseEntity<SolicitudReposicion> crear(@Valid @RequestBody SolicitudReposicion solicitud) {
        try {
            SolicitudReposicion nueva = solicitudService.crearSolicitud(solicitud);
            return new ResponseEntity<>(nueva, HttpStatus.CREATED);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.CONFLICT);
        }
    }
}