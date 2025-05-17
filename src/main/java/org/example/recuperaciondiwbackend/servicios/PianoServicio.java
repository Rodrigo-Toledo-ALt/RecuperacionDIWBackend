package org.example.recuperaciondiwbackend.servicios;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.example.recuperaciondiwbackend.dtos.pianos.PianoDTO;
import org.example.recuperaciondiwbackend.dtos.pianos.PianoRelacionesDTO.CaracteristicaDTO;
import org.example.recuperaciondiwbackend.dtos.pianos.PianoRelacionesDTO.TipoEspecificacionDTO;
import org.example.recuperaciondiwbackend.dtos.pianos.PianoRelacionesDTO.ValorEspecificacionDTO;
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
import java.util.HashSet;
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
        return pianoRepositorio.findAllWithRelations();
    }

    public List<Piano> listarActivos() {
        return pianoRepositorio.findByEstadoWithRelations("activo");
    }

    public Optional<Piano> buscarPorId(Long id) {
        return pianoRepositorio.findByIdWithRelations(id);
    }

    @Transactional
    public Piano guardarPiano(PianoDTO pianoDto) {
        // 1. Crear y guardar el piano primero sin relaciones
        Piano piano = new Piano();
        piano.setNombre(pianoDto.getNombre());
        piano.setModelo(pianoDto.getModelo());
        piano.setPrecio(pianoDto.getPrecio());
        piano.setOpcionAlquiler(pianoDto.getOpcionAlquiler());
        piano.setImagen(pianoDto.getImagen());
        piano.setDescripcion(pianoDto.getDescripcion());
        piano.setFechaCreacion(LocalDateTime.now());
        piano.setEstado("activo");

        // Inicializar con colecciones vacías para evitar NullPointerExceptions
        piano.setCaracteristicas(new HashSet<>());
        piano.setEspecificaciones(new HashSet<>());

        // 2. Guardar el piano primero para obtener su ID
        Piano pianoBD = pianoRepositorio.save(piano);

        // 3. Limpiar la sesión para evitar problemas de modificación concurrente
        pianoRepositorio.flush();

        // 4. Procesar y guardar características una por una
        if (pianoDto.getCaracteristicas() != null) {
            for (CaracteristicaDTO caracteristicaDTO : pianoDto.getCaracteristicas()) {
                Caracteristica caracteristica =
                        obtenerOCrearCaracteristica(caracteristicaDTO.getDescripcion());

                // Usar el método del repositorio
                pianoRepositorio.agregarCaracteristica(pianoBD.getId(), caracteristica.getId());
            }
        }

        // 5. Procesar y guardar especificaciones una por una
        if (pianoDto.getEspecificaciones() != null) {
            for (ValorEspecificacionDTO valorEspecificacionDTO : pianoDto.getEspecificaciones()) {
                String nombreTipo = valorEspecificacionDTO.getTipo().getNombre();
                String valor = valorEspecificacionDTO.getValor();

                ValorEspecificacion valEspec =
                        obtenerOCrearValorEspecificacion(nombreTipo, valor);

                // Usar el método del repositorio
                pianoRepositorio.agregarEspecificacion(pianoBD.getId(), valEspec.getId());
            }
        }

        // 6. Recargar el piano con todas sus relaciones
        return pianoRepositorio.findById(pianoBD.getId())
                .orElseThrow(() -> new RuntimeException("Error al guardar el piano"));
    }



    @Transactional
    public Piano actualizarPiano(Long id, PianoDTO pianoDto) {
        // 1. Buscar y actualizar los datos básicos del piano
        Piano piano = pianoRepositorio.findById(id)
                .orElseThrow(() -> new RuntimeException("Piano no encontrado"));

        piano.setNombre(pianoDto.getNombre());
        piano.setModelo(pianoDto.getModelo());
        piano.setPrecio(pianoDto.getPrecio());
        piano.setOpcionAlquiler(pianoDto.getOpcionAlquiler());
        piano.setImagen(pianoDto.getImagen());
        piano.setDescripcion(pianoDto.getDescripcion());
        piano.setEstado(pianoDto.getEstado());

        // 2. Guardar los cambios básicos
        Piano pianoActualizado = pianoRepositorio.save(piano);

        // 3. Eliminar todas las relaciones existentes
        pianoRepositorio.eliminarTodasLasCaracteristicas(id);
        pianoRepositorio.eliminarTodasLasEspecificaciones(id);

        // 4. Crear nuevas relaciones para características
        if (pianoDto.getCaracteristicas() != null) {
            for (CaracteristicaDTO caracteristicaDTO : pianoDto.getCaracteristicas()) {
                // 4.1 Obtener o crear la característica
                Caracteristica caracteristica;

                if (caracteristicaDTO.getId() != null) {
                    // Si tiene ID, intentamos encontrarla primero
                    caracteristica = caracteristicaRepositorio.findById(caracteristicaDTO.getId())
                            .orElseGet(() -> obtenerOCrearCaracteristica(caracteristicaDTO.getDescripcion()));
                } else {
                    // Si no tiene ID, buscamos por descripción
                    caracteristica = obtenerOCrearCaracteristica(caracteristicaDTO.getDescripcion());
                }

                // 4.2 Crear la relación
                pianoRepositorio.agregarCaracteristica(id, caracteristica.getId());
            }
        }

        // 5. Crear nuevas relaciones para especificaciones
        if (pianoDto.getEspecificaciones() != null) {
            for (ValorEspecificacionDTO especificacionDTO : pianoDto.getEspecificaciones()) {
                // 5.1 Obtener el tipo
                TipoEspecificacion tipo;

                if (especificacionDTO.getTipo().getId() != null) {
                    tipo = tipoEspecificacionRepositorio.findById(especificacionDTO.getTipo().getId())
                            .orElseGet(() -> obtenerOCrearTipoEspecificacion(especificacionDTO.getTipo().getNombre()));
                } else {
                    tipo = obtenerOCrearTipoEspecificacion(especificacionDTO.getTipo().getNombre());
                }

                // 5.2 Obtener o crear el valor de especificación
                ValorEspecificacion valorEspec =
                        obtenerOCrearValorEspecificacion(tipo.getNombre(), especificacionDTO.getValor());

                // 5.3 Crear la relación
                pianoRepositorio.agregarEspecificacion(id, valorEspec.getId());
            }
        }

        // 6. Recargar el piano con todas sus relaciones actualizadas
        return pianoRepositorio.findById(id)
                .orElseThrow(() -> new RuntimeException("Error al actualizar el piano"));
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

    private ValorEspecificacion obtenerOCrearValorEspecificacion(String nombreTipo, String valor) {
        TipoEspecificacion tipo = tipoEspecificacionRepositorio.findByNombre(nombreTipo)
                .orElseGet(() -> {
                    TipoEspecificacion nuevoTipo = new TipoEspecificacion();
                    nuevoTipo.setNombre(nombreTipo);
                    return tipoEspecificacionRepositorio.save(nuevoTipo);
                });

        return valorEspecificacionRepositorio.findByTipoAndValor(tipo, valor)
                .orElseGet(() -> {
                    ValorEspecificacion nuevoValor = new ValorEspecificacion();
                    nuevoValor.setTipo(tipo);
                    nuevoValor.setValor(valor);
                    return valorEspecificacionRepositorio.save(nuevoValor);
                });
    }

    // Método auxiliar para obtener o crear un tipo de especificación
    private TipoEspecificacion obtenerOCrearTipoEspecificacion(String nombre) {
        return tipoEspecificacionRepositorio.findByNombre(nombre)
                .orElseGet(() -> {
                    TipoEspecificacion nuevoTipo = new TipoEspecificacion();
                    nuevoTipo.setNombre(nombre);
                    return tipoEspecificacionRepositorio.save(nuevoTipo);
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
        Set<CaracteristicaDTO> caracteristicasDTO = new HashSet<>();
        for (Caracteristica caracteristica : piano.getCaracteristicas()) {
            CaracteristicaDTO caracteristicaDTO = new CaracteristicaDTO();
            caracteristicaDTO.setId(caracteristica.getId());
            caracteristicaDTO.setDescripcion(caracteristica.getDescripcion());
            caracteristicasDTO.add(caracteristicaDTO);
        }
        dto.setCaracteristicas(caracteristicasDTO);

        // Convertir especificaciones
        Set<ValorEspecificacionDTO> especificacionesDTO = new HashSet<>();
        for (ValorEspecificacion valorEspecificacion : piano.getEspecificaciones()) {
            TipoEspecificacionDTO tipoDTO = new TipoEspecificacionDTO();
            tipoDTO.setId(valorEspecificacion.getTipo().getId());
            tipoDTO.setNombre(valorEspecificacion.getTipo().getNombre());

            ValorEspecificacionDTO valorEspecificacionDTO = new ValorEspecificacionDTO();
            valorEspecificacionDTO.setId(valorEspecificacion.getId());
            valorEspecificacionDTO.setTipo(tipoDTO);
            valorEspecificacionDTO.setValor(valorEspecificacion.getValor());

            especificacionesDTO.add(valorEspecificacionDTO);
        }
        dto.setEspecificaciones(especificacionesDTO);

        return dto;
    }
}