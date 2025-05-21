package org.example.recuperaciondiwbackend.Utils;

import org.example.recuperaciondiwbackend.dtos.UsuarioDTO;
import org.example.recuperaciondiwbackend.modelos.Usuario;
import org.example.recuperaciondiwbackend.servicios.UsuarioServicio;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Component
public class SecurityUtils {

    private final UsuarioServicio usuarioServicio;

    public SecurityUtils(UsuarioServicio usuarioServicio) {
        this.usuarioServicio = usuarioServicio;
    }

    public Usuario obtenerEntidadUsuarioActual() {
        return usuarioServicio.obtenerUsuarioActual();
    }

    public UsuarioDTO obtenerUsuarioActualDTO() {
        Usuario usuario = obtenerEntidadUsuarioActual();
        return new UsuarioDTO(usuario);
    }

    public Long obtenerIdUsuarioActual() {
        return usuarioServicio.obtenerIdUsuarioActual();
    }
}