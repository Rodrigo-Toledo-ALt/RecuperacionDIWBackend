package org.example.recuperaciondiwbackend.servicios;

import org.example.recuperaciondiwbackend.modelos.Carrito;
import org.example.recuperaciondiwbackend.modelos.Piano;
import org.example.recuperaciondiwbackend.modelos.Usuario;
import org.example.recuperaciondiwbackend.repositorios.CarritoRepositorio;
import org.example.recuperaciondiwbackend.repositorios.PianoRepositorio;
import org.example.recuperaciondiwbackend.repositorios.UsuarioRepositorio;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class CarritoServicio {

    private final CarritoRepositorio carritoRepositorio;
    private final UsuarioRepositorio usuarioRepositorio;
    private final PianoRepositorio pianoRepositorio;

    public CarritoServicio(CarritoRepositorio carritoRepositorio, UsuarioRepositorio usuarioRepositorio,
                          PianoRepositorio pianoRepositorio) {
        this.carritoRepositorio = carritoRepositorio;
        this.usuarioRepositorio = usuarioRepositorio;
        this.pianoRepositorio = pianoRepositorio;
    }

    public List<Carrito> obtenerCarritoPorUsuario(Long usuarioId) {
        Usuario usuario = usuarioRepositorio.findById(usuarioId)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
        return carritoRepositorio.findByUsuario(usuario);
    }

    @Transactional
    public Carrito agregarAlCarrito(Long usuarioId, Long pianoId, Integer cantidad) {
        Usuario usuario = usuarioRepositorio.findById(usuarioId)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
        
        Piano piano = pianoRepositorio.findById(pianoId)
                .orElseThrow(() -> new RuntimeException("Piano no encontrado"));

        return carritoRepositorio.findByUsuarioAndPiano(usuario, piano)
                .map(item -> {
                    item.setCantidad(item.getCantidad() + cantidad);
                    return carritoRepositorio.save(item);
                })
                .orElseGet(() -> {
                    Carrito nuevoItem = new Carrito();
                    nuevoItem.setUsuario(usuario);
                    nuevoItem.setPiano(piano);
                    nuevoItem.setCantidad(cantidad);
                    nuevoItem.setFechaAgregado(LocalDateTime.now());
                    return carritoRepositorio.save(nuevoItem);
                });
    }

    @Transactional
    public Carrito actualizarCantidad(Long itemId, Integer cantidad) {
        Carrito item = carritoRepositorio.findById(itemId)
                .orElseThrow(() -> new RuntimeException("Item del carrito no encontrado"));
        item.setCantidad(cantidad);
        return carritoRepositorio.save(item);
    }

    @Transactional
    public void eliminarDelCarrito(Long itemId) {
        carritoRepositorio.deleteById(itemId);
    }

    @Transactional
    public void vaciarCarrito(Long usuarioId) {
        Usuario usuario = usuarioRepositorio.findById(usuarioId)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
        List<Carrito> items = carritoRepositorio.findByUsuario(usuario);
        carritoRepositorio.deleteAll(items);
    }
}