package com.bookpoint.supplier.repository;

import com.bookpoint.supplier.model.Proveedor;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

import org.springframework.data.domain.Page;


@Repository
public interface ProveedorRepository extends JpaRepository<Proveedor, Long> {
    Page<Proveedor> findByActivo(Boolean activo, PageRequest pageRequest);
    List<Proveedor> findByActivo(Boolean activo);
}
