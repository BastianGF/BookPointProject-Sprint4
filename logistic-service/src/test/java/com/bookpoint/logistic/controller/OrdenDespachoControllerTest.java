package com.bookpoint.logistic.controller;

import com.bookpoint.logistic.model.OrdenDespacho;
import com.bookpoint.logistic.service.OrdenDespachoService;
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
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(OrdenDespachoController.class)
@ActiveProfiles("test")
class OrdenDespachoControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private OrdenDespachoService ordenDespachoService;

    @Autowired
    private ObjectMapper objectMapper;

    private OrdenDespacho ordenBase;

    @BeforeEach
    void setUp() {
        Mockito.reset(ordenDespachoService);
        ordenBase = new OrdenDespacho();
        ordenBase.setId(1L);
        ordenBase.setFechaCreacion(new Date());
        ordenBase.setEstadoDespacho("PENDIENTE");
        ordenBase.setObservacionDespacho("Test");
        ordenBase.setTipo("DOMICILIO");
        ordenBase.setCantidadSolicitada(10);
    }
    // Me aburri de agregar los datos para que espere 409, metodo auxiliar para solucionar eso y que no se repita codigo
    private OrdenDespacho crearOrdenValida() {
        OrdenDespacho orden = new OrdenDespacho();
        orden.setTipo("DOMICILIO");
        orden.setCantidadSolicitada(10);
        orden.setEstadoDespacho("PENDIENTE");
        orden.setFechaCreacion(new Date());
        orden.setCantidadConfirmada(10);
        orden.setCantidadFinal(10);
        orden.setUbicacionBodega("Bodega Test");
        orden.setObservacionDespacho("Test observación");
        orden.setSucursalDestinoId(1L);
        return orden;
    }

    // TEST PARA GET /api/ordenes-despacho
    @Test
    void testListarVacio() throws Exception {
        Mockito.when(ordenDespachoService.listarOrdenes()).thenReturn(new ArrayList<>());

        mockMvc.perform(get("/api/ordenes-despacho"))
                .andExpect(status().isNoContent());
    }

    @Test
    void testListarConDatos() throws Exception {
        OrdenDespacho orden2 = new OrdenDespacho();
        orden2.setId(2L);
        orden2.setEstadoDespacho("CONFIRMADO");

        Mockito.when(ordenDespachoService.listarOrdenes())
                .thenReturn(Arrays.asList(ordenBase, orden2));

        mockMvc.perform(get("/api/ordenes-despacho"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].id", is(1)))
                .andExpect(jsonPath("$[0].estadoDespacho", is("PENDIENTE")))
                .andExpect(jsonPath("$[1].id", is(2)))
                .andExpect(jsonPath("$[1].estadoDespacho", is("CONFIRMADO")));
    }

    // TEST PARA GET /api/ordenes-despacho/{id} 
    @Test
    void testBuscarPorIdExistente() throws Exception {
        Mockito.when(ordenDespachoService.buscarPorId(1L))
                .thenReturn(Optional.of(ordenBase));

        mockMvc.perform(get("/api/ordenes-despacho/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.estadoDespacho", is("PENDIENTE")));
    }

    @Test
    void testBuscarPorIdInexistente() throws Exception {
        Mockito.when(ordenDespachoService.buscarPorId(9999L))
                .thenReturn(Optional.empty());

        mockMvc.perform(get("/api/ordenes-despacho/9999"))
                .andExpect(status().isNotFound());
    }

    // TEST PARA POST /api/ordenes-despacho?proveedorId= (Editado por concepto de error por objeto impleto, el @Valid lo rechazaba)

    @Test
    void testCrearOrdenValida() throws Exception {
        OrdenDespacho nueva = new OrdenDespacho();
        nueva.setTipo("DOMICILIO");
        nueva.setCantidadSolicitada(10);
        nueva.setEstadoDespacho("PENDIENTE");
        nueva.setFechaCreacion(new Date());
        nueva.setSucursalDestinoId(1L);
        nueva.setCantidadConfirmada(10);
        nueva.setCantidadFinal(10);
        nueva.setUbicacionBodega("Bodega Test");

        OrdenDespacho guardada = new OrdenDespacho();
        guardada.setId(1L);
        guardada.setEstadoDespacho("PENDIENTE");
        guardada.setFechaCreacion(new Date());
        guardada.setTipo("DOMICILIO");
        guardada.setCantidadSolicitada(10);
        guardada.setCantidadConfirmada(10);
        guardada.setCantidadFinal(10);

        Mockito.when(ordenDespachoService.crearOrden(any(OrdenDespacho.class), eq(1L)))
                .thenReturn(guardada);

        mockMvc.perform(post("/api/ordenes-despacho")
                .param("proveedorId", "1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(nueva)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.estadoDespacho", is("PENDIENTE")));
    }

    @Test
    void testCrearOrdenProveedorNoEncontrado() throws Exception {
        OrdenDespacho nueva = new OrdenDespacho();
        nueva.setTipo("DOMICILIO");
        nueva.setCantidadSolicitada(10);
        nueva.setEstadoDespacho("PENDIENTE");      
        nueva.setFechaCreacion(new Date());
        nueva.setCantidadConfirmada(10);  
        nueva.setCantidadFinal(10);                   
        nueva.setUbicacionBodega("Bodega Test"); 

        Mockito.when(ordenDespachoService.crearOrden(any(OrdenDespacho.class), eq(9999L)))
                .thenThrow(new RuntimeException("Proveedor no encontrado"));

        mockMvc.perform(post("/api/ordenes-despacho")
                .param("proveedorId", "9999")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(nueva))) // AHORA SI UN OBJETO VALIDO LA MADRE MIA QUE ME PARIO
                .andExpect(status().isNotFound());
    }

    @Test
    void testCrearOrdenConErrorInterno() throws Exception {
        OrdenDespacho nueva = crearOrdenValida();

        Mockito.when(ordenDespachoService.crearOrden(any(OrdenDespacho.class), eq(1L)))
                .thenThrow(new RuntimeException("Error inesperado"));

        mockMvc.perform(post("/api/ordenes-despacho")
                .param("proveedorId", "1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(nueva)))
                .andExpect(status().isConflict());
    }
    
    // TEST PARA PUT /api/ordenes-despacho/{id}/confirmar 
    @Test
    void testConfirmarExistente() throws Exception {
        OrdenDespacho confirmada = new OrdenDespacho();
        confirmada.setId(1L);
        confirmada.setEstadoDespacho("CONFIRMADO");

        Mockito.when(ordenDespachoService.confirmarDespacho(1L))
                .thenReturn(confirmada);

        mockMvc.perform(put("/api/ordenes-despacho/1/confirmar"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.estadoDespacho", is("CONFIRMADO")));
    }

    @Test
    void testConfirmarInexistente() throws Exception {
        Mockito.when(ordenDespachoService.confirmarDespacho(9999L))
                .thenReturn(null);

        mockMvc.perform(put("/api/ordenes-despacho/9999/confirmar"))
                .andExpect(status().isNotFound());
    }

    // TEST PARA PUT /api/ordenes-despacho/{id}/cancelar 
    @Test
    void testCancelarExistente() throws Exception {
        OrdenDespacho cancelada = new OrdenDespacho();
        cancelada.setId(1L);
        cancelada.setEstadoDespacho("CANCELADO");
        Mockito.when(ordenDespachoService.cancelarDespacho(1L))
                .thenReturn(cancelada);
        mockMvc.perform(put("/api/ordenes-despacho/1/cancelar"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.estadoDespacho", is("CANCELADO")));
    }

    @Test
    void testCancelarInexistente() throws Exception {
        Mockito.when(ordenDespachoService.cancelarDespacho(9999L))
                .thenReturn(null);

        mockMvc.perform(put("/api/ordenes-despacho/9999/cancelar"))
                .andExpect(status().isNotFound());
    }

    // TEST PARA PUT /api/ordenes-despacho/{ordenId}/preparar-mercaderia
    @Test
    void testPrepararMercaderiaValido() throws Exception {
        OrdenDespacho preparada = new OrdenDespacho();
        preparada.setId(1L);
        preparada.setEstadoDespacho("PREPARADO");
        preparada.setUbicacionBodega("Bodega Norte");
        preparada.setCantidadConfirmada(15);

        Mockito.when(ordenDespachoService.prepararMercaderia(eq(1L), eq("Bodega Norte"), eq(15)))
                .thenReturn(preparada);

        mockMvc.perform(put("/api/ordenes-despacho/1/preparar-mercaderia")
                .param("ubicacionBodega", "Bodega Norte")
                .param("cantidadConfirmada", "15"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.estadoDespacho", is("PREPARADO")))
                .andExpect(jsonPath("$.ubicacionBodega", is("Bodega Norte")))
                .andExpect(jsonPath("$.cantidadConfirmada", is(15)));
    }

    // TES PARA PUT /api/ordenes-despacho/{ordenId}/registrar-salida-bodega
    @Test
    void testRegistrarSalidaBodegaValido() throws Exception {
        OrdenDespacho despachado = new OrdenDespacho();
        despachado.setId(1L);
        despachado.setEstadoDespacho("DESPACHADO");
        despachado.setCantidadFinal(20);

        Mockito.when(ordenDespachoService.registrarSalidaBodega(eq(1L), eq(1L), eq(20)))
                .thenReturn(despachado);

        mockMvc.perform(put("/api/ordenes-despacho/1/registrar-salida-bodega")
                .param("transportistaId", "1")
                .param("cantidadFinal", "20"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.estadoDespacho", is("DESPACHADO")))
                .andExpect(jsonPath("$.cantidadFinal", is(20)));
    }

        @Test
        void testCrearOrdenConErrorInesperado() throws Exception {
        OrdenDespacho nueva = crearOrdenValida();

        when(ordenDespachoService.crearOrden(any(OrdenDespacho.class), eq(1L)))
                .thenThrow(new RuntimeException("Error de base de datos"));

        mockMvc.perform(post("/api/ordenes-despacho")
                .param("proveedorId", "1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(nueva)))
                .andExpect(status().isConflict());  // status 409 less go
    }
}