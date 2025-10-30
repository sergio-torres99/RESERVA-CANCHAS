package com.reservatucancha.reserva_canchas_api.service;

import com.reservatucancha.reserva_canchas_api.entity.Reserva;
import com.reservatucancha.reserva_canchas_api.entity.Cancha;
import com.reservatucancha.reserva_canchas_api.entity.Usuario;
import com.reservatucancha.reserva_canchas_api.repository.ReservaRepository;
import com.reservatucancha.reserva_canchas_api.service.CanchaService;
import com.reservatucancha.reserva_canchas_api.service.UsuarioService;
import org.springframework.stereotype.Service;

import java.time.LocalTime;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.ArrayList;
import java.util.Arrays;

@Service
public class ReservaService {

    private final ReservaRepository reservaRepository;
    private final CanchaService canchaService;
    private final UsuarioService usuarioService;

    public ReservaService(ReservaRepository reservaRepository, CanchaService canchaService, UsuarioService usuarioService) {
        this.reservaRepository = reservaRepository;
        this.canchaService = canchaService;
        this.usuarioService = usuarioService;
    }

    public List<Reserva> findAll() {
        return reservaRepository.findAll();
    }

    public Optional<Reserva> findById(Long id) {
        return reservaRepository.findById(id);
    }

    public Reserva save(Reserva reserva) {
        // Validar horarios 8AM-8PM (12 slots de 1 hora)
        validarHorarios(reserva);
        return reservaRepository.save(reserva);
    }

    public void deleteById(Long id) {
        reservaRepository.deleteById(id);
    }

    // Métodos para buscar por ID
    public Optional<Usuario> findUsuarioById(Long id) {
        return usuarioService.findById(id);
    }

    public Optional<Cancha> findCanchaById(Long id) {
        return canchaService.findById(id);
    }

    /**
     * Obtener slots disponibles para una cancha en una fecha específica
     */
    public List<String> getSlotsDisponibles(Long canchaId, LocalDate fecha) {
        // Los 12 slots disponibles (8AM-8PM)
        List<LocalTime> todosLosSlots = Arrays.asList(
            LocalTime.of(8, 0), LocalTime.of(9, 0), LocalTime.of(10, 0), LocalTime.of(11, 0),
            LocalTime.of(12, 0), LocalTime.of(13, 0), LocalTime.of(14, 0), LocalTime.of(15, 0),
            LocalTime.of(16, 0), LocalTime.of(17, 0), LocalTime.of(18, 0), LocalTime.of(19, 0)
        );

        // Obtener reservas existentes para esa cancha y fecha
        List<Reserva> reservasExistentes = reservaRepository.findAll().stream()
            .filter(r -> r.getCancha().getId().equals(canchaId) && r.getFechaReserva().equals(fecha))
            .toList();

        // Filtrar slots disponibles
        List<String> slotsDisponibles = new ArrayList<>();
        for (LocalTime slot : todosLosSlots) {
            boolean ocupado = reservasExistentes.stream()
                .anyMatch(r -> r.getHoraInicio().equals(slot));
            
            if (!ocupado) {
                slotsDisponibles.add(slot + " - " + slot.plusHours(1));
            }
        }

        return slotsDisponibles;
    }

    /**
     * Validar que las reservas sean de 8AM a 8PM (12 slots de 1 hora)
     */
    private void validarHorarios(Reserva reserva) {
        LocalTime horaInicio = reserva.getHoraInicio();
        LocalTime horaFin = reserva.getHoraFin();
        
        // Validar que esté en el rango 8AM-7PM (para que termine a las 8PM)
        if (horaInicio.isBefore(LocalTime.of(8, 0)) || horaInicio.isAfter(LocalTime.of(19, 0))) {
            throw new IllegalArgumentException("Las reservas solo pueden comenzar entre las 8:00 AM y las 7:00 PM");
        }
        
        // Validar que sea exactamente 1 hora
        if (!horaFin.equals(horaInicio.plusHours(1))) {
            throw new IllegalArgumentException("Las reservas deben ser de exactamente 1 hora");
        }
        
        // Validar que no termine después de las 8PM
        if (horaFin.isAfter(LocalTime.of(20, 0))) {
            throw new IllegalArgumentException("Las reservas no pueden terminar después de las 8:00 PM");
        }
    }
}