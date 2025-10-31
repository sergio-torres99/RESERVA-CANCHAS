package com.reservatucancha.reserva_canchas_api.controller;

import com.reservatucancha.reserva_canchas_api.entity.Cancha;
import com.reservatucancha.reserva_canchas_api.service.CanchaService;
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
@RequestMapping("/api/canchas")
@Tag(name = "Canchas", description = "Gestión de canchas de fútbol")
public class CanchaController {

    private final CanchaService canchaService;

    public CanchaController(CanchaService canchaService) {
        this.canchaService = canchaService;
    }

    @Operation(summary = "Obtener todas las canchas", description = "Retorna una lista de todas las canchas disponibles en el sistema")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista de canchas obtenida exitosamente", content = @Content(mediaType = "application/json", schema = @Schema(implementation = Cancha.class)))
    })
    @GetMapping
    public List<Cancha> getAllCanchas() {
        return canchaService.findAll();
    }

    @Operation(summary = "Obtener cancha por ID", description = "Retorna los detalles de una cancha específica por su ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Cancha encontrada", content = @Content(mediaType = "application/json", schema = @Schema(implementation = Cancha.class))),
            @ApiResponse(responseCode = "404", description = "Cancha no encontrada", content = @Content)
    })
    @GetMapping("/{id}")
    public ResponseEntity<Cancha> getCanchaById(
            @Parameter(description = "ID único de la cancha", required = true) @PathVariable Long id) {
        Optional<Cancha> optionalCancha = canchaService.findById(id);
        if (optionalCancha.isPresent()) {
            return ResponseEntity.ok(optionalCancha.get());
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @Operation(summary = "Crear nueva cancha", description = "Registra una nueva cancha en el sistema")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Cancha creada exitosamente", content = @Content(mediaType = "application/json", schema = @Schema(implementation = Cancha.class))),
            @ApiResponse(responseCode = "400", description = "Datos de entrada inválidos", content = @Content)
    })
    @PostMapping
    public ResponseEntity<Cancha> createCancha(
            @Parameter(description = "Datos de la nueva cancha", required = true) @RequestBody Cancha cancha) {
        Cancha savedCancha = canchaService.save(cancha);
        return ResponseEntity.ok(savedCancha);
    }

    @Operation(summary = "Actualizar cancha", description = "Actualiza los datos de una cancha existente")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Cancha actualizada exitosamente", content = @Content(mediaType = "application/json", schema = @Schema(implementation = Cancha.class))),
            @ApiResponse(responseCode = "404", description = "Cancha no encontrada", content = @Content),
            @ApiResponse(responseCode = "400", description = "Datos de entrada inválidos", content = @Content)
    })
    @PutMapping("/{id}")
    public ResponseEntity<Cancha> updateCancha(
            @Parameter(description = "ID único de la cancha a actualizar", required = true) @PathVariable Long id,
            @Parameter(description = "Nuevos datos de la cancha", required = true) @RequestBody Cancha canchaDetails) {
        Optional<Cancha> optionalCancha = canchaService.findById(id);
        if (optionalCancha.isPresent()) {
            Cancha existingCancha = optionalCancha.get();
            existingCancha.setNombre(canchaDetails.getNombre());
            existingCancha.setTipoCancha(canchaDetails.getTipoCancha());
            existingCancha.setPrecioPorHora(canchaDetails.getPrecioPorHora());
            Cancha updatedCancha = canchaService.save(existingCancha);
            return ResponseEntity.ok(updatedCancha);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @Operation(summary = "Eliminar cancha", description = "Elimina una cancha del sistema")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Cancha eliminada exitosamente"),
            @ApiResponse(responseCode = "404", description = "Cancha no encontrada", content = @Content)
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCancha(
            @Parameter(description = "ID único de la cancha a eliminar", required = true) @PathVariable Long id) {
        if (canchaService.findById(id).isPresent()) {
            canchaService.deleteById(id);
            return ResponseEntity.noContent().build();
        } else {
            return ResponseEntity.notFound().build();
        }
    }
}
