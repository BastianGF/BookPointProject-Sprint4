package com.bookpoint.supplier.repository;

import com.bookpoint.supplier.model.HistorialCompras;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface HistorialComprasRepository extends JpaRepository<HistorialCompras, Long> {
    List<HistorialCompras> findByProveedorId(Long proveedorId);
}
