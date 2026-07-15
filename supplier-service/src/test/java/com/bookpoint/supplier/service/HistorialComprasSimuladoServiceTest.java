package com.bookpoint.supplier.service;

import com.bookpoint.supplier.dto.HistorialCompraDTO;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class HistorialComprasSimuladoServiceTest {

    @Mock
    private ObjectMapper objectMapperMock;

    @InjectMocks
    private HistorialComprasSimuladoService service;

    @Test
    void testCargarHistorial_ArchivoNoEncontrado() throws Exception {
        HistorialComprasSimuladoService serviceReal = new HistorialComprasSimuladoService();
        
        ReflectionTestUtils.setField(serviceReal, "historialCompras", null);
        
        List<HistorialCompraDTO> resultado = serviceReal.obtenerPorProveedor(1L);
        
        assertThat(resultado).isNotNull();
        assertThat(resultado).isEmpty();
    }

    @Test
    void testCargarHistorial_ErrorAlParsearJSON() throws Exception {
        String jsonInvalido = "{ esto no es un json valido }";
        InputStream inputStreamInvalido = new ByteArrayInputStream(jsonInvalido.getBytes());
        
        ObjectMapper mapperSpy = spy(new ObjectMapper());
        doThrow(new RuntimeException("Error al parsear JSON"))
                .when(mapperSpy)
                .readValue(any(InputStream.class), any(com.fasterxml.jackson.core.type.TypeReference.class));

        HistorialComprasSimuladoService serviceConMapper = new HistorialComprasSimuladoService(mapperSpy);

        List<HistorialCompraDTO> resultado = serviceConMapper.obtenerPorProveedor(1L);
        
        assertThat(resultado).isNotNull();
        assertThat(resultado).isEmpty();
    }

    @Test
    void testRegistrarCompra() {
        HistorialCompraDTO nuevaCompra = new HistorialCompraDTO();
        nuevaCompra.setProveedorId(1L);
        nuevaCompra.setMontoTotal(1000.0);
        nuevaCompra.setDescripcionCompra("Compra test");

        HistorialCompraDTO resultado = service.registrarCompra(nuevaCompra);

        assertThat(resultado).isNotNull();
        assertThat(resultado.getId()).isNotNull();
        assertThat(resultado.getId()).isEqualTo(1L);
        assertThat(resultado.getProveedorId()).isEqualTo(1L);
        assertThat(resultado.getMontoTotal()).isEqualTo(1000.0);
    }

    @Test
    void testObtenerPorProveedorExitoso() {
        // 1. Crear un DTO
        HistorialCompraDTO compra = new HistorialCompraDTO();
        compra.setId(1L);
        compra.setProveedorId(1L);
        compra.setMontoTotal(1000.0);
        compra.setDescripcionCompra("Compra test");

        ReflectionTestUtils.setField(service, "historialCompras", List.of(compra));

        List<HistorialCompraDTO> resultado = service.obtenerPorProveedor(1L);

        assertThat(resultado).hasSize(1);
        assertThat(resultado.get(0).getMontoTotal()).isEqualTo(1000.0);
    }
}