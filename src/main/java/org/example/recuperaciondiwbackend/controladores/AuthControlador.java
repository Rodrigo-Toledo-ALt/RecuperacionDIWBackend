package org.example.recuperaciondiwbackend.controladores;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.recuperaciondiwbackend.dtos.*;
import org.example.recuperaciondiwbackend.dtos.auth.LoginRequestDTO;
import org.example.recuperaciondiwbackend.dtos.auth.RefreshTokenRequestDTO;
import org.example.recuperaciondiwbackend.dtos.auth.RegistroRequestDTO;
import org.example.recuperaciondiwbackend.modelos.Usuario;
import org.example.recuperaciondiwbackend.servicios.AuthServicio;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthControlador {

    private final AuthServicio authServicio;

    @PostMapping("/login")
    public ResponseEntity<?> autenticarUsuario(@Valid @RequestBody LoginRequestDTO loginRequestDTO) {
        return ResponseEntity.ok(authServicio.login(loginRequestDTO));
    }

    @PostMapping("/registro")
    public ResponseEntity<?> registrarUsuario(@Valid @RequestBody RegistroRequestDTO registroRequestDTO) {
        return ResponseEntity.ok(authServicio.registro(registroRequestDTO));
    }
    
    @PostMapping("/refresh")
    public ResponseEntity<?> refreshToken(@Valid @RequestBody RefreshTokenRequestDTO refreshTokenRequestDTO) {
        return ResponseEntity.ok(authServicio.refreshToken(refreshTokenRequestDTO));
    }

    @PostMapping("/registro/admin")
    public ResponseEntity<?> registrarAdmin(@Valid @RequestBody RegistroRequestDTO registroRequestDTO) {
        // Esta ruta debería estar protegida adicionalmente
        Usuario admin = authServicio.registrarAdmin(registroRequestDTO);
        return ResponseEntity.ok(new MensajeResponseDTO("Administrador registrado exitosamente"));
    }
}