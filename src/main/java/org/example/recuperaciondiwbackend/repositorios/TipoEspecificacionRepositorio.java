package org.example.recuperaciondiwbackend.repositorios;

import org.example.recuperaciondiwbackend.modelos.TipoEspecificacion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface TipoEspecificacionRepositorio extends JpaRepository<TipoEspecificacion, Long> {
    Optional<TipoEspecificacion> findByNombre(String nombre);
}