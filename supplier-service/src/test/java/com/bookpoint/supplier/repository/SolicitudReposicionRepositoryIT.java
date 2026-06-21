package com.bookpoint.supplier.repository;

import com.bookpoint.supplier.model.SolicitudReposicion;
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
class SolicitudReposicionRepositoryIT {

    @Autowired
    private SolicitudReposicionRepository solicitudRepository;

    @BeforeEach
    void cleanDb() {
        solicitudRepository.deleteAll();
    }

    private SolicitudReposicion crearSolicitudBase() {
        SolicitudReposicion solicitud = new SolicitudReposicion();
        solicitud.setFechaSolicitud(new Date());
        solicitud.setEstadoSolicitud("PENDIENTE");
        solicitud.setCantidadSolicitada(10);
        return solicitud;
    }

    @Test
    void testGuardarSolicitud() {
        SolicitudReposicion solicitud = crearSolicitudBase();
        SolicitudReposicion guardado = solicitudRepository.save(solicitud);

        assertThat(guardado).isNotNull();
        assertThat(guardado.getId()).isNotNull();
        assertThat(guardado.getEstadoSolicitud()).isEqualTo("PENDIENTE");
        assertThat(guardado.getCantidadSolicitada()).isEqualTo(10);
    }

    @Test
    void testBuscarPorId() {
        SolicitudReposicion solicitud = crearSolicitudBase();
        SolicitudReposicion guardado = solicitudRepository.save(solicitud);

        Optional<SolicitudReposicion> encontrado = solicitudRepository.findById(guardado.getId());

        assertThat(encontrado).isPresent();
        assertThat(encontrado.get().getEstadoSolicitud()).isEqualTo("PENDIENTE");
    }

    @Test
    void testBuscarPorIdInexistente() {
        Optional<SolicitudReposicion> encontrado = solicitudRepository.findById(9999L);
        assertThat(encontrado).isEmpty();
    }

    @Test
    void testListarTodos() {
        solicitudRepository.save(crearSolicitudBase());
        solicitudRepository.save(crearSolicitudBase());

        List<SolicitudReposicion> todos = solicitudRepository.findAll();

        assertThat(todos).hasSize(2);
    }

    @Test
    void testActualizar() {
        SolicitudReposicion solicitud = crearSolicitudBase();
        SolicitudReposicion guardado = solicitudRepository.save(solicitud);

        guardado.setEstadoSolicitud("APROBADA");
        guardado.setCantidadSolicitada(20);
        SolicitudReposicion actualizado = solicitudRepository.save(guardado);

        assertThat(actualizado.getEstadoSolicitud()).isEqualTo("APROBADA");
        assertThat(actualizado.getCantidadSolicitada()).isEqualTo(20);
    }

    @Test
    void testEliminar() {
        SolicitudReposicion solicitud = crearSolicitudBase();
        SolicitudReposicion guardado = solicitudRepository.save(solicitud);

        solicitudRepository.deleteById(guardado.getId());
        Optional<SolicitudReposicion> encontrado = solicitudRepository.findById(guardado.getId());

        assertThat(encontrado).isEmpty();
    }

    @Test
    void testCount() {
        solicitudRepository.save(crearSolicitudBase());
        solicitudRepository.save(crearSolicitudBase());

        long count = solicitudRepository.count();

        assertThat(count).isEqualTo(2);
    }

    @Test
    void testExistsById() {
        SolicitudReposicion solicitud = crearSolicitudBase();
        SolicitudReposicion guardado = solicitudRepository.save(solicitud);

        boolean existe = solicitudRepository.existsById(guardado.getId());
        boolean noExiste = solicitudRepository.existsById(9999L);

        assertThat(existe).isTrue();
        assertThat(noExiste).isFalse();
    }
}
