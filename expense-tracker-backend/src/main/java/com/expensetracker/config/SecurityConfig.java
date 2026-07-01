package com.expensetracker.config;

import com.expensetracker.security.JwtAuthenticationFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

/**
 * Spring Security configuration.
 *
 * <ul>
 *   <li>CSRF disabled  — stateless JWT API needs no CSRF protection.</li>
 *   <li>Sessions STATELESS — Spring Security never creates an HttpSession.</li>
 *   <li>Public routes: {@code /auth/**} (register, login).</li>
 *   <li>All other routes require a valid Bearer JWT.</li>
 * </ul>
 *
 * <p><strong>Why no {@code DaoAuthenticationProvider} bean?</strong><br>
 * Spring Boot 3 auto-configures one when it detects a {@link org.springframework.security.core.userdetails.UserDetailsService}
 * bean and a {@link PasswordEncoder} bean. Declaring it explicitly here AND
 * injecting {@code UserDetailsServiceImpl} via constructor creates a circular
 * dependency with Spring Security's own initialisation. The auto-configured
 * provider is wired into the {@link AuthenticationManager} via
 * {@link AuthenticationConfiguration#getAuthenticationManager()}.
 */
@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {

    /** BCrypt with default cost (10 rounds). */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /**
     * Exposes the {@link AuthenticationManager} as a bean so that
     * {@code AuthService} can call {@code authenticate()} programmatically.
     * Spring Boot wires the auto-configured {@code DaoAuthenticationProvider}
     * (using our {@code UserDetailsServiceImpl} + {@code PasswordEncoder}) into
     * this manager automatically.
     */
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config)
            throws Exception {
        return config.getAuthenticationManager();
    }

    /**
     * {@code JwtAuthenticationFilter} is injected as a method parameter here
     * (not as a constructor field) to avoid a potential Spring Security
     * circular-dependency during context initialisation.
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http,
                                                    JwtAuthenticationFilter jwtAuthFilter)
            throws Exception {
        http
            .csrf(AbstractHttpConfigurer::disable)

            // Delegate CORS to CorsConfig.corsConfigurationSource()
            .cors(cors -> {})

            // No session — every request must carry a JWT
            .sessionManagement(session ->
                session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))

            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/auth/**").permitAll()
                .anyRequest().authenticated()
            )

            // JWT filter runs before Spring Security's UsernamePasswordAuthenticationFilter
            .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}
