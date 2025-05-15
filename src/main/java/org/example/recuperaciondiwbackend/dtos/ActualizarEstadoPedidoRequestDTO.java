package org.example.recuperaciondiwbackend.dtos;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.example.recuperaciondiwbackend.modelos.EstadoPedido;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ActualizarEstadoPedidoRequestDTO {

    @NotBlank(message = "El estado es obligatorio")
    private EstadoPedido estado;
}