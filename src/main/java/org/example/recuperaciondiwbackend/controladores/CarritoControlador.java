package org.example.recuperaciondiwbackend.controladores;

import jakarta.validation.Valid;
import org.example.recuperaciondiwbackend.Utils.SecurityUtils;
import org.example.recuperaciondiwbackend.dtos.carrito.ActualizarCantidadRequest;
import org.example.recuperaciondiwbackend.dtos.carrito.AgregarAlCarritoRequest;
import org.example.recuperaciondiwbackend.dtos.MensajeResponseDTO;
import org.example.recuperaciondiwbackend.dtos.carrito.CarritoResponse;
import org.example.recuperaciondiwbackend.modelos.Carrito;
import org.example.recuperaciondiwbackend.servicios.CarritoServicio;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/carrito")
@PreAuthorize("hasAnyRole('USER', 'ADMIN')")
public class CarritoControlador {

    private final CarritoServicio carritoServicio;
    private final SecurityUtils securityUtils;

    public CarritoControlador(CarritoServicio carritoServicio, SecurityUtils securityUtils) {
        this.carritoServicio = carritoServicio;
        this.securityUtils = securityUtils;
    }

    @GetMapping
    public ResponseEntity<List<Carrito>> obtenerCarrito() {
        Long usuarioId = securityUtils.obtenerIdUsuarioActual();
        return ResponseEntity.ok(carritoServicio.obtenerCarritoPorUsuario(usuarioId));
    }

    @PostMapping("/agregar")
    public ResponseEntity<CarritoResponse> agregarAlCarrito(@Valid @RequestBody AgregarAlCarritoRequest request) {
        Long usuarioId = securityUtils.obtenerIdUsuarioActual();
        Carrito carrito = carritoServicio.agregarAlCarrito(usuarioId, request.getPianoId(), request.getCantidad());
        return ResponseEntity.ok(CarritoResponse.fromCarrito(carrito));
    }

    @PutMapping("/{itemId}")
    public ResponseEntity<CarritoResponse> actualizarCantidad(
            @PathVariable Long itemId,
            @Valid @RequestBody ActualizarCantidadRequest request) {

        Carrito carritoActualizado = carritoServicio.actualizarCantidad(itemId, request.getCantidad());
        return ResponseEntity.ok(CarritoResponse.fromCarrito(carritoActualizado));
    }

    @DeleteMapping("/{itemId}")
    public ResponseEntity<MensajeResponseDTO> eliminarItem(@PathVariable Long itemId) {
        carritoServicio.eliminarDelCarrito(itemId);
        return ResponseEntity.ok(new MensajeResponseDTO("Item eliminado del carrito"));
    }

    @DeleteMapping
    public ResponseEntity<MensajeResponseDTO> vaciarCarrito() {
        Long usuarioId = securityUtils.obtenerIdUsuarioActual();
        carritoServicio.vaciarCarrito(usuarioId);
        return ResponseEntity.ok(new MensajeResponseDTO("Carrito vaciado correctamente"));
    }

}