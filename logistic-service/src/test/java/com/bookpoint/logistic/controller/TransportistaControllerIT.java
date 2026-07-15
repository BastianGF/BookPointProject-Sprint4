package com.bookpoint.logistic.controller;

import com.bookpoint.logistic.client.SucursalClient;
import com.bookpoint.logistic.dto.SucursalDTO;
import com.bookpoint.logistic.model.Ruta;
import com.bookpoint.logistic.model.Transportista;
import com.bookpoint.logistic.repository.RutaRepository;
import com.bookpoint.logistic.repository.TransportistaRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class TransportistaControllerIT {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private TransportistaRepository transportistaRepository;
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private RutaRepository rutaRepository;
    @MockBean
    private SucursalClient sucursalClient;

    @BeforeEach
    void cleanDb() {
        rutaRepository.deleteAll();
        transportistaRepository.deleteAll();
    }

    @Test
    void testCrearYObtenerTransportista() throws Exception {
        Transportista nuevo = new Transportista(null, "Juan Pérez", "12345678-9", "987654321", true);

        mockMvc.perform(post("/api/transportistas")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(nuevo)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.nombre").value("Juan Pérez"));
    }

    @Test
    void testListarTransportistas() throws Exception {
        Transportista t1 = new Transportista(null, "Transportista 1", "11111111-1", "911111111", true);
        Transportista t2 = new Transportista(null, "Transportista 2", "22222222-2", "922222222", true);
        transportistaRepository.save(t1);
        transportistaRepository.save(t2);

        mockMvc.perform(get("/api/transportistas"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].nombre").value("Transportista 1"));
    }

    @Test
    void testBuscarPorId() throws Exception {
        Transportista t = new Transportista(null, "Carlos", "33333333-3", "933333333", true);
        Transportista guardado = transportistaRepository.save(t);

        mockMvc.perform(get("/api/transportistas/" + guardado.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nombre").value("Carlos"));
    }

    @Test
    void testActualizarTransportista() throws Exception {
        Transportista t = new Transportista(null, "Original", "44444444-4", "944444444", true);
        Transportista guardado = transportistaRepository.save(t);

        Transportista actualizado = new Transportista(null, "Modificado", "44444444-4", "944444444", false);

        mockMvc.perform(put("/api/transportistas/" + guardado.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(actualizado)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nombre").value("Modificado"))
                .andExpect(jsonPath("$.disponible").value(false));
    }

    @Test
    void testEliminarTransportista() throws Exception {
        Transportista t = new Transportista(null, "A Eliminar", "55555555-5", "955555555", true);
        Transportista guardado = transportistaRepository.save(t);

        mockMvc.perform(delete("/api/transportistas/" + guardado.getId()))
                .andExpect(status().isNoContent());
    }

    @Test
    void testAsignarRuta() throws Exception {
        SucursalDTO sucursalMock = new SucursalDTO();
        sucursalMock.setId(1L);
        sucursalMock.setNombre("Sucursal Test");
        when(sucursalClient.obtenerSucursalPorId(1L)).thenReturn(sucursalMock);
        
        Transportista t = new Transportista(null, "Juan Pérez", "12345678-9", "987654321", true);
        Transportista guardado = transportistaRepository.save(t);

        Ruta ruta = new Ruta();
        ruta.setOrigen("Santiago");
        ruta.setDestino("Valparaíso");
        ruta.setEstado("PENDIENTE");
        ruta.setSucursalId(1L);

        mockMvc.perform(post("/api/transportistas/" + guardado.getId() + "/rutas")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(ruta)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.origen", is("Santiago")))
                .andExpect(jsonPath("$.destino", is("Valparaíso")))
                .andExpect(jsonPath("$.transportista.id", is(guardado.getId().intValue())));
    }

    @Test
    void testListarRutas() throws Exception {
        Transportista t = transportistaRepository.save(new Transportista(null, "Juan Pérez", "12345678-9", "987654321", true));

        Ruta r1 = new Ruta(null, "Ruta1", "Santiago", "Valparaíso", "PENDIENTE", 1L, t);
        Ruta r2 = new Ruta(null, "Ruta2", "Concepción", "Los Angeles", "PENDIENTE", 1L, t);
        rutaRepository.save(r1);
        rutaRepository.save(r2);

        mockMvc.perform(get("/api/transportistas/rutas"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].origen", is("Santiago")))
                .andExpect(jsonPath("$[1].origen", is("Concepción")));
    }

    @Test
    void testListarRutasPorSucursal() throws Exception {
        Transportista t = transportistaRepository.save(new Transportista(null, "Juan Pérez", "12345678-9", "987654321", true));

        Ruta r1 = new Ruta(null, "Ruta1", "Santiago", "Valparaíso", "PENDIENTE", 1L, t);
        Ruta r2 = new Ruta(null, "Ruta2", "Concepción", "Los Angeles", "PENDIENTE", 2L, t);
        rutaRepository.save(r1);
        rutaRepository.save(r2);

        mockMvc.perform(get("/api/transportistas/rutas")
                .param("sucursalId", "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].sucursalId", is(1)));
    }

    @Test
    void testListarRutasVacio() throws Exception {
        mockMvc.perform(get("/api/transportistas/rutas"))
                .andExpect(status().isNoContent());
    }

    @Test
    void testObtenerRuta() throws Exception {
        Transportista t = transportistaRepository.save(new Transportista(null, "Juan Pérez", "12345678-9", "987654321", true));
        Ruta ruta = new Ruta(null, "Ruta Test", "Rancagua", "San Fernando", "PENDIENTE", 1L, t);
        Ruta guardado = rutaRepository.save(ruta);

        mockMvc.perform(get("/api/transportistas/" + guardado.getId() + "/ruta"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.destino", is("San Fernando")))
                .andExpect(jsonPath("$.transportista.id", is(t.getId().intValue())));
    }

    @Test
    void testObtenerRutaInexistente() throws Exception {
        mockMvc.perform(get("/api/transportistas/9999/ruta"))
                .andExpect(status().isNotFound());
    }

    @Test
    void testActualizarRuta() throws Exception {
        Transportista t = transportistaRepository.save(new Transportista(null, "Juan Pérez", "12345678-9", "987654321", true));
        Ruta ruta = new Ruta(null, "Ruta Original", "Santiago", "Valparaíso", "PENDIENTE", 1L, t);
        Ruta guardado = rutaRepository.save(ruta);

        Ruta datos = new Ruta();
        datos.setOrigen("Rancagua");
        datos.setDestino("San Fernando");

        mockMvc.perform(put("/api/transportistas/" + guardado.getId() + "/ruta")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(datos)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.origen", is("Rancagua")))
                .andExpect(jsonPath("$.destino", is("San Fernando")))
                .andExpect(jsonPath("$.estado", is("PENDIENTE")));
    }

    @Test
    void testActualizarRutaEstadoNoPendiente() throws Exception {
        Transportista t = transportistaRepository.save(new Transportista(null, "Juan Pérez", "12345678-9", "987654321", true));
        Ruta ruta = new Ruta(null, "Ruta Original", "Santiago", "Valparaíso", "ACTIVA", 1L, t);
        Ruta guardado = rutaRepository.save(ruta);

        Ruta datos = new Ruta();
        datos.setOrigen("Rancagua");
        datos.setDestino("San Fernando");

        mockMvc.perform(put("/api/transportistas/" + guardado.getId() + "/ruta")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(datos)))
                .andExpect(status().isConflict());
    }

    @Test
    void testReactivarTransportistaEnBD() throws Exception {

        Transportista t = new Transportista(null, "Juan", "12345678-9", "911111111", true);
        Transportista guardado = transportistaRepository.save(t);
        

        guardado.setDisponible(false);
        transportistaRepository.save(guardado);

        mockMvc.perform(get("/api/transportistas"))
                .andExpect(status().isNoContent());


        mockMvc.perform(patch("/api/transportistas/" + guardado.getId() + "/reactivar"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.disponible", is(true)));

        mockMvc.perform(get("/api/transportistas"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].nombre", is("Juan")));
    }
}