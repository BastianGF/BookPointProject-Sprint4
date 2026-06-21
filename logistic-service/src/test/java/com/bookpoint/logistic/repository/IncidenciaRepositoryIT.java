package com.bookpoint.logistic.repository;

import com.bookpoint.logistic.model.Incidencia;
import com.bookpoint.logistic.model.Envio;
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
class IncidenciaRepositoryIT {

    @Autowired
    private IncidenciaRepository incidenciaRepository;

    @Autowired
    private EnvioRepository envioRepository;

    @BeforeEach
    void cleanDb() {
        incidenciaRepository.deleteAll();
        envioRepository.deleteAll();
    }

    private Incidencia crearIncidenciaBase() {
        Incidencia incidencia = new Incidencia();
        incidencia.setDescripcionIncidencia("Problema con el envío");
        incidencia.setFecha(new Date());
        incidencia.setTipo("RETRASO");
        return incidencia;
    }

    private Envio crearEnvioBase() {
        Envio envio = new Envio();
        envio.setEstadoEnvio("PENDIENTE");
        envio.setFechaActualizacion(new Date());
        envio.setUbicacionActual("Bodega Central");
        return envio;
    }

    @Test
    void testGuardarIncidencia() {
        Incidencia incidencia = crearIncidenciaBase();
        Incidencia guardado = incidenciaRepository.save(incidencia);

        assertThat(guardado).isNotNull();
        assertThat(guardado.getId()).isNotNull();
        assertThat(guardado.getDescripcionIncidencia()).isEqualTo("Problema con el envío");
        assertThat(guardado.getTipo()).isEqualTo("RETRASO");
    }

    @Test
    void testBuscarPorId() {
        Incidencia incidencia = crearIncidenciaBase();
        Incidencia guardado = incidenciaRepository.save(incidencia);

        Optional<Incidencia> encontrado = incidenciaRepository.findById(guardado.getId());

        assertThat(encontrado).isPresent();
        assertThat(encontrado.get().getDescripcionIncidencia()).isEqualTo("Problema con el envío");
    }

    @Test
    void testBuscarPorIdInexistente() {
        Optional<Incidencia> encontrado = incidenciaRepository.findById(9999L);
        assertThat(encontrado).isEmpty();
    }

    @Test
    void testListarTodos() {
        incidenciaRepository.save(crearIncidenciaBase());
        incidenciaRepository.save(crearIncidenciaBase());

        List<Incidencia> todos = incidenciaRepository.findAll();

        assertThat(todos).hasSize(2);
    }

    @Test
    void testActualizar() {
        Incidencia incidencia = crearIncidenciaBase();
        Incidencia guardado = incidenciaRepository.save(incidencia);

        guardado.setDescripcionIncidencia("Nuevo problema");
        Incidencia actualizado = incidenciaRepository.save(guardado);

        assertThat(actualizado.getDescripcionIncidencia()).isEqualTo("Nuevo problema");
    }

    @Test
    void testEliminar() {
        Incidencia incidencia = crearIncidenciaBase();
        Incidencia guardado = incidenciaRepository.save(incidencia);

        incidenciaRepository.deleteById(guardado.getId());
        Optional<Incidencia> encontrado = incidenciaRepository.findById(guardado.getId());

        assertThat(encontrado).isEmpty();
    }

    @Test
    void testGuardarConEnvioAsociado() {
        Envio envio = crearEnvioBase();
        Envio eGuardado = envioRepository.save(envio);

        Incidencia incidencia = crearIncidenciaBase();
        incidencia.setEnvio(eGuardado);
        Incidencia iGuardado = incidenciaRepository.save(incidencia);

        assertThat(iGuardado.getEnvio()).isNotNull();
        assertThat(iGuardado.getEnvio().getId()).isEqualTo(eGuardado.getId());
    }

    @Test
    void testCount() {
        incidenciaRepository.save(crearIncidenciaBase());
        incidenciaRepository.save(crearIncidenciaBase());

        long count = incidenciaRepository.count();

        assertThat(count).isEqualTo(2);
    }

    @Test
    void testExistById() {
        Incidencia incidencia = crearIncidenciaBase();
        Incidencia guardado = incidenciaRepository.save(incidencia);

        boolean existe = incidenciaRepository.existsById(guardado.getId());
        boolean noExiste = incidenciaRepository.existsById(9999L);

        assertThat(existe).isTrue();
        assertThat(noExiste).isFalse();
    }
}