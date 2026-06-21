package com.bookpoint.supplier.controller;

import com.bookpoint.supplier.model.EvaluacionProveedor;
import com.bookpoint.supplier.model.ReporteEvaluacion;
import com.bookpoint.supplier.repository.EvaluacionProveedorRepository;
import com.bookpoint.supplier.repository.ReporteEvaluacionRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Date;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class EvaluacionProveedorControllerIT {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private EvaluacionProveedorRepository evaluacionRepository;

    @Autowired
    private ReporteEvaluacionRepository reporteRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @BeforeEach
    void cleanDb() {
        reporteRepository.deleteAll();
        evaluacionRepository.deleteAll();
    }

    private EvaluacionProveedor crearEvaluacionBase() {
        EvaluacionProveedor evaluacion = new EvaluacionProveedor();
        evaluacion.setFechaEvaluacion(new Date());
        evaluacion.setPuntaje(85);
        evaluacion.setObservacionEvaluacion("Evaluación de prueba");
        return evaluacion;
    }

    private EvaluacionProveedor crearEvaluacionValida() {
        EvaluacionProveedor evaluacion = new EvaluacionProveedor();
        evaluacion.setFechaEvaluacion(new Date());
        evaluacion.setPuntaje(90);
        evaluacion.setObservacionEvaluacion("Nueva evaluación IT");
        return evaluacion;
    }

    private ReporteEvaluacion crearReporteValido() {
        ReporteEvaluacion reporte = new ReporteEvaluacion();
        reporte.setFechaReporte(new Date());
        reporte.setContenidoReporte("Reporte de integración");
        return reporte;
    }

    // TEST PARA POST /api/evaluaciones

    @Test
    void testRegistrarEvaluacion() throws Exception {
        EvaluacionProveedor nueva = crearEvaluacionValida();

        mockMvc.perform(post("/api/evaluaciones")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(nueva)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.puntaje", is(90)))
                .andExpect(jsonPath("$.observacionEvaluacion", is("Nueva evaluación IT")))
                .andExpect(jsonPath("$.fechaEvaluacion").exists());
    }

    // TEST PARA GET /api/evaluaciones

    @Test
    void testListarEvaluaciones() throws Exception {
        evaluacionRepository.save(crearEvaluacionBase());
        evaluacionRepository.save(crearEvaluacionBase());

        mockMvc.perform(get("/api/evaluaciones"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].puntaje", is(85)));
    }

    @Test
    void testListarEvaluacionesVacio() throws Exception {
        mockMvc.perform(get("/api/evaluaciones"))
                .andExpect(status().isNoContent());
    }

    // TEST PARA GET /api/evaluaciones/reportes

    @Test
    void testListarReportes() throws Exception {
        EvaluacionProveedor evaluacion = crearEvaluacionBase();
        EvaluacionProveedor guardado = evaluacionRepository.save(evaluacion);

        ReporteEvaluacion reporte = new ReporteEvaluacion();
        reporte.setFechaReporte(new Date());
        reporte.setContenidoReporte("Reporte de integración");
        reporte.setEvaluacionProveedor(guardado);
        reporteRepository.save(reporte);

        mockMvc.perform(get("/api/evaluaciones/reportes"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].contenidoReporte", is("Reporte de integración")));
    }

    @Test
    void testListarReportesVacio() throws Exception {
        mockMvc.perform(get("/api/evaluaciones/reportes"))
                .andExpect(status().isNoContent());
    }

    // TEST PARA POST /api/evaluaciones/{evaluacionId}/reporte

    @Test
    void testGenerarReporte() throws Exception {
        EvaluacionProveedor evaluacion = crearEvaluacionBase();
        EvaluacionProveedor guardado = evaluacionRepository.save(evaluacion);

        ReporteEvaluacion nuevo = crearReporteValido();

        mockMvc.perform(post("/api/evaluaciones/" + guardado.getId() + "/reporte")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(nuevo)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.contenidoReporte", is("Reporte de integración")))
                .andExpect(jsonPath("$.evaluacionProveedor.id", is(guardado.getId().intValue())));
    }

    @Test
    void testGenerarReporteEvaluacionInexistente() throws Exception {
        ReporteEvaluacion nuevo = crearReporteValido();

        mockMvc.perform(post("/api/evaluaciones/9999/reporte")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(nuevo)))
                .andExpect(status().isNotFound());
    }
}
