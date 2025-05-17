package org.example.recuperaciondiwbackend.dtos.pianos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;
import org.example.recuperaciondiwbackend.dtos.pianos.PianoRelacionesDTO.CaracteristicaDTO;
import org.example.recuperaciondiwbackend.dtos.pianos.PianoRelacionesDTO.ValorEspecificacionDTO;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PianoDTO {
    private Long id;
    private String nombre;
    private String modelo;
    private BigDecimal precio;
    private BigDecimal opcionAlquiler;
    private String imagen;
    private String descripcion;
    private LocalDateTime fechaCreacion;
    private String estado;
    private Set<CaracteristicaDTO> caracteristicas = new HashSet<>();
    private Set<ValorEspecificacionDTO> especificaciones = new HashSet<>();
}