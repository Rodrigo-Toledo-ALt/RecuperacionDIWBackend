package org.example.recuperaciondiwbackend.servicios;

import org.example.recuperaciondiwbackend.modelos.Usuario;
import org.example.recuperaciondiwbackend.repositorios.UsuarioRepositorio;
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
}