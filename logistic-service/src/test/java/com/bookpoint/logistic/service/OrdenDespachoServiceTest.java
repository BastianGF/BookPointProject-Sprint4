package com.bookpoint.logistic.service;

import com.bookpoint.logistic.client.SupplierClient;
import com.bookpoint.logistic.dto.ProveedorDTO;
import com.bookpoint.logistic.model.OrdenDespacho;
import com.bookpoint.logistic.model.Transportista;
import com.bookpoint.logistic.repository.OrdenDespachoRepository;
import com.bookpoint.logistic.repository.TransportistaRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class OrdenDespachoServiceTest {

    @Mock
    private OrdenDespachoRepository ordenDespachoRepository;

    @Mock
    private TransportistaRepository transportistaRepository;

    @Mock
    private SupplierClient supplierClient;

    @InjectMocks
    private OrdenDespachoService ordenDespachoService;

    private OrdenDespacho ordenBase;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        ordenBase = new OrdenDespacho();
        ordenBase.setFechaCreacion(new Date());
        ordenBase.setEstadoDespacho("PENDIENTE");
        ordenBase.setObservacionDespacho("Test");
        ordenBase.setTipo("DOMICILIO");
        ordenBase.setCantidadSolicitada(10);
        ordenBase.setCantidadConfirmada(10);
        ordenBase.setCantidadFinal(10);
    }

    // TEST PARA LISTAR ORDENES 
    @Test
    void testListarOrdenes() {
        List<OrdenDespacho> ordenes = new ArrayList<>();
        ordenes.add(ordenBase);
        when(ordenDespachoRepository.findAll()).thenReturn(ordenes);

        List<OrdenDespacho> resultado = ordenDespachoService.listarOrdenes();

        assertThat(resultado).hasSize(1);
        verify(ordenDespachoRepository).findAll();
    }

    // TEST PARA BUSCAR POR ID 
    @Test
    void testBuscarPorIdExistente() {
        when(ordenDespachoRepository.findById(1L)).thenReturn(Optional.of(ordenBase));

        Optional<OrdenDespacho> resultado = ordenDespachoService.buscarPorId(1L);

        assertThat(resultado).isPresent();
        verify(ordenDespachoRepository).findById(1L);
    }

    @Test
    void testBuscarPorIdInexistente() {
        when(ordenDespachoRepository.findById(9999L)).thenReturn(Optional.empty());

        Optional<OrdenDespacho> resultado = ordenDespachoService.buscarPorId(9999L);

        assertThat(resultado).isEmpty();
        verify(ordenDespachoRepository).findById(9999L);
    }

    // TEST PARA CREAR ORDEN
    @Test
    void testCrearOrdenConProveedorExistente() {
        ProveedorDTO proveedor = new ProveedorDTO();
        proveedor.setId(1L);
        proveedor.setNombre("Proveedor Test");

        when(supplierClient.obtenerProveedorPorId(1L)).thenReturn(proveedor);
        when(ordenDespachoRepository.save(any(OrdenDespacho.class))).thenReturn(ordenBase);

        // Modif por el JSON emulado
        OrdenDespacho resultado = ordenDespachoService.crearOrden(ordenBase, 1L, null);

        assertThat(resultado).isNotNull();
        assertThat(resultado.getEstadoDespacho()).isEqualTo("PENDIENTE");
        assertThat(resultado.getObservacionDespacho()).contains("Proveedor Test");
        verify(supplierClient).obtenerProveedorPorId(1L);
        verify(ordenDespachoRepository).save(any(OrdenDespacho.class));
    }

        @Test
    void testCrearOrdenConProveedorInexistente() {
        when(supplierClient.obtenerProveedorPorId(9999L)).thenReturn(null);

        // otra modiff, null como tercer parametro
        assertThatThrownBy(() -> ordenDespachoService.crearOrden(ordenBase, 9999L, null))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Proveedor no encontrado");

        verify(supplierClient).obtenerProveedorPorId(9999L);
        verify(ordenDespachoRepository, never()).save(any(OrdenDespacho.class));
    }

    // TEST PARA CONFIRMAR DESPACHO
    @Test
    void testConfirmarDespachoExistente() {
        when(ordenDespachoRepository.findById(1L)).thenReturn(Optional.of(ordenBase));
        when(ordenDespachoRepository.save(any(OrdenDespacho.class))).thenReturn(ordenBase);

        OrdenDespacho resultado = ordenDespachoService.confirmarDespacho(1L);

        assertThat(resultado).isNotNull();
        assertThat(resultado.getEstadoDespacho()).isEqualTo("CONFIRMADO");
        verify(ordenDespachoRepository).findById(1L);
        verify(ordenDespachoRepository).save(any(OrdenDespacho.class));
    }

    @Test
    void testConfirmarDespachoInexistente() {
        when(ordenDespachoRepository.findById(9999L)).thenReturn(Optional.empty());

        OrdenDespacho resultado = ordenDespachoService.confirmarDespacho(9999L);

        assertThat(resultado).isNull();
        verify(ordenDespachoRepository).findById(9999L);
        verify(ordenDespachoRepository, never()).save(any(OrdenDespacho.class));
    }

    // TEST PARA CANCELAR DESPACHO
    @Test
    void testCancelarDespachoExistente() {
        when(ordenDespachoRepository.findById(1L)).thenReturn(Optional.of(ordenBase));
        when(ordenDespachoRepository.save(any(OrdenDespacho.class))).thenReturn(ordenBase);

        OrdenDespacho resultado = ordenDespachoService.cancelarDespacho(1L);

        assertThat(resultado).isNotNull();
        assertThat(resultado.getEstadoDespacho()).isEqualTo("CANCELADO");
        verify(ordenDespachoRepository).findById(1L);
        verify(ordenDespachoRepository).save(any(OrdenDespacho.class));
    }

    @Test
    void testCancelarDespachoInexistente() {
        when(ordenDespachoRepository.findById(9999L)).thenReturn(Optional.empty());

        OrdenDespacho resultado = ordenDespachoService.cancelarDespacho(9999L);

        assertThat(resultado).isNull();
        verify(ordenDespachoRepository).findById(9999L);
        verify(ordenDespachoRepository, never()).save(any(OrdenDespacho.class));
    }

    // TEST PARA PREPARAR MERCADERIA 
    @Test
    void testPrepararMercaderiaEstadoPendiente() {
        OrdenDespacho ordenPendiente = new OrdenDespacho();
        ordenPendiente.setEstadoDespacho("PENDIENTE");

        when(ordenDespachoRepository.findById(1L)).thenReturn(Optional.of(ordenPendiente));
        when(ordenDespachoRepository.save(any(OrdenDespacho.class))).thenReturn(ordenPendiente);

        OrdenDespacho resultado = ordenDespachoService.prepararMercaderia(1L, "Bodega Norte", 15);

        assertThat(resultado.getEstadoDespacho()).isEqualTo("PREPARADO");
        assertThat(resultado.getUbicacionBodega()).isEqualTo("Bodega Norte");
        assertThat(resultado.getCantidadConfirmada()).isEqualTo(15);
        verify(ordenDespachoRepository).save(any(OrdenDespacho.class));
    }

    @Test
    void testPrepararMercaderiaOrdenNoExiste() {
        when(ordenDespachoRepository.findById(9999L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> ordenDespachoService.prepararMercaderia(9999L, "Bodega", 10))
            .isInstanceOf(RuntimeException.class)
            .hasMessageContaining("Orden con ID 9999 no existe");
    }

    @Test
    void testPrepararMercaderiaEstadoInvalido() {
        OrdenDespacho ordenConfirmada = new OrdenDespacho();
        ordenConfirmada.setEstadoDespacho("CONFIRMADO");

        when(ordenDespachoRepository.findById(1L)).thenReturn(Optional.of(ordenConfirmada));

        assertThatThrownBy(() -> ordenDespachoService.prepararMercaderia(1L, "Bodega", 10))
            .isInstanceOf(RuntimeException.class)
            .hasMessageContaining("Orden no en estado PENDIENTE");
    }

    // TEST PARA REGISTRAR SALIDA BODEGA 
    @Test
    void testRegistrarSalidaBodegaEstadoPreparado() {
        OrdenDespacho ordenPreparado = new OrdenDespacho();
        ordenPreparado.setEstadoDespacho("PREPARADO");

        Transportista transportista = new Transportista();
        transportista.setId(1L);

        when(ordenDespachoRepository.findById(1L)).thenReturn(Optional.of(ordenPreparado));
        when(transportistaRepository.findById(1L)).thenReturn(Optional.of(transportista));
        when(ordenDespachoRepository.save(any(OrdenDespacho.class))).thenReturn(ordenPreparado);

        OrdenDespacho resultado = ordenDespachoService.registrarSalidaBodega(1L, 1L, 20);

        assertThat(resultado.getEstadoDespacho()).isEqualTo("DESPACHADO");
        assertThat(resultado.getCantidadFinal()).isEqualTo(20);
        assertThat(resultado.getTransportista()).isNotNull();
        verify(ordenDespachoRepository).save(any(OrdenDespacho.class));
    }

    @Test
    void testRegistrarSalidaBodegaOrdenNoExiste() {
        when(ordenDespachoRepository.findById(9999L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> ordenDespachoService.registrarSalidaBodega(9999L, 1L, 10))
            .isInstanceOf(RuntimeException.class)
            .hasMessageContaining("Orden con ID 9999 no existe");
    }

    @Test
    void testRegistrarSalidaBodegaEstadoInvalido() {
        OrdenDespacho ordenPendiente = new OrdenDespacho();
        ordenPendiente.setEstadoDespacho("PENDIENTE");

        when(ordenDespachoRepository.findById(1L)).thenReturn(Optional.of(ordenPendiente));

        assertThatThrownBy(() -> ordenDespachoService.registrarSalidaBodega(1L, 1L, 10))
            .isInstanceOf(RuntimeException.class)
            .hasMessageContaining("Orden no en estado PREPARADO");
    }

    @Test
    void testRegistrarSalidaBodegaTransportistaNoExiste() {
        OrdenDespacho ordenPreparado = new OrdenDespacho();
        ordenPreparado.setEstadoDespacho("PREPARADO");

        when(ordenDespachoRepository.findById(1L)).thenReturn(Optional.of(ordenPreparado));
        when(transportistaRepository.findById(9999L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> ordenDespachoService.registrarSalidaBodega(1L, 9999L, 10))
            .isInstanceOf(RuntimeException.class)
            .hasMessageContaining("Transportista con ID 9999 no existe");
    }

    // He aqui los test que voy a tener que poner unicamente por el maldito 92% por completar el flujo del software.

    @Test
    void testCrearOrdenConProductos() {
        // esto e para Configurar proveedor
        ProveedorDTO proveedor = new ProveedorDTO();
        proveedor.setId(1L);
        proveedor.setNombre("Proveedor Test");

        // esto para lista de IDs de productos
        List<Integer> productoIds = Arrays.asList(1, 3);

        // esto para configurar mocks
        when(supplierClient.obtenerProveedorPorId(1L)).thenReturn(proveedor);
        when(ordenDespachoRepository.save(any(OrdenDespacho.class))).thenReturn(ordenBase);

        // para ejecutar
        OrdenDespacho resultado = ordenDespachoService.crearOrden(ordenBase, 1L, productoIds);

        // para verificar
        assertThat(resultado).isNotNull();
        assertThat(resultado.getEstadoDespacho()).isEqualTo("PENDIENTE");
        assertThat(resultado.getObservacionDespacho()).contains("Proveedor Test");
        
        // con esto se verificar que los productos se agregaron
        assertThat(resultado.getProductos()).isNotNull();
        assertThat(resultado.getProductos()).contains("Libro de Java");
        assertThat(resultado.getProductos()).contains("Lápiz");
        assertThat(resultado.getProductos()).doesNotContain("Cuaderno");
        assertThat(resultado.getProductos()).doesNotContain("Calculadora");
        
        verify(supplierClient).obtenerProveedorPorId(1L);
        verify(ordenDespachoRepository).save(any(OrdenDespacho.class));
    }

        @Test
    void testCrearOrdenConListaDeProductosVacia() {
        // Mismo tema de configurar proveedor
        ProveedorDTO proveedor = new ProveedorDTO();
        proveedor.setId(1L);
        proveedor.setNombre("Proveedor Test");

        // Aqui se crea una lista VACÍA de productos (hasta Collection meti, que fuerte)
        List<Integer> productoIds = Collections.emptyList();

        // para configurar mocks
        when(supplierClient.obtenerProveedorPorId(1L)).thenReturn(proveedor);
        when(ordenDespachoRepository.save(any(OrdenDespacho.class))).thenReturn(ordenBase);

        // EJECUTA
        OrdenDespacho resultado = ordenDespachoService.crearOrden(ordenBase, 1L, productoIds);

        // verif
        assertThat(resultado).isNotNull();
        assertThat(resultado.getEstadoDespacho()).isEqualTo("PENDIENTE");
        
        // "Perdona.. los productos deben ser null.. NO SE AGREGARON"
        assertThat(resultado.getProductos()).isNull();
        
        verify(supplierClient).obtenerProveedorPorId(1L);
        verify(ordenDespachoRepository).save(any(OrdenDespacho.class));
    }
}
