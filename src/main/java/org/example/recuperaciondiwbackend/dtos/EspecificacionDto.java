package org.example.recuperaciondiwbackend.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class EspecificacionDto {
    private String tipo;
    private String valor;
}