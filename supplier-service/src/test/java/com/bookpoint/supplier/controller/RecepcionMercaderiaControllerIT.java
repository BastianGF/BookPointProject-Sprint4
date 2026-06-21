package com.bookpoint.supplier.controller;

import com.bookpoint.supplier.model.RecepcionMercaderia;
import com.bookpoint.supplier.repository.RecepcionMercaderiaRepository;
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
class RecepcionMercaderiaControllerIT {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private RecepcionMercaderiaRepository recepcionRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @BeforeEach
    void cleanDb() {
        recepcionRepository.deleteAll();
    }

    private RecepcionMercaderia crearRecepcionBase() {
        RecepcionMercaderia recepcion = new RecepcionMercaderia();
        recepcion.setFechaRecepcion(new Date());
        recepcion.setCantidadRecibida(50);
        recepcion.setObservacionRecepcion("Recepción de prueba");
        return recepcion;
    }

    private RecepcionMercaderia crearRecepcionValida() {
        RecepcionMercaderia recepcion = new RecepcionMercaderia();
        recepcion.setFechaRecepcion(new Date());
        recepcion.setCantidadRecibida(30);
        recepcion.setObservacionRecepcion("Nueva recepción IT");
        return recepcion;
    }

    // TEST PARA POST /api/recepciones

    @Test
    void testRegistrarRecepcion() throws Exception {
        RecepcionMercaderia nueva = crearRecepcionValida();

        mockMvc.perform(post("/api/recepciones")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(nueva)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.cantidadRecibida", is(30)))
                .andExpect(jsonPath("$.observacionRecepcion", is("Nueva recepción IT")))
                .andExpect(jsonPath("$.fechaRecepcion").exists());
    }

    // TEST PARA GET /api/recepciones

    @Test
    void testListarRecepciones() throws Exception {
        recepcionRepository.save(crearRecepcionBase());
        recepcionRepository.save(crearRecepcionBase());

        mockMvc.perform(get("/api/recepciones"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].cantidadRecibida", is(50)))
                .andExpect(jsonPath("$[0].observacionRecepcion", is("Recepción de prueba")));
    }

    @Test
    void testListarRecepcionesVacio() throws Exception {
        mockMvc.perform(get("/api/recepciones"))
                .andExpect(status().isNoContent());
    }
}