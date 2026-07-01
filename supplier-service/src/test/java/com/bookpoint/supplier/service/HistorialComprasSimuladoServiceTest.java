package com.bookpoint.supplier.service;

import com.bookpoint.supplier.dto.HistorialCompraDTO;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
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

    private HistorialComprasSimuladoService service;

    @BeforeEach
    void setUp() {
        service = new HistorialComprasSimuladoService(objectMapperMock);
    }

    // ============================================================
    // TEST 1: CARGA EXITOSA (cubre logger.info)
    // ============================================================
    @Test
    void testCargarHistorial_Exitoso() throws Exception {
        String jsonValido = "[{\"id\":1,\"proveedorId\":1,\"montoTotal\":1000.0,\"descripcionCompra\":\"Compra test\"}]";
        InputStream inputStreamValido = new ByteArrayInputStream(jsonValido.getBytes());
        
        HistorialCompraDTO compraMock = new HistorialCompraDTO();
        compraMock.setId(1L);
        compraMock.setProveedorId(1L);
        compraMock.setMontoTotal(1000.0);
        compraMock.setDescripcionCompra("Compra test");
        
        List<HistorialCompraDTO> historialMock = List.of(compraMock);
        
        when(objectMapperMock.readValue(any(InputStream.class), any(com.fasterxml.jackson.core.type.TypeReference.class)))
                .thenReturn(historialMock);
        
        List<HistorialCompraDTO> resultado = service.obtenerPorProveedor(1L);
        
        assertThat(resultado).isNotNull();
        assertThat(resultado).hasSize(1);
        assertThat(resultado.get(0).getProveedorId()).isEqualTo(1L);
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

    @Test
    void testObtenerPorProveedor_HistorialVacio() {
        ReflectionTestUtils.setField(service, "historialCompras", List.of());

        List<HistorialCompraDTO> resultado = service.obtenerPorProveedor(999L);

        assertThat(resultado).isNotNull();
        assertThat(resultado).isEmpty();
    }
}