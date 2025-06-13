package org.example.recuperaciondiwbackend.serviciosTest;

import org.example.recuperaciondiwbackend.modelos.*;
import org.example.recuperaciondiwbackend.repositorios.*;
import org.example.recuperaciondiwbackend.servicios.PedidoServicio;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests Unitarios para PedidoServicio usando H2
 * Tests que verifican el comportamiento del servicio con base de datos real
 */
@SpringBootTest
@ActiveProfiles("test") // Usar perfil de test con H2
@Transactional // Cada test se ejecuta en una transacción que se rollback al final
public class PedidoServicioTest {

    @Autowired
    private PedidoServicio pedidoServicio;

    @Autowired
    private PedidoRepositorio pedidoRepositorio;

    @Autowired
    private ItemPedidoRepositorio itemPedidoRepositorio;

    @Autowired
    private UsuarioRepositorio usuarioRepositorio;

    @Autowired
    private PianoRepositorio pianoRepositorio;

    @Autowired
    private CarritoRepositorio carritoRepositorio;

    // Datos reales para tests
    private Usuario usuarioReal;
    private Piano pianoReal;
    private Piano pianoReal2;
    private Pedido pedidoReal;

    @BeforeEach
    void setUp() {
        // Limpiar datos previos para asegurar estado limpio
        carritoRepositorio.deleteAll();
        itemPedidoRepositorio.deleteAll();
        pedidoRepositorio.deleteAll();
        pianoRepositorio.deleteAll();
        usuarioRepositorio.deleteAll();

        // Crear y guardar usuario real
        usuarioReal = new Usuario();
        usuarioReal.setNombre("Juan Pérez Test");
        usuarioReal.setEmail("juan.test@example.com");
        usuarioReal.setContrasenaHash("$2a$10$testHash");
        usuarioReal.setRol("user");
        usuarioReal.setEstado("activo");
        usuarioReal.setFechaRegistro(LocalDateTime.now());
        usuarioReal = usuarioRepositorio.save(usuarioReal);

        // Crear y guardar piano real 1
        pianoReal = new Piano();
        pianoReal.setNombre("STEINWAY & SONS Test");
        pianoReal.setModelo("K-132-TEST");
        pianoReal.setPrecio(new BigDecimal("39325.00"));
        pianoReal.setImagen("steinway_test.jpg");
        pianoReal.setDescripcion("Piano Steinway para testing");
        pianoReal.setFechaCreacion(LocalDateTime.now());
        pianoReal.setEstado("activo");
        pianoReal = pianoRepositorio.save(pianoReal);

        // Crear y guardar piano real 2
        pianoReal2 = new Piano();
        pianoReal2.setNombre("BOSTON Test");
        pianoReal2.setModelo("GP-193-TEST");
        pianoReal2.setPrecio(new BigDecimal("25000.00"));
        pianoReal2.setImagen("boston_test.jpg");
        pianoReal2.setDescripcion("Piano Boston para testing");
        pianoReal2.setFechaCreacion(LocalDateTime.now());
        pianoReal2.setEstado("activo");
        pianoReal2 = pianoRepositorio.save(pianoReal2);

        // Crear pedido real para algunos tests
        pedidoReal = new Pedido();
        pedidoReal.setUsuario(usuarioReal);
        pedidoReal.setTotal(new BigDecimal("39325.00"));
        pedidoReal.setEstado("pendiente");
        pedidoReal.setFechaPedido(LocalDateTime.now());
        pedidoReal.setDireccionEnvio("Calle Principal 123, Madrid");
        pedidoReal.setMetodoPago("tarjeta");
        pedidoReal = pedidoRepositorio.save(pedidoReal);
    }

    // ====================================
    // TESTS PARA listarTodos()
    // ====================================

    @Test
    @DisplayName("testListarTodos_CasoPositivo - Verificar que se obtiene lista completa de pedidos cuando existen datos")
    public void testListarTodos_CasoPositivo() {
        // Given - Ya tenemos pedidoReal creado en setUp()

        // When
        List<Pedido> resultado = pedidoServicio.listarTodos();

        // Then
        assertNotNull(resultado);
        assertFalse(resultado.isEmpty());
        assertEquals(1, resultado.size());

        Pedido pedidoObtenido = resultado.get(0);
        assertEquals(pedidoReal.getId(), pedidoObtenido.getId());
        assertEquals(pedidoReal.getTotal(), pedidoObtenido.getTotal());
        assertEquals(pedidoReal.getEstado(), pedidoObtenido.getEstado());
        assertEquals(pedidoReal.getDireccionEnvio(), pedidoObtenido.getDireccionEnvio());
        assertEquals(pedidoReal.getMetodoPago(), pedidoObtenido.getMetodoPago());

        // Verificar que el usuario está cargado correctamente
        assertNotNull(pedidoObtenido.getUsuario());
        assertEquals(usuarioReal.getId(), pedidoObtenido.getUsuario().getId());
        assertEquals(usuarioReal.getNombre(), pedidoObtenido.getUsuario().getNombre());
    }

    @Test
    @DisplayName("testListarTodos_ListaVacia - Verificar que se obtiene lista vacía cuando no hay pedidos")
    public void testListarTodos_ListaVacia() {
        // Given - Eliminar el pedido creado en setUp()
        pedidoRepositorio.deleteAll();

        // When
        List<Pedido> resultado = pedidoServicio.listarTodos();

        // Then
        assertNotNull(resultado);
        assertTrue(resultado.isEmpty());
        assertEquals(0, resultado.size());
    }

    // ====================================
    // TESTS PARA listarPorUsuario()
    // ====================================

    @Test
    @DisplayName("testListarPorUsuario_CasoPositivo - Verificar que se obtienen pedidos de usuario existente con pedidos")
    public void testListarPorUsuario_CasoPositivo() {
        // Given - Ya tenemos usuarioReal y pedidoReal creados
        Long usuarioId = usuarioReal.getId();

        // When
        List<Pedido> resultado = pedidoServicio.listarPorUsuario(usuarioId);

        // Then
        assertNotNull(resultado);
        assertEquals(1, resultado.size());

        Pedido pedidoObtenido = resultado.get(0);
        assertEquals(pedidoReal.getId(), pedidoObtenido.getId());
        assertEquals(usuarioReal.getId(), pedidoObtenido.getUsuario().getId());
        assertEquals(pedidoReal.getTotal(), pedidoObtenido.getTotal());
    }

    @Test
    @DisplayName("testListarPorUsuario_UsuarioSinPedidos - Verificar que se obtiene lista vacía para usuario sin pedidos")
    public void testListarPorUsuario_UsuarioSinPedidos() {
        // Given - Crear un segundo usuario sin pedidos
        Usuario usuarioSinPedidos = new Usuario();
        usuarioSinPedidos.setNombre("María García Test");
        usuarioSinPedidos.setEmail("maria.test@example.com");
        usuarioSinPedidos.setContrasenaHash("$2a$10$testHash2");
        usuarioSinPedidos.setRol("user");
        usuarioSinPedidos.setEstado("activo");
        usuarioSinPedidos.setFechaRegistro(LocalDateTime.now());
        usuarioSinPedidos = usuarioRepositorio.save(usuarioSinPedidos);

        // When
        List<Pedido> resultado = pedidoServicio.listarPorUsuario(usuarioSinPedidos.getId());

        // Then
        assertNotNull(resultado);
        assertTrue(resultado.isEmpty());
        assertEquals(0, resultado.size());
    }

    @Test
    @DisplayName("testListarPorUsuario_UsuarioNoEncontrado - Verificar error cuando usuario no existe")
    public void testListarPorUsuario_UsuarioNoEncontrado() {
        // Given
        Long usuarioIdInexistente = 999L;

        // When & Then
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            pedidoServicio.listarPorUsuario(usuarioIdInexistente);
        });

        assertEquals("Usuario no encontrado", exception.getMessage());
    }

    @Test
    @DisplayName("testListarPorUsuario_IdNulo - Verificar error cuando usuarioId es null")
    public void testListarPorUsuario_IdNulo() {
        // Given
        Long usuarioIdNulo = null;

        // When & Then
        // Spring Data JPA lanza IllegalArgumentException con mensaje específico cuando el ID es null
        Exception exception = assertThrows(Exception.class, () -> {
            pedidoServicio.listarPorUsuario(usuarioIdNulo);
        });

        // Verificar que contiene información sobre el ID nulo
        assertTrue(exception.getMessage().contains("null") ||
                exception instanceof IllegalArgumentException);
    }

    // ====================================
    // TESTS PARA buscarPorId()
    // ====================================

    @Test
    @DisplayName("testBuscarPorId_CasoPositivo - Verificar que se encuentra pedido existente")
    public void testBuscarPorId_CasoPositivo() {
        // Given
        Long pedidoId = pedidoReal.getId();

        // When
        Optional<Pedido> resultado = pedidoServicio.buscarPorId(pedidoId);

        // Then
        assertTrue(resultado.isPresent());
        assertEquals(pedidoReal.getId(), resultado.get().getId());
        assertEquals(pedidoReal.getTotal(), resultado.get().getTotal());
        assertEquals(pedidoReal.getEstado(), resultado.get().getEstado());

        // Verificar que el usuario está cargado
        assertNotNull(resultado.get().getUsuario());
        assertEquals(usuarioReal.getId(), resultado.get().getUsuario().getId());
    }

    @Test
    @DisplayName("testBuscarPorId_PedidoNoExiste - Verificar que se maneja correctamente pedido inexistente")
    public void testBuscarPorId_PedidoNoExiste() {
        // Given
        Long pedidoIdInexistente = 999L;

        // When
        Optional<Pedido> resultado = pedidoServicio.buscarPorId(pedidoIdInexistente);

        // Then
        assertTrue(resultado.isEmpty());
    }

    @Test
    @DisplayName("testBuscarPorId_IdNulo - Verificar error cuando ID es null")
    public void testBuscarPorId_IdNulo() {
        // Given
        Long idNulo = null;

        // When & Then
        // Verificar que se lanza la excepción esperada (puede ser IllegalArgumentException o RuntimeException)
        assertThrows(Exception.class, () -> {
            pedidoServicio.buscarPorId(idNulo);
        });
    }

    // ====================================
    // TESTS PARA crearPedidoDesdeCarrito()
    // ====================================

    @Test
    @DisplayName("testCrearPedidoDesdeCarrito_CasoPositivo - Verificar creación exitosa con un piano en carrito")
    public void testCrearPedidoDesdeCarrito_CasoPositivo() {
        // Given - Crear item en carrito
        Carrito itemCarrito = new Carrito();
        itemCarrito.setUsuario(usuarioReal);
        itemCarrito.setPiano(pianoReal);
        itemCarrito.setCantidad(1);
        itemCarrito.setFechaAgregado(LocalDateTime.now());
        carritoRepositorio.save(itemCarrito);

        Long usuarioId = usuarioReal.getId();
        String direccionEnvio = "Calle Nueva 456, Barcelona";
        String metodoPago = "tarjeta";

        // When
        Pedido resultado = pedidoServicio.crearPedidoDesdeCarrito(usuarioId, direccionEnvio, metodoPago);

        // Then
        assertNotNull(resultado);
        assertNotNull(resultado.getId());
        assertEquals("pendiente", resultado.getEstado());
        assertEquals(direccionEnvio, resultado.getDireccionEnvio());
        assertEquals(metodoPago, resultado.getMetodoPago());
        assertEquals(pianoReal.getPrecio(), resultado.getTotal());
        assertEquals(usuarioReal.getId(), resultado.getUsuario().getId());
        assertNotNull(resultado.getFechaPedido());

        // Verificar que el pedido se guardó en la BD
        Optional<Pedido> pedidoEnBD = pedidoRepositorio.findById(resultado.getId());
        assertTrue(pedidoEnBD.isPresent());

        // Verificar que se creó el item del pedido
        List<ItemPedido> itemsPedido = itemPedidoRepositorio.findByPedido(resultado);
        assertEquals(1, itemsPedido.size());

        ItemPedido item = itemsPedido.get(0);
        assertEquals(pianoReal.getId(), item.getPiano().getId());
        assertEquals(1, item.getCantidad());
        assertEquals(pianoReal.getPrecio(), item.getPrecioUnitario());
        assertEquals(pianoReal.getPrecio(), item.getSubtotal());

        // Verificar que el carrito se vació
        List<Carrito> carritoFinal = carritoRepositorio.findByUsuario(usuarioReal);
        assertTrue(carritoFinal.isEmpty());
    }

    @Test
    @DisplayName("testCrearPedidoDesdeCarrito_MultiplesItems - Verificar creación con múltiples pianos en carrito")
    public void testCrearPedidoDesdeCarrito_MultiplesItems() {
        // Given - Crear múltiples items en carrito
        Carrito item1 = new Carrito();
        item1.setUsuario(usuarioReal);
        item1.setPiano(pianoReal);
        item1.setCantidad(1);
        item1.setFechaAgregado(LocalDateTime.now());
        carritoRepositorio.save(item1);

        Carrito item2 = new Carrito();
        item2.setUsuario(usuarioReal);
        item2.setPiano(pianoReal2);
        item2.setCantidad(2);
        item2.setFechaAgregado(LocalDateTime.now());
        carritoRepositorio.save(item2);

        Long usuarioId = usuarioReal.getId();
        String direccionEnvio = "Calle Multiple 789";
        String metodoPago = "transferencia";

        // When
        Pedido resultado = pedidoServicio.crearPedidoDesdeCarrito(usuarioId, direccionEnvio, metodoPago);

        // Then
        assertNotNull(resultado);

        // Total esperado: 39325 + (25000 * 2) = 89325
        BigDecimal totalEsperado = new BigDecimal("89325.00");
        assertEquals(totalEsperado, resultado.getTotal());

        // Verificar que se crearon 2 items del pedido
        List<ItemPedido> itemsPedido = itemPedidoRepositorio.findByPedido(resultado);
        assertEquals(2, itemsPedido.size());

        // Verificar primer item
        ItemPedido primerItem = itemsPedido.stream()
                .filter(item -> item.getPiano().getId().equals(pianoReal.getId()))
                .findFirst()
                .orElseThrow();
        assertEquals(1, primerItem.getCantidad());
        assertEquals(new BigDecimal("39325.00"), primerItem.getSubtotal());

        // Verificar segundo item
        ItemPedido segundoItem = itemsPedido.stream()
                .filter(item -> item.getPiano().getId().equals(pianoReal2.getId()))
                .findFirst()
                .orElseThrow();
        assertEquals(2, segundoItem.getCantidad());
        assertEquals(new BigDecimal("50000.00"), segundoItem.getSubtotal()); // 25000 * 2

        // Verificar que el carrito se vació
        List<Carrito> carritoFinal = carritoRepositorio.findByUsuario(usuarioReal);
        assertTrue(carritoFinal.isEmpty());
    }

    @Test
    @DisplayName("testCrearPedidoDesdeCarrito_CantidadMayorUno - Verificar creación con cantidad > 1 del mismo piano")
    public void testCrearPedidoDesdeCarrito_CantidadMayorUno() {
        // Given - Crear item con cantidad 3
        Carrito itemConCantidad = new Carrito();
        itemConCantidad.setUsuario(usuarioReal);
        itemConCantidad.setPiano(pianoReal);
        itemConCantidad.setCantidad(3);
        itemConCantidad.setFechaAgregado(LocalDateTime.now());
        carritoRepositorio.save(itemConCantidad);

        Long usuarioId = usuarioReal.getId();
        String direccionEnvio = "Calle Cantidad 101";
        String metodoPago = "financiacion";

        // When
        Pedido resultado = pedidoServicio.crearPedidoDesdeCarrito(usuarioId, direccionEnvio, metodoPago);

        // Then
        assertNotNull(resultado);

        // Total esperado: 39325 * 3 = 117975
        BigDecimal totalEsperado = new BigDecimal("117975.00");
        assertEquals(totalEsperado, resultado.getTotal());

        // Verificar el item del pedido
        List<ItemPedido> itemsPedido = itemPedidoRepositorio.findByPedido(resultado);
        assertEquals(1, itemsPedido.size());

        ItemPedido item = itemsPedido.get(0);
        assertEquals(3, item.getCantidad());
        assertEquals(pianoReal.getPrecio(), item.getPrecioUnitario());
        assertEquals(totalEsperado, item.getSubtotal());
    }

    @Test
    @DisplayName("testCrearPedidoDesdeCarrito_UsuarioNoEncontrado - Verificar error cuando usuario no existe")
    public void testCrearPedidoDesdeCarrito_UsuarioNoEncontrado() {
        // Given
        Long usuarioIdInexistente = 999L;
        String direccionEnvio = "Calle Inexistente 123";
        String metodoPago = "tarjeta";

        // When & Then
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            pedidoServicio.crearPedidoDesdeCarrito(usuarioIdInexistente, direccionEnvio, metodoPago);
        });

        assertEquals("Usuario no encontrado", exception.getMessage());
    }

    @Test
    @DisplayName("testCrearPedidoDesdeCarrito_CarritoVacio - Verificar error cuando carrito está vacío")
    public void testCrearPedidoDesdeCarrito_CarritoVacio() {
        // Given - Usuario existe pero sin items en carrito
        Long usuarioId = usuarioReal.getId();
        String direccionEnvio = "Calle Vacia 123";
        String metodoPago = "tarjeta";

        // When & Then
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            pedidoServicio.crearPedidoDesdeCarrito(usuarioId, direccionEnvio, metodoPago);
        });

        assertEquals("El carrito está vacío", exception.getMessage());
    }

    @Test
    @DisplayName("testCrearPedidoDesdeCarrito_DireccionVacia - Verificar comportamiento con dirección vacía")
    public void testCrearPedidoDesdeCarrito_DireccionVacia() {
        // Given - Crear item en carrito
        Carrito item = new Carrito();
        item.setUsuario(usuarioReal);
        item.setPiano(pianoReal);
        item.setCantidad(1);
        item.setFechaAgregado(LocalDateTime.now());
        carritoRepositorio.save(item);

        Long usuarioId = usuarioReal.getId();
        String direccionVacia = "";
        String metodoPago = "tarjeta";

        // When & Then
        // Nota: Dependiendo de la implementación del servicio, esto podría fallar o no
        // Si no hay validación, el test pasará; si hay validación, fallará
        assertDoesNotThrow(() -> {
            Pedido resultado = pedidoServicio.crearPedidoDesdeCarrito(usuarioId, direccionVacia, metodoPago);
            assertNotNull(resultado);
            assertEquals(direccionVacia, resultado.getDireccionEnvio());
        });
    }

    @Test
    @DisplayName("testCrearPedidoDesdeCarrito_MetodoPagoNulo - Verificar comportamiento con método de pago null")
    public void testCrearPedidoDesdeCarrito_MetodoPagoNulo() {
        // Given - Crear item en carrito
        Carrito item = new Carrito();
        item.setUsuario(usuarioReal);
        item.setPiano(pianoReal);
        item.setCantidad(1);
        item.setFechaAgregado(LocalDateTime.now());
        carritoRepositorio.save(item);

        Long usuarioId = usuarioReal.getId();
        String direccionEnvio = "Calle Test 123";
        String metodoPagoNulo = null;

        // When & Then
        assertDoesNotThrow(() -> {
            Pedido resultado = pedidoServicio.crearPedidoDesdeCarrito(usuarioId, direccionEnvio, metodoPagoNulo);
            assertNotNull(resultado);
            assertEquals(metodoPagoNulo, resultado.getMetodoPago());
        });
    }

    // ====================================
    // TESTS PARA actualizarEstadoPedido()
    // ====================================

    @Test
    @DisplayName("testActualizarEstadoPedido_CasoPositivo - Verificar actualización exitosa de pendiente a confirmado")
    public void testActualizarEstadoPedido_CasoPositivo() {
        // Given
        Long pedidoId = pedidoReal.getId();
        EstadoPedido nuevoEstado = EstadoPedido.CONFIRMADO;

        // When
        Pedido resultado = pedidoServicio.actualizarEstadoPedido(pedidoId, nuevoEstado);

        // Then
        assertNotNull(resultado);
        assertEquals(pedidoId, resultado.getId());
        assertEquals("CONFIRMADO", resultado.getEstado());

        // Verificar que se guardó en la BD
        Optional<Pedido> pedidoActualizado = pedidoRepositorio.findById(pedidoId);
        assertTrue(pedidoActualizado.isPresent());
        assertEquals("CONFIRMADO", pedidoActualizado.get().getEstado());
    }

    @Test
    @DisplayName("testActualizarEstadoPedido_CambioAEnviado - Verificar cambio de confirmado a enviado")
    public void testActualizarEstadoPedido_CambioAEnviado() {
        // Given - Cambiar primero a confirmado
        pedidoReal.setEstado("CONFIRMADO");
        pedidoRepositorio.save(pedidoReal);

        Long pedidoId = pedidoReal.getId();
        EstadoPedido nuevoEstado = EstadoPedido.ENVIADO;

        // When
        Pedido resultado = pedidoServicio.actualizarEstadoPedido(pedidoId, nuevoEstado);

        // Then
        assertNotNull(resultado);
        assertEquals("ENVIADO", resultado.getEstado());

        // Verificar persistencia
        Optional<Pedido> pedidoActualizado = pedidoRepositorio.findById(pedidoId);
        assertTrue(pedidoActualizado.isPresent());
        assertEquals("ENVIADO", pedidoActualizado.get().getEstado());
    }

    @Test
    @DisplayName("testActualizarEstadoPedido_CambioAEntregado - Verificar cambio de enviado a entregado")
    public void testActualizarEstadoPedido_CambioAEntregado() {
        // Given - Cambiar primero a enviado
        pedidoReal.setEstado("ENVIADO");
        pedidoRepositorio.save(pedidoReal);

        Long pedidoId = pedidoReal.getId();
        EstadoPedido nuevoEstado = EstadoPedido.ENTREGADO;

        // When
        Pedido resultado = pedidoServicio.actualizarEstadoPedido(pedidoId, nuevoEstado);

        // Then
        assertNotNull(resultado);
        assertEquals("ENTREGADO", resultado.getEstado());

        // Verificar persistencia
        Optional<Pedido> pedidoActualizado = pedidoRepositorio.findById(pedidoId);
        assertTrue(pedidoActualizado.isPresent());
        assertEquals("ENTREGADO", pedidoActualizado.get().getEstado());
    }

    @Test
    @DisplayName("testActualizarEstadoPedido_PedidoNoEncontrado - Verificar error cuando pedido no existe")
    public void testActualizarEstadoPedido_PedidoNoEncontrado() {
        // Given
        Long pedidoIdInexistente = 999L;
        EstadoPedido nuevoEstado = EstadoPedido.CONFIRMADO;

        // When & Then
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            pedidoServicio.actualizarEstadoPedido(pedidoIdInexistente, nuevoEstado);
        });

        assertEquals("Pedido no encontrado", exception.getMessage());
    }

    @Test
    @DisplayName("testActualizarEstadoPedido_EstadoNulo - Verificar error cuando nuevo estado es null")
    public void testActualizarEstadoPedido_EstadoNulo() {
        // Given
        Long pedidoId = pedidoReal.getId();
        EstadoPedido estadoNulo = null;

        // When & Then
        assertThrows(Exception.class, () -> {
            pedidoServicio.actualizarEstadoPedido(pedidoId, estadoNulo);
        });
    }

    // ====================================
    // TESTS ADICIONALES PARA VERIFICAR PERSISTENCIA
    // ====================================

    @Test
    @DisplayName("testVerificarPersistencia_PedidoCompleto - Verificar que toda la información se persiste correctamente")
    public void testVerificarPersistencia_PedidoCompleto() {
        // Given - Crear carrito con múltiples items
        Carrito item1 = new Carrito();
        item1.setUsuario(usuarioReal);
        item1.setPiano(pianoReal);
        item1.setCantidad(2);
        item1.setFechaAgregado(LocalDateTime.now());
        carritoRepositorio.save(item1);

        Carrito item2 = new Carrito();
        item2.setUsuario(usuarioReal);
        item2.setPiano(pianoReal2);
        item2.setCantidad(1);
        item2.setFechaAgregado(LocalDateTime.now());
        carritoRepositorio.save(item2);

        // When
        Pedido pedidoCreado = pedidoServicio.crearPedidoDesdeCarrito(
                usuarioReal.getId(),
                "Calle Persistencia 999",
                "transferencia"
        );

        // Then - Verificar que todo se persistió correctamente

        // 1. Verificar pedido principal
        Optional<Pedido> pedidoEnBD = pedidoRepositorio.findById(pedidoCreado.getId());
        assertTrue(pedidoEnBD.isPresent());

        Pedido pedidoPersistido = pedidoEnBD.get();
        assertEquals("Calle Persistencia 999", pedidoPersistido.getDireccionEnvio());
        assertEquals("transferencia", pedidoPersistido.getMetodoPago());
        assertEquals("pendiente", pedidoPersistido.getEstado());

        // Total: (39325 * 2) + 25000 = 103650
        assertEquals(new BigDecimal("103650.00"), pedidoPersistido.getTotal());

        // 2. Verificar items del pedido
        List<ItemPedido> itemsEnBD = itemPedidoRepositorio.findByPedido(pedidoPersistido);
        assertEquals(2, itemsEnBD.size());

        // 3. Verificar relaciones
        for (ItemPedido item : itemsEnBD) {
            assertNotNull(item.getPedido());
            assertNotNull(item.getPiano());
            assertEquals(pedidoPersistido.getId(), item.getPedido().getId());
            assertTrue(item.getPiano().getId().equals(pianoReal.getId()) ||
                    item.getPiano().getId().equals(pianoReal2.getId()));
        }

        // 4. Verificar que el carrito se vació
        List<Carrito> carritoFinal = carritoRepositorio.findByUsuario(usuarioReal);
        assertTrue(carritoFinal.isEmpty());

        // 5. Verificar que se puede buscar el pedido por usuario
        List<Pedido> pedidosUsuario = pedidoServicio.listarPorUsuario(usuarioReal.getId());
        assertEquals(2, pedidosUsuario.size()); // pedidoReal + pedidoCreado
        assertTrue(pedidosUsuario.stream().anyMatch(p -> p.getId().equals(pedidoCreado.getId())));
    }
}