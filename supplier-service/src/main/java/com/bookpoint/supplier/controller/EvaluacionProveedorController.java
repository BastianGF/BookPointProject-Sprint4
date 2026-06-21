package com.bookpoint.supplier.controller;

import com.bookpoint.supplier.model.EvaluacionProveedor;
import com.bookpoint.supplier.model.ReporteEvaluacion;
import com.bookpoint.supplier.service.EvaluacionProveedorService;

import jakarta.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

@RestController
@RequestMapping("/api/evaluaciones")
public class EvaluacionProveedorController {
    
    private static final Logger logger = LoggerFactory.getLogger(EvaluacionProveedorController.class);

    @Autowired
    private EvaluacionProveedorService evaluacionService;


    @GetMapping
    public ResponseEntity<List<EvaluacionProveedor>> listar() {
        List<EvaluacionProveedor> lista = evaluacionService.listarEvaluaciones();
        if (lista.isEmpty()) return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        return new ResponseEntity<>(lista, HttpStatus.OK);
    }

    @PostMapping
    public ResponseEntity<EvaluacionProveedor> registrar(@Valid @RequestBody EvaluacionProveedor evaluacion) {
        try {
            EvaluacionProveedor nueva = evaluacionService.registrarEvaluacion(evaluacion);
            return new ResponseEntity<>(nueva, HttpStatus.CREATED);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.CONFLICT);
        }
    }

    @GetMapping("/reportes")
    public ResponseEntity<List<ReporteEvaluacion>> listarReportes() {
        logger.info("GET /api/evaluaciones/reportes");
        List<ReporteEvaluacion> reportes = evaluacionService.listarReportesEvaluacion();
        if (reportes.isEmpty()) return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        return new ResponseEntity<>(reportes, HttpStatus.OK);
    }

    @PostMapping("/{evaluacionId}/reporte")
    public ResponseEntity<ReporteEvaluacion> generarReporte(@PathVariable Long evaluacionId, @Valid @RequestBody ReporteEvaluacion reporte) {
        logger.info("POST /api/evaluaciones/{}/reporte", evaluacionId);
        try {
            ReporteEvaluacion nuevo = evaluacionService.generarReporteParaEvaluacion(evaluacionId, reporte);
            return new ResponseEntity<>(nuevo, HttpStatus.CREATED);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }
}