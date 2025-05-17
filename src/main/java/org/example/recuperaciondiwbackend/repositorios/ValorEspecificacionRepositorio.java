package org.example.recuperaciondiwbackend.repositorios;

import org.example.recuperaciondiwbackend.modelos.TipoEspecificacion;
import org.example.recuperaciondiwbackend.modelos.ValorEspecificacion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ValorEspecificacionRepositorio extends JpaRepository<ValorEspecificacion, Long> {
    List<ValorEspecificacion> findByTipo(TipoEspecificacion tipo);
    Optional<ValorEspecificacion> findByTipoAndValor(TipoEspecificacion tipo, String valor);
    
    @Query(value = "SELECT ve.* FROM tienda_pianos.valores_especificacion ve " +
           "JOIN tienda_pianos.piano_especificaciones pe ON ve.id = pe.valor_especificacion_id " +
           "WHERE pe.piano_id = :pianoId", nativeQuery = true)
    List<ValorEspecificacion> findByPianoId(@Param("pianoId") Long pianoId);
    
    @Query(value = "SELECT ve.* FROM tienda_pianos.valores_especificacion ve " +
           "JOIN tienda_pianos.piano_especificaciones pe ON ve.id = pe.valor_especificacion_id " +
           "WHERE pe.piano_id IN :pianoIds", nativeQuery = true)
    List<ValorEspecificacion> findByPianoIdIn(@Param("pianoIds") List<Long> pianoIds);
}