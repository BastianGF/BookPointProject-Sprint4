package com.bookpoint.supplier.controller;

import com.bookpoint.supplier.dto.HistorialCompraDTO;
import com.bookpoint.supplier.model.HistorialCompras;
import com.bookpoint.supplier.model.Proveedor;
import com.bookpoint.supplier.service.ProveedorService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.Optional;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ProveedorController.class)
@ActiveProfiles("test")
class ProveedorControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ProveedorService proveedorService;

    @Autowired
    private ObjectMapper objectMapper;

    private Proveedor proveedorBase;
    private HistorialCompras historialBase;

    @BeforeEach
    void setUp() {
        Mockito.reset(proveedorService);

        proveedorBase = new Proveedor();
        proveedorBase.setId(1L);
        proveedorBase.setNombre("Proveedor Test");
        proveedorBase.setRut("12345678-9");
        proveedorBase.setTelefono("987654321");
        proveedorBase.setEmail("test@proveedor.com");
        proveedorBase.setActivo(true);
        proveedorBase.setPuntaje(85);

        historialBase = new HistorialCompras();
        historialBase.setId(1L);
        historialBase.setFechaCompra(new Date());
        historialBase.setMontoTotal(1000.0);
        historialBase.setDescripcionCompra("Compra de prueba");
    }

    private Proveedor crearProveedorValido() {
        Proveedor proveedor = new Proveedor();
        proveedor.setNombre("Nuevo Proveedor");
        proveedor.setRut("87654321-0");
        proveedor.setTelefono("912345678");
        proveedor.setEmail("nuevo@proveedor.com");
        proveedor.setActivo(true);
        proveedor.setPuntaje(90);
        return proveedor;
    }

    private HistorialCompras crearHistorialValido() {
        HistorialCompras historial = new HistorialCompras();
        historial.setMontoTotal(1500.0);
        historial.setDescripcionCompra("Nueva compra");
        return historial;
    }

    // TESTS PARA CRUD DE PROVEEDOR

    @Test
    void testListarVacio() throws Exception {
        Page<Proveedor> pagina = new PageImpl<>(new ArrayList<>());
        when(proveedorService.listarProveedores(any(PageRequest.class))).thenReturn(pagina);

        mockMvc.perform(get("/api/proveedores"))
                .andExpect(status().isNoContent());
    }

    @Test
    void testListarConDatos() throws Exception {
        List<Proveedor> proveedores = Arrays.asList(proveedorBase);
        Page<Proveedor> pagina = new PageImpl<>(proveedores);
        when(proveedorService.listarProveedores(any(PageRequest.class))).thenReturn(pagina);

        mockMvc.perform(get("/api/proveedores"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(1)))
                .andExpect(jsonPath("$.content[0].nombre", is("Proveedor Test")));
    }

    @Test
    void testBuscarPorIdExistente() throws Exception {
        when(proveedorService.buscarPorId(1L)).thenReturn(Optional.of(proveedorBase));

        mockMvc.perform(get("/api/proveedores/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.nombre", is("Proveedor Test")));
    }

    @Test
    void testBuscarPorIdInexistente() throws Exception {
        when(proveedorService.buscarPorId(9999L)).thenReturn(Optional.empty());

        mockMvc.perform(get("/api/proveedores/9999"))
                .andExpect(status().isNotFound());
    }

    @Test
    void testRegistrarValido() throws Exception {
        Proveedor nuevo = crearProveedorValido();
        Proveedor guardado = new Proveedor();
        guardado.setId(1L);
        guardado.setNombre("Nuevo Proveedor");

        when(proveedorService.registrarProveedor(any(Proveedor.class))).thenReturn(guardado);

        mockMvc.perform(post("/api/proveedores")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(nuevo)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.nombre", is("Nuevo Proveedor")));
    }

    @Test
    void testRegistrarInvalido() throws Exception {
        Proveedor invalido = new Proveedor(); // Sin campos obligatorios

        mockMvc.perform(post("/api/proveedores")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalido)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testRegistrarConError() throws Exception {
        Proveedor nuevo = crearProveedorValido();

        when(proveedorService.registrarProveedor(any(Proveedor.class)))
                .thenThrow(new RuntimeException("Error al registrar"));

        mockMvc.perform(post("/api/proveedores")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(nuevo)))
                .andExpect(status().isConflict());
    }

    @Test
    void testEditarExistente() throws Exception {
        Proveedor datos = crearProveedorValido();
        when(proveedorService.editarProveedor(eq(1L), any(Proveedor.class))).thenReturn(proveedorBase);

        mockMvc.perform(put("/api/proveedores/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(datos)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nombre", is("Proveedor Test")));
    }

    @Test
    void testEditarInexistente() throws Exception {
        when(proveedorService.editarProveedor(eq(9999L), any(Proveedor.class))).thenReturn(null);

        mockMvc.perform(put("/api/proveedores/9999")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(new Proveedor())))
                .andExpect(status().isNotFound());
    }

    @Test
    void testEliminarExistente() throws Exception {
        when(proveedorService.eliminarProveedor(1L)).thenReturn(true);

        mockMvc.perform(delete("/api/proveedores/1"))
                .andExpect(status().isNoContent());
    }

    @Test
    void testEliminarInexistente() throws Exception {
        when(proveedorService.eliminarProveedor(9999L)).thenReturn(false);

        mockMvc.perform(delete("/api/proveedores/9999"))
                .andExpect(status().isNotFound());
    }

    // TESTS DE HISTORIAL DE COMPRAS

    @Test
        void testObtenerHistorialProveedorExistente() throws Exception {
        // Con esto ya no usa la entidad, sino que uso el DTO
        HistorialCompraDTO historialDTO = new HistorialCompraDTO();
        historialDTO.setId(1L);
        historialDTO.setMontoTotal(1000.0);
        historialDTO.setDescripcionCompra("Compra test");
        historialDTO.setProveedorId(1L);

        when(proveedorService.obtenerHistorialComprasPorProveedor(1L))
                .thenReturn(Arrays.asList(historialDTO));

        mockMvc.perform(get("/api/proveedores/1/historial-compras"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].montoTotal", is(1000.0)));
        }

    @Test
    void testObtenerHistorialProveedorInexistente() throws Exception {
        when(proveedorService.obtenerHistorialComprasPorProveedor(9999L))
                .thenThrow(new RuntimeException("Proveedor con ID 9999 no existe"));

        mockMvc.perform(get("/api/proveedores/9999/historial-compras"))
                .andExpect(status().isNotFound());
    }

    @Test
    void testRegistrarCompraAProveedor() throws Exception {
        HistorialCompraDTO nueva = new HistorialCompraDTO();
        nueva.setMontoTotal(1000.0);
        nueva.setDescripcionCompra("Compra test");
        
        HistorialCompraDTO guardada = new HistorialCompraDTO();
        guardada.setId(1L);
        guardada.setMontoTotal(1000.0);

        when(proveedorService.registrarCompraAProveedor(eq(1L), any(HistorialCompraDTO.class))).thenReturn(guardada);

        mockMvc.perform(post("/api/proveedores/1/historial-compras")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(nueva)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.montoTotal", is(1000.0)));
    }

    @Test
        void testRegistrarCompraAProveedorInexistente() throws Exception {
        HistorialCompraDTO nueva = new HistorialCompraDTO();
        nueva.setMontoTotal(1500.0);
        nueva.setDescripcionCompra("Nueva compra");

        when(proveedorService.registrarCompraAProveedor(eq(9999L), any(HistorialCompraDTO.class)))
                .thenThrow(new RuntimeException("Proveedor con ID 9999 no existe"));

        mockMvc.perform(post("/api/proveedores/9999/historial-compras")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(nueva)))
                .andExpect(status().isNotFound());
        }


}