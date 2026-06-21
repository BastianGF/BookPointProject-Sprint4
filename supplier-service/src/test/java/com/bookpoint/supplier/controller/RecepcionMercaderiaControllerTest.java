package com.bookpoint.supplier.controller;

import com.bookpoint.supplier.model.RecepcionMercaderia;
import com.bookpoint.supplier.service.RecepcionMercaderiaService;
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
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(RecepcionMercaderiaController.class)
@ActiveProfiles("test")
class RecepcionMercaderiaControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private RecepcionMercaderiaService recepcionService;

    @Autowired
    private ObjectMapper objectMapper;

    private RecepcionMercaderia recepcionBase;

    @BeforeEach
    void setUp() {
        Mockito.reset(recepcionService);

        recepcionBase = new RecepcionMercaderia();
        recepcionBase.setId(1L);
        recepcionBase.setFechaRecepcion(new Date());
        recepcionBase.setCantidadRecibida(50);
        recepcionBase.setObservacionRecepcion("Recepción de prueba");
    }

    private RecepcionMercaderia crearRecepcionValida() {
        RecepcionMercaderia recepcion = new RecepcionMercaderia();
        recepcion.setFechaRecepcion(new Date());
        recepcion.setCantidadRecibida(30);
        recepcion.setObservacionRecepcion("Nueva recepción");
        return recepcion;
    }

    // TEST PARA GET /api/recepciones

    @Test
    void testListarVacio() throws Exception {
        when(recepcionService.listarRecepciones()).thenReturn(new ArrayList<>());

        mockMvc.perform(get("/api/recepciones"))
                .andExpect(status().isNoContent());
    }

    @Test
    void testListarConDatos() throws Exception {
        RecepcionMercaderia recepcion2 = new RecepcionMercaderia();
        recepcion2.setId(2L);
        recepcion2.setCantidadRecibida(100);

        when(recepcionService.listarRecepciones()).thenReturn(Arrays.asList(recepcionBase, recepcion2));

        mockMvc.perform(get("/api/recepciones"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].id", is(1)))
                .andExpect(jsonPath("$[0].cantidadRecibida", is(50)))
                .andExpect(jsonPath("$[1].id", is(2)))
                .andExpect(jsonPath("$[1].cantidadRecibida", is(100)));
    }

    // TEST PARA POST /api/recepciones

    @Test
    void testRegistrarRecepcionValida() throws Exception {
        RecepcionMercaderia nueva = crearRecepcionValida();
        RecepcionMercaderia guardada = new RecepcionMercaderia();
        guardada.setId(1L);
        guardada.setCantidadRecibida(30);
        guardada.setObservacionRecepcion("Nueva recepción");

        when(recepcionService.registrarRecepcion(any(RecepcionMercaderia.class))).thenReturn(guardada);

        mockMvc.perform(post("/api/recepciones")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(nueva)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.cantidadRecibida", is(30)))
                .andExpect(jsonPath("$.observacionRecepcion", is("Nueva recepción")));
    }

    @Test
    void testRegistrarRecepcionInvalida() throws Exception {
        RecepcionMercaderia invalida = new RecepcionMercaderia();

        mockMvc.perform(post("/api/recepciones")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalida)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testRegistrarRecepcionConError() throws Exception {
        RecepcionMercaderia nueva = crearRecepcionValida();

        when(recepcionService.registrarRecepcion(any(RecepcionMercaderia.class)))
                .thenThrow(new RuntimeException("Error al registrar recepción"));

        mockMvc.perform(post("/api/recepciones")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(nueva)))
                .andExpect(status().isConflict());
    }
}