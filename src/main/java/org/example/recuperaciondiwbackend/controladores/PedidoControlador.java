package org.example.recuperaciondiwbackend.controladores;

import jakarta.validation.Valid;
import org.example.recuperaciondiwbackend.Utils.SecurityUtils;
import org.example.recuperaciondiwbackend.dtos.pedidos.CrearPedidoRequest;
import org.example.recuperaciondiwbackend.dtos.pedidos.PedidoResponse;
import org.example.recuperaciondiwbackend.modelos.Pedido;
import org.example.recuperaciondiwbackend.modelos.Usuario;
import org.example.recuperaciondiwbackend.servicios.PedidoServicio;
import org.example.recuperaciondiwbackend.servicios.UsuarioServicio;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/pedidos")
@PreAuthorize("hasAnyRole('USER', 'ADMIN')")
public class PedidoControlador {

    private final PedidoServicio pedidoServicio;
    private final SecurityUtils securityUtils;

    public PedidoControlador(PedidoServicio pedidoServicio, SecurityUtils securityUtils) {
        this.pedidoServicio = pedidoServicio;
        this.securityUtils = securityUtils;
    }

    @GetMapping
    public ResponseEntity<List<PedidoResponse>> listarPedidosUsuario() {
        Long usuarioId = securityUtils.obtenerIdUsuarioActual();
        List<Pedido> pedidosRespuesta = pedidoServicio.listarPorUsuario(usuarioId);
        List<PedidoResponse> pedidosResponse = new ArrayList<>();

        for (Pedido p : pedidosRespuesta) {
            pedidosResponse.add(PedidoResponse.fromPedido(p));
        }

        return ResponseEntity.ok(pedidosResponse);
    }

    @GetMapping("/{id}")
    public ResponseEntity<PedidoResponse> obtenerPedido(@PathVariable Long id) {
        Long usuarioId = securityUtils.obtenerIdUsuarioActual();
        return pedidoServicio.buscarPorId(id)
                .filter(pedido -> pedido.getUsuario().getId().equals(usuarioId))
                .map(PedidoResponse::fromPedido)  // Convertir a DTO
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<PedidoResponse> crearPedido(@Valid @RequestBody CrearPedidoRequest request) {
        Long usuarioId = securityUtils.obtenerIdUsuarioActual();
        Pedido pedido = pedidoServicio.crearPedidoDesdeCarrito(
                usuarioId,
                request.getDireccionEnvio(),
                request.getMetodoPago()
        );
        return ResponseEntity.ok(PedidoResponse.fromPedido(pedido));
    }
}