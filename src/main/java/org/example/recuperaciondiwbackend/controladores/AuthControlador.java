package org.example.recuperaciondiwbackend.controladores;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.recuperaciondiwbackend.dtos.*;
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
    public ResponseEntity<?> autenticarUsuario(@Valid @RequestBody LoginRequest loginRequest) {
        return ResponseEntity.ok(authServicio.login(loginRequest));
    }

    @PostMapping("/registro")
    public ResponseEntity<?> registrarUsuario(@Valid @RequestBody RegistroRequest registroRequest) {
        return ResponseEntity.ok(authServicio.registro(registroRequest));
    }
    
    @PostMapping("/refresh")
    public ResponseEntity<?> refreshToken(@Valid @RequestBody RefreshTokenRequest refreshTokenRequest) {
        return ResponseEntity.ok(authServicio.refreshToken(refreshTokenRequest));
    }

    @PostMapping("/registro/admin")
    public ResponseEntity<?> registrarAdmin(@Valid @RequestBody RegistroRequest registroRequest) {
        // Esta ruta debería estar protegida adicionalmente
        Usuario admin = authServicio.registrarAdmin(registroRequest);
        return ResponseEntity.ok(new MensajeResponse("Administrador registrado exitosamente"));
    }
}