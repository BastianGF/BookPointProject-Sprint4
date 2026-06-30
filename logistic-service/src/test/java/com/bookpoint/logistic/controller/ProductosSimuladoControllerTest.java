package com.bookpoint.logistic.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ProductoSimuladoController.class)
@ActiveProfiles("test")
class ProductoSimuladoControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    //TEST: LISTAR TODOS LOS PRODUCTOS

    @Test
    void testListarProductos() throws Exception {
        mockMvc.perform(get("/api/productos"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(4)))
                .andExpect(jsonPath("$[0].id", is(1)))
                .andExpect(jsonPath("$[0].nombre", is("Libro de Java")))
                .andExpect(jsonPath("$[1].id", is(2)))
                .andExpect(jsonPath("$[1].nombre", is("Cuaderno")))
                .andExpect(jsonPath("$[2].id", is(3)))
                .andExpect(jsonPath("$[2].nombre", is("Lápiz")))
                .andExpect(jsonPath("$[3].id", is(4)))
                .andExpect(jsonPath("$[3].nombre", is("Calculadora")));
    }

    //TEST: OBTENER PRODUCTO POR ID EXISTENTE

    @Test
    void testObtenerProductoPorIdExistente() throws Exception {
        mockMvc.perform(get("/api/productos/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.nombre", is("Libro de Java")))
                .andExpect(jsonPath("$.precio", is(25000)))
                .andExpect(jsonPath("$.stock", is(50)))
                .andExpect(jsonPath("$.categoria", is("Educación")));
    }

    @Test
    void testObtenerProductoPorIdExistente_2() throws Exception {
        mockMvc.perform(get("/api/productos/3"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(3)))
                .andExpect(jsonPath("$.nombre", is("Lápiz")))
                .andExpect(jsonPath("$.precio", is(1500)));
    }

    //TEST: OBTENER PRODUCTO POR ID INEXISTENTE

    @Test
    void testObtenerProductoPorIdInexistente() throws Exception {
        mockMvc.perform(get("/api/productos/999"))
                .andExpect(status().isNotFound());
    }

    @Test
    void testObtenerProductoPorIdInexistente_Negativo() throws Exception {
        mockMvc.perform(get("/api/productos/-1"))
                .andExpect(status().isNotFound());
    }
}