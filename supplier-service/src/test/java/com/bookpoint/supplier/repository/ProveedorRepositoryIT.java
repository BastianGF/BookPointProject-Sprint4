package com.bookpoint.supplier.repository;

import com.bookpoint.supplier.model.Proveedor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ActiveProfiles("test")
class ProveedorRepositoryIT {

    @Autowired
    private ProveedorRepository proveedorRepository;

    @BeforeEach
    void cleanDb() {
        proveedorRepository.deleteAll();
    }

    private Proveedor crearProveedorBase() {
        return crearProveedorBase("12345678-9");
    }

    private Proveedor crearProveedorBase(String rut) {
        Proveedor proveedor = new Proveedor();
        proveedor.setNombre("Proveedor Test");
        proveedor.setRut(rut);
        proveedor.setTelefono("987654321");
        proveedor.setEmail("test@proveedor.com");
        proveedor.setActivo(true);
        proveedor.setPuntaje(85);
        return proveedor;
    }

    @Test
    void testGuardarProveedor() {
        Proveedor proveedor = crearProveedorBase();
        Proveedor guardado = proveedorRepository.save(proveedor);

        assertThat(guardado).isNotNull();
        assertThat(guardado.getId()).isNotNull();
        assertThat(guardado.getNombre()).isEqualTo("Proveedor Test");
        assertThat(guardado.getActivo()).isTrue();
    }

    @Test
    void testBuscarPorId() {
        Proveedor proveedor = crearProveedorBase();
        Proveedor guardado = proveedorRepository.save(proveedor);

        Optional<Proveedor> encontrado = proveedorRepository.findById(guardado.getId());

        assertThat(encontrado).isPresent();
        assertThat(encontrado.get().getNombre()).isEqualTo("Proveedor Test");
    }

    @Test
    void testBuscarPorIdInexistente() {
        Optional<Proveedor> encontrado = proveedorRepository.findById(9999L);
        assertThat(encontrado).isEmpty();
    }

    @Test
    void testListarTodos() {
        proveedorRepository.save(crearProveedorBase("11111111-1"));
        proveedorRepository.save(crearProveedorBase("22222222-2"));

        List<Proveedor> todos = proveedorRepository.findAll();

        assertThat(todos).hasSize(2);
    }

    @Test
    void testFindByActivo() {
        Proveedor activo1 = crearProveedorBase("11111111-1");
        activo1.setNombre("Activo 1");
        proveedorRepository.save(activo1);

        Proveedor activo2 = crearProveedorBase("22222222-2");
        activo2.setNombre("Activo 2");
        proveedorRepository.save(activo2);

        Proveedor inactivo = crearProveedorBase("33333333-3");
        inactivo.setNombre("Inactivo");
        inactivo.setActivo(false);
        proveedorRepository.save(inactivo);

        List<Proveedor> activos = proveedorRepository.findByActivo(true);
        List<Proveedor> inactivos = proveedorRepository.findByActivo(false);

        assertThat(activos).hasSize(2);
        assertThat(inactivos).hasSize(1);
        assertThat(activos.get(0).getNombre()).isEqualTo("Activo 1");
    }

    @Test
    void testFindByActivoPaginado() {
        for (int i = 1; i <= 5; i++) {
            Proveedor p = crearProveedorBase("12345678-" + i);
            p.setNombre("Proveedor " + i);
            proveedorRepository.save(p);
        }

        Page<Proveedor> pagina = proveedorRepository.findByActivo(true, PageRequest.of(0, 3));

        assertThat(pagina.getContent()).hasSize(3);
        assertThat(pagina.getTotalElements()).isEqualTo(5);
        assertThat(pagina.getTotalPages()).isEqualTo(2);
    }

    @Test
    void testActualizar() {
        Proveedor proveedor = crearProveedorBase();
        Proveedor guardado = proveedorRepository.save(proveedor);

        guardado.setNombre("Modificado");
        guardado.setPuntaje(95);
        Proveedor actualizado = proveedorRepository.save(guardado);

        assertThat(actualizado.getNombre()).isEqualTo("Modificado");
        assertThat(actualizado.getPuntaje()).isEqualTo(95);
    }

    @Test
    void testEliminar() {
        Proveedor proveedor = crearProveedorBase();
        Proveedor guardado = proveedorRepository.save(proveedor);

        proveedorRepository.deleteById(guardado.getId());
        Optional<Proveedor> encontrado = proveedorRepository.findById(guardado.getId());

        assertThat(encontrado).isEmpty();
    }

    @Test
    void testExistsById() {
        Proveedor proveedor = crearProveedorBase();
        Proveedor guardado = proveedorRepository.save(proveedor);

        boolean existe = proveedorRepository.existsById(guardado.getId());
        boolean noExiste = proveedorRepository.existsById(9999L);

        assertThat(existe).isTrue();
        assertThat(noExiste).isFalse();
    }

    @Test
    void testCount() {
        proveedorRepository.save(crearProveedorBase("11111111-1"));
        proveedorRepository.save(crearProveedorBase("22222222-2"));

        long count = proveedorRepository.count();

        assertThat(count).isEqualTo(2);
    }
}