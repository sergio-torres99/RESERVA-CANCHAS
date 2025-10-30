package com.reservatucancha.reserva_canchas_api.controller;

import com.reservatucancha.reserva_canchas_api.dto.ReservaDto;
import com.reservatucancha.reserva_canchas_api.entity.Reserva;
import com.reservatucancha.reserva_canchas_api.service.ReservaService;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/reservas")
public class ReservaController {

    private final ReservaService reservaService;

    public ReservaController(ReservaService reservaService) {
        this.reservaService = reservaService;
    }

    @GetMapping
    public List<ReservaDto> getAllReservas() {
        return reservaService.findAll().stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    @GetMapping("/{id}")
    public ResponseEntity<ReservaDto> getReservaById(@PathVariable Long id) {
        Optional<Reserva> optionalReserva = reservaService.findById(id);
        return optionalReserva.map(this::convertToDto)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping("/slots-disponibles")
    public ResponseEntity<List<String>> getSlotsDisponibles(
            @RequestParam Long canchaId, 
            @RequestParam String fecha) {
        try {
            LocalDate fechaParsed = LocalDate.parse(fecha);
            List<String> slots = reservaService.getSlotsDisponibles(canchaId, fechaParsed);
            return ResponseEntity.ok(slots);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(List.of());
        }
    }

    @PostMapping
    public ResponseEntity<ReservaDto> createReserva(@RequestBody ReservaDto reservaDto) {
        try {
            Reserva reserva = convertToEntity(reservaDto);
            Reserva savedReserva = reservaService.save(reserva);
            return new ResponseEntity<>(convertToDto(savedReserva), HttpStatus.CREATED);
        } catch (IllegalArgumentException e) {
            return new ResponseEntity(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<ReservaDto> updateReserva(@PathVariable Long id, @RequestBody ReservaDto reservaDto) {
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

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteReserva(@PathVariable Long id) {
        if (reservaService.findById(id).isPresent()) {
            reservaService.deleteById(id);
            return ResponseEntity.noContent().build();
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    // Refactor: Los métodos de conversión ahora usan los campos de fecha y hora separados
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