package org.example.recuperaciondiwbackend.controladores;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.recuperaciondiwbackend.dtos.*;
import org.example.recuperaciondiwbackend.dtos.auth.JwtResponseDTO;
import org.example.recuperaciondiwbackend.dtos.auth.LoginRequestDTO;
import org.example.recuperaciondiwbackend.dtos.auth.RefreshTokenRequestDTO;
import org.example.recuperaciondiwbackend.dtos.auth.RegistroRequestDTO;
import org.example.recuperaciondiwbackend.modelos.Usuario;
import org.example.recuperaciondiwbackend.servicios.AuthServicio;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthControlador {

    private final AuthServicio authServicio;

    @PostMapping("/login")
    public ResponseEntity<JwtResponseDTO> autenticarUsuario(@Valid @RequestBody LoginRequestDTO loginRequestDTO) {
        return ResponseEntity.ok(authServicio.login(loginRequestDTO));
    }

    @PostMapping("/registro")
    public ResponseEntity<JwtResponseDTO> registrarUsuario(@Valid @RequestBody RegistroRequestDTO registroRequestDTO) {
        return ResponseEntity.ok(authServicio.registro(registroRequestDTO));
    }
    
    @PostMapping("/refresh")
    public ResponseEntity<JwtResponseDTO> refreshToken(@Valid @RequestBody RefreshTokenRequestDTO refreshTokenRequestDTO) {
        return ResponseEntity.ok(authServicio.refreshToken(refreshTokenRequestDTO));
    }

    @PostMapping("/registro/admin")
    public ResponseEntity<?> registrarAdmin(@Valid @RequestBody RegistroRequestDTO registroRequestDTO) {
        // Esta ruta debería estar protegida adicionalmente
        Usuario admin = authServicio.registrarAdmin(registroRequestDTO);
        return ResponseEntity.ok(new MensajeResponseDTO("Administrador registrado exitosamente"));
    }
}