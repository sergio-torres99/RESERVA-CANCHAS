package com.reservatucancha.reserva_canchas_api.entity;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data // Genera getters, setters, toString, equals y hashCode de Lombok
@NoArgsConstructor // Genera un constructor sin argumentos de Lombok
@AllArgsConstructor // Genera un constructor con todos los argumentos de Lombok
@Entity // Indica que esta clase es una entidad JPA
@Table(name = "cancha") // Mapea la clase a una tabla llamada "cancha"
@Schema(description = "Entidad que representa una cancha de fútbol disponible para reserva")
public class Cancha {

    @Id // Marca el campo como la clave primaria
    @GeneratedValue(strategy = GenerationType.IDENTITY) // Configura la estrategia de generación de la clave primaria
    @Schema(description = "ID único de la cancha", example = "1")
    private Long id;

    @Column(name = "nombre") // Mapea el campo a una columna llamada "nombre"
    @Schema(description = "Nombre descriptivo de la cancha", example = "Cancha Principal")
    private String nombre;

    @Column(name = "tipo_cancha") // Mapea el campo a una columna llamada "tipo_cancha"
    @Schema(description = "Tipo de cancha según capacidad", example = "Fútbol 5", allowableValues = {"Fútbol 5", "Fútbol 7", "Fútbol 11"})
    private String tipoCancha; // Ej: "Fútbol 5", "Fútbol 7"

    @Column(name = "precio_por_hora") // Mapea el campo a una columna llamada "precio_por_hora"
    @Schema(description = "Precio por hora de alquiler de la cancha", example = "25000.0")
    private double precioPorHora;

    @Column(name = "imagen_url") // Mapea el campo a una columna llamada "imagen_url"
    @Schema(description = "URL de la imagen de la cancha", example = "https://ejemplo.com/images/cancha1.jpg")
    private String imagenURL;

    @Column(name = "ubicacion") // Mapea el campo a una columna llamada "ubicacion"
    @Schema(description = "Ubicación física de la cancha", example = "Sector Norte, Cancha #1")
    private String ubicacion;

}