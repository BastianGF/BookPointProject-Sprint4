package com.bookpoint.supplier.service;

import com.bookpoint.supplier.model.EvaluacionProveedor;
import com.bookpoint.supplier.model.ReporteEvaluacion;
import com.bookpoint.supplier.repository.EvaluacionProveedorRepository;
import com.bookpoint.supplier.repository.ReporteEvaluacionRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Service
public class EvaluacionProveedorService {

    private static final Logger logger = LoggerFactory.getLogger(EvaluacionProveedorService.class);

    @Autowired
    private EvaluacionProveedorRepository evaluacionRepository;

    @Autowired
    private ReporteEvaluacionRepository reporteRepository;

    public List<EvaluacionProveedor> listarEvaluaciones() {
        return evaluacionRepository.findAll();
    }

    public EvaluacionProveedor registrarEvaluacion(EvaluacionProveedor evaluacion) {
        evaluacion.setFechaEvaluacion(new Date());
        return evaluacionRepository.save(evaluacion);
    }

    public EvaluacionProveedor buscarEvaluacionPorId(Long id) {
        logger.info("Buscando evaluación con id: {}", id);
        return evaluacionRepository.findById(id).orElse(null);
    }

    public List<ReporteEvaluacion> listarReportesEvaluacion() {
        logger.info("Listando todos los reportes de evaluación");
        return reporteRepository.findAll();
    }

    public ReporteEvaluacion generarReporteEvaluacion(ReporteEvaluacion reporte) {
        logger.info("Generando reporte de evaluación");
        reporte.setFechaReporte(new Date());
        return reporteRepository.save(reporte);
    }

    public ReporteEvaluacion generarReporteParaEvaluacion(Long evaluacionId, ReporteEvaluacion reporte) {
        logger.info("Generando reporte para evaluación {}", evaluacionId);
        EvaluacionProveedor evaluacion = evaluacionRepository.findById(evaluacionId)
            .orElseThrow(() -> new RuntimeException("Evaluación con ID " + evaluacionId + " no existe"));
        reporte.setEvaluacionProveedor(evaluacion);
        reporte.setFechaReporte(new Date());
        return reporteRepository.save(reporte);
    }
}