package com.bookpoint.supplier.controller;

import com.bookpoint.supplier.model.SolicitudReposicion;
import com.bookpoint.supplier.service.SolicitudReposicionService;
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
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(SolicitudReposicionController.class)
@ActiveProfiles("test")
class SolicitudReposicionControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private SolicitudReposicionService solicitudService;

    @Autowired
    private ObjectMapper objectMapper;

    private SolicitudReposicion solicitudBase;

    @BeforeEach
    void setUp() {
        Mockito.reset(solicitudService);

        solicitudBase = new SolicitudReposicion();
        solicitudBase.setId(1L);
        solicitudBase.setFechaSolicitud(new Date());
        solicitudBase.setEstadoSolicitud("PENDIENTE");
        solicitudBase.setCantidadSolicitada(10);
    }

    private SolicitudReposicion crearSolicitudValida() {
        SolicitudReposicion solicitud = new SolicitudReposicion();
        solicitud.setCantidadSolicitada(15);
        solicitud.setEstadoSolicitud("PENDIENTE");
        return solicitud;
    }

    // TEST PARA GET /api/solicitudes-reposicion

    @Test
    void testListarVacio() throws Exception {
        when(solicitudService.listarSolicitudes()).thenReturn(new ArrayList<>());

        mockMvc.perform(get("/api/solicitudes-reposicion"))
                .andExpect(status().isNoContent());
    }

    @Test
    void testListarConDatos() throws Exception {
        SolicitudReposicion solicitud2 = new SolicitudReposicion();
        solicitud2.setId(2L);
        solicitud2.setEstadoSolicitud("APROBADA");

        when(solicitudService.listarSolicitudes()).thenReturn(Arrays.asList(solicitudBase, solicitud2));

        mockMvc.perform(get("/api/solicitudes-reposicion"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].id", is(1)))
                .andExpect(jsonPath("$[0].estadoSolicitud", is("PENDIENTE")))
                .andExpect(jsonPath("$[1].id", is(2)))
                .andExpect(jsonPath("$[1].estadoSolicitud", is("APROBADA")));
    }

    // TEST PARA GET /api/solicitudes-reposicion/{id}

    @Test
    void testBuscarPorIdExistente() throws Exception {
        when(solicitudService.buscarPorId(1L)).thenReturn(Optional.of(solicitudBase));

        mockMvc.perform(get("/api/solicitudes-reposicion/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.estadoSolicitud", is("PENDIENTE")))
                .andExpect(jsonPath("$.cantidadSolicitada", is(10)));
    }

    @Test
    void testBuscarPorIdInexistente() throws Exception {
        when(solicitudService.buscarPorId(9999L)).thenReturn(Optional.empty());

        mockMvc.perform(get("/api/solicitudes-reposicion/9999"))
                .andExpect(status().isNotFound());
    }

    // TEST PARA POST /api/solicitudes-reposicion

    @Test
    void testCrearSolicitudValida() throws Exception {
        SolicitudReposicion nueva = crearSolicitudValida();
        SolicitudReposicion guardada = new SolicitudReposicion();
        guardada.setId(1L);
        guardada.setEstadoSolicitud("PENDIENTE");
        guardada.setCantidadSolicitada(15);

        when(solicitudService.crearSolicitud(any(SolicitudReposicion.class))).thenReturn(guardada);

        mockMvc.perform(post("/api/solicitudes-reposicion")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(nueva)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.estadoSolicitud", is("PENDIENTE")))
                .andExpect(jsonPath("$.cantidadSolicitada", is(15)));
    }

    @Test
    void testCrearSolicitudInvalida() throws Exception {
        SolicitudReposicion invalida = new SolicitudReposicion(); // Sin campos obligatorios

        mockMvc.perform(post("/api/solicitudes-reposicion")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalida)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testCrearSolicitudConError() throws Exception {
        SolicitudReposicion nueva = crearSolicitudValida();

        when(solicitudService.crearSolicitud(any(SolicitudReposicion.class)))
                .thenThrow(new RuntimeException("Error al crear solicitud"));

        mockMvc.perform(post("/api/solicitudes-reposicion")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(nueva)))
                .andExpect(status().isConflict());
    }
}