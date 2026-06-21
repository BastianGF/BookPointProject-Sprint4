package com.bookpoint.supplier.controller;

import com.bookpoint.supplier.model.SolicitudReposicion;
import com.bookpoint.supplier.repository.SolicitudReposicionRepository;
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
class SolicitudReposicionControllerIT {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private SolicitudReposicionRepository solicitudRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @BeforeEach
    void cleanDb() {
        solicitudRepository.deleteAll();
    }

    private SolicitudReposicion crearSolicitudBase() {
        SolicitudReposicion solicitud = new SolicitudReposicion();
        solicitud.setFechaSolicitud(new Date());
        solicitud.setEstadoSolicitud("PENDIENTE");
        solicitud.setCantidadSolicitada(10);
        return solicitud;
    }

    private SolicitudReposicion crearSolicitudValida() {
        SolicitudReposicion solicitud = new SolicitudReposicion();
        solicitud.setFechaSolicitud(new Date());
        solicitud.setEstadoSolicitud("PENDIENTE");  // ✅ AGREGADO
        solicitud.setCantidadSolicitada(15);
        return solicitud;
    }

    // TEST PARA POST /api/solicitudes-reposicion

    @Test
    void testCrearSolicitud() throws Exception {
        SolicitudReposicion nueva = crearSolicitudValida();

        mockMvc.perform(post("/api/solicitudes-reposicion")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(nueva)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.estadoSolicitud", is("PENDIENTE")))
                .andExpect(jsonPath("$.cantidadSolicitada", is(15)))
                .andExpect(jsonPath("$.fechaSolicitud").exists());
    }

    // TEST PARA GET /api/solicitudes-reposicion

    @Test
    void testListarSolicitudes() throws Exception {
        solicitudRepository.save(crearSolicitudBase());
        solicitudRepository.save(crearSolicitudBase());

        mockMvc.perform(get("/api/solicitudes-reposicion"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].estadoSolicitud", is("PENDIENTE")));
    }

    @Test
    void testListarSolicitudesVacio() throws Exception {
        mockMvc.perform(get("/api/solicitudes-reposicion"))
                .andExpect(status().isNoContent());
    }

    // TEST PARA GET /api/solicitudes-reposicion/{id}

    @Test
    void testBuscarSolicitudPorId() throws Exception {
        SolicitudReposicion solicitud = crearSolicitudBase();
        SolicitudReposicion guardado = solicitudRepository.save(solicitud);

        mockMvc.perform(get("/api/solicitudes-reposicion/" + guardado.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(guardado.getId().intValue())))
                .andExpect(jsonPath("$.estadoSolicitud", is("PENDIENTE")))
                .andExpect(jsonPath("$.cantidadSolicitada", is(10)));
    }

    @Test
    void testBuscarSolicitudPorIdInexistente() throws Exception {
        mockMvc.perform(get("/api/solicitudes-reposicion/9999"))
                .andExpect(status().isNotFound());
    }
}