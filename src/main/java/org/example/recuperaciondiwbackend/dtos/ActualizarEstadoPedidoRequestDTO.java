package org.example.recuperaciondiwbackend.dtos;


import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.example.recuperaciondiwbackend.modelos.EstadoPedido;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ActualizarEstadoPedidoRequestDTO {

    @NotNull(message = "El estado es obligatorio")
    private EstadoPedido estado;
}