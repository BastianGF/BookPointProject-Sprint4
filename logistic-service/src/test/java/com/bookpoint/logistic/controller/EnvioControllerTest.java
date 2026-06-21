package com.bookpoint.logistic.controller;

import com.bookpoint.logistic.model.Envio;
import com.bookpoint.logistic.model.Incidencia;
import com.bookpoint.logistic.service.EnvioService;
import com.bookpoint.logistic.service.IncidenciaService;
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
import java.util.Optional;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(EnvioController.class)
@ActiveProfiles("test")
class EnvioControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private EnvioService envioService;

    @MockBean
    private IncidenciaService incidenciaService;

    @Autowired
    private ObjectMapper objectMapper;

    private Envio envioBase;
    private Incidencia incidenciaBase;

    @BeforeEach
    void setUp() {
        Mockito.reset(envioService, incidenciaService);
        
        envioBase = new Envio();
        envioBase.setId(1L);
        envioBase.setEstadoEnvio("PENDIENTE");
        envioBase.setFechaActualizacion(new Date());
        envioBase.setUbicacionActual("Bodega Central");

        incidenciaBase = new Incidencia();
        incidenciaBase.setId(1L);
        incidenciaBase.setDescripcionIncidencia("Problema con el envío");
        incidenciaBase.setTipo("RETRASO");
        incidenciaBase.setFecha(new Date());
    }

    private Envio crearEnvioValido() {
        Envio envio = new Envio();
        envio.setEstadoEnvio("PENDIENTE");
        envio.setUbicacionActual("Bodega Central");
        return envio;
    }

    private Incidencia crearIncidenciaValida() {
        Incidencia incidencia = new Incidencia();
        incidencia.setDescripcionIncidencia("Problema con el envío");
        incidencia.setTipo("RETRASO");
        return incidencia;
    }

    // TEST PARA EL GET /api/envios
    @Test
    void testListarVacio() throws Exception {
        when(envioService.listarEnvios()).thenReturn(new ArrayList<>());

        mockMvc.perform(get("/api/envios"))
                .andExpect(status().isNoContent());
    }

    @Test
    void testListarConDatos() throws Exception {
        Envio envio2 = new Envio();
        envio2.setId(2L);
        envio2.setEstadoEnvio("EN_TRANSITO");

        when(envioService.listarEnvios()).thenReturn(Arrays.asList(envioBase, envio2));

        mockMvc.perform(get("/api/envios"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].id", is(1)))
                .andExpect(jsonPath("$[0].estadoEnvio", is("PENDIENTE")))
                .andExpect(jsonPath("$[1].id", is(2)))
                .andExpect(jsonPath("$[1].estadoEnvio", is("EN_TRANSITO")));
    }

    // TEST PARA EL GET /api/envios/{id}
    @Test
    void testBuscarPorIdExistente() throws Exception {
        when(envioService.buscarPorId(1L)).thenReturn(Optional.of(envioBase));

        mockMvc.perform(get("/api/envios/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.estadoEnvio", is("PENDIENTE")));
    }

    @Test
    void testBuscarPorIdInexistente() throws Exception {
        when(envioService.buscarPorId(9999L)).thenReturn(Optional.empty());

        mockMvc.perform(get("/api/envios/9999"))
                .andExpect(status().isNotFound());
    }

    // TEST PARA EL POST /api/envios
    @Test
    void testCrearEnvioValido() throws Exception {
        Envio nuevo = crearEnvioValido();
        Envio guardado = new Envio();
        guardado.setId(1L);
        guardado.setEstadoEnvio("PENDIENTE");

        when(envioService.crearEnvio(any(Envio.class))).thenReturn(guardado);

        mockMvc.perform(post("/api/envios")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(nuevo)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.estadoEnvio", is("PENDIENTE")));
    }

    @Test
    void testCrearEnvioInvalido() throws Exception {
        Envio invalido = new Envio();

        mockMvc.perform(post("/api/envios")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalido)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testCrearEnvioConError() throws Exception {
        Envio nuevo = crearEnvioValido();

        when(envioService.crearEnvio(any(Envio.class)))
                .thenThrow(new RuntimeException("Error al crear envío"));

        mockMvc.perform(post("/api/envios")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(nuevo)))
                .andExpect(status().isConflict());
    }

    // TEST PARA EL PUT /api/envios/{id}/estado
    @Test
    void testActualizarEstadoExistente() throws Exception {
        Envio actualizado = new Envio();
        actualizado.setId(1L);
        actualizado.setEstadoEnvio("EN_TRANSITO");

        when(envioService.actualizarEstadoEnvio(eq(1L), eq("EN_TRANSITO"))).thenReturn(actualizado);

        mockMvc.perform(put("/api/envios/1/estado")
                .param("estado", "EN_TRANSITO"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.estadoEnvio", is("EN_TRANSITO")));
    }

    @Test
    void testActualizarEstadoInexistente() throws Exception {
        when(envioService.actualizarEstadoEnvio(eq(9999L), eq("EN_TRANSITO"))).thenReturn(null);

        mockMvc.perform(put("/api/envios/9999/estado")
                .param("estado", "EN_TRANSITO"))
                .andExpect(status().isNotFound());
    }

    // TEST PARA EL POST /api/envios/{envioId}/incidencias
    @Test
    void testRegistrarIncidenciaEnEnvio() throws Exception {
        Incidencia nueva = crearIncidenciaValida();
        Incidencia guardada = new Incidencia();
        guardada.setId(1L);
        guardada.setDescripcionIncidencia("Problema con el envío");
        guardada.setTipo("RETRASO");
        guardada.setEnvio(envioBase);

        when(incidenciaService.registrarIncidenciaEnEnvio(eq(1L), any(Incidencia.class)))
                .thenReturn(guardada);

        mockMvc.perform(post("/api/envios/1/incidencias")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(nueva)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.descripcionIncidencia", is("Problema con el envío")))
                .andExpect(jsonPath("$.tipo", is("RETRASO")));
    }

    @Test
    void testRegistrarIncidenciaEnEnvioInexistente() throws Exception {
        Incidencia nueva = crearIncidenciaValida();

        when(incidenciaService.registrarIncidenciaEnEnvio(eq(9999L), any(Incidencia.class)))
                .thenThrow(new RuntimeException("Envío con ID 9999 no existe"));

        mockMvc.perform(post("/api/envios/9999/incidencias")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(nueva)))
                .andExpect(status().isNotFound());
    }

    // TEST PARA EL GET /api/envios/incidencias
    @Test
    void testListarIncidencias() throws Exception {
        when(incidenciaService.listarIncidencias()).thenReturn(Arrays.asList(incidenciaBase));

        mockMvc.perform(get("/api/envios/incidencias"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id", is(1)))
                .andExpect(jsonPath("$[0].tipo", is("RETRASO")));
    }

    @Test
    void testListarIncidenciasVacio() throws Exception {
        when(incidenciaService.listarIncidencias()).thenReturn(new ArrayList<>());

        mockMvc.perform(get("/api/envios/incidencias"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(0)));
    }
}