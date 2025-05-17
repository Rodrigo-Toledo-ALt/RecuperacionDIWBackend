package org.example.recuperaciondiwbackend.modelos;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "tipos_especificacion", schema="tienda_pianos")
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(exclude = {"valores"})
public class TipoEspecificacion {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(unique = true, nullable = false)
    private String nombre;
    
    @OneToMany(mappedBy = "tipo", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Set<ValorEspecificacion> valores = new HashSet<>();
}