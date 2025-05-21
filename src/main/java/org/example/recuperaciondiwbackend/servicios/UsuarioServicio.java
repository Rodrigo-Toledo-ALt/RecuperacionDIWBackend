package org.example.recuperaciondiwbackend.servicios;

import jakarta.validation.Valid;

import org.example.recuperaciondiwbackend.dtos.CambioContrasenaDTO;
import org.example.recuperaciondiwbackend.dtos.UsuarioDTO;
import org.example.recuperaciondiwbackend.modelos.Usuario;
import org.example.recuperaciondiwbackend.repositorios.UsuarioRepositorio;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class UsuarioServicio {

    private final UsuarioRepositorio usuarioRepositorio;
    private final PasswordEncoder passwordEncoder;


    public UsuarioServicio(UsuarioRepositorio usuarioRepositorio, PasswordEncoder passwordEncoder) {
        this.usuarioRepositorio = usuarioRepositorio;
        this.passwordEncoder = passwordEncoder;

    }

    @Transactional
    public Usuario registrarUsuario(String nombre, String email, String contrasena) {
        if (usuarioRepositorio.existsByEmail(email)) {
            throw new RuntimeException("El email ya está en uso");
        }

        Usuario nuevoUsuario = new Usuario();
        nuevoUsuario.setNombre(nombre);
        nuevoUsuario.setEmail(email);
        nuevoUsuario.setContrasenaHash(passwordEncoder.encode(contrasena));
        nuevoUsuario.setRol("user");
        nuevoUsuario.setFechaRegistro(LocalDateTime.now());

        return usuarioRepositorio.save(nuevoUsuario);
    }

    @Transactional
    public Usuario registrarAdmin(String nombre, String email, String contrasena) {
        if (usuarioRepositorio.existsByEmail(email)) {
            throw new RuntimeException("El email ya está en uso");
        }

        Usuario nuevoUsuario = new Usuario();
        nuevoUsuario.setNombre(nombre);
        nuevoUsuario.setEmail(email);
        nuevoUsuario.setContrasenaHash(passwordEncoder.encode(contrasena));
        nuevoUsuario.setRol("admin");
        nuevoUsuario.setFechaRegistro(LocalDateTime.now());

        return usuarioRepositorio.save(nuevoUsuario);
    }

    @Transactional
    public void actualizarUltimoLogin(String email) {
        usuarioRepositorio.findByEmail(email).ifPresent(usuario -> {
            usuario.setUltimoLogin(LocalDateTime.now());
            usuarioRepositorio.save(usuario);
        });
    }

    public Optional<Usuario> buscarPorEmail(String email) {
        return usuarioRepositorio.findByEmail(email);
    }

    public Optional<Usuario> buscarPorId(Long id) {
        return usuarioRepositorio.findById(id);
    }

    public List<Usuario> listarTodos() {
        return usuarioRepositorio.findAll();
    }

    @Transactional
    public Usuario cambiarEstadoUsuario(Long id, String nuevoEstado) {
        Usuario usuario = buscarPorId(id)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        // Validar el nuevo estado
        if (nuevoEstado == null || (!nuevoEstado.equals("activo") && !nuevoEstado.equals("inactivo"))) {
            throw new IllegalArgumentException("Estado no válido. Debe ser 'activo' o 'inactivo'.");
        }

        usuario.setEstado(nuevoEstado);
        return usuarioRepositorio.save(usuario);
    }

    @Transactional
    public Usuario actualizarUsuario(Long id, String nombre, String email) {
        Usuario usuario = buscarPorId(id)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        // Comprobar si el nuevo email ya está en uso (si ha cambiado)
        if (email != null && !usuario.getEmail().equals(email) && usuarioRepositorio.existsByEmail(email)) {
            throw new RuntimeException("El email ya está en uso por otro usuario");
        }

        if (nombre != null) {
            usuario.setNombre(nombre);
        }

        if (email != null) {
            usuario.setEmail(email);
        }

        return usuarioRepositorio.save(usuario);
    }

    @Transactional
    public Usuario actualizarUsuarioCompleto(Long id, String nombre, String email,
                                             String rol, String estado) {
        Usuario usuario = buscarPorId(id)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        // Comprobar si el nuevo email ya está en uso (si ha cambiado)
        if (!usuario.getEmail().equals(email) && usuarioRepositorio.existsByEmail(email)) {
            throw new RuntimeException("El email ya está en uso por otro usuario");
        }

        if (nombre != null) {
            usuario.setNombre(nombre);
        }

        if (email != null) {
            usuario.setEmail(email);
        }

        // Solo permitir cambios de rol y estado si quien hace la petición es admin
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        boolean isAdmin = auth.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_ADMIN"));

        if (isAdmin) {
            if (rol != null && ("admin".equals(rol) || "user".equals(rol))) {
                usuario.setRol(rol);
            }

            if (estado != null && ("activo".equals(estado) || "inactivo".equals(estado))) {
                usuario.setEstado(estado);
            }
        }

        return usuarioRepositorio.save(usuario);
    }



    /**
     * Actualiza la información de un usuario según su DTO
     */
    @Transactional
    public Usuario actualizarUsuarioDesdeDTO(Long id, UsuarioDTO usuarioDTO) {
        // Verificar que el usuario existe
        Usuario usuario = buscarPorId(id)
                .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado"));

        // Verificar permisos usando los métodos internos
        boolean isAdmin = isUsuarioActualAdmin();
        boolean isCurrentUser = isUsuarioActual(id);

        if (!isAdmin && !isCurrentUser) {
            throw new SecurityException("No tiene permisos para actualizar este usuario");
        }

        // Actualizar según el rol del solicitante
        if (!isAdmin && isCurrentUser) {
            // Usuario normal actualizando su propia información
            return actualizarUsuario(id, usuarioDTO.getNombre(), usuarioDTO.getEmail());
        } else {
            // Administrador actualizando cualquier usuario
            return actualizarUsuarioCompleto(
                    id,
                    usuarioDTO.getNombre(),
                    usuarioDTO.getEmail(),
                    usuarioDTO.getRol(),
                    usuarioDTO.getEstado());
        }
    }

    /**
     * Cambia la contraseña de un usuario según su DTO
     */
    @Transactional
    public Usuario cambiarContrasena(Long id, CambioContrasenaDTO cambioContrasenaDTO) {
        // Validar contraseñas
        if (!cambioContrasenaDTO.getNuevaContrasena().equals(cambioContrasenaDTO.getConfirmacionContrasena())) {
            throw new IllegalArgumentException("Las contraseñas no coinciden");
        }

        // Verificar que el usuario existe
        Usuario usuario = buscarPorId(id)
                .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado"));

        // Verificar permisos usando los métodos internos
        boolean isAdmin = isUsuarioActualAdmin();
        boolean isCurrentUser = isUsuarioActual(id);

        if (!isAdmin && !isCurrentUser) {
            throw new SecurityException("No tiene permisos para cambiar la contraseña de este usuario");
        }

        // Para usuarios normales, exigir y validar la contraseña actual
        if (!isAdmin && isCurrentUser) {
            if (cambioContrasenaDTO.getContrasenaActual() == null || cambioContrasenaDTO.getContrasenaActual().isEmpty()) {
                throw new IllegalArgumentException("La contraseña actual es requerida");
            }

            // Verificar que la contraseña actual sea correcta
            if (!passwordEncoder.matches(cambioContrasenaDTO.getContrasenaActual(), usuario.getContrasenaHash())) {
                throw new IllegalArgumentException("La contraseña actual es incorrecta");
            }
        }

        // Actualizar la contraseña
        usuario.setContrasenaHash(passwordEncoder.encode(cambioContrasenaDTO.getNuevaContrasena()));
        return usuarioRepositorio.save(usuario);
    }

    public Usuario obtenerUsuarioActual() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String email = auth.getName();
        return buscarPorEmail(email)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
    }

    public Long obtenerIdUsuarioActual() {
        return obtenerUsuarioActual().getId();
    }

    public boolean isUsuarioActualAdmin() {
        Usuario usuario = obtenerUsuarioActual();
        return "admin".equalsIgnoreCase(usuario.getRol());
    }

    public boolean isUsuarioActual(Long id) {
        return id.equals(obtenerIdUsuarioActual());
    }



}