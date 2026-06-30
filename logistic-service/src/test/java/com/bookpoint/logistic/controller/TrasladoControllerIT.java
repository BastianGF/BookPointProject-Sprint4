package com.bookpoint.logistic.controller;

import com.bookpoint.logistic.model.Traslado;
import com.bookpoint.logistic.model.Transportista;
import com.bookpoint.logistic.repository.TrasladoRepository;
import com.bookpoint.logistic.repository.OrdenDespachoRepository;
import com.bookpoint.logistic.repository.TransportistaRepository;
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

import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class TrasladoControllerIT {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private TrasladoRepository trasladoRepository;

    @Autowired
    private TransportistaRepository transportistaRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private OrdenDespachoRepository ordenDespachoRepository;

    @BeforeEach
    void cleanDb() {
        ordenDespachoRepository.deleteAll();
        trasladoRepository.deleteAll();
        transportistaRepository.deleteAll();
    }

    private Traslado crearTrasladoBase() {
        Traslado traslado = new Traslado();
        traslado.setOrigenId(1L);
        traslado.setDestinoId(2L);
        traslado.setEstado("PENDIENTE");
        traslado.setFechaRegistro(new Date());
        traslado.setProductos("{\"producto\": \"libro\", \"cantidad\": 10}");
        traslado.setUbicacionActual("Bodega Central");
        return traslado;
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
    void testRegistrarTraslado() throws Exception {
        Traslado nuevo = crearTrasladoValido();

        mockMvc.perform(post("/api/traslados")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(nuevo)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.estado", is("PENDIENTE")))
                .andExpect(jsonPath("$.fechaRegistro").exists());
    }

    // TEST PARA PUT /api/traslados/{id}/asignar-transportista
    @Test
    void testAsignarTransportista() throws Exception {
        Traslado traslado = crearTrasladoBase();
        Traslado guardado = trasladoRepository.save(traslado);

        Transportista transportista = new Transportista(null, "Transportista Test", "12345678-9", "987654321", true);
        Transportista tGuardado = transportistaRepository.save(transportista);

        mockMvc.perform(put("/api/traslados/" + guardado.getId() + "/asignar-transportista")
                .param("transportistaId", tGuardado.getId().toString()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.estado", is("ASIGNADO")))
                .andExpect(jsonPath("$.transportista.id", is(tGuardado.getId().intValue())));
    }

    @Test
    void testAsignarTransportistaTrasladoNoExiste() throws Exception {
        mockMvc.perform(put("/api/traslados/9999/asignar-transportista")
                .param("transportistaId", "1"))
                .andExpect(status().isNotFound());
    }

    @Test
    void testAsignarTransportistaTransportistaNoExiste() throws Exception {
        Traslado traslado = crearTrasladoBase();
        Traslado guardado = trasladoRepository.save(traslado);

        mockMvc.perform(put("/api/traslados/" + guardado.getId() + "/asignar-transportista")
                .param("transportistaId", "9999"))
                .andExpect(status().isNotFound());
    }

    @Test
    void testAsignarTransportistaEstadoInvalido() throws Exception {
        Traslado traslado = crearTrasladoBase();
        traslado.setEstado("ASIGNADO");
        Traslado guardado = trasladoRepository.save(traslado);

        mockMvc.perform(put("/api/traslados/" + guardado.getId() + "/asignar-transportista")
                .param("transportistaId", "1"))
                .andExpect(status().isNotFound());
    }

    // TEST PARA PUT /api/traslados/{id}/confirmar-salida
    @Test
    void testConfirmarSalida() throws Exception {
        Traslado traslado = crearTrasladoBase();
        traslado.setEstado("ASIGNADO");
        Traslado guardado = trasladoRepository.save(traslado);

        mockMvc.perform(put("/api/traslados/" + guardado.getId() + "/confirmar-salida"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.estado", is("EN_TRANSITO")))
                .andExpect(jsonPath("$.ubicacionActual").value(org.hamcrest.Matchers.containsString("En tránsito")));
    }

    @Test
    void testConfirmarSalidaTrasladoNoExiste() throws Exception {
        mockMvc.perform(put("/api/traslados/9999/confirmar-salida"))
                .andExpect(status().isNotFound());
    }

    @Test
    void testConfirmarSalidaEstadoInvalido() throws Exception {
        Traslado traslado = crearTrasladoBase(); // Estado PENDIENTE
        Traslado guardado = trasladoRepository.save(traslado);

        mockMvc.perform(put("/api/traslados/" + guardado.getId() + "/confirmar-salida"))
                .andExpect(status().isNotFound());
    }

    // TEST PARA PUT /api/traslados/{id}/confirmar-recepcion
    @Test
    void testConfirmarRecepcion() throws Exception {
        Traslado traslado = crearTrasladoBase();
        traslado.setEstado("EN_TRANSITO");
        Traslado guardado = trasladoRepository.save(traslado);

        mockMvc.perform(put("/api/traslados/" + guardado.getId() + "/confirmar-recepcion")
                .param("observaciones", "Recepción exitosa"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.estado", is("ENTREGADO")))
                .andExpect(jsonPath("$.observaciones", is("Recepción exitosa")));
    }

    @Test
    void testConfirmarRecepcionSinObservaciones() throws Exception {
        Traslado traslado = crearTrasladoBase();
        traslado.setEstado("EN_TRANSITO");
        Traslado guardado = trasladoRepository.save(traslado);

        mockMvc.perform(put("/api/traslados/" + guardado.getId() + "/confirmar-recepcion"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.estado", is("ENTREGADO")))
                .andExpect(jsonPath("$.observaciones").isEmpty());
    }

    @Test
    void testConfirmarRecepcionTrasladoNoExiste() throws Exception {
        mockMvc.perform(put("/api/traslados/9999/confirmar-recepcion"))
                .andExpect(status().isNotFound());
    }

    @Test
    void testConfirmarRecepcionEstadoInvalido() throws Exception {
        Traslado traslado = crearTrasladoBase(); // Estado PENDIENTE
        Traslado guardado = trasladoRepository.save(traslado);

        mockMvc.perform(put("/api/traslados/" + guardado.getId() + "/confirmar-recepcion"))
                .andExpect(status().isNotFound());
    }

    // TEST PARA GET /api/traslados/{id}
    @Test
    void testObtenerTraslado() throws Exception {
        Traslado traslado = crearTrasladoBase();
        Traslado guardado = trasladoRepository.save(traslado);

        mockMvc.perform(get("/api/traslados/" + guardado.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(guardado.getId().intValue())))
                .andExpect(jsonPath("$.estado", is("PENDIENTE")))
                .andExpect(jsonPath("$.origenId", is(1)))
                .andExpect(jsonPath("$.destinoId", is(2)));
    }

    @Test
    void testObtenerTrasladoInexistente() throws Exception {
        mockMvc.perform(get("/api/traslados/9999"))
                .andExpect(status().isNotFound());
    }
}