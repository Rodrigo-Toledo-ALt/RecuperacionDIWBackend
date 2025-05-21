package org.example.recuperaciondiwbackend.controladores;

import jakarta.validation.Valid;
import org.example.recuperaciondiwbackend.Utils.SecurityUtils;
import org.example.recuperaciondiwbackend.dtos.CambioContrasenaDTO;
import org.example.recuperaciondiwbackend.dtos.UsuarioDTO;
import org.example.recuperaciondiwbackend.modelos.Usuario;
import org.example.recuperaciondiwbackend.servicios.UsuarioServicio;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/admin/usuarios")
@PreAuthorize("hasRole('ADMIN')")
public class AdminUsuarioControlador {

    private final UsuarioServicio usuarioServicio;
    private final SecurityUtils securityUtils;

    public AdminUsuarioControlador(UsuarioServicio usuarioServicio, SecurityUtils securityUtils) {
        this.usuarioServicio = usuarioServicio;
        this.securityUtils = securityUtils;
    }

    @GetMapping
    public ResponseEntity<List<UsuarioDTO>> listarUsuarios() {
        List<UsuarioDTO> usuariosDTO = usuarioServicio.listarTodos().stream()
                .map(UsuarioDTO::new)
                .collect(Collectors.toList());

        return ResponseEntity.ok(usuariosDTO);
    }

    @GetMapping("/{id}")
    public ResponseEntity<UsuarioDTO> obtenerUsuario(@PathVariable Long id) {
        return usuarioServicio.buscarPorId(id)
                .map(usuario -> ResponseEntity.ok(new UsuarioDTO(usuario)))
                .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/{id}/estado")
    public ResponseEntity<UsuarioDTO> cambiarEstadoUsuario(
            @PathVariable Long id,
            @Valid @RequestBody UsuarioDTO usuarioDTO) {

        Usuario usuarioActualizado = usuarioServicio.cambiarEstadoUsuario(id, usuarioDTO.getEstado());
        return ResponseEntity.ok(new UsuarioDTO(usuarioActualizado));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or @securityUtils.obtenerIdUsuarioActual() == #id")
    public ResponseEntity<UsuarioDTO> actualizarUsuario(
            @PathVariable Long id,
            @Valid @RequestBody UsuarioDTO usuarioDTO) {

        try {
            Usuario usuarioActualizado = usuarioServicio.actualizarUsuarioDesdeDTO(id, usuarioDTO);
            return ResponseEntity.ok(new UsuarioDTO(usuarioActualizado));
        } catch (SecurityException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        } catch (IllegalArgumentException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Error al actualizar usuario");
        }
    }

    /**
     * Endpoint para cambiar la contraseña de un usuario
     */
    @PutMapping("/{id}/password")
    @PreAuthorize("hasRole('ADMIN') or @securityUtils.obtenerIdUsuarioActual() == #id")
    public ResponseEntity<UsuarioDTO> cambiarContrasena(
            @PathVariable Long id,
            @Valid @RequestBody CambioContrasenaDTO cambioContrasenaDTO) {

        try {
            Usuario usuarioActualizado = usuarioServicio.cambiarContrasena(id, cambioContrasenaDTO);
            return ResponseEntity.ok(new UsuarioDTO(usuarioActualizado));
        } catch (SecurityException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        } catch (IllegalArgumentException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Error al cambiar contraseña");
        }
    }
}
