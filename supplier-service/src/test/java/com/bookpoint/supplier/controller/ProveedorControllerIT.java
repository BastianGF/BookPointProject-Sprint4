package com.bookpoint.supplier.controller;

import com.bookpoint.supplier.model.HistorialCompras;
import com.bookpoint.supplier.model.Proveedor;
import com.bookpoint.supplier.repository.HistorialComprasRepository;
import com.bookpoint.supplier.repository.ProveedorRepository;
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
class ProveedorControllerIT {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ProveedorRepository proveedorRepository;

    @Autowired
    private HistorialComprasRepository historialComprasRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @BeforeEach
    void cleanDb() {
        historialComprasRepository.deleteAll();
        proveedorRepository.deleteAll();
    }

    private Proveedor crearProveedorBase() {
        return crearProveedorBase("12345678-9");
    }

    private Proveedor crearProveedorBase(String rut) {
        Proveedor proveedor = new Proveedor();
        proveedor.setNombre("Proveedor Test");
        proveedor.setRut(rut);
        proveedor.setTelefono("987654321");
        proveedor.setEmail("test@proveedor.com");
        proveedor.setActivo(true);
        proveedor.setPuntaje(85);
        return proveedor;
    }

    private Proveedor crearProveedorValido() {
        Proveedor proveedor = new Proveedor();
        proveedor.setNombre("Nuevo Proveedor IT");
        proveedor.setRut("87654321-0");
        proveedor.setTelefono("912345678");
        proveedor.setEmail("it@proveedor.com");
        proveedor.setActivo(true);
        proveedor.setPuntaje(90);
        return proveedor;
    }

    @Test
    void testCrearProveedor() throws Exception {
        Proveedor nuevo = crearProveedorValido();

        mockMvc.perform(post("/api/proveedores")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(nuevo)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.nombre", is("Nuevo Proveedor IT")))
                .andExpect(jsonPath("$.activo", is(true)));
    }

    @Test
    void testListarProveedores() throws Exception {
        proveedorRepository.save(crearProveedorBase("11111111-1"));
        proveedorRepository.save(crearProveedorBase("22222222-2"));

        mockMvc.perform(get("/api/proveedores"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(2)))
                .andExpect(jsonPath("$.content[0].nombre", is("Proveedor Test")));
    }

    @Test
    void testListarProveedoresVacio() throws Exception {
        mockMvc.perform(get("/api/proveedores"))
                .andExpect(status().isNoContent());
    }

    @Test
    void testBuscarProveedorPorId() throws Exception {
        Proveedor proveedor = crearProveedorBase();
        Proveedor guardado = proveedorRepository.save(proveedor);

        mockMvc.perform(get("/api/proveedores/" + guardado.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(guardado.getId().intValue())))
                .andExpect(jsonPath("$.nombre", is("Proveedor Test")));
    }

    @Test
    void testBuscarProveedorPorIdInexistente() throws Exception {
        mockMvc.perform(get("/api/proveedores/9999"))
                .andExpect(status().isNotFound());
    }

    @Test
    void testEditarProveedor() throws Exception {
        Proveedor proveedor = crearProveedorBase();
        Proveedor guardado = proveedorRepository.save(proveedor);

        Proveedor datos = new Proveedor();
        datos.setNombre("Modificado IT");
        datos.setRut("12345678-9");
        datos.setTelefono("987654321");
        datos.setEmail("modificado@proveedor.com");

        mockMvc.perform(put("/api/proveedores/" + guardado.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(datos)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nombre", is("Modificado IT")))
                .andExpect(jsonPath("$.email", is("modificado@proveedor.com")));
    }

    @Test
    void testEliminarProveedor() throws Exception {
        Proveedor proveedor = crearProveedorBase();
        Proveedor guardado = proveedorRepository.save(proveedor);

        mockMvc.perform(delete("/api/proveedores/" + guardado.getId()))
                .andExpect(status().isNoContent());
    }

    @Test
    void testObtenerHistorialCompras() throws Exception {
        Proveedor proveedor = crearProveedorBase();
        Proveedor guardado = proveedorRepository.save(proveedor);

        HistorialCompras historial = new HistorialCompras();
        historial.setFechaCompra(new Date());
        historial.setMontoTotal(1000.0);
        historial.setDescripcionCompra("Compra de integración");
        historial.setProveedor(guardado);
        historialComprasRepository.save(historial);

        mockMvc.perform(get("/api/proveedores/" + guardado.getId() + "/historial-compras"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].montoTotal", is(1000.0)))
                .andExpect(jsonPath("$[0].descripcionCompra", is("Compra de integración")));
    }

    @Test
    void testRegistrarCompraAProveedor() throws Exception {
        Proveedor proveedor = crearProveedorBase("11111111-1");
        Proveedor guardado = proveedorRepository.save(proveedor);

        HistorialCompras nueva = new HistorialCompras();
        nueva.setFechaCompra(new Date());
        nueva.setMontoTotal(2000.0);
        nueva.setDescripcionCompra("Nueva compra IT");

        mockMvc.perform(post("/api/proveedores/" + guardado.getId() + "/historial-compras")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(nueva)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.montoTotal", is(2000.0)))
                .andExpect(jsonPath("$.descripcionCompra", is("Nueva compra IT")))
                .andExpect(jsonPath("$.proveedor.id", is(guardado.getId().intValue())));
    }

    @Test
    void testRegistrarCompraAProveedorInexistente() throws Exception {
        HistorialCompras nueva = new HistorialCompras();
        nueva.setMontoTotal(2000.0);
        nueva.setDescripcionCompra("Nueva compra IT");

        mockMvc.perform(post("/api/proveedores/9999/historial-compras")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(nueva)))
                .andExpect(status().isNotFound());
    }
}