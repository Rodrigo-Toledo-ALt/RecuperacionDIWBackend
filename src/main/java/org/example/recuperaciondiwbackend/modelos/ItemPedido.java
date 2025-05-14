package org.example.recuperaciondiwbackend.modelos;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Entity
@Table(name = "items_pedido", schema="tienda_pianos")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ItemPedido {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne
    @JoinColumn(name = "pedido_id")
    private Pedido pedido;
    
    @ManyToOne
    @JoinColumn(name = "piano_id")
    private Piano piano;
    
    @Column(nullable = false)
    private Integer cantidad = 1;
    
    @Column(name = "precio_unitario", nullable = false)
    private BigDecimal precioUnitario;
    
    @Column(nullable = false)
    private BigDecimal subtotal;
}