package org.example.recuperaciondiwbackend.modelos;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "pedidos", schema="tienda_pianos")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Pedido {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne
    @JoinColumn(name = "usuario_id")
    private Usuario usuario;
    
    @Column(name = "fecha_pedido")
    private LocalDateTime fechaPedido;
    
    private String estado = "pendiente";
    
    @Column(nullable = false)
    private BigDecimal total;
    
    @Column(name = "direccion_envio")
    private String direccionEnvio;
    
    @Column(name = "metodo_pago")
    private String metodoPago;
    
    @OneToMany(mappedBy = "pedido", cascade = CascadeType.ALL)
    private List<ItemPedido> items = new ArrayList<>();
}