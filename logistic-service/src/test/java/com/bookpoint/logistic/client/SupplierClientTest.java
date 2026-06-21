package com.bookpoint.logistic.client;

import com.bookpoint.logistic.dto.ProveedorDTO;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.client.RestTemplate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class SupplierClientTest {

    @Mock
    private RestTemplate restTemplate;

    @InjectMocks
    private SupplierClient supplierClient;

    @Test
    void testObtenerProveedorPorIdExitoso() {
        ProveedorDTO proveedorMock = new ProveedorDTO();
        proveedorMock.setId(1L);
        proveedorMock.setNombre("Proveedor Test");

        when(restTemplate.getForObject(anyString(), eq(ProveedorDTO.class)))
                .thenReturn(proveedorMock);

        ProveedorDTO resultado = supplierClient.obtenerProveedorPorId(1L);

        assertThat(resultado).isNotNull();
        assertThat(resultado.getId()).isEqualTo(1L);
        assertThat(resultado.getNombre()).isEqualTo("Proveedor Test");
    }

    @Test
    void testObtenerProveedorPorIdError() {
        when(restTemplate.getForObject(anyString(), eq(ProveedorDTO.class)))
                .thenThrow(new RuntimeException("Error de conexión"));

        ProveedorDTO resultado = supplierClient.obtenerProveedorPorId(1L);

        assertThat(resultado).isNull();
    }
}
