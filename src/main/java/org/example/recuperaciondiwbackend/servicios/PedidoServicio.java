package org.example.recuperaciondiwbackend.servicios;

import org.example.recuperaciondiwbackend.modelos.*;
import org.example.recuperaciondiwbackend.repositorios.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class PedidoServicio {

    private final PedidoRepositorio pedidoRepositorio;
    private final ItemPedidoRepositorio itemPedidoRepositorio;
    private final UsuarioRepositorio usuarioRepositorio;
    private final PianoRepositorio pianoRepositorio;
    private final CarritoRepositorio carritoRepositorio;

    public PedidoServicio(PedidoRepositorio pedidoRepositorio, ItemPedidoRepositorio itemPedidoRepositorio,
                         UsuarioRepositorio usuarioRepositorio, PianoRepositorio pianoRepositorio,
                         CarritoRepositorio carritoRepositorio) {
        this.pedidoRepositorio = pedidoRepositorio;
        this.itemPedidoRepositorio = itemPedidoRepositorio;
        this.usuarioRepositorio = usuarioRepositorio;
        this.pianoRepositorio = pianoRepositorio;
        this.carritoRepositorio = carritoRepositorio;
    }

    public List<Pedido> listarTodos() {
        return pedidoRepositorio.findAll();
    }

    public List<Pedido> listarPorUsuario(Long usuarioId) {
        Usuario usuario = usuarioRepositorio.findById(usuarioId)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
        return pedidoRepositorio.findByUsuario(usuario);
    }

    public Optional<Pedido> buscarPorId(Long id) {
        return pedidoRepositorio.findById(id);
    }

    @Transactional
    public Pedido crearPedidoDesdeCarrito(Long usuarioId, String direccionEnvio, String metodoPago) {
        Usuario usuario = usuarioRepositorio.findById(usuarioId)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
        
        List<Carrito> itemsCarrito = carritoRepositorio.findByUsuario(usuario);
        if (itemsCarrito.isEmpty()) {
            throw new RuntimeException("El carrito está vacío");
        }

        Pedido pedido = new Pedido();
        pedido.setUsuario(usuario);
        pedido.setFechaPedido(LocalDateTime.now());
        pedido.setEstado("pendiente");
        pedido.setDireccionEnvio(direccionEnvio);
        pedido.setMetodoPago(metodoPago);
        
        BigDecimal total = BigDecimal.ZERO;
        
        Pedido pedidoGuardado = pedidoRepositorio.save(pedido);
        
        for (Carrito item : itemsCarrito) {
            ItemPedido itemPedido = new ItemPedido();
            itemPedido.setPedido(pedidoGuardado);
            itemPedido.setPiano(item.getPiano());
            itemPedido.setCantidad(item.getCantidad());
            itemPedido.setPrecioUnitario(item.getPiano().getPrecio());
            BigDecimal subtotal = item.getPiano().getPrecio().multiply(BigDecimal.valueOf(item.getCantidad()));
            itemPedido.setSubtotal(subtotal);
            itemPedidoRepositorio.save(itemPedido);
            
            total = total.add(subtotal);
        }
        
        pedidoGuardado.setTotal(total);
        Pedido resultado = pedidoRepositorio.save(pedidoGuardado);
        
        // Vaciar el carrito
        carritoRepositorio.deleteAll(itemsCarrito);
        
        return resultado;
    }

    @Transactional
    public Pedido actualizarEstadoPedido(Long pedidoId, EstadoPedido nuevoEstado) {
        Pedido pedido = pedidoRepositorio.findById(pedidoId)
                .orElseThrow(() -> new RuntimeException("Pedido no encontrado"));

        // Actualizamos el estado usando el enum
        pedido.setEstado(nuevoEstado.name());

        return pedidoRepositorio.save(pedido);
    }
}