package com.reservatucancha.reserva_canchas_api.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.time.LocalDate;
import java.time.LocalTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "DTO para transferencia de datos de reservas")
public class ReservaDto {
    
    @Schema(description = "ID único de la reserva", example = "1")
    private Long id;
    
    @Schema(description = "Fecha de la reserva", example = "2024-12-15")
    private LocalDate fechaReserva;
    
    @Schema(description = "Hora de inicio de la reserva (formato 24h)", example = "14:00")
    private LocalTime horaInicio;
    
    @Schema(description = "Hora de fin de la reserva (formato 24h)", example = "15:00")
    private LocalTime horaFin;
    
    @Schema(description = "ID del usuario que realiza la reserva", example = "1")
    private Long usuarioId;
    
    @Schema(description = "ID de la cancha reservada", example = "1")
    private Long canchaId;
}