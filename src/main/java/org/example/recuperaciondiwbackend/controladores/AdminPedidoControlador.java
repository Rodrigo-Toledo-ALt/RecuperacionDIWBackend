package org.example.recuperaciondiwbackend.controladores;

import jakarta.validation.Valid;
import org.example.recuperaciondiwbackend.dtos.ActualizarEstadoPedidoRequestDTO;
import org.example.recuperaciondiwbackend.dtos.pedidos.PedidoResponse;
import org.example.recuperaciondiwbackend.modelos.Pedido;
import org.example.recuperaciondiwbackend.servicios.PedidoServicio;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/admin/pedidos")
@PreAuthorize("hasRole('ADMIN')")
public class AdminPedidoControlador {

    private final PedidoServicio pedidoServicio;

    public AdminPedidoControlador(PedidoServicio pedidoServicio) {
        this.pedidoServicio = pedidoServicio;
    }

    @GetMapping
    public ResponseEntity<List<PedidoResponse>> listarTodosPedidos() {
        List<Pedido> pedidos = pedidoServicio.listarTodos();
        List<PedidoResponse> pedidosResponse = new ArrayList<>();

        for (Pedido p : pedidos) {
            pedidosResponse.add(PedidoResponse.fromPedido(p));
        }

        return ResponseEntity.ok(pedidosResponse);
    }

    @GetMapping("/{id}")
    public ResponseEntity<PedidoResponse> obtenerPedido(@PathVariable Long id) {
        return pedidoServicio.buscarPorId(id)
                .map(PedidoResponse::fromPedido)  // Convertir a DTO
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/{id}/estado")
    public ResponseEntity<PedidoResponse> actualizarEstadoPedido(
            @PathVariable Long id,
            @Valid @RequestBody ActualizarEstadoPedidoRequestDTO request) {

        Pedido pedidoActualizado = pedidoServicio.actualizarEstadoPedido(id, request.getEstado());
        return ResponseEntity.ok(PedidoResponse.fromPedido(pedidoActualizado));
    }
}