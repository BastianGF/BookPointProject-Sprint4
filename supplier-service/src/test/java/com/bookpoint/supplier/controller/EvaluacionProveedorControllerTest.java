package com.bookpoint.supplier.controller;

import com.bookpoint.supplier.model.EvaluacionProveedor;
import com.bookpoint.supplier.model.ReporteEvaluacion;
import com.bookpoint.supplier.service.EvaluacionProveedorService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(EvaluacionProveedorController.class)
@ActiveProfiles("test")
class EvaluacionProveedorControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private EvaluacionProveedorService evaluacionService;

    @Autowired
    private ObjectMapper objectMapper;

    private EvaluacionProveedor evaluacionBase;
    private ReporteEvaluacion reporteBase;

    @BeforeEach
    void setUp() {
        Mockito.reset(evaluacionService);

        evaluacionBase = new EvaluacionProveedor();
        evaluacionBase.setId(1L);
        evaluacionBase.setFechaEvaluacion(new Date());
        evaluacionBase.setPuntaje(85);
        evaluacionBase.setObservacionEvaluacion("Evaluación de prueba");

        reporteBase = new ReporteEvaluacion();
        reporteBase.setId(1L);
        reporteBase.setFechaReporte(new Date());
        reporteBase.setContenidoReporte("Reporte de prueba");
    }

    private EvaluacionProveedor crearEvaluacionValida() {
        EvaluacionProveedor evaluacion = new EvaluacionProveedor();
        evaluacion.setFechaEvaluacion(new Date());
        evaluacion.setPuntaje(90);
        evaluacion.setObservacionEvaluacion("Nueva evaluación");
        return evaluacion;
    }

    private ReporteEvaluacion crearReporteValido() {
        ReporteEvaluacion reporte = new ReporteEvaluacion();
        reporte.setContenidoReporte("Nuevo reporte");
        return reporte;
    }

    // TEST PARA GET /api/evaluaciones

    @Test
    void testListarEvaluacionesVacio() throws Exception {
        when(evaluacionService.listarEvaluaciones()).thenReturn(new ArrayList<>());

        mockMvc.perform(get("/api/evaluaciones"))
                .andExpect(status().isNoContent());
    }

    @Test
    void testListarEvaluacionesConDatos() throws Exception {
        EvaluacionProveedor evaluacion2 = new EvaluacionProveedor();
        evaluacion2.setId(2L);
        evaluacion2.setPuntaje(70);

        when(evaluacionService.listarEvaluaciones()).thenReturn(Arrays.asList(evaluacionBase, evaluacion2));

        mockMvc.perform(get("/api/evaluaciones"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].id", is(1)))
                .andExpect(jsonPath("$[0].puntaje", is(85)))
                .andExpect(jsonPath("$[1].id", is(2)))
                .andExpect(jsonPath("$[1].puntaje", is(70)));
    }

    // TEST PARA POST /api/evaluaciones

    @Test
    void testRegistrarEvaluacionValida() throws Exception {
        EvaluacionProveedor nueva = crearEvaluacionValida();
        EvaluacionProveedor guardada = new EvaluacionProveedor();
        guardada.setId(1L);
        guardada.setPuntaje(90);
        guardada.setObservacionEvaluacion("Nueva evaluación");

        when(evaluacionService.registrarEvaluacion(any(EvaluacionProveedor.class))).thenReturn(guardada);

        mockMvc.perform(post("/api/evaluaciones")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(nueva)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.puntaje", is(90)))
                .andExpect(jsonPath("$.observacionEvaluacion", is("Nueva evaluación")));
    }

    @Test
    void testRegistrarEvaluacionInvalida() throws Exception {
        EvaluacionProveedor invalida = new EvaluacionProveedor(); // Sin campos obligatorios

        mockMvc.perform(post("/api/evaluaciones")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalida)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testRegistrarEvaluacionConError() throws Exception {
        EvaluacionProveedor nueva = crearEvaluacionValida();

        when(evaluacionService.registrarEvaluacion(any(EvaluacionProveedor.class)))
                .thenThrow(new RuntimeException("Error al registrar evaluación"));

        mockMvc.perform(post("/api/evaluaciones")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(nueva)))
                .andExpect(status().isConflict());
    }

    // TEST PARA GET /api/evaluaciones/reportes

    @Test
    void testListarReportesConDatos() throws Exception {
        when(evaluacionService.listarReportesEvaluacion()).thenReturn(Arrays.asList(reporteBase));

        mockMvc.perform(get("/api/evaluaciones/reportes"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id", is(1)))
                .andExpect(jsonPath("$[0].contenidoReporte", is("Reporte de prueba")));
    }

    @Test
    void testListarReportesVacio() throws Exception {
        when(evaluacionService.listarReportesEvaluacion()).thenReturn(new ArrayList<>());

        mockMvc.perform(get("/api/evaluaciones/reportes"))
                .andExpect(status().isNoContent());
    }

    // TEST PARA POST /api/evaluaciones/{evaluacionId}/reporte

    @Test
    void testGenerarReporteExitoso() throws Exception {
        ReporteEvaluacion nuevo = crearReporteValido();
        ReporteEvaluacion guardado = new ReporteEvaluacion();
        guardado.setId(1L);
        guardado.setContenidoReporte("Nuevo reporte");
        guardado.setEvaluacionProveedor(evaluacionBase);

        when(evaluacionService.generarReporteParaEvaluacion(eq(1L), any(ReporteEvaluacion.class)))
                .thenReturn(guardado);

        mockMvc.perform(post("/api/evaluaciones/1/reporte")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(nuevo)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.contenidoReporte", is("Nuevo reporte")));
    }

    @Test
    void testGenerarReporteEvaluacionInexistente() throws Exception {
        ReporteEvaluacion nuevo = crearReporteValido();

        when(evaluacionService.generarReporteParaEvaluacion(eq(9999L), any(ReporteEvaluacion.class)))
                .thenThrow(new RuntimeException("Evaluación con ID 9999 no existe"));

        mockMvc.perform(post("/api/evaluaciones/9999/reporte")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(nuevo)))
                .andExpect(status().isNotFound());
    }
}
