package com.bookpoint.logistic.controller;

import com.bookpoint.logistic.model.Traslado;
import com.bookpoint.logistic.model.Transportista;
import com.bookpoint.logistic.service.TrasladoService;
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

import java.util.Date;

import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(TrasladoController.class)
@ActiveProfiles("test")
class TrasladoControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private TrasladoService trasladoService;

    @Autowired
    private ObjectMapper objectMapper;

    private Traslado trasladoBase;

    @BeforeEach
    void setUp() {
        Mockito.reset(trasladoService);
        
        trasladoBase = new Traslado();
        trasladoBase.setId(1L);
        trasladoBase.setOrigenId(1L);
        trasladoBase.setDestinoId(2L);
        trasladoBase.setEstado("PENDIENTE");
        trasladoBase.setFechaRegistro(new Date());
        trasladoBase.setProductos("{\"producto\": \"test\"}");
    }

    private Traslado crearTrasladoValido() {
        Traslado traslado = new Traslado();
        traslado.setOrigenId(1L);
        traslado.setDestinoId(2L);
        traslado.setEstado("PENDIENTE");
        traslado.setProductos("{\"producto\": \"libro\", \"cantidad\": 10}");
        return traslado;
    }

    // TEST PARA POST /api/traslados
    @Test
    void testRegistrarTrasladoValido() throws Exception {
        Traslado nuevo = crearTrasladoValido();
        Traslado guardado = new Traslado();
        guardado.setId(1L);
        guardado.setEstado("PENDIENTE");

        when(trasladoService.registrarTraslado(any(Traslado.class))).thenReturn(guardado);

        mockMvc.perform(post("/api/traslados")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(nuevo)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.estado", is("PENDIENTE")));
    }

    @Test
    void testRegistrarTrasladoInvalido() throws Exception {
        Traslado invalido = new Traslado(); // Sin campos obligatorios

        mockMvc.perform(post("/api/traslados")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalido)))
                .andExpect(status().isBadRequest());
    }

    // TEST PARA PUT /api/traslados/{id}/asignar-transportista
    @Test
    void testAsignarTransportistaExitoso() throws Exception {
        Traslado asignado = new Traslado();
        asignado.setId(1L);
        asignado.setEstado("ASIGNADO");
        Transportista t = new Transportista();
        t.setId(1L);
        asignado.setTransportista(t);

        when(trasladoService.asignarTransportista(eq(1L), eq(1L))).thenReturn(asignado);

        mockMvc.perform(put("/api/traslados/1/asignar-transportista")
                .param("transportistaId", "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.estado", is("ASIGNADO")));
    }

    @Test
    void testAsignarTransportistaTrasladoNoExiste() throws Exception {
        when(trasladoService.asignarTransportista(eq(9999L), eq(1L)))
                .thenThrow(new RuntimeException("Traslado con ID 9999 no existe"));

        mockMvc.perform(put("/api/traslados/9999/asignar-transportista")
                .param("transportistaId", "1"))
                .andExpect(status().isNotFound());
    }

    @Test
    void testAsignarTransportistaTransportistaNoDisponible() throws Exception {
        when(trasladoService.asignarTransportista(eq(1L), eq(1L)))
                .thenThrow(new RuntimeException("Transportista no disponible"));

        mockMvc.perform(put("/api/traslados/1/asignar-transportista")
                .param("transportistaId", "1"))
                .andExpect(status().isNotFound());
    }

    // TEST PARA PUT /api/traslados/{id}/confirmar-salida
    @Test
    void testConfirmarSalidaExitoso() throws Exception {
        Traslado enTransito = new Traslado();
        enTransito.setId(1L);
        enTransito.setEstado("EN_TRANSITO");

        when(trasladoService.confirmarSalida(1L)).thenReturn(enTransito);

        mockMvc.perform(put("/api/traslados/1/confirmar-salida"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.estado", is("EN_TRANSITO")));
    }

    @Test
    void testConfirmarSalidaTrasladoNoExiste() throws Exception {
        when(trasladoService.confirmarSalida(9999L))
                .thenThrow(new RuntimeException("Traslado con ID 9999 no existe"));

        mockMvc.perform(put("/api/traslados/9999/confirmar-salida"))
                .andExpect(status().isNotFound());
    }

    // TEST PARA PUT /api/traslados/{id}/confirmar-recepcion
    @Test
    void testConfirmarRecepcionExitoso() throws Exception {
        Traslado entregado = new Traslado();
        entregado.setId(1L);
        entregado.setEstado("ENTREGADO");
        entregado.setObservaciones("Recepción exitosa");

        when(trasladoService.confirmarRecepcion(eq(1L), eq("Recepción exitosa"))).thenReturn(entregado);

        mockMvc.perform(put("/api/traslados/1/confirmar-recepcion")
                .param("observaciones", "Recepción exitosa"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.estado", is("ENTREGADO")))
                .andExpect(jsonPath("$.observaciones", is("Recepción exitosa")));
    }

    @Test
    void testConfirmarRecepcionSinObservaciones() throws Exception {
        Traslado entregado = new Traslado();
        entregado.setId(1L);
        entregado.setEstado("ENTREGADO");

        when(trasladoService.confirmarRecepcion(eq(1L), eq(null))).thenReturn(entregado);

        mockMvc.perform(put("/api/traslados/1/confirmar-recepcion"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.estado", is("ENTREGADO")));
    }

    @Test
    void testConfirmarRecepcionTrasladoNoExiste() throws Exception {
        when(trasladoService.confirmarRecepcion(eq(9999L), any()))
                .thenThrow(new RuntimeException("Traslado con ID 9999 no existe"));

        mockMvc.perform(put("/api/traslados/9999/confirmar-recepcion"))
                .andExpect(status().isNotFound());
    }

    // TEST PARA GET /api/traslados/{id}
    @Test
    void testObtenerTrasladoExistente() throws Exception {
        when(trasladoService.obtenerPorId(1L)).thenReturn(trasladoBase);

        mockMvc.perform(get("/api/traslados/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.estado", is("PENDIENTE")));
    }

    @Test
    void testObtenerTrasladoInexistente() throws Exception {
        when(trasladoService.obtenerPorId(9999L)).thenReturn(null);

        mockMvc.perform(get("/api/traslados/9999"))
                .andExpect(status().isNotFound());
    }
}
