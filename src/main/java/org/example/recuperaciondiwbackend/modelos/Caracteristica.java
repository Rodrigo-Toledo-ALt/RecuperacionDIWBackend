package org.example.recuperaciondiwbackend.modelos;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "caracteristicas", schema="tienda_pianos")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Caracteristica {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(unique = true, nullable = false)
    private String descripcion;
    
    @ManyToMany(mappedBy = "caracteristicas", fetch = FetchType.LAZY)
    private Set<Piano> pianos = new HashSet<>();
}