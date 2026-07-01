package com.bookpoint.supplier.service;

import com.bookpoint.supplier.dto.HistorialCompraDTO;
import com.bookpoint.supplier.model.HistorialCompras;
import com.bookpoint.supplier.model.Proveedor;
import com.bookpoint.supplier.repository.HistorialComprasRepository;
import com.bookpoint.supplier.repository.ProveedorRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

class ProveedorServiceTest {

    @Mock
    private ProveedorRepository proveedorRepository;

    @Mock
    private HistorialComprasRepository historialComprasRepository;

    @Mock
    private HistorialComprasSimuladoService historialSimuladoService;

    @InjectMocks
    private ProveedorService proveedorService;

    private Proveedor proveedorBase;
    private HistorialCompras historialBase;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

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
        historialBase.setProveedor(proveedorBase);
    }

    // TESTS PARA CRUD DE PROVEEDOR

    @Test
    void testListarProveedoresPaginado() {
        List<Proveedor> proveedores = new ArrayList<>();
        proveedores.add(proveedorBase);
        Page<Proveedor> page = new PageImpl<>(proveedores);

        when(proveedorRepository.findByActivo(anyBoolean(), any(PageRequest.class))).thenReturn(page);

        Page<Proveedor> resultado = proveedorService.listarProveedores(PageRequest.of(0, 10));

        assertThat(resultado).isNotNull();
        assertThat(resultado.getContent()).hasSize(1);
        verify(proveedorRepository).findByActivo(true, PageRequest.of(0, 10));
    }

    @Test
    void testListarProveedoresSinPaginacion() {
        List<Proveedor> proveedores = new ArrayList<>();
        proveedores.add(proveedorBase);
        when(proveedorRepository.findByActivo(true)).thenReturn(proveedores);

        List<Proveedor> resultado = proveedorService.listarProveedores();

        assertThat(resultado).hasSize(1);
        verify(proveedorRepository).findByActivo(true);
    }

    @Test
    void testBuscarPorIdExistente() {
        when(proveedorRepository.findById(1L)).thenReturn(Optional.of(proveedorBase));

        Optional<Proveedor> resultado = proveedorService.buscarPorId(1L);

        assertThat(resultado).isPresent();
        assertThat(resultado.get().getId()).isEqualTo(1L);
        verify(proveedorRepository).findById(1L);
    }

    @Test
    void testBuscarPorIdInexistente() {
        when(proveedorRepository.findById(9999L)).thenReturn(Optional.empty());

        Optional<Proveedor> resultado = proveedorService.buscarPorId(9999L);

        assertThat(resultado).isEmpty();
        verify(proveedorRepository).findById(9999L);
    }

    @Test
    void testRegistrarProveedor() {
        when(proveedorRepository.save(any(Proveedor.class))).thenReturn(proveedorBase);

        Proveedor resultado = proveedorService.registrarProveedor(proveedorBase);

        assertThat(resultado).isNotNull();
        assertThat(resultado.getId()).isEqualTo(1L);
        verify(proveedorRepository).save(any(Proveedor.class));
    }

    @Test
    void testEditarProveedorExistente() {
        Proveedor datosNuevos = new Proveedor();
        datosNuevos.setNombre("Modificado");
        datosNuevos.setRut("87654321-0");
        datosNuevos.setTelefono("912345678");
        datosNuevos.setEmail("modificado@proveedor.com");

        when(proveedorRepository.findById(1L)).thenReturn(Optional.of(proveedorBase));
        when(proveedorRepository.save(any(Proveedor.class))).thenReturn(proveedorBase);

        Proveedor resultado = proveedorService.editarProveedor(1L, datosNuevos);

        assertThat(resultado).isNotNull();
        assertThat(resultado.getNombre()).isEqualTo("Modificado");
        assertThat(resultado.getRut()).isEqualTo("87654321-0");
        verify(proveedorRepository).findById(1L);
        verify(proveedorRepository).save(any(Proveedor.class));
    }

    @Test
    void testEditarProveedorInexistente() {
        when(proveedorRepository.findById(9999L)).thenReturn(Optional.empty());

        Proveedor resultado = proveedorService.editarProveedor(9999L, new Proveedor());

        assertThat(resultado).isNull();
        verify(proveedorRepository).findById(9999L);
        verify(proveedorRepository, never()).save(any(Proveedor.class));
    }

    @Test
    void testEliminarProveedorExistente() {
        when(proveedorRepository.findById(1L)).thenReturn(Optional.of(proveedorBase));
        when(proveedorRepository.save(any(Proveedor.class))).thenReturn(proveedorBase);

        boolean resultado = proveedorService.eliminarProveedor(1L);

        assertThat(resultado).isTrue();
        assertThat(proveedorBase.getActivo()).isFalse();
        verify(proveedorRepository).findById(1L);
        verify(proveedorRepository).save(any(Proveedor.class));
    }

    @Test
    void testEliminarProveedorInexistente() {
        when(proveedorRepository.findById(9999L)).thenReturn(Optional.empty());

        boolean resultado = proveedorService.eliminarProveedor(9999L);

        assertThat(resultado).isFalse();
        verify(proveedorRepository).findById(9999L);
        verify(proveedorRepository, never()).save(any(Proveedor.class));
    }

    // TESTS DE HISTORIAL COMPRAS

    @Test
    void testObtenerHistorialComprasPorProveedorExitoso() {
        when(proveedorRepository.existsById(1L)).thenReturn(true);
        when(proveedorRepository.findById(1L)).thenReturn(Optional.of(new Proveedor()));
        
        // 2. Como el historial ahora viene del servicio simulado, no del repository,
        // debemos mockear el nuevo servicio.
        // 🔥 Asumiendo que inyectaste HistorialComprasSimuladoService
        HistorialCompraDTO compra1 = new HistorialCompraDTO();
        compra1.setId(1L);
        compra1.setMontoTotal(1000.0);
        List<HistorialCompraDTO> historialMock = Arrays.asList(compra1);
        when(historialSimuladoService.obtenerPorProveedor(1L)).thenReturn(historialMock);

        // 3. Ejecutar
        List<HistorialCompraDTO> resultado = proveedorService.obtenerHistorialComprasPorProveedor(1L);

        // 4. Verificar
        assertThat(resultado).hasSize(1);
        assertThat(resultado.get(0).getMontoTotal()).isEqualTo(1000.0);
        verify(proveedorRepository).existsById(1L);
        verify(historialSimuladoService).obtenerPorProveedor(1L);
    }

    @Test
    void testObtenerHistorialComprasPorProveedorInexistente() {
        when(proveedorRepository.existsById(9999L)).thenReturn(false);

        assertThatThrownBy(() -> proveedorService.obtenerHistorialComprasPorProveedor(9999L))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Proveedor con ID 9999 no existe");

        verify(proveedorRepository).existsById(9999L);
        verify(historialComprasRepository, never()).findByProveedorId(anyLong());
    }

    @Test
    void testRegistrarCompraAProveedorExitoso() {
        when(proveedorRepository.existsById(1L)).thenReturn(true);
        
        HistorialCompraDTO nuevaCompra = new HistorialCompraDTO();
        nuevaCompra.setMontoTotal(500.0);
        nuevaCompra.setDescripcionCompra("Compra test");
        
        HistorialCompraDTO compraGuardada = new HistorialCompraDTO();
        compraGuardada.setId(1L);
        compraGuardada.setMontoTotal(500.0);
        when(historialSimuladoService.registrarCompra(any(HistorialCompraDTO.class))).thenReturn(compraGuardada);

        HistorialCompraDTO resultado = proveedorService.registrarCompraAProveedor(1L, nuevaCompra);

        assertThat(resultado).isNotNull();
        assertThat(resultado.getId()).isEqualTo(1L);
        verify(proveedorRepository).existsById(1L);
        verify(historialSimuladoService).registrarCompra(any(HistorialCompraDTO.class));
    
    }

    @Test
    void testRegistrarCompraAProveedorInexistente() {
        when(proveedorRepository.existsById(9999L)).thenReturn(false);

        HistorialCompraDTO nuevaCompra = new HistorialCompraDTO();
        nuevaCompra.setMontoTotal(500.0);
        nuevaCompra.setDescripcionCompra("Compra test");

        assertThatThrownBy(() -> proveedorService.registrarCompraAProveedor(9999L, nuevaCompra))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Proveedor con ID 9999 no existe");

        verify(proveedorRepository).existsById(9999L);
        verify(historialSimuladoService, never()).registrarCompra(any(HistorialCompraDTO.class));
    }

    @Test
    void testObtenerHistorialCompraPorIdExistente() {
        when(historialComprasRepository.findById(1L)).thenReturn(Optional.of(historialBase));

        HistorialCompras resultado = proveedorService.obtenerHistorialCompraPorId(1L);

        assertThat(resultado).isNotNull();
        assertThat(resultado.getId()).isEqualTo(1L);
        verify(historialComprasRepository).findById(1L);
    }

    @Test
    void testObtenerHistorialCompraPorIdInexistente() {
        when(historialComprasRepository.findById(9999L)).thenReturn(Optional.empty());

        HistorialCompras resultado = proveedorService.obtenerHistorialCompraPorId(9999L);

        assertThat(resultado).isNull();
        verify(historialComprasRepository).findById(9999L);
    }
}
