package com.bookpoint.logistic.repository;

import com.bookpoint.logistic.model.Transportista;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TransportistaRepository extends JpaRepository<Transportista, Long> {
    List<Transportista> findByDisponible(Boolean disponible);
}
