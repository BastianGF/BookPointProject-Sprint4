package com.bookpoint.logistic.repository;

import com.bookpoint.logistic.model.Envio;
import com.bookpoint.logistic.model.OrdenDespacho;
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
class EnvioRepositoryIT {

    @Autowired
    private EnvioRepository envioRepository;

    @Autowired
    private OrdenDespachoRepository ordenDespachoRepository;

    @Autowired IncidenciaRepository incidenciaRepository;

    @BeforeEach
    void cleanDb() {
        incidenciaRepository.deleteAll();
        envioRepository.deleteAll();
        ordenDespachoRepository.deleteAll();
    }

    private Envio crearEnvioBase() {
        Envio envio = new Envio();
        envio.setEstadoEnvio("PENDIENTE");
        envio.setFechaActualizacion(new Date());
        envio.setUbicacionActual("Bodega Central");
        return envio;
    }

    private OrdenDespacho crearOrdenDespachoBase() {
        OrdenDespacho orden = new OrdenDespacho();
        orden.setFechaCreacion(new Date());
        orden.setEstadoDespacho("PENDIENTE");
        orden.setObservacionDespacho("Test");
        orden.setTipo("DOMICILIO");
        orden.setCantidadSolicitada(10);
        orden.setCantidadConfirmada(10);
        orden.setCantidadFinal(10);
        return orden;
    }

    @Test
    void testGuardarEnvio() {
        Envio envio = crearEnvioBase();
        Envio guardado = envioRepository.save(envio);

        assertThat(guardado).isNotNull();
        assertThat(guardado.getId()).isNotNull();
        assertThat(guardado.getEstadoEnvio()).isEqualTo("PENDIENTE");
    }

    @Test
    void testBuscarPorId() {
        Envio envio = crearEnvioBase();
        Envio guardado = envioRepository.save(envio);

        Optional<Envio> encontrado = envioRepository.findById(guardado.getId());

        assertThat(encontrado).isPresent();
        assertThat(encontrado.get().getEstadoEnvio()).isEqualTo("PENDIENTE");
    }

    @Test
    void testBuscarPorIdInexistente() {
        Optional<Envio> encontrado = envioRepository.findById(9999L);
        assertThat(encontrado).isEmpty();
    }

    @Test
    void testListarTodos() {
        envioRepository.save(crearEnvioBase());
        envioRepository.save(crearEnvioBase());

        List<Envio> todos = envioRepository.findAll();

        assertThat(todos).hasSize(2);
    }

    @Test
    void testActualizar() {
        Envio envio = crearEnvioBase();
        Envio guardado = envioRepository.save(envio);

        guardado.setEstadoEnvio("EN_TRANSITO");
        Envio actualizado = envioRepository.save(guardado);

        assertThat(actualizado.getEstadoEnvio()).isEqualTo("EN_TRANSITO");
    }

    @Test
    void testEliminar() {
        Envio envio = crearEnvioBase();
        Envio guardado = envioRepository.save(envio);

        envioRepository.deleteById(guardado.getId());
        Optional<Envio> encontrado = envioRepository.findById(guardado.getId());

        assertThat(encontrado).isEmpty();
    }

    @Test
    void testGuardarConOrdenDespachoAsociado() {
        OrdenDespacho orden = crearOrdenDespachoBase();
        OrdenDespacho oGuardado = ordenDespachoRepository.save(orden);

        Envio envio = crearEnvioBase();
        envio.setOrdenDespacho(oGuardado);
        Envio eGuardado = envioRepository.save(envio);

        assertThat(eGuardado.getOrdenDespacho()).isNotNull();
        assertThat(eGuardado.getOrdenDespacho().getId()).isEqualTo(oGuardado.getId());
    }

    @Test
    void testCount() {
        envioRepository.save(crearEnvioBase());
        envioRepository.save(crearEnvioBase());

        long count = envioRepository.count();

        assertThat(count).isEqualTo(2);
    }

    @Test
    void testExistById() {
        Envio envio = crearEnvioBase();
        Envio guardado = envioRepository.save(envio);

        boolean existe = envioRepository.existsById(guardado.getId());
        boolean noExiste = envioRepository.existsById(9999L);

        assertThat(existe).isTrue();
        assertThat(noExiste).isFalse();
    }
}
