package com.reservatucancha.reserva_canchas_api.controller;

import com.reservatucancha.reserva_canchas_api.entity.Usuario;
import com.reservatucancha.reserva_canchas_api.service.UsuarioService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/usuarios")
@Tag(name = "Usuarios", description = "Gestión de usuarios del sistema")
public class UsuarioController {

    private final UsuarioService usuarioService;

    public UsuarioController(UsuarioService usuarioService) {
        this.usuarioService = usuarioService;
    }

    @Operation(summary = "Obtener todos los usuarios", description = "Retorna una lista de todos los usuarios registrados en el sistema")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista de usuarios obtenida exitosamente", content = @Content(mediaType = "application/json", schema = @Schema(implementation = Usuario.class)))
    })
    @GetMapping
    public List<Usuario> getAllUsuarios() {
        return usuarioService.findAll();
    }

    @Operation(summary = "Obtener usuario por ID", description = "Retorna los detalles de un usuario específico por su ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Usuario encontrado", content = @Content(mediaType = "application/json", schema = @Schema(implementation = Usuario.class))),
            @ApiResponse(responseCode = "404", description = "Usuario no encontrado", content = @Content)
    })
    @GetMapping("/{id}")
    public ResponseEntity<Usuario> getUsuarioById(
            @Parameter(description = "ID único del usuario", required = true) @PathVariable Long id) {
        Optional<Usuario> optionalUsuario = usuarioService.findById(id);
        if (optionalUsuario.isPresent()) {
            return ResponseEntity.ok(optionalUsuario.get());
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @Operation(summary = "Crear nuevo usuario", description = "Registra un nuevo usuario en el sistema")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Usuario creado exitosamente", content = @Content(mediaType = "application/json", schema = @Schema(implementation = Usuario.class))),
            @ApiResponse(responseCode = "400", description = "Datos de entrada inválidos", content = @Content)
    })
    @PostMapping
    public ResponseEntity<Usuario> createUsuario(
            @Parameter(description = "Datos del nuevo usuario", required = true) @RequestBody Usuario usuario) {
        Usuario savedUsuario = usuarioService.save(usuario);
        return ResponseEntity.ok(savedUsuario);
    }

    @Operation(summary = "Actualizar usuario", description = "Actualiza los datos de un usuario existente")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Usuario actualizado exitosamente", content = @Content(mediaType = "application/json", schema = @Schema(implementation = Usuario.class))),
            @ApiResponse(responseCode = "404", description = "Usuario no encontrado", content = @Content),
            @ApiResponse(responseCode = "400", description = "Datos de entrada inválidos", content = @Content)
    })
    @PutMapping("/{id}")
    public ResponseEntity<Usuario> updateUsuario(
            @Parameter(description = "ID único del usuario a actualizar", required = true) @PathVariable Long id,
            @Parameter(description = "Nuevos datos del usuario", required = true) @RequestBody Usuario usuarioDetails) {
        Optional<Usuario> optionalUsuario = usuarioService.findById(id);
        if (optionalUsuario.isPresent()) {
            Usuario existingUsuario = optionalUsuario.get();
            existingUsuario.setNombre(usuarioDetails.getNombre());
            existingUsuario.setApellido(usuarioDetails.getApellido());
            existingUsuario.setEmail(usuarioDetails.getEmail());
            existingUsuario.setPassword(usuarioDetails.getPassword());
            existingUsuario.setIdentificacion(usuarioDetails.getIdentificacion());
            existingUsuario.setTelefono(usuarioDetails.getTelefono());
            Usuario updatedUsuario = usuarioService.save(existingUsuario);
            return ResponseEntity.ok(updatedUsuario);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @Operation(summary = "Eliminar usuario", description = "Elimina un usuario del sistema")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Usuario eliminado exitosamente"),
            @ApiResponse(responseCode = "404", description = "Usuario no encontrado", content = @Content)
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUsuario(
            @Parameter(description = "ID único del usuario a eliminar", required = true) @PathVariable Long id) {
        if (usuarioService.findById(id).isPresent()) {
            usuarioService.deleteById(id);
            return ResponseEntity.noContent().build();
        } else {
            return ResponseEntity.notFound().build();
        }
    }
}
