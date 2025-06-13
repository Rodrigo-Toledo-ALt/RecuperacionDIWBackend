package org.example.recuperaciondiwbackend.serviciosTest;

import org.example.recuperaciondiwbackend.modelos.*;
import org.example.recuperaciondiwbackend.repositorios.*;
import org.example.recuperaciondiwbackend.servicios.PedidoServicio;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * Tests de Integración para PedidoServicio usando Mockito
 * Estos tests verifican la comunicación entre componentes sin usar BD real
 */
@ExtendWith(MockitoExtension.class)
public class PedidoServicionIntegracionTest {

    @Mock
    private UsuarioRepositorio usuarioRepositorio;

    @Mock
    private PianoRepositorio pianoRepositorio;

    @Mock
    private CarritoRepositorio carritoRepositorio;

    @Mock
    private PedidoRepositorio pedidoRepositorio;

    @Mock
    private ItemPedidoRepositorio itemPedidoRepositorio;

    @InjectMocks
    private PedidoServicio pedidoServicio;

    // Datos de prueba
    private Usuario usuarioTest;
    private Piano pianoTest1;
    private Piano pianoTest2;
    private Pedido pedidoTest1;
    private Pedido pedidoTest2;

    @BeforeEach
    void setUp() {
        // Configurar datos de prueba
        usuarioTest = new Usuario();
        usuarioTest.setId(1L);
        usuarioTest.setNombre("Ana García Test");
        usuarioTest.setEmail("ana.test@test.com");
        usuarioTest.setContrasenaHash("$2a$10$test");
        usuarioTest.setRol("user");
        usuarioTest.setEstado("activo");
        usuarioTest.setFechaRegistro(LocalDateTime.now());

        pianoTest1 = new Piano();
        pianoTest1.setId(1L);
        pianoTest1.setNombre("STEINWAY & SONS Test");
        pianoTest1.setModelo("TEST-001");
        pianoTest1.setPrecio(new BigDecimal("25000.00"));
        pianoTest1.setImagen("test.jpg");
        pianoTest1.setDescripcion("Piano de prueba para integración");
        pianoTest1.setFechaCreacion(LocalDateTime.now());
        pianoTest1.setEstado("activo");

        pianoTest2 = new Piano();
        pianoTest2.setId(2L);
        pianoTest2.setNombre("BOSTON Test");
        pianoTest2.setModelo("TEST-002");
        pianoTest2.setPrecio(new BigDecimal("15000.00"));
        pianoTest2.setImagen("test2.jpg");
        pianoTest2.setDescripcion("Segundo piano de prueba");
        pianoTest2.setFechaCreacion(LocalDateTime.now());
        pianoTest2.setEstado("activo");

        // Crear pedidos de prueba
        pedidoTest1 = crearPedidoTest(1L, "Calle Test 1", "tarjeta", new BigDecimal("25000.00"));
        pedidoTest2 = crearPedidoTest(2L, "Calle Test 2", "transferencia", new BigDecimal("15000.00"));
    }

    @Test
    @DisplayName("testIntegracion_ListarTodos - Verificar comunicación entre servicio y repositorio")
    public void testIntegracion_ListarTodos() {
        // Given - Configurar comportamiento de los mocks
        List<Pedido> pedidosEsperados = Arrays.asList(pedidoTest1, pedidoTest2);
        when(pedidoRepositorio.findAll()).thenReturn(pedidosEsperados);

        // When - Ejecutar el método del servicio
        List<Pedido> pedidosObtenidos = pedidoServicio.listarTodos();

        // Then - Verificar la comunicación y resultados

        // 1. Verificar que se llamó al repositorio
        verify(pedidoRepositorio, times(1)).findAll();

        // 2. Verificar que el servicio retorna los datos correctos
        assertNotNull(pedidosObtenidos);
        assertEquals(2, pedidosObtenidos.size());

        // 3. Verificar que los pedidos mantienen su estructura
        for (Pedido pedido : pedidosObtenidos) {
            assertNotNull(pedido.getId());
            assertNotNull(pedido.getUsuario());
            assertNotNull(pedido.getTotal());
            assertNotNull(pedido.getEstado());
            assertNotNull(pedido.getFechaPedido());
            assertNotNull(pedido.getDireccionEnvio());
            assertNotNull(pedido.getMetodoPago());

            // Verificar que el usuario está vinculado correctamente
            assertEquals(usuarioTest.getId(), pedido.getUsuario().getId());
            assertEquals(usuarioTest.getNombre(), pedido.getUsuario().getNombre());
        }

        // 4. Verificar que los totales son los esperados
        List<BigDecimal> totales = pedidosObtenidos.stream()
                .map(Pedido::getTotal)
                .sorted()
                .toList();
        assertEquals(new BigDecimal("15000.00"), totales.get(0));
        assertEquals(new BigDecimal("25000.00"), totales.get(1));

        // 5. Verificar que no se realizaron operaciones de escritura
        verify(pedidoRepositorio, never()).save(any(Pedido.class));
        verify(pedidoRepositorio, never()).delete(any(Pedido.class));
    }

    @Test
    @DisplayName("testIntegracion_CrearPedidoDesdeCarrito - Verificar comunicación completa entre todos los componentes")
    public void testIntegracion_CrearPedidoDesdeCarrito() {
        // Given - Configurar el escenario completo con mocks
        Long usuarioId = usuarioTest.getId();
        String direccionEnvio = "Calle de Integración 123, Madrid";
        String metodoPago = "tarjeta";

        // Configurar items del carrito
        Carrito itemCarrito1 = new Carrito();
        itemCarrito1.setId(1L);
        itemCarrito1.setUsuario(usuarioTest);
        itemCarrito1.setPiano(pianoTest1);
        itemCarrito1.setCantidad(1);
        itemCarrito1.setFechaAgregado(LocalDateTime.now());

        Carrito itemCarrito2 = new Carrito();
        itemCarrito2.setId(2L);
        itemCarrito2.setUsuario(usuarioTest);
        itemCarrito2.setPiano(pianoTest2);
        itemCarrito2.setCantidad(2);
        itemCarrito2.setFechaAgregado(LocalDateTime.now());

        List<Carrito> itemsCarrito = Arrays.asList(itemCarrito1, itemCarrito2);

        // Configurar pedido que se va a crear
        Pedido pedidoEsperado = new Pedido();
        pedidoEsperado.setId(10L);
        pedidoEsperado.setUsuario(usuarioTest);
        pedidoEsperado.setFechaPedido(LocalDateTime.now());
        pedidoEsperado.setEstado("pendiente");
        pedidoEsperado.setTotal(new BigDecimal("55000.00")); // 25000 + (15000 * 2)
        pedidoEsperado.setDireccionEnvio(direccionEnvio);
        pedidoEsperado.setMetodoPago(metodoPago);

        // Configurar comportamiento de los mocks
        when(usuarioRepositorio.findById(usuarioId)).thenReturn(Optional.of(usuarioTest));
        when(carritoRepositorio.findByUsuario(usuarioTest)).thenReturn(itemsCarrito);
        when(pedidoRepositorio.save(any(Pedido.class))).thenReturn(pedidoEsperado);
        when(itemPedidoRepositorio.save(any(ItemPedido.class))).thenAnswer(invocation -> {
            ItemPedido item = invocation.getArgument(0);
            item.setId(System.currentTimeMillis()); // Simular ID generado
            return item;
        });

        // When - Ejecutar el método del servicio
        Pedido pedidoCreado = pedidoServicio.crearPedidoDesdeCarrito(usuarioId, direccionEnvio, metodoPago);

        // Then - Verificar toda la comunicación entre componentes

        // 1. Verificar que se consultó el usuario
        verify(usuarioRepositorio, times(1)).findById(usuarioId);

        // 2. Verificar que se consultaron los items del carrito
        verify(carritoRepositorio, times(1)).findByUsuario(usuarioTest);

        // 3. Verificar que se guardó el pedido
        verify(pedidoRepositorio, times(1)).save(argThat(pedido ->
                pedido.getUsuario().equals(usuarioTest) &&
                        pedido.getDireccionEnvio().equals(direccionEnvio) &&
                        pedido.getMetodoPago().equals(metodoPago) &&
                        pedido.getEstado().equals("pendiente") &&
                        pedido.getTotal().equals(new BigDecimal("55000.00"))
        ));

        // 4. Verificar que se guardaron los items del pedido (2 items)
        verify(itemPedidoRepositorio, times(2)).save(any(ItemPedido.class));

        // 5. Verificar que se guardó el primer item correctamente
        verify(itemPedidoRepositorio).save(argThat(item ->
                item.getPiano().equals(pianoTest1) &&
                        item.getCantidad() == 1 &&
                        item.getPrecioUnitario().equals(new BigDecimal("25000.00")) &&
                        item.getSubtotal().equals(new BigDecimal("25000.00"))
        ));

        // 6. Verificar que se guardó el segundo item correctamente
        verify(itemPedidoRepositorio).save(argThat(item ->
                item.getPiano().equals(pianoTest2) &&
                        item.getCantidad() == 2 &&
                        item.getPrecioUnitario().equals(new BigDecimal("15000.00")) &&
                        item.getSubtotal().equals(new BigDecimal("30000.00"))
        ));

        // 7. Verificar que se vació el carrito
        verify(carritoRepositorio, times(1)).deleteAll(itemsCarrito);

        // 8. Verificar el resultado final
        assertNotNull(pedidoCreado);
        assertEquals(pedidoEsperado.getId(), pedidoCreado.getId());
        assertEquals(usuarioTest.getId(), pedidoCreado.getUsuario().getId());
        assertEquals(direccionEnvio, pedidoCreado.getDireccionEnvio());
        assertEquals(metodoPago, pedidoCreado.getMetodoPago());
        assertEquals("pendiente", pedidoCreado.getEstado());
        assertEquals(new BigDecimal("55000.00"), pedidoCreado.getTotal());

        // 9. Verificar que el flujo de comunicación fue el correcto
        // (primero usuario, luego carrito, luego pedido, luego items, finalmente limpiar carrito)
        var inOrder = inOrder(usuarioRepositorio, carritoRepositorio, pedidoRepositorio,
                itemPedidoRepositorio, carritoRepositorio);
        inOrder.verify(usuarioRepositorio).findById(usuarioId);
        inOrder.verify(carritoRepositorio).findByUsuario(usuarioTest);
        inOrder.verify(pedidoRepositorio).save(any(Pedido.class));
        inOrder.verify(itemPedidoRepositorio, times(2)).save(any(ItemPedido.class));
        inOrder.verify(carritoRepositorio).deleteAll(itemsCarrito);
    }

    @Test
    @DisplayName("testIntegracion_CrearPedidoDesdeCarrito_UsuarioNoExiste - Verificar manejo de errores en la comunicación")
    public void testIntegracion_CrearPedidoDesdeCarrito_UsuarioNoExiste() {
        // Given
        Long usuarioIdInexistente = 999L;
        when(usuarioRepositorio.findById(usuarioIdInexistente)).thenReturn(Optional.empty());

        // When & Then
        assertThrows(RuntimeException.class, () -> {
            pedidoServicio.crearPedidoDesdeCarrito(usuarioIdInexistente, "Dirección", "tarjeta");
        });

        // Verificar que solo se intentó buscar el usuario y no se hicieron más operaciones
        verify(usuarioRepositorio, times(1)).findById(usuarioIdInexistente);
        verify(carritoRepositorio, never()).findByUsuario(any());
        verify(pedidoRepositorio, never()).save(any());
        verify(itemPedidoRepositorio, never()).save(any());
        verify(carritoRepositorio, never()).deleteAll(any());
    }

    @Test
    @DisplayName("testIntegracion_CrearPedidoDesdeCarrito_CarritoVacio - Verificar comunicación con carrito vacío")
    public void testIntegracion_CrearPedidoDesdeCarrito_CarritoVacio() {
        // Given
        Long usuarioId = usuarioTest.getId();
        when(usuarioRepositorio.findById(usuarioId)).thenReturn(Optional.of(usuarioTest));
        when(carritoRepositorio.findByUsuario(usuarioTest)).thenReturn(Arrays.asList()); // Carrito vacío

        // When & Then
        assertThrows(RuntimeException.class, () -> {
            pedidoServicio.crearPedidoDesdeCarrito(usuarioId, "Dirección", "tarjeta");
        });

        // Verificar que se consultó usuario y carrito, pero no se creó pedido
        verify(usuarioRepositorio, times(1)).findById(usuarioId);
        verify(carritoRepositorio, times(1)).findByUsuario(usuarioTest);
        verify(pedidoRepositorio, never()).save(any());
        verify(itemPedidoRepositorio, never()).save(any());
        verify(carritoRepositorio, never()).deleteAll(any());
    }

    /**
     * Método auxiliar para crear pedidos de prueba
     */
    private Pedido crearPedidoTest(Long id, String direccion, String metodoPago, BigDecimal total) {
        Pedido pedido = new Pedido();
        pedido.setId(id);
        pedido.setUsuario(usuarioTest);
        pedido.setFechaPedido(LocalDateTime.now());
        pedido.setEstado("pendiente");
        pedido.setTotal(total);
        pedido.setDireccionEnvio(direccion);
        pedido.setMetodoPago(metodoPago);
        return pedido;
    }
}