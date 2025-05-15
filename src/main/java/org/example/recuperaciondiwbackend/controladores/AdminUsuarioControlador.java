package org.example.recuperaciondiwbackend.controladores;

import org.example.recuperaciondiwbackend.dtos.UsuarioDTO;
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
}