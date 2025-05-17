package org.example.recuperaciondiwbackend.repositorios;

import org.example.recuperaciondiwbackend.modelos.Piano;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

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


}