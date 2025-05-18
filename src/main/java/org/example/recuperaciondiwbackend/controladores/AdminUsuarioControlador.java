package org.example.recuperaciondiwbackend.controladores;

import jakarta.validation.Valid;
import org.example.recuperaciondiwbackend.Utils.SecurityUtils;
import org.example.recuperaciondiwbackend.dtos.UsuarioDTO;
import org.example.recuperaciondiwbackend.modelos.Usuario;
import org.example.recuperaciondiwbackend.servicios.UsuarioServicio;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

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

        // Los usuarios normales solo pueden actualizar nombre y email
        if (!SecurityContextHolder.getContext().getAuthentication().getAuthorities()
                .contains(new SimpleGrantedAuthority("ROLE_ADMIN"))
                && !id.equals(securityUtils.obtenerIdUsuarioActual())) {
            // Si no es admin ni el propio usuario, solo permitir actualizar nombre y email
            Usuario usuarioActualizado = usuarioServicio.actualizarUsuario(id, usuarioDTO.getNombre(), usuarioDTO.getEmail());
            return ResponseEntity.ok(new UsuarioDTO(usuarioActualizado));
        } else {
            // Si es admin, permitir actualizar todos los campos
            Usuario usuarioActualizado = usuarioServicio.actualizarUsuarioCompleto(
                    id,
                    usuarioDTO.getNombre(),
                    usuarioDTO.getEmail(),
                    usuarioDTO.getRol(),
                    usuarioDTO.getEstado());
            return ResponseEntity.ok(new UsuarioDTO(usuarioActualizado));
        }
    }
}