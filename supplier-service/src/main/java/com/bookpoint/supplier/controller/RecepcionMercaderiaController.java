package com.bookpoint.supplier.controller;

import com.bookpoint.supplier.model.RecepcionMercaderia;
import com.bookpoint.supplier.service.RecepcionMercaderiaService;

import jakarta.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/recepciones")
public class RecepcionMercaderiaController {

    @Autowired
    private RecepcionMercaderiaService recepcionService;

    @GetMapping
    public ResponseEntity<List<RecepcionMercaderia>> listar() {
        List<RecepcionMercaderia> lista = recepcionService.listarRecepciones();
        if (lista.isEmpty()) return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        return new ResponseEntity<>(lista, HttpStatus.OK);
    }

    @PostMapping
    public ResponseEntity<RecepcionMercaderia> registrar(@Valid @RequestBody RecepcionMercaderia recepcion) {
        try {
            RecepcionMercaderia nueva = recepcionService.registrarRecepcion(recepcion);
            return new ResponseEntity<>(nueva, HttpStatus.CREATED);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.CONFLICT);
        }
    }
}
