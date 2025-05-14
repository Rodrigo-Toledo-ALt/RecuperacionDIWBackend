package org.example.recuperaciondiwbackend.dtos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class JwtResponse {
    private String token;
    private String refreshToken;
    private String tipo = "Bearer";
    private Long id;
    private String nombre;
    private String email;
    private String rol;
    
    public JwtResponse(String token, Long id, String nombre, String email, String rol) {
        this.token = token;
        this.id = id;
        this.nombre = nombre;
        this.email = email;
        this.rol = rol;
    }
    
    public JwtResponse(String token, String refreshToken, Long id, String nombre, String email, String rol) {
        this.token = token;
        this.refreshToken = refreshToken;
        this.id = id;
        this.nombre = nombre;
        this.email = email;
        this.rol = rol;
    }
}