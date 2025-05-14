package org.example.recuperaciondiwbackend.controladores;

import org.example.recuperaciondiwbackend.modelos.Usuario;
import org.example.recuperaciondiwbackend.servicios.UsuarioServicio;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/admin/usuarios")
@PreAuthorize("hasRole('ADMIN')")
public class AdminUsuarioControlador {

    private final UsuarioServicio usuarioServicio;

    public AdminUsuarioControlador(UsuarioServicio usuarioServicio) {
        this.usuarioServicio = usuarioServicio;
    }

    @GetMapping
    public ResponseEntity<List<Usuario>> listarUsuarios() {
        // Por seguridad, no devolvemos los hashes de contraseñas
        List<Usuario> usuarios = usuarioServicio.listarTodos().stream()
                .peek(u -> u.setContrasenaHash(null))
                .collect(Collectors.toList());
        
        return ResponseEntity.ok(usuarios);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Usuario> obtenerUsuario(@PathVariable Long id) {
        return usuarioServicio.buscarPorId(id)
                .map(usuario -> {
                    // Por seguridad, no devolvemos el hash de la contraseña
                    usuario.setContrasenaHash(null);
                    return ResponseEntity.ok(usuario);
                })
                .orElse(ResponseEntity.notFound().build());
    }
}