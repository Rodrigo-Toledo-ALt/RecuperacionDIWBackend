package org.example.recuperaciondiwbackend.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.example.recuperaciondiwbackend.modelos.Usuario;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UsuarioDTO {
    private Long id;
    private String nombre;
    private String email;
    private String rol;
    private LocalDateTime fechaRegistro;
    private LocalDateTime ultimoLogin;

    // Constructor para convertir de Usuario a UsuarioDTO, esto sería el mapper
    // Si necesitamos más mapeos haremos clases específicas para ello
    public UsuarioDTO(Usuario usuario) {
        this.id = usuario.getId();
        this.nombre = usuario.getNombre();
        this.email = usuario.getEmail();
        this.rol = usuario.getRol();
        this.fechaRegistro = usuario.getFechaRegistro();
        this.ultimoLogin = usuario.getUltimoLogin();
    }
}