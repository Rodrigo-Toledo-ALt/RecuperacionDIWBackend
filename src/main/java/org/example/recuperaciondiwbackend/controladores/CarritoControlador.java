package org.example.recuperaciondiwbackend.controladores;

import org.example.recuperaciondiwbackend.dtos.MensajeResponse;
import org.example.recuperaciondiwbackend.modelos.Carrito;
import org.example.recuperaciondiwbackend.modelos.Usuario;
import org.example.recuperaciondiwbackend.servicios.CarritoServicio;
import org.example.recuperaciondiwbackend.servicios.UsuarioServicio;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/carrito")
@PreAuthorize("hasAnyRole('USER', 'ADMIN')")
public class CarritoControlador {

    private final CarritoServicio carritoServicio;
    private final UsuarioServicio usuarioServicio;

    public CarritoControlador(CarritoServicio carritoServicio, UsuarioServicio usuarioServicio) {
        this.carritoServicio = carritoServicio;
        this.usuarioServicio = usuarioServicio;
    }

    @GetMapping
    public ResponseEntity<List<Carrito>> obtenerCarrito() {
        Long usuarioId = obtenerUsuarioActual().getId();
        return ResponseEntity.ok(carritoServicio.obtenerCarritoPorUsuario(usuarioId));
    }

    @PostMapping("/agregar")
    public ResponseEntity<Carrito> agregarAlCarrito(@RequestBody Map<String, Object> datos) {
        Long usuarioId = obtenerUsuarioActual().getId();
        Long pianoId = Long.valueOf(datos.get("pianoId").toString());
        Integer cantidad = Integer.valueOf(datos.get("cantidad").toString());
        
        return ResponseEntity.ok(carritoServicio.agregarAlCarrito(usuarioId, pianoId, cantidad));
    }

    @PutMapping("/{itemId}")
    public ResponseEntity<MensajeResponse> actualizarCantidad(
            @PathVariable Long itemId,
            @RequestBody Map<String, Integer> datos) {
        
        carritoServicio.actualizarCantidad(itemId, datos.get("cantidad"));
        return ResponseEntity.ok(new MensajeResponse("Cantidad actualizada correctamente"));
    }

    @DeleteMapping("/{itemId}")
    public ResponseEntity<MensajeResponse> eliminarItem(@PathVariable Long itemId) {
        carritoServicio.eliminarDelCarrito(itemId);
        return ResponseEntity.ok(new MensajeResponse("Item eliminado del carrito"));
    }

    @DeleteMapping
    public ResponseEntity<MensajeResponse> vaciarCarrito() {
        Long usuarioId = obtenerUsuarioActual().getId();
        carritoServicio.vaciarCarrito(usuarioId);
        return ResponseEntity.ok(new MensajeResponse("Carrito vaciado correctamente"));
    }

    private Usuario obtenerUsuarioActual() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String email = auth.getName();
        return usuarioServicio.buscarPorEmail(email)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
    }
}