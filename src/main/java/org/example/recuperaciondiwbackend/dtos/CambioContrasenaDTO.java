package org.example.recuperaciondiwbackend.dtos;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CambioContrasenaDTO {

    // Campo opcional, será requerido solo para usuarios normales
    private String contrasenaActual;

    @NotBlank(message = "La nueva contraseña es obligatoria")
    @Size(min = 6, message = "La contraseña debe tener al menos 6 caracteres")
    private String nuevaContrasena;

    @NotBlank(message = "La confirmación de contraseña es obligatoria")
    private String confirmacionContrasena;
}