package org.example.recuperaciondiwbackend.dtos.carrito;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.example.recuperaciondiwbackend.modelos.Carrito;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CarritoResponse {
    private Long id;
    private Long usuarioId;
    private String nombreUsuario;
    private Long pianoId;
    private String nombrePiano;
    private String modeloPiano;
    private BigDecimal precioPiano;
    private Integer cantidad;
    private LocalDateTime fechaAgregado;

    // Método estático para convertir desde entidad Carrito
    public static CarritoResponse fromCarrito(Carrito carrito) {
        CarritoResponse response = new CarritoResponse();
        response.setId(carrito.getId());
        response.setUsuarioId(carrito.getUsuario().getId());
        response.setNombreUsuario(carrito.getUsuario().getNombre());
        response.setPianoId(carrito.getPiano().getId());
        response.setNombrePiano(carrito.getPiano().getNombre());
        response.setModeloPiano(carrito.getPiano().getModelo());
        response.setPrecioPiano(carrito.getPiano().getPrecio());
        response.setCantidad(carrito.getCantidad());
        response.setFechaAgregado(carrito.getFechaAgregado());

        return response;
    }
}