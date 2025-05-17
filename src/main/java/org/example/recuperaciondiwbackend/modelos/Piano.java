package org.example.recuperaciondiwbackend.modelos;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "pianos", schema="tienda_pianos")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Piano {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false)
    private String nombre;
    
    @Column(nullable = false)
    private String modelo;
    
    @Column(nullable = false)
    private BigDecimal precio;
    
    @Column(name = "opcion_alquiler")
    private BigDecimal opcionAlquiler;
    
    @Column(nullable = false)
    private String imagen;
    
    private String descripcion;
    
    @Column(name = "fecha_creacion")
    private LocalDateTime fechaCreacion;
    
    private String estado = "activo";
    
    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
        name = "piano_caracteristicas",
        schema = "tienda_pianos",
        joinColumns = @JoinColumn(name = "piano_id"),
        inverseJoinColumns = @JoinColumn(name = "caracteristica_id")
    )
    private Set<Caracteristica> caracteristicas = new HashSet<>();
    
    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
        name = "piano_especificaciones",
        schema = "tienda_pianos",
        joinColumns = @JoinColumn(name = "piano_id"),
        inverseJoinColumns = @JoinColumn(name = "valor_especificacion_id")
    )
    private Set<ValorEspecificacion> especificaciones = new HashSet<>();
}