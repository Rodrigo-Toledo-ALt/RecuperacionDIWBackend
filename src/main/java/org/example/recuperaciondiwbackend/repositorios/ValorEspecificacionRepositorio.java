package org.example.recuperaciondiwbackend.repositorios;

import org.example.recuperaciondiwbackend.modelos.TipoEspecificacion;
import org.example.recuperaciondiwbackend.modelos.ValorEspecificacion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ValorEspecificacionRepositorio extends JpaRepository<ValorEspecificacion, Long> {
    List<ValorEspecificacion> findByTipo(TipoEspecificacion tipo);
    Optional<ValorEspecificacion> findByTipoAndValor(TipoEspecificacion tipo, String valor);
}