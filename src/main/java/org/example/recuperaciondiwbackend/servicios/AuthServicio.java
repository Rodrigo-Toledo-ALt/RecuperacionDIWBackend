package org.example.recuperaciondiwbackend.servicios;

import lombok.RequiredArgsConstructor;
import org.example.recuperaciondiwbackend.dtos.JwtResponse;
import org.example.recuperaciondiwbackend.dtos.LoginRequest;
import org.example.recuperaciondiwbackend.dtos.RefreshTokenRequest;
import org.example.recuperaciondiwbackend.dtos.RegistroRequest;
import org.example.recuperaciondiwbackend.modelos.Usuario;
import org.example.recuperaciondiwbackend.seguridad.JwtTokenUtil;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AuthServicio {

    private final AuthenticationManager authenticationManager;
    private final UsuarioServicio usuarioServicio;
    private final JwtTokenUtil jwtTokenUtil;

    @Transactional
    public JwtResponse login(LoginRequest loginRequest) {
        // Autenticar al usuario
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginRequest.getEmail(), loginRequest.getContrasena()));

        SecurityContextHolder.getContext().setAuthentication(authentication);
        String jwt = jwtTokenUtil.generarToken(authentication);

        Usuario usuario = usuarioServicio.buscarPorEmail(loginRequest.getEmail())
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
        
        String refreshToken = jwtTokenUtil.generarRefreshToken(usuario);
        
        usuarioServicio.actualizarUltimoLogin(loginRequest.getEmail());

        return JwtResponse.builder()
                .token(jwt)
                .refreshToken(refreshToken)
                .id(usuario.getId())
                .nombre(usuario.getNombre())
                .email(usuario.getEmail())
                .rol(usuario.getRol())
                .tipo("Bearer")
                .build();
    }

    @Transactional
    public JwtResponse registro(RegistroRequest registroRequest) {
        Usuario usuario = usuarioServicio.registrarUsuario(
                registroRequest.getNombre(),
                registroRequest.getEmail(),
                registroRequest.getContrasena()
        );
        
        // Autenticar al usuario automáticamente
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(registroRequest.getEmail(), registroRequest.getContrasena()));

        SecurityContextHolder.getContext().setAuthentication(authentication);
        String jwt = jwtTokenUtil.generarToken(authentication);
        String refreshToken = jwtTokenUtil.generarRefreshToken(usuario);

        return JwtResponse.builder()
                .token(jwt)
                .refreshToken(refreshToken)
                .id(usuario.getId())
                .nombre(usuario.getNombre())
                .email(usuario.getEmail())
                .rol(usuario.getRol())
                .tipo("Bearer")
                .build();
    }
    
    @Transactional
    public JwtResponse refreshToken(RefreshTokenRequest refreshTokenRequest) {
        // Validar el refresh token
        if (!jwtTokenUtil.validarToken(refreshTokenRequest.getRefreshToken())) {
            throw new RuntimeException("Refresh token inválido o expirado");
        }
        
        // Obtener el email del usuario a partir del token
        String email = jwtTokenUtil.obtenerEmailDelToken(refreshTokenRequest.getRefreshToken());
        
        // Obtener el usuario
        Usuario usuario = usuarioServicio.buscarPorEmail(email)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
        
        // Generar nuevos tokens
        Authentication authentication = new UsernamePasswordAuthenticationToken(
                usuario.getEmail(), null, null);
        String jwt = jwtTokenUtil.generarToken(authentication);
        String refreshToken = jwtTokenUtil.generarRefreshToken(usuario);
        
        return JwtResponse.builder()
                .token(jwt)
                .refreshToken(refreshToken)
                .id(usuario.getId())
                .nombre(usuario.getNombre())
                .email(usuario.getEmail())
                .rol(usuario.getRol())
                .tipo("Bearer")
                .build();
    }
    
    @Transactional
    public Usuario registrarAdmin(RegistroRequest registroRequest) {
        return usuarioServicio.registrarAdmin(
                registroRequest.getNombre(),
                registroRequest.getEmail(),
                registroRequest.getContrasena()
        );
    }
}