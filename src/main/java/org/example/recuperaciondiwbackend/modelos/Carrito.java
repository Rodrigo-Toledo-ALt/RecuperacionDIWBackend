package org.example.recuperaciondiwbackend.modelos;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "carrito", schema="tienda_pianos")
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(exclude = {"usuario", "piano"}) //puede ser que de problemas
@ToString(exclude = {"usuario", "piano"})
public class Carrito {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne
    @JoinColumn(name = "usuario_id")
    private Usuario usuario;
    
    @ManyToOne
    @JoinColumn(name = "piano_id")
    private Piano piano;
    
    @Column(nullable = false)
    private Integer cantidad = 1;
    
    @Column(name = "fecha_agregado")
    private LocalDateTime fechaAgregado;
}