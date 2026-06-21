package com.bookpoint.supplier.repository;

import com.bookpoint.supplier.model.SolicitudReposicion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SolicitudReposicionRepository extends JpaRepository<SolicitudReposicion, Long> {
}
