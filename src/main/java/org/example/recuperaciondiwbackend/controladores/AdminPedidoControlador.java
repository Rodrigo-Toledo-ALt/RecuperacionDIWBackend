package org.example.recuperaciondiwbackend.controladores;

import jakarta.validation.Valid;
import org.example.recuperaciondiwbackend.dtos.ActualizarEstadoPedidoRequestDTO;
import org.example.recuperaciondiwbackend.modelos.Pedido;
import org.example.recuperaciondiwbackend.servicios.PedidoServicio;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

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
    public ResponseEntity<List<Pedido>> listarTodosPedidos() {
        return ResponseEntity.ok(pedidoServicio.listarTodos());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Pedido> obtenerPedido(@PathVariable Long id) {
        return pedidoServicio.buscarPorId(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/{id}/estado")
    public ResponseEntity<Pedido> actualizarEstadoPedido(
            @PathVariable Long id,
            @Valid @RequestBody ActualizarEstadoPedidoRequestDTO request) {

        return ResponseEntity.ok(pedidoServicio.actualizarEstadoPedido(id, request.getEstado()));
    }
}