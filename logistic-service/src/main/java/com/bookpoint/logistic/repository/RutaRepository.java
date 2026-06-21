package com.bookpoint.logistic.repository;

import com.bookpoint.logistic.model.Ruta;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RutaRepository extends JpaRepository<Ruta, Long> {
    List<Ruta> findBySucursalId(Long sucursalId);
}
