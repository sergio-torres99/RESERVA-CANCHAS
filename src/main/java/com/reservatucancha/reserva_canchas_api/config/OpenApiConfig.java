package com.reservatucancha.reserva_canchas_api.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("API de Reserva de Canchas")
                        .description("Sistema de gestión para reservas de canchas de fútbol. " +
                                "Permite a los usuarios registrarse, autenticarse y realizar reservas " +
                                "de canchas en horarios disponibles de 8:00 AM a 8:00 PM.")
                        .version("v1.0.0")
                        .contact(new Contact()
                                .name("Equipo de Desarrollo")
                                .email("desarrollo@reservatucancha.com")
                                .url("https://github.com/sergio-torres99/RESERVA-CANCHAS"))
                        .license(new License()
                                .name("MIT License")
                                .url("https://opensource.org/licenses/MIT")))
                .servers(List.of(
                        new Server()
                                .url("http://localhost:8080")
                                .description("Servidor de Desarrollo"),
                        new Server()
                                .url("https://api.reservatucancha.com")
                                .description("Servidor de Producción")))
                .components(new Components()
                        .addSecuritySchemes("bearerAuth", new SecurityScheme()
                                .type(SecurityScheme.Type.HTTP)
                                .scheme("bearer")
                                .bearerFormat("JWT")
                                .description("Token JWT para autenticación. " +
                                        "Formato: 'Bearer {token}'")))
                .addSecurityItem(new SecurityRequirement()
                        .addList("bearerAuth"));
    }
}