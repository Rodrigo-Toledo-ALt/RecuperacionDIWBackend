package org.example.recuperaciondiwbackend.controladores;

import org.example.recuperaciondiwbackend.dtos.pianos.PianoDTO;
import org.example.recuperaciondiwbackend.servicios.PianoServicio;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/pianos")
public class PianoControlador {

    private final PianoServicio pianoServicio;

    public PianoControlador(PianoServicio pianoServicio) {
        this.pianoServicio = pianoServicio;
    }

    @GetMapping
    public ResponseEntity<List<PianoDTO>> listarPianos() {
        List<PianoDTO> pianos = pianoServicio.listarActivos().stream()
                .map(pianoServicio::convertirADto)
                .collect(Collectors.toList());
        return ResponseEntity.ok(pianos);
    }

    @GetMapping("/{id}")
    public ResponseEntity<PianoDTO> obtenerPiano(@PathVariable Long id) {
        return pianoServicio.buscarPorId(id)
                .map(piano -> ResponseEntity.ok(pianoServicio.convertirADto(piano)))
                .orElse(ResponseEntity.notFound().build());
    }
}