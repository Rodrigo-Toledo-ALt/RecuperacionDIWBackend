package org.example.recuperaciondiwbackend.repositorios;

import org.example.recuperaciondiwbackend.modelos.Piano;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PianoRepositorio extends JpaRepository<Piano, Long> {
    List<Piano> findByEstado(String estado);
    List<Piano> findByNombreContainingIgnoreCase(String nombre);
    List<Piano> findByModeloContainingIgnoreCase(String modelo);
}