package com.bookpoint.logistic.controller;

import com.bookpoint.logistic.model.Envio;
import com.bookpoint.logistic.model.Incidencia;
import com.bookpoint.logistic.model.OrdenDespacho;
import com.bookpoint.logistic.repository.EnvioRepository;
import com.bookpoint.logistic.repository.IncidenciaRepository;
import com.bookpoint.logistic.repository.OrdenDespachoRepository;
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
class EnvioControllerIT {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private EnvioRepository envioRepository;

    @Autowired
    private IncidenciaRepository incidenciaRepository;

    @Autowired
    private OrdenDespachoRepository ordenDespachoRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @BeforeEach
    void cleanDb() {
        incidenciaRepository.deleteAll();
        envioRepository.deleteAll();
        ordenDespachoRepository.deleteAll();
    }

    private Envio crearEnvioBase() {
        Envio envio = new Envio();
        envio.setEstadoEnvio("PENDIENTE");
        envio.setFechaActualizacion(new Date());
        envio.setUbicacionActual("Bodega Central");
        return envio;
    }

    private Envio crearEnvioValido() {
        Envio envio = new Envio();
        envio.setEstadoEnvio("PENDIENTE");
        envio.setUbicacionActual("Bodega Central");
        return envio;
    }

    private OrdenDespacho crearOrdenDespachoBase() {
        OrdenDespacho orden = new OrdenDespacho();
        orden.setFechaCreacion(new Date());
        orden.setEstadoDespacho("PENDIENTE");
        orden.setObservacionDespacho("Test");
        orden.setTipo("DOMICILIO");
        orden.setCantidadSolicitada(10);
        orden.setCantidadConfirmada(10);
        orden.setCantidadFinal(10);
        return orden;
    }

    private Incidencia crearIncidenciaValida() {
        Incidencia incidencia = new Incidencia();
        incidencia.setDescripcionIncidencia("Problema con el envío");
        incidencia.setTipo("RETRASO");
        return incidencia;
    }

    // TEST PARA POST /api/envios
    @Test
    void testCrearEnvio() throws Exception {
        Envio nuevo = crearEnvioValido();

        mockMvc.perform(post("/api/envios")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(nuevo)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.estadoEnvio", is("PENDIENTE")))
                .andExpect(jsonPath("$.fechaActualizacion").exists());
    }

    // TEST PARA GET /api/envios
    @Test
    void testListarEnvios() throws Exception {
        envioRepository.save(crearEnvioBase());
        envioRepository.save(crearEnvioBase());

        mockMvc.perform(get("/api/envios"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].estadoEnvio", is("PENDIENTE")));
    }

    @Test
    void testListarEnviosVacio() throws Exception {
        mockMvc.perform(get("/api/envios"))
                .andExpect(status().isNoContent());
    }

    // TEST PARA GET /api/envios/{id}
    @Test
    void testBuscarEnvioPorId() throws Exception {
        Envio envio = crearEnvioBase();
        Envio guardado = envioRepository.save(envio);

        mockMvc.perform(get("/api/envios/" + guardado.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(guardado.getId().intValue())))
                .andExpect(jsonPath("$.estadoEnvio", is("PENDIENTE")));
    }

    @Test
    void testBuscarEnvioPorIdInexistente() throws Exception {
        mockMvc.perform(get("/api/envios/9999"))
                .andExpect(status().isNotFound());
    }

    // TEST PARA PUT /api/envios/{id}/estado
    @Test
    void testActualizarEstadoEnvio() throws Exception {
        Envio envio = crearEnvioBase();
        Envio guardado = envioRepository.save(envio);

        mockMvc.perform(put("/api/envios/" + guardado.getId() + "/estado")
                .param("estado", "EN_TRANSITO"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.estadoEnvio", is("EN_TRANSITO")))
                .andExpect(jsonPath("$.fechaActualizacion").exists());
    }

    @Test
    void testActualizarEstadoEnvioInexistente() throws Exception {
        mockMvc.perform(put("/api/envios/9999/estado")
                .param("estado", "EN_TRANSITO"))
                .andExpect(status().isNotFound());
    }

    // TEST PARA POST /api/envios/{envioId}/incidencias
    @Test
    void testRegistrarIncidenciaEnEnvio() throws Exception {
        Envio envio = crearEnvioBase();
        Envio guardado = envioRepository.save(envio);

        Incidencia nueva = crearIncidenciaValida();

        mockMvc.perform(post("/api/envios/" + guardado.getId() + "/incidencias")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(nueva)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.descripcionIncidencia", is("Problema con el envío")))
                .andExpect(jsonPath("$.tipo", is("RETRASO")))
                .andExpect(jsonPath("$.envio.id", is(guardado.getId().intValue())));
    }

    @Test
    void testRegistrarIncidenciaEnEnvioInexistente() throws Exception {
        Incidencia nueva = crearIncidenciaValida();

        mockMvc.perform(post("/api/envios/9999/incidencias")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(nueva)))
                .andExpect(status().isNotFound());
    }

    // TEST PARA GET /api/envios/incidencias
    @Test
    void testListarIncidencias() throws Exception {
        Envio envio = crearEnvioBase();
        Envio guardado = envioRepository.save(envio);

        Incidencia incidencia = new Incidencia();
        incidencia.setDescripcionIncidencia("Problema");
        incidencia.setTipo("RETRASO");
        incidencia.setFecha(new Date());
        incidencia.setEnvio(guardado);
        incidenciaRepository.save(incidencia);

        mockMvc.perform(get("/api/envios/incidencias"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].descripcionIncidencia", is("Problema")))
                .andExpect(jsonPath("$[0].envio.id", is(guardado.getId().intValue())));
    }

    @Test
    void testListarIncidenciasVacio() throws Exception {
        mockMvc.perform(get("/api/envios/incidencias"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(0)));
    }

    // TEST PARA PROBAR RELACION CON ORDEN_DESPACHO
    @Test
    void testCrearEnvioConOrdenDespacho() throws Exception {
        OrdenDespacho orden = crearOrdenDespachoBase();
        OrdenDespacho oGuardado = ordenDespachoRepository.save(orden);

        Envio envio = crearEnvioValido();
        envio.setOrdenDespacho(oGuardado);

        mockMvc.perform(post("/api/envios")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(envio)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.ordenDespacho.id", is(oGuardado.getId().intValue())));
    }
}
