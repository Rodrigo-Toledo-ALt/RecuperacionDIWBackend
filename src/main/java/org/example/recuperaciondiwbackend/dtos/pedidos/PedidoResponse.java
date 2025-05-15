package org.example.recuperaciondiwbackend.dtos.pedidos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.example.recuperaciondiwbackend.modelos.ItemPedido;
import org.example.recuperaciondiwbackend.modelos.Pedido;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PedidoResponse {
    private Long id;
    private Long usuarioId;
    private String nombreUsuario;
    private LocalDateTime fechaPedido;
    private String estado;
    private BigDecimal total;
    private String direccionEnvio;
    private String metodoPago;
    private List<ItemPedidoResponse> items = new ArrayList<>();

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ItemPedidoResponse {
        private Long id;
        private Long pianoId;
        private String nombrePiano;
        private String modeloPiano;
        private Integer cantidad;
        private BigDecimal precioUnitario;
        private BigDecimal subtotal;
    }

    public static PedidoResponse fromPedido(Pedido pedido) {
        PedidoResponse response = new PedidoResponse();
        response.setId(pedido.getId());
        response.setUsuarioId(pedido.getUsuario().getId());
        response.setNombreUsuario(pedido.getUsuario().getNombre());
        response.setFechaPedido(pedido.getFechaPedido());
        response.setEstado(pedido.getEstado());
        response.setTotal(pedido.getTotal());
        response.setDireccionEnvio(pedido.getDireccionEnvio());
        response.setMetodoPago(pedido.getMetodoPago());

        // Convertir los items del pedido
        response.setItems(pedido.getItems().stream()
                .map(item -> mapItemToResponse(item))
                .collect(Collectors.toList()));

        return response;
    }

    private static ItemPedidoResponse mapItemToResponse(ItemPedido item) {
        ItemPedidoResponse itemResponse = new ItemPedidoResponse();
        itemResponse.setId(item.getId());
        itemResponse.setPianoId(item.getPiano().getId());
        itemResponse.setNombrePiano(item.getPiano().getNombre());
        itemResponse.setModeloPiano(item.getPiano().getModelo());
        itemResponse.setCantidad(item.getCantidad());
        itemResponse.setPrecioUnitario(item.getPrecioUnitario());
        itemResponse.setSubtotal(item.getSubtotal());
        return itemResponse;
    }
}