package com.reservatucancha.reserva_canchas_api.config;

import com.reservatucancha.reserva_canchas_api.jwt.JwtAuthenticationFilter;
import com.reservatucancha.reserva_canchas_api.service.UsuarioService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfigurationSource;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final UsuarioService usuarioService;
    private final CorsConfigurationSource corsConfigurationSource;

    // ¡Aquí está la clave! Ya no inyectamos el filtro en el constructor.
    // Solo necesitamos el usuarioService para el AuthenticationManager.
    public SecurityConfig(UsuarioService usuarioService, CorsConfigurationSource corsConfigurationSource) {
        this.usuarioService = usuarioService;
        this.corsConfigurationSource = corsConfigurationSource;
    }

    @Bean
    public JwtAuthenticationFilter jwtAuthenticationFilter() {
        return new JwtAuthenticationFilter();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        return http
                .cors(cors -> cors.configurationSource(corsConfigurationSource))
                .csrf(csrf -> csrf.disable())
                .authorizeHttpRequests(auth -> auth
                        // Permitir acceso público a endpoints de autenticación
                        .requestMatchers("/api/auth/**").permitAll()
                        // Permitir acceso público a documentación OpenAPI/Swagger y sus recursos
                        // - /v3/api-docs/** y /api-docs/** : Especificación OpenAPI (JSON/YAML)
                        // - /swagger-ui/** : Interfaz de usuario interactiva
                        // - /swagger-resources/** : Configuración de Swagger
                        // - /webjars/** : Recursos web (CSS, JS)
                        .requestMatchers("/v3/api-docs/**", "/v3/api-docs.yaml", "/api-docs/**", "/api-docs.yaml")
                        .permitAll()
                        .requestMatchers("/swagger-ui/**", "/swagger-ui.html").permitAll()
                        .requestMatchers("/swagger-resources/**").permitAll()
                        .requestMatchers("/webjars/**").permitAll()
                        // Cualquier otra petición requiere autenticación
                        .anyRequest().authenticated())
                .sessionManagement(sess -> sess.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                // ¡Aquí agregamos el filtro usando el método @Bean!
                .addFilterBefore(jwtAuthenticationFilter(), UsernamePasswordAuthenticationFilter.class)
                .build();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }
}