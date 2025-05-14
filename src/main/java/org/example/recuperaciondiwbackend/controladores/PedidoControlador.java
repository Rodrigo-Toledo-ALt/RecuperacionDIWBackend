package org.example.recuperaciondiwbackend.controladores;

import org.example.recuperaciondiwbackend.modelos.Pedido;
import org.example.recuperaciondiwbackend.modelos.Usuario;
import org.example.recuperaciondiwbackend.servicios.PedidoServicio;
import org.example.recuperaciondiwbackend.servicios.UsuarioServicio;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/pedidos")
@PreAuthorize("hasAnyRole('USER', 'ADMIN')")
public class PedidoControlador {

    private final PedidoServicio pedidoServicio;
    private final UsuarioServicio usuarioServicio;

    public PedidoControlador(PedidoServicio pedidoServicio, UsuarioServicio usuarioServicio) {
        this.pedidoServicio = pedidoServicio;
        this.usuarioServicio = usuarioServicio;
    }

    @GetMapping
    public ResponseEntity<List<Pedido>> listarPedidosUsuario() {
        Long usuarioId = obtenerUsuarioActual().getId();
        return ResponseEntity.ok(pedidoServicio.listarPorUsuario(usuarioId));
    }

    @GetMapping("/{id}")
    public ResponseEntity<Pedido> obtenerPedido(@PathVariable Long id) {
        Long usuarioId = obtenerUsuarioActual().getId();
        return pedidoServicio.buscarPorId(id)
                .filter(pedido -> pedido.getUsuario().getId().equals(usuarioId))
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<Pedido> crearPedido(@RequestBody Map<String, String> datos) {
        Long usuarioId = obtenerUsuarioActual().getId();
        String direccionEnvio = datos.get("direccionEnvio");
        String metodoPago = datos.get("metodoPago");
        
        return ResponseEntity.ok(pedidoServicio.crearPedidoDesdeCarrito(usuarioId, direccionEnvio, metodoPago));
    }

    private Usuario obtenerUsuarioActual() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String email = auth.getName();
        return usuarioServicio.buscarPorEmail(email)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
    }
}