package com.bookpoint.logistic.repository;

import com.bookpoint.logistic.model.OrdenDespacho;
import com.bookpoint.logistic.model.Transportista;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.Date;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ActiveProfiles("test")
class OrdenDespachoRepositoryIT {

    @Autowired
    private OrdenDespachoRepository ordenDespachoRepository;

    @Autowired
    private TransportistaRepository transportistaRepository;

    @Autowired
    private EnvioRepository envioRepository;

    @Autowired
    private IncidenciaRepository incidenciaRepository;

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
        orden.setObservacionDespacho("Test observación");
        orden.setTipo("DOMICILIO");
        orden.setSucursalDestinoId(1L);
        orden.setUbicacionBodega("Bodega Central");
        orden.setCantidadSolicitada(10);
        orden.setCantidadConfirmada(10);
        orden.setCantidadFinal(10);
        return orden;
    }

    @Test
    void testGuardarOrdenDespacho() {
        OrdenDespacho orden = crearOrdenBase();
        OrdenDespacho guardado = ordenDespachoRepository.save(orden);

        assertThat(guardado).isNotNull();
        assertThat(guardado.getId()).isNotNull();
        assertThat(guardado.getEstadoDespacho()).isEqualTo("PENDIENTE");
    }

    @Test
    void testBuscarPorId() {
        OrdenDespacho orden = crearOrdenBase();
        OrdenDespacho guardado = ordenDespachoRepository.save(orden);

        Optional<OrdenDespacho> encontrado = ordenDespachoRepository.findById(guardado.getId());

        assertThat(encontrado).isPresent();
        assertThat(encontrado.get().getEstadoDespacho()).isEqualTo("PENDIENTE");
    }

    @Test
    void testBuscarPorIdInexistente() {
        Optional<OrdenDespacho> encontrado = ordenDespachoRepository.findById(9999L);
        assertThat(encontrado).isEmpty();
    }

    @Test
    void testListarTodos() {
        ordenDespachoRepository.save(crearOrdenBase());
        ordenDespachoRepository.save(crearOrdenBase());

        List<OrdenDespacho> todos = ordenDespachoRepository.findAll();

        assertThat(todos).hasSize(2);
    }

    @Test
    void testActualizar() {
        OrdenDespacho orden = crearOrdenBase();
        OrdenDespacho guardado = ordenDespachoRepository.save(orden);

        guardado.setEstadoDespacho("CONFIRMADO");
        OrdenDespacho actualizado = ordenDespachoRepository.save(guardado);

        assertThat(actualizado.getEstadoDespacho()).isEqualTo("CONFIRMADO");
    }

    @Test
    void testEliminar() {
        OrdenDespacho orden = crearOrdenBase();
        OrdenDespacho guardado = ordenDespachoRepository.save(orden);

        ordenDespachoRepository.deleteById(guardado.getId());
        Optional<OrdenDespacho> encontrado = ordenDespachoRepository.findById(guardado.getId());

        assertThat(encontrado).isEmpty();
    }

    @Test
    void testGuardarConTransportistaAsociado() {
        Transportista transportista = new Transportista(null, "Transportista Test", "12345678-9", "987654321", true);
        Transportista tGuardado = transportistaRepository.save(transportista);

        OrdenDespacho orden = crearOrdenBase();
        orden.setTransportista(tGuardado);
        OrdenDespacho oGuardado = ordenDespachoRepository.save(orden);

        assertThat(oGuardado.getTransportista()).isNotNull();
        assertThat(oGuardado.getTransportista().getId()).isEqualTo(tGuardado.getId());
    }

    @Test
    void testCount() {
        ordenDespachoRepository.save(crearOrdenBase());
        ordenDespachoRepository.save(crearOrdenBase());

        long count = ordenDespachoRepository.count();

        assertThat(count).isEqualTo(2);
    }

    @Test
    void testExistById() {
        OrdenDespacho orden = crearOrdenBase();
        OrdenDespacho guardado = ordenDespachoRepository.save(orden);

        boolean existe = ordenDespachoRepository.existsById(guardado.getId());
        boolean noExiste = ordenDespachoRepository.existsById(9999L);

        assertThat(existe).isTrue();
        assertThat(noExiste).isFalse();
    }
}