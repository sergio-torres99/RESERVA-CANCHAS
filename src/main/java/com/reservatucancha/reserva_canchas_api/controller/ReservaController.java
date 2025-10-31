package com.reservatucancha.reserva_canchas_api.controller;

import com.reservatucancha.reserva_canchas_api.dto.ReservaDto;
import com.reservatucancha.reserva_canchas_api.entity.Reserva;
import com.reservatucancha.reserva_canchas_api.service.ReservaService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/reservas")
@Tag(name = "Reservas", description = "Gestión de reservas de canchas de fútbol")
public class ReservaController {

    private final ReservaService reservaService;

    public ReservaController(ReservaService reservaService) {
        this.reservaService = reservaService;
    }

    @Operation(summary = "Obtener todas las reservas", description = "Retorna una lista de todas las reservas registradas en el sistema")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista de reservas obtenida exitosamente", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ReservaDto.class)))
    })
    @GetMapping
    public List<ReservaDto> getAllReservas() {
        return reservaService.findAll().stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    @Operation(summary = "Obtener reserva por ID", description = "Retorna los detalles de una reserva específica por su ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Reserva encontrada", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ReservaDto.class))),
            @ApiResponse(responseCode = "404", description = "Reserva no encontrada", content = @Content)
    })
    @GetMapping("/{id}")
    public ResponseEntity<ReservaDto> getReservaById(
            @Parameter(description = "ID único de la reserva", required = true) @PathVariable Long id) {
        Optional<Reserva> optionalReserva = reservaService.findById(id);
        return optionalReserva.map(this::convertToDto)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @Operation(summary = "Obtener horarios disponibles", description = "Retorna los slots de tiempo disponibles para una cancha en una fecha específica. "
            +
            "Los horarios disponibles son de 8:00 AM a 8:00 PM en slots de 1 hora.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Slots disponibles obtenidos exitosamente", content = @Content(mediaType = "application/json", schema = @Schema(type = "array", implementation = String.class))),
            @ApiResponse(responseCode = "400", description = "Parámetros inválidos", content = @Content)
    })
    @GetMapping("/slots-disponibles")
    public ResponseEntity<List<String>> getSlotsDisponibles(
            @Parameter(description = "ID de la cancha para consultar disponibilidad", required = true) @RequestParam Long canchaId,
            @Parameter(description = "Fecha para consultar disponibilidad (formato: YYYY-MM-DD)", required = true) @RequestParam String fecha) {
        try {
            LocalDate fechaParsed = LocalDate.parse(fecha);
            List<String> slots = reservaService.getSlotsDisponibles(canchaId, fechaParsed);
            return ResponseEntity.ok(slots);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(List.of());
        }
    }

    @Operation(summary = "Obtener reservas por usuario", description = "Retorna todas las reservas realizadas por un usuario específico")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Reservas del usuario obtenidas exitosamente", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ReservaDto.class))),
            @ApiResponse(responseCode = "400", description = "ID de usuario inválido", content = @Content)
    })
    @GetMapping("/usuario/{usuarioId}")
    public ResponseEntity<List<ReservaDto>> getReservasByUsuario(
            @Parameter(description = "ID único del usuario", required = true) @PathVariable Long usuarioId) {
        try {
            List<ReservaDto> reservasUsuario = reservaService.findAll().stream()
                    .filter(reserva -> reserva.getUsuario().getId().equals(usuarioId))
                    .map(this::convertToDto)
                    .collect(Collectors.toList());
            return ResponseEntity.ok(reservasUsuario);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(List.of());
        }
    }

    @Operation(summary = "Crear nueva reserva", description = "Registra una nueva reserva en el sistema. " +
            "Las reservas solo pueden realizarse entre las 8:00 AM y 8:00 PM.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Reserva creada exitosamente", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ReservaDto.class))),
            @ApiResponse(responseCode = "400", description = "Datos de entrada inválidos o horario no disponible", content = @Content)
    })
    @PostMapping
    public ResponseEntity<ReservaDto> createReserva(
            @Parameter(description = "Datos de la nueva reserva", required = true) @RequestBody ReservaDto reservaDto) {
        try {
            Reserva reserva = convertToEntity(reservaDto);
            Reserva savedReserva = reservaService.save(reserva);
            return new ResponseEntity<>(convertToDto(savedReserva), HttpStatus.CREATED);
        } catch (IllegalArgumentException e) {
            return new ResponseEntity(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @Operation(summary = "Actualizar reserva", description = "Actualiza los datos de una reserva existente")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Reserva actualizada exitosamente", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ReservaDto.class))),
            @ApiResponse(responseCode = "404", description = "Reserva no encontrada", content = @Content),
            @ApiResponse(responseCode = "400", description = "Datos de entrada inválidos", content = @Content)
    })
    @PutMapping("/{id}")
    public ResponseEntity<ReservaDto> updateReserva(
            @Parameter(description = "ID único de la reserva a actualizar", required = true) @PathVariable Long id,
            @Parameter(description = "Nuevos datos de la reserva", required = true) @RequestBody ReservaDto reservaDto) {
        Optional<Reserva> optionalReserva = reservaService.findById(id);
        if (optionalReserva.isPresent()) {
            Reserva existingReserva = optionalReserva.get();
            // Refactor: Actualizamos los nuevos campos de fecha y hora.
            existingReserva.setFechaReserva(reservaDto.getFechaReserva());
            existingReserva.setHoraInicio(reservaDto.getHoraInicio());
            existingReserva.setHoraFin(reservaDto.getHoraFin());
            existingReserva.setCancha(reservaService.findCanchaById(reservaDto.getCanchaId()).get());
            existingReserva.setUsuario(reservaService.findUsuarioById(reservaDto.getUsuarioId()).get());

            Reserva updatedReserva = reservaService.save(existingReserva);
            return ResponseEntity.ok(convertToDto(updatedReserva));
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @Operation(summary = "Eliminar reserva", description = "Elimina una reserva del sistema")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Reserva eliminada exitosamente"),
            @ApiResponse(responseCode = "404", description = "Reserva no encontrada", content = @Content)
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteReserva(
            @Parameter(description = "ID único de la reserva a eliminar", required = true) @PathVariable Long id) {
        if (reservaService.findById(id).isPresent()) {
            reservaService.deleteById(id);
            return ResponseEntity.noContent().build();
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    // Refactor: Los métodos de conversión ahora usan los campos de fecha y hora
    // separados
    private ReservaDto convertToDto(Reserva reserva) {
        ReservaDto reservaDto = new ReservaDto();
        reservaDto.setId(reserva.getId());
        reservaDto.setFechaReserva(reserva.getFechaReserva());
        reservaDto.setHoraInicio(reserva.getHoraInicio());
        reservaDto.setHoraFin(reserva.getHoraFin());
        reservaDto.setUsuarioId(reserva.getUsuario().getId());
        reservaDto.setCanchaId(reserva.getCancha().getId());
        return reservaDto;
    }

    private Reserva convertToEntity(ReservaDto reservaDto) {
        Reserva reserva = new Reserva();
        reserva.setId(reservaDto.getId());
        reserva.setFechaReserva(reservaDto.getFechaReserva());
        reserva.setHoraInicio(reservaDto.getHoraInicio());
        reserva.setHoraFin(reservaDto.getHoraFin());
        reserva.setUsuario(reservaService.findUsuarioById(reservaDto.getUsuarioId())
                .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado")));
        reserva.setCancha(reservaService.findCanchaById(reservaDto.getCanchaId())
                .orElseThrow(() -> new IllegalArgumentException("Cancha no encontrada")));
        return reserva;
    }
}