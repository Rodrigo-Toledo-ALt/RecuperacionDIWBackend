package org.example.recuperaciondiwbackend.repositorios;

import org.example.recuperaciondiwbackend.modelos.Caracteristica;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.Set;

@Repository
public interface CaracteristicaRepositorio extends JpaRepository<Caracteristica, Long> {
    Optional<Caracteristica> findByDescripcion(String descripcion);
    
    @Query(value = "SELECT c.* FROM tienda_pianos.caracteristicas c " +
           "JOIN tienda_pianos.piano_caracteristicas pc ON c.id = pc.caracteristica_id " +
           "WHERE pc.piano_id = :pianoId", nativeQuery = true)
    List<Caracteristica> findByPianoId(@Param("pianoId") Long pianoId);
    
    @Query(value = "SELECT c.* FROM tienda_pianos.caracteristicas c " +
           "JOIN tienda_pianos.piano_caracteristicas pc ON c.id = pc.caracteristica_id " +
           "WHERE pc.piano_id IN :pianoIds", nativeQuery = true)
    List<Caracteristica> findByPianoIdIn(@Param("pianoIds") List<Long> pianoIds);
}