package com.bookpoint.logistic.controller;

import com.bookpoint.logistic.model.Ruta;
import com.bookpoint.logistic.model.Transportista;
import com.bookpoint.logistic.service.RutaService;
import com.bookpoint.logistic.service.TransportistaService;
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
import java.util.List;
import java.util.Optional;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(TransportistaController.class)
@ActiveProfiles("test")
class TransportistaControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private TransportistaService transportistaService;

    @MockBean
    private RutaService rutaService;

    @Autowired
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        Mockito.reset(transportistaService, rutaService);
    }

    // ========== TESTS CRUD TRANSPORTISTA ==========

    @Test
    void testListarVacio() throws Exception {
        when(transportistaService.listarTransportistas()).thenReturn(new ArrayList<>());

        mockMvc.perform(get("/api/transportistas"))
                .andExpect(status().isNoContent());
    }

    @Test
    void testListarConDatos() throws Exception {
        List<Transportista> lista = Arrays.asList(
                new Transportista(1L, "Transportes Alfa", "11111111-1", "911111111", true),
                new Transportista(2L, "Transportes Beta", "22222222-2", "922222222", false)
        );
        when(transportistaService.listarTransportistas()).thenReturn(lista);

        mockMvc.perform(get("/api/transportistas"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].nombre", is("Transportes Alfa")))
                .andExpect(jsonPath("$[1].nombre", is("Transportes Beta")));
    }

    @Test
    void testBuscarPorIdExistente() throws Exception {
        Transportista t = new Transportista(1L, "Transportes Gamma", "33333333-3", "933333333", true);
        when(transportistaService.obtainPorId(1L)).thenReturn(t);

        mockMvc.perform(get("/api/transportistas/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.nombre", is("Transportes Gamma")));
    }

    @Test
    void testBuscarPorIdInexistente() throws Exception {
        when(transportistaService.obtainPorId(9999L)).thenReturn(null);

        mockMvc.perform(get("/api/transportistas/9999"))
                .andExpect(status().isNotFound());
    }

    @Test
    void testRegistrarValido() throws Exception {
        Transportista nuevo = new Transportista(null, "Transportes Delta", "44444444-4", "944444444", true);
        Transportista guardado = new Transportista(3L, "Transportes Delta", "44444444-4", "944444444", true);
        
        when(transportistaService.registerTransportista(any(Transportista.class))).thenReturn(guardado);

        mockMvc.perform(post("/api/transportistas")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(nuevo)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id", is(3)));
    }

    @Test
    void testRegistrarConNombreVacio() throws Exception {
        Transportista invalido = new Transportista(null, "", "55555555-5", "955555555", true);

        mockMvc.perform(post("/api/transportistas")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalido)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testActualizarExistente() throws Exception {
        Transportista actualizado = new Transportista(1L, "Modificado", "11111111-1", "911111111", false);
        when(transportistaService.updateTransportista(eq(1L), any(Transportista.class))).thenReturn(actualizado);

        mockMvc.perform(put("/api/transportistas/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(actualizado)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nombre", is("Modificado")));
    }

    @Test
    void testActualizarInexistente() throws Exception {
        when(transportistaService.updateTransportista(eq(9999L), any(Transportista.class))).thenReturn(null);

        mockMvc.perform(put("/api/transportistas/9999")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(new Transportista())))
                .andExpect(status().isNotFound());
    }

    @Test
    void testEliminarExistente() throws Exception {
        when(transportistaService.deleteTransportista(1L)).thenReturn(true);

        mockMvc.perform(delete("/api/transportistas/1"))
                .andExpect(status().isNoContent());
    }

    @Test
    void testEliminarInexistente() throws Exception {
        when(transportistaService.deleteTransportista(9999L)).thenReturn(false);

        mockMvc.perform(delete("/api/transportistas/9999"))
                .andExpect(status().isNotFound());
    }

    @Test
    void testAsignarRutaValida() throws Exception {
        Ruta ruta = new Ruta();
        ruta.setOrigen("Santiago");
        ruta.setDestino("Valparaíso");
        ruta.setEstado("PENDIENTE");

        Ruta rutaGuardada = new Ruta();
        rutaGuardada.setId(1L);
        rutaGuardada.setOrigen("Santiago");
        rutaGuardada.setDestino("Valparaíso");

        when(rutaService.asignarRutaATransportista(eq(1L), any(Ruta.class))).thenReturn(rutaGuardada);

        mockMvc.perform(post("/api/transportistas/1/rutas")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(ruta)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.origen", is("Santiago")))
                .andExpect(jsonPath("$.destino", is("Valparaíso")));
    }

    @Test
    void testAsignarRutaTransportistaInexistente() throws Exception {
        Ruta ruta = new Ruta();
        ruta.setOrigen("Santiago");
        ruta.setDestino("Valparaíso");

        when(rutaService.asignarRutaATransportista(eq(9999L), any(Ruta.class)))
                .thenThrow(new RuntimeException("Transportista no existe"));

        mockMvc.perform(post("/api/transportistas/9999/rutas")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(ruta)))
                .andExpect(status().isNotFound());
    }


    @Test
    void testListarRutasConDatos() throws Exception {
        Transportista t = new Transportista(1L, "Transportista", "11111111-1", "911111111", true);
        List<Ruta> rutas = Arrays.asList(
                new Ruta(1L, "Ruta1", "Santiago", "Valparaíso", "PENDIENTE", 1L, t),
                new Ruta(2L, "Ruta2", "Concepción", "Los Angeles", "PENDIENTE", 1L, t)
        );
        when(rutaService.listarRutas()).thenReturn(rutas);

        mockMvc.perform(get("/api/transportistas/rutas"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].origen", is("Santiago")))
                .andExpect(jsonPath("$[1].origen", is("Concepción")));
    }

    @Test
        void testListarRutasVacio() throws Exception {
        when(rutaService.listarRutas()).thenReturn(new ArrayList<>());
        mockMvc.perform(get("/api/transportistas/rutas"))
                .andExpect(status().isNoContent());
        }

    @Test
    void testListarRutasPorSucursal() throws Exception {
        Transportista t = new Transportista(1L, "Transportista", "11111111-1", "911111111", true);
        List<Ruta> rutas = Arrays.asList(
                new Ruta(1L, "Ruta1", "Santiago", "Valparaíso", "PENDIENTE", 1L, t)
        );
        when(rutaService.listarPorSucursal(1L)).thenReturn(rutas);

        mockMvc.perform(get("/api/transportistas/rutas")
                .param("sucursalId", "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].sucursalId", is(1)));
    }

    // GET /api/transportistas/{rutaId}/ruta
    @Test
    void testObtenerRutaExistente() throws Exception {
        Transportista t = new Transportista(1L, "Transportista", "11111111-1", "911111111", true);
        Ruta ruta = new Ruta(1L, "Ruta Test", "Rancagua", "San Fernando", "PENDIENTE", 1L, t);
        when(rutaService.buscarPorId(1L)).thenReturn(Optional.of(ruta));

        mockMvc.perform(get("/api/transportistas/1/ruta"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.destino", is("San Fernando")));
    }

    @Test
    void testObtenerRutaInexistente() throws Exception {
        when(rutaService.buscarPorId(9999L)).thenReturn(Optional.empty());

        mockMvc.perform(get("/api/transportistas/9999/ruta"))
                .andExpect(status().isNotFound());
    }

    // PUT /api/transportistas/{rutaId}/ruta
    @Test
    void testActualizarRutaValida() throws Exception {
        Ruta datos = new Ruta();
        datos.setOrigen("Rancagua");
        datos.setDestino("San Fernando");

        Transportista t = new Transportista(1L, "Transportista", "11111111-1", "911111111", true);
        Ruta actualizada = new Ruta(1L, "Ruta Test", "Rancagua", "San Fernando", "PENDIENTE", 1L, t);
        when(rutaService.actualizarRuta(eq(1L), any(Ruta.class))).thenReturn(actualizada);

        mockMvc.perform(put("/api/transportistas/1/ruta")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(datos)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.origen", is("Rancagua")))
                .andExpect(jsonPath("$.destino", is("San Fernando")));
    }

    @Test
    void testActualizarRutaEstadoNoPendiente() throws Exception {
        Ruta datos = new Ruta();
        datos.setOrigen("Rancagua");
        datos.setDestino("San Fernando");

        when(rutaService.actualizarRuta(eq(1L), any(Ruta.class)))
                .thenThrow(new RuntimeException("Ruta solo editable en estado PENDIENTE"));

        mockMvc.perform(put("/api/transportistas/1/ruta")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(datos)))
                .andExpect(status().isConflict());
    }
}