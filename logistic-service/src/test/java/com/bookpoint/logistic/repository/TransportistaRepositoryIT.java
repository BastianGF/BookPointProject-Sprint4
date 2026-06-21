package com.bookpoint.logistic.repository;

import com.bookpoint.logistic.model.Transportista;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ActiveProfiles("test")
class TransportistaRepositoryIT {

    @Autowired
    private TransportistaRepository transportistaRepository;

    @Autowired
    private OrdenDespachoRepository ordenDespachoRepository;

    @Autowired
    private TrasladoRepository trasladoRepository;

    @Autowired
    private EnvioRepository envioRepository;

    @Autowired
    private IncidenciaRepository incidenciaRepository;

    @Autowired
    private RutaRepository rutaRepository;

    @BeforeEach
    void cleanDb() {
        incidenciaRepository.deleteAll();
        envioRepository.deleteAll();
        trasladoRepository.deleteAll();
        if (rutaRepository != null) {
            rutaRepository.deleteAll();
        }
        ordenDespachoRepository.deleteAll();
        transportistaRepository.deleteAll();
    }

    @Test
    void testGuardarTransportista() {
        Transportista t = new Transportista(null, "Juan Pérez", "12345678-9", "987654321", true);
        Transportista guardado = transportistaRepository.save(t);

        assertThat(guardado).isNotNull();
        assertThat(guardado.getId()).isNotNull();
        assertThat(guardado.getNombre()).isEqualTo("Juan Pérez");
    }

    @Test
    void testBuscarPorId() {
        Transportista t = new Transportista(null, "Carlos", "87654321-0", "912345678", true);
        Transportista guardado = transportistaRepository.save(t);

        Optional<Transportista> encontrado = transportistaRepository.findById(guardado.getId());

        assertThat(encontrado).isPresent();
        assertThat(encontrado.get().getNombre()).isEqualTo("Carlos");
    }

    @Test
    void testBuscarPorIdInexistente() {
        Optional<Transportista> encontrado = transportistaRepository.findById(9999L);
        assertThat(encontrado).isEmpty();
    }

    @Test
    void testListarTodos() {
        transportistaRepository.save(new Transportista(null, "Juan", "11111111-1", "111111111", true));
        transportistaRepository.save(new Transportista(null, "Pedro", "22222222-2", "222222222", false));

        List<Transportista> todos = transportistaRepository.findAll();

        assertThat(todos).hasSize(2);
    }

    @Test
    void testActualizar() {
        Transportista t = new Transportista(null, "Original", "33333333-3", "333333333", true);
        Transportista guardado = transportistaRepository.save(t);

        guardado.setNombre("Modificado");
        Transportista actualizado = transportistaRepository.save(guardado);

        assertThat(actualizado.getNombre()).isEqualTo("Modificado");
    }

    @Test
    void testEliminar() {
        Transportista t = new Transportista(null, "Eliminar", "44444444-4", "444444444", true);
        Transportista guardado = transportistaRepository.save(t);

        transportistaRepository.deleteById(guardado.getId());
        Optional<Transportista> encontrado = transportistaRepository.findById(guardado.getId());

        assertThat(encontrado).isEmpty();
    }

    @Test
    void testFindByDisponible() {
        transportistaRepository.save(new Transportista(null, "Disponible", "55555555-5", "555555555", true));
        transportistaRepository.save(new Transportista(null, "NoDispo", "66666666-6", "666666666", false));

        List<Transportista> disponibles = transportistaRepository.findByDisponible(true);

        assertThat(disponibles).hasSize(1);
        assertThat(disponibles.get(0).getNombre()).isEqualTo("Disponible");
    }

    @Test
    void testCount() {
        transportistaRepository.save(new Transportista(null, "T1", "77777777-7", "777777777", true));
        transportistaRepository.save(new Transportista(null, "T2", "88888888-8", "888888888", true));

        long count = transportistaRepository.count();

        assertThat(count).isEqualTo(2);
    }

    @Test
    void testExistById() {
        Transportista t = new Transportista(null, "Existe", "99999999-9", "999999999", true);
        Transportista guardado = transportistaRepository.save(t);

        boolean existe = transportistaRepository.existsById(guardado.getId());
        boolean noExiste = transportistaRepository.existsById(9999L);

        assertThat(existe).isTrue();
        assertThat(noExiste).isFalse();
    }
}