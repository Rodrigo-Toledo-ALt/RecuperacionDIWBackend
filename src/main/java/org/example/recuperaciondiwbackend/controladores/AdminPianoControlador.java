package org.example.recuperaciondiwbackend.controladores;

import org.example.recuperaciondiwbackend.dtos.MensajeResponseDTO;
import org.example.recuperaciondiwbackend.dtos.PianoDTO;
import org.example.recuperaciondiwbackend.servicios.PianoServicio;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/admin/pianos")
@PreAuthorize("hasRole('ADMIN')")
public class AdminPianoControlador {

    private final PianoServicio pianoServicio;

    public AdminPianoControlador(PianoServicio pianoServicio) {
        this.pianoServicio = pianoServicio;
    }

    @GetMapping
    public ResponseEntity<List<PianoDTO>> listarTodosPianos() {
        List<PianoDTO> pianos = pianoServicio.listarTodos().stream()
                .map(pianoServicio::convertirADto)
                .collect(Collectors.toList());
        return ResponseEntity.ok(pianos);
    }

    @PostMapping
    public ResponseEntity<PianoDTO> crearPiano(@Valid @RequestBody PianoDTO pianoDto) {
        return ResponseEntity.ok(pianoServicio.convertirADto(pianoServicio.guardarPiano(pianoDto)));
    }

    @PutMapping("/{id}")
    public ResponseEntity<PianoDTO> actualizarPiano(@PathVariable Long id, @Valid @RequestBody PianoDTO pianoDto) {
        return ResponseEntity.ok(pianoServicio.convertirADto(pianoServicio.actualizarPiano(id, pianoDto)));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<MensajeResponseDTO> eliminarPiano(@PathVariable Long id) {
        pianoServicio.eliminarPiano(id);
        return ResponseEntity.ok(new MensajeResponseDTO("Piano eliminado correctamente"));
    }
}