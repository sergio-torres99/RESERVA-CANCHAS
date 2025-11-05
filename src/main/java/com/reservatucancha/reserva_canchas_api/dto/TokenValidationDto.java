package com.reservatucancha.reserva_canchas_api.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TokenValidationDto {
    private boolean valid;
    private Long id;
    private String username;
    private String email;
    private Date issuedAt;
    private Date expiresAt;
    private String token;
    private String message;
    
    // Constructor para token inválido
    public TokenValidationDto(boolean valid, String message) {
        this.valid = valid;
        this.message = message;
    }
}