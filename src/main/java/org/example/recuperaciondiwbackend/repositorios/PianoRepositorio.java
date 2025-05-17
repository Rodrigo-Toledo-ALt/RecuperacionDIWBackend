package org.example.recuperaciondiwbackend.repositorios;

import org.example.recuperaciondiwbackend.modelos.Caracteristica;
import org.example.recuperaciondiwbackend.modelos.Piano;
import org.example.recuperaciondiwbackend.modelos.ValorEspecificacion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PianoRepositorio extends JpaRepository<Piano, Long> {
    List<Piano> findByEstado(String estado);
    List<Piano> findByNombreContainingIgnoreCase(String nombre);
    List<Piano> findByModeloContainingIgnoreCase(String modelo);

    @Modifying
    @Query(value = "INSERT INTO tienda_pianos.piano_caracteristicas (piano_id, caracteristica_id) " +
            "VALUES (:pianoId, :caracId) " +
            "ON CONFLICT DO NOTHING", nativeQuery = true)
    void agregarCaracteristica(@Param("pianoId") Long pianoId, @Param("caracId") Long caracteristicaId);

    @Modifying
    @Query(value = "INSERT INTO tienda_pianos.piano_especificaciones (piano_id, valor_especificacion_id) " +
            "VALUES (:pianoId, :valorId) " +
            "ON CONFLICT DO NOTHING", nativeQuery = true)
    void agregarEspecificacion(@Param("pianoId") Long pianoId, @Param("valorId") Long valorEspecificacionId);


    // Métodos para eliminar relaciones
    @Modifying
    @Query(value = "DELETE FROM tienda_pianos.piano_caracteristicas WHERE piano_id = :pianoId",
            nativeQuery = true)
    void eliminarTodasLasCaracteristicas(@Param("pianoId") Long pianoId);

    @Modifying
    @Query(value = "DELETE FROM tienda_pianos.piano_especificaciones WHERE piano_id = :pianoId",
            nativeQuery = true)
    void eliminarTodasLasEspecificaciones(@Param("pianoId") Long pianoId);

    @Query("SELECT DISTINCT p FROM Piano p LEFT JOIN FETCH p.caracteristicas WHERE p.estado = :estado")
    List<Piano> findByEstadoWithCaracteristicas(@Param("estado") String estado);
    
    @Query("SELECT DISTINCT p FROM Piano p LEFT JOIN FETCH p.especificaciones e LEFT JOIN FETCH e.tipo WHERE p.estado = :estado")
    List<Piano> findByEstadoWithEspecificaciones(@Param("estado") String estado);
    
    default List<Piano> findByEstadoWithRelations(@Param("estado") String estado) {
        List<Piano> pianosWithCaracteristicas = findByEstadoWithCaracteristicas(estado);
        List<Piano> pianosWithEspecificaciones = findByEstadoWithEspecificaciones(estado);
        
        // Combine the results - use first query result as base
        for (Piano pianoWithSpecs : pianosWithEspecificaciones) {
            pianosWithCaracteristicas.stream()
                .filter(p -> p.getId().equals(pianoWithSpecs.getId()))
                .findFirst()
                .ifPresent(piano -> piano.setEspecificaciones(pianoWithSpecs.getEspecificaciones()));
        }
        
        return pianosWithCaracteristicas;
    }

    @Query("SELECT DISTINCT p FROM Piano p LEFT JOIN FETCH p.caracteristicas WHERE p.id = :id")
    Optional<Piano> findByIdWithCaracteristicas(@Param("id") Long id);
    
    @Query("SELECT DISTINCT p FROM Piano p LEFT JOIN FETCH p.especificaciones e LEFT JOIN FETCH e.tipo WHERE p.id = :id")
    Optional<Piano> findByIdWithEspecificaciones(@Param("id") Long id);
    
    default Optional<Piano> findByIdWithRelations(@Param("id") Long id) {
        Optional<Piano> pianoWithCaracteristicas = findByIdWithCaracteristicas(id);
        if (!pianoWithCaracteristicas.isPresent()) {
            return Optional.empty();
        }
        
        Optional<Piano> pianoWithEspecificaciones = findByIdWithEspecificaciones(id);
        if (pianoWithEspecificaciones.isPresent()) {
            pianoWithCaracteristicas.get().setEspecificaciones(pianoWithEspecificaciones.get().getEspecificaciones());
        }
        
        return pianoWithCaracteristicas;
    }

    @Query("SELECT DISTINCT p FROM Piano p LEFT JOIN FETCH p.caracteristicas")
    List<Piano> findAllWithCaracteristicas();
    
    @Query("SELECT DISTINCT p FROM Piano p LEFT JOIN FETCH p.especificaciones e LEFT JOIN FETCH e.tipo")
    List<Piano> findAllWithEspecificaciones();
    
    default List<Piano> findAllWithRelations() {
        List<Piano> pianosWithCaracteristicas = findAllWithCaracteristicas();
        List<Piano> pianosWithEspecificaciones = findAllWithEspecificaciones();
        
        // Combine the results - use first query result as base
        for (Piano pianoWithSpecs : pianosWithEspecificaciones) {
            pianosWithCaracteristicas.stream()
                .filter(p -> p.getId().equals(pianoWithSpecs.getId()))
                .findFirst()
                .ifPresent(piano -> piano.setEspecificaciones(pianoWithSpecs.getEspecificaciones()));
        }
        
        return pianosWithCaracteristicas;
    }










}