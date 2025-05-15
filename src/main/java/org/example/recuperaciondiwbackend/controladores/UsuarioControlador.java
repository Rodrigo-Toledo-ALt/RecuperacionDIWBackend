package org.example.recuperaciondiwbackend.controladores;

import org.example.recuperaciondiwbackend.dtos.UsuarioDTO;
import org.example.recuperaciondiwbackend.modelos.Usuario;
import org.example.recuperaciondiwbackend.servicios.UsuarioServicio;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/usuarios")
@PreAuthorize("hasAnyRole('USER', 'ADMIN')")
public class UsuarioControlador {

    private final UsuarioServicio usuarioServicio;

    public UsuarioControlador(UsuarioServicio usuarioServicio) {
        this.usuarioServicio = usuarioServicio;
    }

    @GetMapping("/perfil")
    public ResponseEntity<UsuarioDTO> obtenerPerfilUsuario() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String email = auth.getName();

        return usuarioServicio.buscarPorEmail(email)
                .map(usuario -> ResponseEntity.ok(new UsuarioDTO(usuario)))
                .orElse(ResponseEntity.notFound().build());
    }
}