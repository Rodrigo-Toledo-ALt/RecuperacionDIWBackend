package org.example.recuperaciondiwbackend.dtos.pianos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class EspecificacionDTO {
    private String tipo;
    private String valor;
}