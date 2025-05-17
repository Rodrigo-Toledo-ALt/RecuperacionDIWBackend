package org.example.recuperaciondiwbackend.modelos;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "valores_especificacion", schema="tienda_pianos")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ValorEspecificacion {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne
    @JoinColumn(name = "tipo_id")
    private TipoEspecificacion tipo;
    
    @Column(nullable = false)
    private String valor;
    
    @ManyToMany(mappedBy = "especificaciones", fetch = FetchType.LAZY)
    private Set<Piano> pianos = new HashSet<>();
}