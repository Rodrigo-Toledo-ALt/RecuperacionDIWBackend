package org.example.recuperaciondiwbackend.servicios;

import lombok.RequiredArgsConstructor;
import org.example.recuperaciondiwbackend.dtos.auth.JwtResponseDTO;
import org.example.recuperaciondiwbackend.dtos.auth.LoginRequestDTO;
import org.example.recuperaciondiwbackend.dtos.auth.RefreshTokenRequestDTO;
import org.example.recuperaciondiwbackend.dtos.auth.RegistroRequestDTO;
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
    public JwtResponseDTO login(LoginRequestDTO loginRequestDTO) {
        // Autenticar al usuario
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginRequestDTO.getEmail(), loginRequestDTO.getContrasena()));

        SecurityContextHolder.getContext().setAuthentication(authentication);
        String jwt = jwtTokenUtil.generarToken(authentication);

        Usuario usuario = usuarioServicio.buscarPorEmail(loginRequestDTO.getEmail())
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
        
        String refreshToken = jwtTokenUtil.generarRefreshToken(usuario);
        
        usuarioServicio.actualizarUltimoLogin(loginRequestDTO.getEmail());

        return JwtResponseDTO.builder()
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
    public JwtResponseDTO registro(RegistroRequestDTO registroRequestDTO) {
        Usuario usuario = usuarioServicio.registrarUsuario(
                registroRequestDTO.getNombre(),
                registroRequestDTO.getEmail(),
                registroRequestDTO.getContrasena()
        );
        
        // Autenticar al usuario automáticamente
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(registroRequestDTO.getEmail(), registroRequestDTO.getContrasena()));

        SecurityContextHolder.getContext().setAuthentication(authentication);
        String jwt = jwtTokenUtil.generarToken(authentication);
        String refreshToken = jwtTokenUtil.generarRefreshToken(usuario);

        return JwtResponseDTO.builder()
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
    public JwtResponseDTO refreshToken(RefreshTokenRequestDTO refreshTokenRequestDTO) {
        // Validar el refresh token
        if (!jwtTokenUtil.validarToken(refreshTokenRequestDTO.getRefreshToken())) {
            throw new RuntimeException("Refresh token inválido o expirado");
        }
        
        // Obtener el email del usuario a partir del token
        String email = jwtTokenUtil.obtenerEmailDelToken(refreshTokenRequestDTO.getRefreshToken());
        
        // Obtener el usuario
        Usuario usuario = usuarioServicio.buscarPorEmail(email)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
        
        // Generar nuevos tokens
        Authentication authentication = new UsernamePasswordAuthenticationToken(
                usuario.getEmail(), null, null);
        String jwt = jwtTokenUtil.generarToken(authentication);
        String refreshToken = jwtTokenUtil.generarRefreshToken(usuario);
        
        return JwtResponseDTO.builder()
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
    public Usuario registrarAdmin(RegistroRequestDTO registroRequestDTO) {
        return usuarioServicio.registrarAdmin(
                registroRequestDTO.getNombre(),
                registroRequestDTO.getEmail(),
                registroRequestDTO.getContrasena()
        );
    }
}