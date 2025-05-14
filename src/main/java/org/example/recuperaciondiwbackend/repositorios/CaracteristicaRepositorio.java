package org.example.recuperaciondiwbackend.repositorios;

import org.example.recuperaciondiwbackend.modelos.Caracteristica;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CaracteristicaRepositorio extends JpaRepository<Caracteristica, Long> {
    Optional<Caracteristica> findByDescripcion(String descripcion);
}