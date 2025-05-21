package org.example.recuperaciondiwbackend.controladores;

import jakarta.validation.Valid;
import org.example.recuperaciondiwbackend.Utils.SecurityUtils;
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

    //Este método es demasiado largo, seguro puede ser refactorizado
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or @securityUtils.obtenerIdUsuarioActual() == #id")
    public ResponseEntity<UsuarioDTO> actualizarUsuario(
            @PathVariable Long id,
            @Valid @RequestBody UsuarioDTO usuarioDTO) {

        // Verificar si hay solicitud de cambio de contraseña
        boolean cambioContrasena = (usuarioDTO.getNuevaContrasena() != null && !usuarioDTO.getNuevaContrasena().isEmpty());

        // Si hay solicitud de cambio de contraseña, procesar primero
        if (cambioContrasena) {
            boolean isAdmin = SecurityContextHolder.getContext().getAuthentication().getAuthorities()
                    .contains(new SimpleGrantedAuthority("ROLE_ADMIN"));
            boolean isCurrentUser = id.equals(securityUtils.obtenerIdUsuarioActual());

            // Si es el propio usuario (y no es admin), verificar contraseña actual
            if (isCurrentUser && !isAdmin) {
                // Verificar contraseña actual
                if (usuarioDTO.getContrasenaActual() == null || usuarioDTO.getContrasenaActual().isEmpty()) {
                    throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "La contraseña actual es requerida");
                }
            } else if (!isAdmin && !isCurrentUser) {
                // No es ni admin ni el propio usuario
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body(null);
            }

            try {
                // Si es admin, la contraseña actual puede ser null
                // Si es usuario normal, ya verificamos que la contraseña actual existe
                usuarioServicio.cambiarContrasena(
                        id,
                        isAdmin ? null : usuarioDTO.getContrasenaActual(),
                        usuarioDTO.getNuevaContrasena()
                );
            } catch (RuntimeException e) {
                // Si falla el cambio de contraseña, devolver el error
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
            }
        }

        // Procesar actualización de información
        Usuario usuarioActualizado;

        boolean isAdmin = SecurityContextHolder.getContext().getAuthentication().getAuthorities()
                .contains(new SimpleGrantedAuthority("ROLE_ADMIN"));
        boolean isCurrentUser = id.equals(securityUtils.obtenerIdUsuarioActual());

        // Los usuarios normales solo pueden actualizar nombre y email de su propia cuenta
        if (!isAdmin && !isCurrentUser) {
            // Si no es admin ni el propio usuario, no tiene permiso
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(null);
        } else if (!isAdmin && isCurrentUser) {
            // Si es el propio usuario pero no es admin, solo puede actualizar nombre y email
            usuarioActualizado = usuarioServicio.actualizarUsuario(id, usuarioDTO.getNombre(), usuarioDTO.getEmail());
        } else {
            // Si es admin, permitir actualizar todos los campos
            usuarioActualizado = usuarioServicio.actualizarUsuarioCompleto(
                    id,
                    usuarioDTO.getNombre(),
                    usuarioDTO.getEmail(),
                    usuarioDTO.getRol(),
                    usuarioDTO.getEstado());
        }

        return ResponseEntity.ok(new UsuarioDTO(usuarioActualizado));
    }
}