package org.example.recuperaciondiwbackend.servicios;

import org.example.recuperaciondiwbackend.dtos.EspecificacionDTO;
import org.example.recuperaciondiwbackend.dtos.PianoDTO;
import org.example.recuperaciondiwbackend.modelos.Caracteristica;
import org.example.recuperaciondiwbackend.modelos.Piano;
import org.example.recuperaciondiwbackend.modelos.TipoEspecificacion;
import org.example.recuperaciondiwbackend.modelos.ValorEspecificacion;
import org.example.recuperaciondiwbackend.repositorios.CaracteristicaRepositorio;
import org.example.recuperaciondiwbackend.repositorios.PianoRepositorio;
import org.example.recuperaciondiwbackend.repositorios.TipoEspecificacionRepositorio;
import org.example.recuperaciondiwbackend.repositorios.ValorEspecificacionRepositorio;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class PianoServicio {

    private final PianoRepositorio pianoRepositorio;
    private final CaracteristicaRepositorio caracteristicaRepositorio;
    private final TipoEspecificacionRepositorio tipoEspecificacionRepositorio;
    private final ValorEspecificacionRepositorio valorEspecificacionRepositorio;

    public PianoServicio(PianoRepositorio pianoRepositorio, CaracteristicaRepositorio caracteristicaRepositorio,
                         TipoEspecificacionRepositorio tipoEspecificacionRepositorio,
                         ValorEspecificacionRepositorio valorEspecificacionRepositorio) {
        this.pianoRepositorio = pianoRepositorio;
        this.caracteristicaRepositorio = caracteristicaRepositorio;
        this.tipoEspecificacionRepositorio = tipoEspecificacionRepositorio;
        this.valorEspecificacionRepositorio = valorEspecificacionRepositorio;
    }

    public List<Piano> listarTodos() {
        return pianoRepositorio.findAll();
    }

    public List<Piano> listarActivos() {
        return pianoRepositorio.findByEstado("activo");
    }

    public Optional<Piano> buscarPorId(Long id) {
        return pianoRepositorio.findById(id);
    }

    @Transactional
    public Piano guardarPiano(PianoDTO pianoDto) {
        Piano piano = new Piano();
        piano.setNombre(pianoDto.getNombre());
        piano.setModelo(pianoDto.getModelo());
        piano.setPrecio(pianoDto.getPrecio());
        piano.setOpcionAlquiler(pianoDto.getOpcionAlquiler());
        piano.setImagen(pianoDto.getImagen());
        piano.setDescripcion(pianoDto.getDescripcion());
        piano.setFechaCreacion(LocalDateTime.now());
        piano.setEstado("activo");

        Piano pianoBD = pianoRepositorio.save(piano);

        // Agregar características
        if (pianoDto.getCaracteristicas() != null) {
            Set<Caracteristica> caracteristicas = pianoDto.getCaracteristicas().stream()
                    .map(this::obtenerOCrearCaracteristica)
                    .collect(Collectors.toSet());
            pianoBD.setCaracteristicas(caracteristicas);
        }

        // Agregar especificaciones
        if (pianoDto.getEspecificaciones() != null) {
            Set<ValorEspecificacion> especificaciones = pianoDto.getEspecificaciones().stream()
                    .map(this::obtenerOCrearValorEspecificacion)
                    .collect(Collectors.toSet());
            pianoBD.setEspecificaciones(especificaciones);
        }

        return pianoRepositorio.save(pianoBD);
    }

    @Transactional
    public Piano actualizarPiano(Long id, PianoDTO pianoDto) {
        Piano piano = pianoRepositorio.findById(id)
                .orElseThrow(() -> new RuntimeException("Piano no encontrado"));

        piano.setNombre(pianoDto.getNombre());
        piano.setModelo(pianoDto.getModelo());
        piano.setPrecio(pianoDto.getPrecio());
        piano.setOpcionAlquiler(pianoDto.getOpcionAlquiler());
        piano.setImagen(pianoDto.getImagen());
        piano.setDescripcion(pianoDto.getDescripcion());
        piano.setEstado(pianoDto.getEstado());

        // Actualizar características
        if (pianoDto.getCaracteristicas() != null) {
            piano.getCaracteristicas().clear();
            Set<Caracteristica> caracteristicas = pianoDto.getCaracteristicas().stream()
                    .map(this::obtenerOCrearCaracteristica)
                    .collect(Collectors.toSet());
            piano.setCaracteristicas(caracteristicas);
        }

        // Actualizar especificaciones
        if (pianoDto.getEspecificaciones() != null) {
            piano.getEspecificaciones().clear();
            Set<ValorEspecificacion> especificaciones = pianoDto.getEspecificaciones().stream()
                    .map(this::obtenerOCrearValorEspecificacion)
                    .collect(Collectors.toSet());
            piano.setEspecificaciones(especificaciones);
        }

        return pianoRepositorio.save(piano);
    }

    @Transactional
    public void eliminarPiano(Long id) {
        Piano piano = pianoRepositorio.findById(id)
                .orElseThrow(() -> new RuntimeException("Piano no encontrado"));
        piano.setEstado("inactivo");
        pianoRepositorio.save(piano);
    }

    private Caracteristica obtenerOCrearCaracteristica(String descripcion) {
        return caracteristicaRepositorio.findByDescripcion(descripcion)
                .orElseGet(() -> {
                    Caracteristica nuevaCaracteristica = new Caracteristica();
                    nuevaCaracteristica.setDescripcion(descripcion);
                    return caracteristicaRepositorio.save(nuevaCaracteristica);
                });
    }

    private ValorEspecificacion obtenerOCrearValorEspecificacion(EspecificacionDTO especificacionDto) {
        TipoEspecificacion tipo = tipoEspecificacionRepositorio.findByNombre(especificacionDto.getTipo())
                .orElseGet(() -> {
                    TipoEspecificacion nuevoTipo = new TipoEspecificacion();
                    nuevoTipo.setNombre(especificacionDto.getTipo());
                    return tipoEspecificacionRepositorio.save(nuevoTipo);
                });

        return valorEspecificacionRepositorio.findByTipoAndValor(tipo, especificacionDto.getValor())
                .orElseGet(() -> {
                    ValorEspecificacion nuevoValor = new ValorEspecificacion();
                    nuevoValor.setTipo(tipo);
                    nuevoValor.setValor(especificacionDto.getValor());
                    return valorEspecificacionRepositorio.save(nuevoValor);
                });
    }

    public PianoDTO convertirADto(Piano piano) {
        PianoDTO dto = new PianoDTO();
        dto.setId(piano.getId());
        dto.setNombre(piano.getNombre());
        dto.setModelo(piano.getModelo());
        dto.setPrecio(piano.getPrecio());
        dto.setOpcionAlquiler(piano.getOpcionAlquiler());
        dto.setImagen(piano.getImagen());
        dto.setDescripcion(piano.getDescripcion());
        dto.setFechaCreacion(piano.getFechaCreacion());
        dto.setEstado(piano.getEstado());

        // Convertir características
        Set<String> caracteristicas = piano.getCaracteristicas().stream()
                .map(Caracteristica::getDescripcion)
                .collect(Collectors.toSet());
        dto.setCaracteristicas(caracteristicas);

        // Convertir especificaciones
        Set<EspecificacionDTO> especificaciones = piano.getEspecificaciones().stream()
                .map(ve -> new EspecificacionDTO(ve.getTipo().getNombre(), ve.getValor()))
                .collect(Collectors.toSet());
        dto.setEspecificaciones(especificaciones);

        return dto;
    }
}