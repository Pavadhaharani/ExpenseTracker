package com.expensetracker.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

/**
 * CORS configuration.
 *
 * <p>Allowed origins are read from {@code app.cors.allowed-origins} so that
 * dev ({@code localhost:4200}) and prod (the real domain) can be handled
 * via separate Spring profiles without touching the code.
 *
 * <p>The {@link CorsConfigurationSource} bean is picked up automatically by
 * Spring Security's {@code .cors(cors -> {})} configurer.
 */
@Configuration
public class CorsConfig {

    /** Comma-separated list of allowed origins, e.g. {@code http://localhost:4200}. */
    @Value("${app.cors.allowed-origins:http://localhost:4200}")
    private String allowedOrigins;

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();

        // Parse comma-separated origins from property
        config.setAllowedOrigins(List.of(allowedOrigins.split(",")));

        config.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        config.setAllowedHeaders(List.of("*"));

        // Expose Authorization header so the Angular interceptor can read it
        config.setExposedHeaders(List.of("Authorization"));

        // Required when sending cookies or Authorization headers cross-origin
        config.setAllowCredentials(true);

        // Cache preflight response for 1 hour
        config.setMaxAge(3600L);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        return source;
    }
}
