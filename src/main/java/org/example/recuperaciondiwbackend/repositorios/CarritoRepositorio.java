package org.example.recuperaciondiwbackend.repositorios;

import org.example.recuperaciondiwbackend.modelos.Carrito;
import org.example.recuperaciondiwbackend.modelos.Piano;
import org.example.recuperaciondiwbackend.modelos.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CarritoRepositorio extends JpaRepository<Carrito, Long> {
    List<Carrito> findByUsuario(Usuario usuario);
    Optional<Carrito> findByUsuarioAndPiano(Usuario usuario, Piano piano);
}