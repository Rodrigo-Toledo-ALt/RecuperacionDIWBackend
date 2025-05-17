package org.example.recuperaciondiwbackend.dtos.pianos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


public class PianoRelacionesDTO {

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CaracteristicaDTO {
        private Long id;
        private String descripcion;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class TipoEspecificacionDTO {
        private Long id;
        private String nombre;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ValorEspecificacionDTO {
        private Long id;
        private TipoEspecificacionDTO tipo;
        private String valor;
    }
}