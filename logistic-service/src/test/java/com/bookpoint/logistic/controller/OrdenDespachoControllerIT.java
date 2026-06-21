package com.bookpoint.logistic.controller;

import com.bookpoint.logistic.dto.ProveedorDTO;
import com.bookpoint.logistic.model.OrdenDespacho;
import com.bookpoint.logistic.model.Transportista;
import com.bookpoint.logistic.repository.EnvioRepository;
import com.bookpoint.logistic.repository.IncidenciaRepository;
import com.bookpoint.logistic.repository.OrdenDespachoRepository;
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
import org.springframework.web.client.RestTemplate;

import java.util.Date;

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class OrdenDespachoControllerIT {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private OrdenDespachoRepository ordenDespachoRepository;
    @Autowired
    private TransportistaRepository transportistaRepository;
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private EnvioRepository envioRepository;
    @Autowired
    private IncidenciaRepository incidenciaRepository;

    @MockBean
    private RestTemplate restTemplate;

    @BeforeEach
    void cleanDb() {
        incidenciaRepository.deleteAll();
        envioRepository.deleteAll();
        ordenDespachoRepository.deleteAll();
        transportistaRepository.deleteAll();
    }
    
    private OrdenDespacho crearOrdenBase() {
        OrdenDespacho orden = new OrdenDespacho();
        orden.setFechaCreacion(new Date());
        orden.setEstadoDespacho("PENDIENTE");
        orden.setObservacionDespacho("Test IT");
        orden.setTipo("DOMICILIO");
        orden.setSucursalDestinoId(1L);
        orden.setUbicacionBodega("Bodega Central");
        orden.setCantidadSolicitada(10);
        orden.setCantidadConfirmada(10);
        orden.setCantidadFinal(10);
        return orden;
    }

    // Si, otro metodo para crear ordenes validas, que.
    private OrdenDespacho crearOrdenValida() {
        OrdenDespacho orden = new OrdenDespacho();
        orden.setTipo("DOMICILIO");
        orden.setCantidadSolicitada(10);
        orden.setEstadoDespacho("PENDIENTE");
        orden.setFechaCreacion(new Date());
        orden.setCantidadConfirmada(10);
        orden.setCantidadFinal(10);
        orden.setUbicacionBodega("Bodega Test IT");
        orden.setObservacionDespacho("Test observación IT");
        orden.setSucursalDestinoId(1L);
        return orden;
    }

    //  TEST PARA CREAR ORDEN CON PROVEEDOR EXISTENTE
    @Test
    void testCrearOrdenConProveedorExistente() throws Exception {
        // Mock del proveedor
        ProveedorDTO proveedorMock = new ProveedorDTO();
        proveedorMock.setId(1L);
        proveedorMock.setNombre("Proveedor Test");
        proveedorMock.setRut("12345678-9");

        when(restTemplate.getForObject(anyString(), eq(ProveedorDTO.class)))
                .thenReturn(proveedorMock);

        OrdenDespacho nueva = crearOrdenValida();

        mockMvc.perform(post("/api/ordenes-despacho")
                .param("proveedorId", "1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(nueva)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.estadoDespacho", is("PENDIENTE")))
                .andExpect(jsonPath("$.observacionDespacho").value(containsString("Proveedor Test")));
    }

    // TEST PARA CREAR ORDEN CON PROVEEDOR NO ENCONTRADO
    @Test
    void testCrearOrdenConProveedorNoEncontrado() throws Exception {
        // Mock del RestTemplate retornando null (proveedor no encontrado)
        when(restTemplate.getForObject(anyString(), eq(ProveedorDTO.class)))
                .thenReturn(null);
        OrdenDespacho nueva = crearOrdenValida();

        mockMvc.perform(post("/api/ordenes-despacho")
                .param("proveedorId", "9999")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(nueva)))
                .andExpect(status().isNotFound());
    }

    // TEST PARA GET /api/ordenes-despacho 
    @Test
    void testListarOrdenes() throws Exception {
        ordenDespachoRepository.save(crearOrdenBase());
        ordenDespachoRepository.save(crearOrdenBase());

        mockMvc.perform(get("/api/ordenes-despacho"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].estadoDespacho", is("PENDIENTE")));
    }

    // TEST PARA GET /api/ordenes-despacho/{id}
    @Test
    void testBuscarPorId() throws Exception {
        OrdenDespacho orden = crearOrdenBase();
        OrdenDespacho guardado = ordenDespachoRepository.save(orden);

        mockMvc.perform(get("/api/ordenes-despacho/" + guardado.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(guardado.getId().intValue())))
                .andExpect(jsonPath("$.estadoDespacho", is("PENDIENTE")));
    }

    @Test
    void testBuscarPorIdInexistente() throws Exception {
        mockMvc.perform(get("/api/ordenes-despacho/9999"))
                .andExpect(status().isNotFound());
    }

    // TEST PARA PUT /api/ordenes-despacho/{id}/confirmar 
    @Test
    void testConfirmarDespacho() throws Exception {
        OrdenDespacho orden = crearOrdenBase();
        OrdenDespacho guardado = ordenDespachoRepository.save(orden);

        mockMvc.perform(put("/api/ordenes-despacho/" + guardado.getId() + "/confirmar"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.estadoDespacho", is("CONFIRMADO")));
    }

    @Test
    void testConfirmarDespachoInexistente() throws Exception {
        mockMvc.perform(put("/api/ordenes-despacho/9999/confirmar"))
                .andExpect(status().isNotFound());
    }

    // TEST PARA PUT /api/ordenes-despacho/{id}/cancelar
    @Test
    void testCancelarDespacho() throws Exception {
        OrdenDespacho orden = crearOrdenBase();
        OrdenDespacho guardado = ordenDespachoRepository.save(orden);

        mockMvc.perform(put("/api/ordenes-despacho/" + guardado.getId() + "/cancelar"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.estadoDespacho", is("CANCELADO")));
    }

    @Test
    void testCancelarDespachoInexistente() throws Exception {
        mockMvc.perform(put("/api/ordenes-despacho/9999/cancelar"))
                .andExpect(status().isNotFound());
    }

    // TEST PARA PUT /api/ordenes-despacho/{ordenId}/preparar-mercaderia
    @Test
    void testPrepararMercaderia() throws Exception {
        OrdenDespacho orden = crearOrdenBase();
        OrdenDespacho guardado = ordenDespachoRepository.save(orden);

        mockMvc.perform(put("/api/ordenes-despacho/" + guardado.getId() + "/preparar-mercaderia")
                .param("ubicacionBodega", "Bodega Norte")
                .param("cantidadConfirmada", "15"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.estadoDespacho", is("PREPARADO")))
                .andExpect(jsonPath("$.ubicacionBodega", is("Bodega Norte")))
                .andExpect(jsonPath("$.cantidadConfirmada", is(15)));
    }

    @Test
    void testPrepararMercaderiaOrdenNoExistente() throws Exception {
        mockMvc.perform(put("/api/ordenes-despacho/9999/preparar-mercaderia")
                .param("ubicacionBodega", "Bodega Norte")
                .param("cantidadConfirmada", "15"))
                .andExpect(status().isNotFound());
    }

    // TEST PARA PUT /api/ordenes-despacho/{ordenId}/registrar-salida-bodega
    @Test
    void testRegistrarSalidaBodega() throws Exception {
        // Crear Transportista
        Transportista transportista = new Transportista(null, "Transportista Test", "12345678-9", "987654321", true);
        Transportista tGuardado = transportistaRepository.save(transportista);

        // Crear Orden en estado PREPARADO
        OrdenDespacho orden = crearOrdenBase();
        orden.setEstadoDespacho("PREPARADO");
        OrdenDespacho oGuardado = ordenDespachoRepository.save(orden);

        mockMvc.perform(put("/api/ordenes-despacho/" + oGuardado.getId() + "/registrar-salida-bodega")
                .param("transportistaId", tGuardado.getId().toString())
                .param("cantidadFinal", "20"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.estadoDespacho", is("DESPACHADO")))
                .andExpect(jsonPath("$.cantidadFinal", is(20)))
                .andExpect(jsonPath("$.transportista.id", is(tGuardado.getId().intValue())));
    }

    @Test
    void testRegistrarSalidaBodegaTransportistaNoExiste() throws Exception {
        OrdenDespacho orden = crearOrdenBase();
        orden.setEstadoDespacho("PREPARADO");
        OrdenDespacho oGuardado = ordenDespachoRepository.save(orden);

        mockMvc.perform(put("/api/ordenes-despacho/" + oGuardado.getId() + "/registrar-salida-bodega")
                .param("transportistaId", "9999")
                .param("cantidadFinal", "20"))
                .andExpect(status().isNotFound());
    }

    @Test
    void testRegistrarSalidaBodegaOrdenNoExistente() throws Exception {
        mockMvc.perform(put("/api/ordenes-despacho/9999/registrar-salida-bodega")
                .param("transportistaId", "1")
                .param("cantidadFinal", "20"))
                .andExpect(status().isNotFound());
    }

    @Test
    void testRegistrarSalidaBodegaEstadoInvalido() throws Exception {
        OrdenDespacho orden = crearOrdenBase();
        OrdenDespacho oGuardado = ordenDespachoRepository.save(orden);
        mockMvc.perform(put("/api/ordenes-despacho/" + oGuardado.getId() + "/registrar-salida-bodega")
                .param("transportistaId", "1")
                .param("cantidadFinal", "20"))
                .andExpect(status().isNotFound());
    }
}
